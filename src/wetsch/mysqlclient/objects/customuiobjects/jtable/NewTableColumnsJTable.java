package wetsch.mysqlclient.objects.customuiobjects.jtable;


import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import wetsch.mysqlclient.objects.enums.JTableID;

public class NewTableColumnsJTable extends CustomJTable {
	private static final long serialVersionUID = -5943821983357684939L;

	public NewTableColumnsJTable(DefaultTableModel model, TableCellRenderer renderer, JTableID ID) {
		super(model, renderer, ID);
		setRowHeight(20);
		
	}
	

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		if(column == 2)
			return Boolean.class;
		return super.getColumnClass(column);
	}

	


}
