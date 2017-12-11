package wetsch.mysqlclient.objects.customuiobjects.interfaces;

import wetsch.mysqlclient.objects.database.Database;

public interface SchemaChangedListener {

	/**
	 * This method is fired when a new table is successful created in the SQL database.
	 * @param tableName The name of the created table.
	 * @param schema The schema to which the table belongs.
	 */
	void newTableAdded(String tableName, Database schema);
	
	void tableRemoved(String tableName, Database schema);
	
	void tableRowCountUpdated(String tableName, String schemaName, int rowCount);
	
}
