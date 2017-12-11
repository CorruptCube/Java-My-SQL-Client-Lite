package wetsch.mysqlclient.objects.customuiobjects.tablemodels;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class NewColumnsJTableModel extends DefaultTableModel{

	private static final long serialVersionUID = 1L;

	public NewColumnsJTableModel(String[] ColumnNames, int rowCount){
		super(ColumnNames, rowCount);
	}
	
	public NewColumnsJTableModel(ArrayList<String[]> data, String[] columnNames) {
		super(columnNames , data.size());
		loadModelData(data);
	}
	
	
	/**
	 * Populates the table model wieth row data that is stored in the passed in array list.
	 * @param rowData Table model row data.
	 */
	public void loadModelData(ArrayList<String[]> rowData){
		getDataVector().clear();
		for(String[] r : rowData)
			addRow(r);
	}

	

}
