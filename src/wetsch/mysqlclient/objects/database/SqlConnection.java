package wetsch.mysqlclient.objects.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlConnection {
	private boolean useSSLConnection = false;//Sets the connection to use ssl if true.
	private boolean connectionOK = false;//Set to true if connection is established with know errors.
	private String serverAddress = null;//Servers ip address.
	private Connection sqlCon;//Connection
	private String host = null;//Serve'sr host information.
	private String userName = null;//Server'ss username.
	private String password = null;//Server's password.
	private String schema = null;
	
	public SqlConnection(String serverAddress, String userName, String password, String schema, boolean useSSLConnection) throws SQLException{
		this.serverAddress = serverAddress;
		this.userName = userName;
		this.password = password;
		this.schema = schema;
		this.useSSLConnection = useSSLConnection;
		//Check to see if the SQL connection should use SSL.
		if(useSSLConnection)
			host = "jdbc:mysql://"+this.serverAddress+"/" + schema + 
					"?useSSL=true"+ 
					"&requireSSL=true"+
					"&verifyServerCertificate=false";
		else
			host = "jdbc:mysql://"+this.serverAddress+"/" + schema;
	}
	
	//Return the current database name.
	public String getSchemaName(){
		return schema;
	}
	
	public void setSchemaName(String schemaName){
		this.schema = schemaName;
	}
	
	//Establish connection to server. 
		public boolean openConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
			sqlCon = DriverManager.getConnection(host, userName, password);
			sqlCon.setClientInfo("host", serverAddress);
			sqlCon.setClientInfo("user", userName);
			sqlCon.setClientInfo("security", Boolean.toString(useSSLConnection));
			sqlCon.setClientInfo("status", "OK");
			connectionOK = true;
			return connectionOK;
	}
	
		//Returns true if the connection to the database is ok. 
	public boolean isConnectonOK() {
		return connectionOK;
	}
	
	public void sendQuery(String query) throws SQLException{
		sqlCon.createStatement().execute(query);
	}
	
	
	
	public int getCount(String query) throws SQLException{
		ResultSet result = sqlCon.createStatement().executeQuery(query);
		
		while(result.next())
			return result.getInt(1);
		return 0;
	}
	
	public ArrayList<String[]> getFromSelectStatement(String query) throws SQLException{
		ResultSet result = sqlCon.createStatement().executeQuery(query);
		int rowCounter = 0;
		int columnCounter = 0;
		int columns;
		ArrayList<String[]> data = new ArrayList<String[]>();
		columns = result.getMetaData().getColumnCount();
		while(result.next()){
			data.add(new String[columns]);
			while(columnCounter < columns){
				data.get(rowCounter)[columnCounter] = result.getString(columnCounter+1);
				columnCounter++;
			}
			rowCounter++;
			columnCounter = 0;

		}
		return data;
	}
	
	public ResultSet getFromSelectStatementAsResultSet(String query) throws SQLException {
		return sqlCon.createStatement().executeQuery(query);

	}
	
	//Get data from table.
	public ResultSet selectFromTablePagination(String queryString) throws SQLException{
	     Statement stmt = sqlCon.createStatement(
                  ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_UPDATABLE);
	    ResultSet resultSetTableData = stmt.executeQuery(queryString);
		   return resultSetTableData;
	}
	
	/**
	 * Adds new record to currently in use result-set.
	 * @param data Data to be inserted.
	 * @param ColumnCount Number of columns in table.
	 * @param query Query String
	 * @return
	 * @throws SQLException
	 */
	public boolean addNewRecords(ArrayList<String[]> data,int ColumnCount , String query) throws SQLException{
		int statementCounter = 0;
		String value = null;
		PreparedStatement sqlQuery = sqlCon.prepareStatement(query);
		for(int r = 0; r < data.size(); r++){
			for(int c = 0; c < ColumnCount; c++){
				value = data.get(r)[c];
				statementCounter++;
				sqlQuery.setString(statementCounter, value);
			}
		}
		sqlQuery.execute();
		return true;
	}
	
	/**
	 * Close the SQL Connection
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException{
		sqlCon.close();
	}

}
