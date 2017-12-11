package wetsch.mysqlclient.objects.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wetsch.mysqlclient.objects.customuiobjects.interfaces.SchemaChangedListener;


public class Database {
	private Set<SchemaChangedListener> schemaListeners = new HashSet<SchemaChangedListener>();
	private String[] noDropList = new String[]{"mysql", "information_schema", "performance_schema"};//Read only schemas.
	protected SqlConnection sqlCon;
	protected String schemaName;
	private int tableCount;
	private LinkedHashMap<String, Tables> schemaTabls;
	
	public Database(SqlConnection sqlCon, String schemaName) throws SQLException{
		this.sqlCon = sqlCon;
		this.schemaName = schemaName;
		getSchemaTableCount();
		
	}
	/**
	 * Add a table created listener to the schema.
	 * @param listener
	 */
	public void addTableCreatedListener(SchemaChangedListener listener){
		schemaListeners.add(listener);
	}
	/**
	 * Remove the table created listener from this schema.
	 * @param listener
	 */
	public void removeTableCreatedListener(SchemaChangedListener listener){
		schemaListeners.remove(listener);
	}
	/**
	 * Returns true if the listener exists. 
	 * @param listener
	 * @return
	 */
	public boolean containsTableCreatedListener(SchemaChangedListener listener){
		if(schemaListeners.contains(listener))
			return true;
		else
			return false;
	}


	/**
	 * Call this method to regenerate the map of schemas in the database.
	 * @return HashMap
	 * @throws SQLException
	 */
	public LinkedHashMap<String, Database> refreshSchemas() throws SQLException{
		String query = "SHOW DATABASES";
		LinkedHashMap<String, Database> schemaMap = new LinkedHashMap<String, Database>();
		for(String[] r : sqlCon.getFromSelectStatement(query)){
			for(String s : r)
				schemaMap.put(s, new Database(sqlCon, s));
		}
		return schemaMap;
	}
	
	/**
	 * Call this method to populate the map of tables in the schema.
	 * @return true
	 * @throws SQLException
	 */
	public boolean populateTablesHmap() throws SQLException{
			if(tableCount == 0)
				return false;
			String query = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA ='" + schemaName + "' ORDER BY TABLE_NAME DESC";
			schemaTabls = new LinkedHashMap<String, Tables>();
			for(String[] r : sqlCon.getFromSelectStatement(query)){
				for(String s : r)
					schemaTabls.put(s, new Tables(sqlCon, schemaName, s));
			}
			return true;
	}
	//Get the number of tables in the schema from DB.
	private void getSchemaTableCount() throws SQLException{
		String query = "SELECT COUNT(*) FROM information_schema.TABLES WHERE table_schema ='" + schemaName +"'";
		tableCount = sqlCon.getCount(query);
	}
	
	/**
	 * Returns the name of the schema.
	 * @return String
	 */
	public String getSchemaName(){
		return schemaName;
	}
	
	/**
	 * Returns the number of tables in the schema.
	 * @return int
	 */
	public int getTableCount(){
		return tableCount;
	}
	
	/**
	 * Returns a map of all the tables associated with the  schema.
	 * @return HashMap
	 */
	public HashMap<String, Tables> getSchemaTables(){
		return schemaTabls;
	}
	
	/**
	 * Creates a new schema in the database.
	 * @param schemaName
	 * @param schemas
	 * @return true
	 * @throws SQLException
	 */
	public boolean createNewSchema(String schemaName, LinkedHashMap<String, Database> schemas) throws SQLException{
		String query = "CREATE DATABASE " + schemaName;
		sqlCon.sendQuery(query);
		schemas.put(schemaName, new Database(sqlCon, schemaName));
		return true;
	}
	
	/**
	 * Creates a new table in the selected schema.    
	 * @param tableName The name to be given to the table.
	 * @param query The query that is to be passed to the My-SQL server.
	 * @throws SQLException
	 */
	public void createNewTable(String tableName, String query) throws SQLException{
		Pattern p = Pattern.compile("^(CREATE TABLE ).*");
		Matcher m = p.matcher(query);
		if(!m.matches())
			throw new SQLException("The query does nto appear to be formated properly.");
		sqlCon.sendQuery(query);
		schemaTabls.put(tableName, new Tables(sqlCon, schemaName, tableName));
		tableCount++;
		for(SchemaChangedListener listener : schemaListeners)
			listener.newTableAdded(tableName, this);
	}
	
	/**
	 * Removes the table from the selected schema.
	 * @param tableName The name of the table.
	 * @return true
	 * @throws SQLException  Exception is thrown if query fails.
	 */
	public boolean dropTable(String tableName) throws SQLException{
		if(Arrays.asList(noDropList).contains(schemaName))
			throw new SQLException("Can not drop table from system schema.");
		String query = "DROP TABLE " + schemaName +"." + tableName;
		sqlCon.sendQuery(query);
		schemaTabls.remove(tableName);
		tableCount--;
		for(SchemaChangedListener listener : schemaListeners)
			listener.tableRemoved(tableName, this);
		return true;
	}

	/**
	 * Removes the schema from the My-SQL database.
	 * @return true
	 * @throws SQLException Exception is thrown if query fails.
	 */
	public boolean dropSchema() throws SQLException{
		if(Arrays.asList(noDropList).contains(schemaName))
			throw new SQLException("This schema " + schemaName + " is a system schema and can not be dropped.");
		String query = "DROP DATABASE " + schemaName;
		sqlCon.sendQuery(query);
		return true;
	}
	
	/**
	 * Closes the My-SQL connection.
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException{
		sqlCon.closeConnection();
	}

}
