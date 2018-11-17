package wetsch.mysqlclient.objects.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.sql.ResultSet;
import java.sql.SQLException;

import wetsch.mysqlclient.objects.customuiobjects.Console;
import wetsch.mysqlclient.objects.customuiobjects.interfaces.SchemaChangedListener;



public class Tables{
	private Set<SchemaChangedListener> schemaListeners = new HashSet<SchemaChangedListener>();
	private SqlConnection sqlCon;
	private String[] columnNames = null;//Holds the tables column names.
	private String schemaName;//The schema that the table belongs to.
	private String tableName;//The name of the table.
	private int columnCount;//The number of columns in the table.
	private int rowCount;//The maximum number of throws in the table.
	private int filteredRowCount;//The number of rows matching column constraints.
	private ArrayList<String[]> tableDescribe = null;//Holds the tables description information.
	private String[] noDropList = new String[]{"mysql", "information_schema", "performance_schema"};//Read only schemas.
	private ResultSet resultSet;
	
	public Tables(SqlConnection sqlCon, String schemaName, String tableName) throws SQLException{
		this.sqlCon = sqlCon;
		this.schemaName = schemaName;
		this.tableName = tableName;
		columnCount = findColumnCount();
		rowCount = getTableRowCount();
	}
		
	/**
	 * Returns the filtered row count when filters are in use. 
	 * @return int
	 */
	public int getFilteredRowCount(){
		return filteredRowCount;
	}
	
	/**
	 * Returns a list of the column numbers of the primary key columns.
	 * @return ArrayList<Integer>
	 */
	public ArrayList<Integer> getPrimaryKeyColumnNumbers(){
		ArrayList<Integer> priCN = new ArrayList<Integer>();
		for(int i = 0; i < tableDescribe.size(); i++)
			if(tableDescribe.get(i)[3].equals("PRI"))
				priCN.add(new Integer(i));
		return priCN;
	}
	
	public ResultSet getResultSet(){
		return resultSet;
	}
	
	/**
	 * Returns an array of column names.
	 * @return String array.
	 */
	public String[] getColumnNamesArray(){
		if(columnNames != null)
			return columnNames;
		String[] names = new String[columnCount];
		int c = 0;
		for(String[] r : tableDescribe)
			names[c++] = r[0];
		columnNames = names;
		return columnNames;
	}
	
	/**
	 * Call this method to populate the table description array.
	 * @return
	 * @throws SQLException
	 */
	public boolean populseTableDescribtion() throws SQLException{
		String query = "DESCRIBE " + schemaName + "." + tableName;
		
		if(columnCount == 0)
			return false;
		tableDescribe = sqlCon.getFromSelectStatement(query);
		return true;
	}
	
	/**
	 * Returns the name of the table.
	 * @return String
	 */
	public String getTableName(){
		return tableName;
	}
	
	/**
	 * Returns the name of the schema that the table is stored in.
	 * @return String
	 */
	public String getSchemaLocation(){
		return schemaName;
	}
	
	/**
	 * Returns the number of coluns in the table.
	 * @return int
	 */
	public int getColumnCount(){
		return columnCount;
	}
	
	/**
	 * Returns the number of rows in the table.
	 * @return int
	 */
	public int getRowCount(){
		return rowCount;
	}
	/**
	 * Returns an array list of the table's description.
	 * @return array list
	 */
	public ArrayList<String[]> getTableDescribe(){
		return tableDescribe;
	}
	
	/**
	 * Returns all the data in the table.
	 * @return array list
	 * @throws SQLException
	 */
	public ArrayList<String[]> getAllRecords() throws SQLException{
		String query = "SELECT * FROM " + schemaName + "." + tableName;
		return sqlCon.getFromSelectStatement(query);
	}
	
	/**
	 * This method selects the table data from the database using pagination.
	 * @param page
	 * @param numberOfRows
	 * @param queryString
	 * @param useFilter
	 * @param filtersUpdated
	 * @param filterConstraints
	 * @throws SQLException
	 */
	public void selectFromTablePagination(int page, int numberOfRows, String queryString, boolean useFilter, boolean filtersUpdated, boolean filterConstraints) throws SQLException{
		int offset = (page-1)*numberOfRows;
		String limit = " LIMIT " + offset + "," + numberOfRows;
		StringBuilder query = new StringBuilder();
		
		if(filtersUpdated && filterConstraints){
			query.append("SELECT COUNT(*) FROM " + schemaName + "." + tableName);
			query.append(queryString.split(tableName)[1]);
			filteredRowCount = sqlCon.getCount(query.toString());
		}else if(!filterConstraints)
			filteredRowCount = rowCount;
		
		query.setLength(0);
		query.append(queryString + limit);
		resultSet = sqlCon.selectFromTablePagination(query.toString());
		Console.appendMessage(query.toString() + "\n");
	}
	/**
	 * This method is used to group the data in a SQL SELECT statement.  
	 * The SELECT statement will appends the COUNT(*) column onto the end of the selected columns automatically.  
	 * The COUNT(*) column is hard coded as the alias Record Count 
	 * @param ColumnNames ArrayList of selected columns to group by.
	 * @return ArrayList of data from the SQL table.
	 * @throws SQLException Thrown if the SQL query fails.
	 */
	
