package wetsch.mysqlclient.objects.customuiobjects.tablemodels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;


public class CustomJTableModel extends DefaultTableModel{

	private static final long serialVersionUID = 1L;

	public CustomJTableModel(ResultSet resultSet) throws SQLException{
		super();
		populateFromResultSet(resultSet);
	}
	
	public CustomJTableModel(String[] ColumnNames, int rowCount){
		super(ColumnNames, rowCount);
	}
	
	public CustomJTableModel(ArrayList<String[]> data, String[] columnNames) {
		super(columnNames , data.size());
		loadModelData(data);
	}
	
	
	public CustomJTableModel(String[][] data, String[] columnNames) {
		super(data, columnNames);
	}
	
	/**
	 * This method populate the table model using a result set.
	 * @param resultSet
	 * @throws SQLException
	 */
	public void LoadModelData(ResultSet resultSet) throws SQLException{
		String[] value;
		getDataVector().clear();
		resultSet.beforeFirst();
		while(resultSet.next()){
			value = new String[resultSet.getMetaData().getColumnCount()];
			for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++)
				value[i] = resultSet.getString(i+1);
			addRow(value);

		}

	}

	/**
	 * Populates the table model with row data that is stored in the passed in array list.
	 * @param rowData Table model row data.
	 */
	public void loadModelData(ArrayList<String[]> rowData){
		getDataVector().clear();
		for(String[] r : rowData)
			addRow(r);
	}
	
	/**
	 * This method is hard coded to return false.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	/*
	 * This method take in a sql result set and populate the table model.
	 */
	private void populateFromResultSet(ResultSet resultSet) throws SQLException{
		String[] value;
		int columns;
		resultSet.beforeFirst();
		columns = resultSet.getMetaData().getColumnCount();
		for(int i = 0; i < columns; i++){
			addColumn(resultSet.getMetaData().getColumnName(i+1));
		}
		while(resultSet.next()){
			value = new String[columns];
			for(int i = 0; i < columns; i++){
				value[i] = resultSet.getString(i+1);
			}
			addRow(value);
		}
	}
}