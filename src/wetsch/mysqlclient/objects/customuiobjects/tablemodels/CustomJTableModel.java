package wetsch.mysqlclient.objects.customuiobjects.tablemodels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;import javax.print.attribute.ResolutionSyntax;
import javax.swing.table.DefaultTableModel;

import com.mysql.fabric.xmlrpc.base.Array;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

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
	 * Populates the table model wieth row data that is stored in the passed in array list.
	 * @param rowData Table model row data.
	 */
	public void loadModelData(ArrayList<String[]> rowData){
		getDataVector().clear();
		for(String[] r : rowData)
			addRow(r);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
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