	public ResultSet SelectGroupByColumn(ArrayList<String> ColumnNames) throws SQLException{
		StringBuilder query = new StringBuilder();//store the query to be sent.
		String workingColumns = "";//Columns selected by the user.
		ResultSet data;
		if(sqlCon.getSchemaName() != schemaName){
			sqlCon.setSchemaName(schemaName);
			sqlCon.sendQuery("USE " + schemaName);
			Console.appendMessage("Working schema changed to " + sqlCon.getSchemaName() + "...\n");
		}
		query.append("SELECT ");
		for (String string : ColumnNames)
			workingColumns += string + ", ";
		//The sub-string in the next append cuts off the dangling , before the ORDER BY.
		query.append( workingColumns + "COUNT(*) AS 'Record Count' FROM " + tableName + " GROUP BY "
			+ workingColumns.substring(0, workingColumns.length()-2) + " ORDER BY COUNT(*) DESC");
		data =  sqlCon.getFromSelectStatementAsResultSet(query.toString());
		Console.appendMessage(query.toString() + "\n");
		return data;
	}
	
	
	/**
	 * Adds new records to the table.
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public boolean addNewRecords(ArrayList<String[]> data) throws SQLException{
		StringBuilder queryString = new StringBuilder("INSERT INTO " + schemaName + "."+ tableName + " VALUES ");
		for(int r = 0; r < data.size(); r++){
			queryString.append("(");
			for(int c = 0; c < columnCount; c++){
				if(c == columnCount-1)
					queryString.append("?");
				else
					queryString.append("?,");
			}
			
			if(data.size() == 1 || r == data.size()-1)
				queryString.append(")");
			else
				queryString.append("),");
		}
		if(sqlCon.addNewRecords(data, columnCount,queryString.toString())){
			rowCount += data.size();
			updateSchemaTableMainWindow();
			return true;
		}else return false;
	}
	
	/**
	 * Updates the records in the table.
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public boolean updateRecords(LinkedHashMap<String, Column> data) throws SQLException{
		int row;
		String columnName;
		String value;
		for(Entry<String, Column> entry : data.entrySet()){
			row = entry.getValue().getRowNumber()+1;
			columnName = entry.getValue().getColumnName();
			value = entry.getValue().getDtaValue();
			resultSet.absolute(row);
			resultSet.updateString(columnName, value);
			resultSet.updateRow();
		}		return true;
	}
	
	/**
	 * Removes records from the table.
	 * @param bulkDeletedRecords
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteRecordFromTable(ArrayList<Integer> bulkDeletedRecords) throws SQLException{
		if(Arrays.asList(noDropList).contains(schemaName))
			throw new SQLException("You do not have permission to delete this record.");
		Collections.sort(bulkDeletedRecords);
		Collections.reverse(bulkDeletedRecords);
		for(int i : bulkDeletedRecords){
			resultSet.absolute(i+1);
			resultSet.deleteRow();
		}
			rowCount -= bulkDeletedRecords.size();
		updateSchemaTableMainWindow();
		return true;
	}
	
	/**
	 * Add listener to listen for table row count updates.
	 * @param listener Schema changed listener
	 */
	public void addTableListener(SchemaChangedListener listener){
		if(schemaListeners.contains(listener))
			return;
		schemaListeners.add(listener);
	}
	
	/**
	 * Remove the schema changed listener from the table.
	 * @param listener Schema changed listener
	 */
	public void removeTableListener(SchemaChangedListener listener){
		if(!schemaListeners.contains(listener))
			return;
		schemaListeners.remove(listener);
	}
	
	/**
	 * Returns true if the schema changed listener exists.
	 * @param listener Schema changed listener
	 * @return
	 */
	public boolean containsTableListener(SchemaChangedListener listener){
		if(schemaListeners.contains(listener))
			return true;
		else
			return false;
	}
	
	//Get the number of tables in the schema.
	private int getTableRowCount() throws SQLException{
		String queryString = "SELECT COUNT(*) FROM " +schemaName + "." + tableName;
		return sqlCon.getCount(queryString);
	}
	
	//calls the listener to update.
	private void updateSchemaTableMainWindow(){
		for(SchemaChangedListener listener : schemaListeners)
		listener.tableRowCountUpdated(tableName, schemaName, rowCount);
	}
	
	//Finds the number of columns in the table.
	private int findColumnCount() throws SQLException{
		String query = "SELECT COUNT(*) FROM information_schema.COLUMNS" + " WHERE table_schema ='" + schemaName + "'" + "AND table_name='" + tableName + "'";
		return sqlCon.getCount(query);
	}

}