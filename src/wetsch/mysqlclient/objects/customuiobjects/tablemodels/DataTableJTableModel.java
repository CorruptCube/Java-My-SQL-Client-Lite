package wetsch.mysqlclient.objects.customuiobjects.tablemodels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class DataTableJTableModel extends CustomJTableModel {
	private static final long serialVersionUID = 1L;
	private boolean tableIsEditable = true;
	private boolean primaryKeyEditable = false;
	private ArrayList<Integer> priColumnNumbers;
	
	
	public DataTableJTableModel(ResultSet resultSet, boolean tableIsEditable,
			boolean primaryKeyEditable, ArrayList<Integer> priColumnNumbers) throws SQLException{
			super(resultSet);
			this.primaryKeyEditable = primaryKeyEditable;
			this.priColumnNumbers = priColumnNumbers;
			this.tableIsEditable = tableIsEditable;
	}

	
	/**
	 * This constructor sets the column names, column editable, and primary key columns.
	 * Set the number of rows to 0 if the table modle is going to be empty.
	 * @param columnNames Array of column names.
	 * @param numberOfRows number of rows that are in the model.
	 * @param tableIsEditable Set to true if cells are editable.
	 * @param primaryKeyEditable Set to true if primary key cells are editable.
	 * @param priColumnNumbers Array list of Integers that hold the column numbers for the primary key data.
	 */
	public DataTableJTableModel(String[] columnNames, int numberOfRows, boolean tableIsEditable,
			boolean primaryKeyEditable, ArrayList<Integer> priColumnNumbers){
			super(columnNames, numberOfRows);
			this.primaryKeyEditable = primaryKeyEditable;
			this.priColumnNumbers = priColumnNumbers;
			this.tableIsEditable = tableIsEditable;
	}
	
	/**
	 * This constructor sets the table data, column names, column editable, and primary key columns.
	 * @param data Array list of String[] that hold the row data for the table model.
	 * @param columnNames Array of column names.
	 * @param tableIsEditable Set to true if cells are editable.
	 * @param primaryKeyEditable Set to true if primary key cell is editable.
	 * @param priColumnNumbers Array list of Integers that hold the column numbers for the primary key data.
	 */
	public DataTableJTableModel(ArrayList<String[]> data, String[] columnNames, boolean tableIsEditable,
			boolean primaryKeyEditable, ArrayList<Integer> priColumnNumbers){
		super(data , columnNames);
		this.primaryKeyEditable = primaryKeyEditable;
		this.priColumnNumbers = priColumnNumbers;
		this.tableIsEditable = tableIsEditable;
		loadModelData(data);
	}
	
	
	
	/**
	 * Sets the Cells to be editable or uneditable.
	 * Set to true if cells are to be editable, otherwise, set to false.
	 * @param editable Sets cells editable/uneditable.
	 */
	public void setEditable(boolean editable){
		tableIsEditable = editable;
	}
	
	/**
	 * Sets the primary key cell data to editable/uneditable.
	 * While updating table data, it is not recommended to make 
	 * primary key cell data editable. This could result in broken 
	 * records in your database.  This value may be set to true when 
	 * adding noew records to the database.
	 * @param editable Sets primary key cell data to editable/uneditable.
	 */
	public void setPrimaryKeyEditable(boolean editable){
		primaryKeyEditable = editable;
	}
	

	/**
	 * Returs true if cell is editable, otherwise, returns false.
	 * @param row Row index of table model.
	 * @param column Column index of table model.
	 * @return true if cell is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		
		if(Arrays.asList(priColumnNumbers.toArray()).contains(column))
			return primaryKeyEditable;
		else
			return tableIsEditable;
	}
	/**
	 * Removes the column and data from the table model.
	 * @param column Column index
	 */
	public void removeColumn(int column){
        columnIdentifiers.remove(column);
        for (Object row: dataVector) {
            ((Vector<?>) row).remove(column);
        }
        fireTableStructureChanged();
	}
}
