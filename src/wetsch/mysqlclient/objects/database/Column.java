package wetsch.mysqlclient.objects.database;

import java.util.ArrayList;

import wetsch.mysqlclient.objects.enums.ColumnKeyType;


public class Column {
	private boolean isNull = false;
	private boolean isPrimaryKey = false;
	private String dataType;
	private ColumnKeyType keyType = ColumnKeyType.none;
	private int rowNumber;
	private String columnName = null;
	private String dtaValue = null;
	private ArrayList<String> filteredValues = null;
	private String foreignKey = null;
	private String attributes = null;
	
	
	public Column(int rowNumber, String columnName, String dtaValue) {
		this.columnName = columnName;
		this.dtaValue = dtaValue;
		this.rowNumber = rowNumber;
	}
	
	public Column(String columnName, String dtaValue){
		this.columnName = columnName;
		this.dtaValue = dtaValue;
	}
	
	public Column(String columnName, String dataType, ColumnKeyType keyType, boolean isNull, String attributes){
		
	}
	
	public int getRowNumber(){
		return rowNumber;
	}
	
	public void setColumnName(String columnName){
		this.columnName = columnName;
	}
	
	public String getColumnName() {
		return columnName;
	}
	public String getDtaValue() {
		return dtaValue;
	}
	
	public void setDataType(String dataType){
		this.dataType = dataType;
	}
	
	public String getDataType(){
		return dataType;
	}
	
	public ColumnKeyType keyType(){
		return keyType;
	}
	
	public void setNull(boolean isNull){
		this.isNull = isNull;
	}
	
	public boolean isNull(){
		return isNull;
	}
	
	public void setAttributes(String attributes){
		if(attributes.length() == 0)
			this.attributes = null;
		else
			this.attributes = attributes;
	}
	
	public String getAttributes(){
			return attributes;
	}
	
	public ArrayList<String> getFilteredValues(){
		return filteredValues;
	}
	
	public boolean addFilterValues(String value) throws IllegalArgumentException{
		if(filteredValues == null)
			filteredValues = new ArrayList<String>();
		if(!filteredValues.contains(value)){
			filteredValues.add(value);
			return true;
		}else
			throw new IllegalArgumentException("Value could not be added or already exists.");
	}
	
	public boolean removeFilterValue(String value){
		if(filteredValues != null || filteredValues.size() != 0){
			filteredValues.remove(value);
			return true;
		}
		return false;
	}
	
	
	public void setDtaValue(String dtaValue) {
		this.dtaValue = dtaValue;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	
	public String getForeignKey(){
		return foreignKey;
	}
	
	public void setForeignKey(String table, String column){
		if(table == null || column == null)
			foreignKey = null;
		else
			foreignKey = table + "(" + column + ")";
	}
}
