package wetsch.mysqlclient.objects.enums;


public enum JTableID {
	Schemas("Schema"),
	SchemaTables("Schema Tables"),
	SchemaTableDiscription("Schema Table Description"),
	DataTable("Table Data"),
	NewTableColumns("New Table Columns");
	
	private final String tableName;
	private JTableID(String tableName){
		this.tableName = tableName;
	}
	/**
	 * Returns the JTable name associated with this ID. 
	 * @return String
	 */
	public String getTableName() {
		return tableName;
	}
}
