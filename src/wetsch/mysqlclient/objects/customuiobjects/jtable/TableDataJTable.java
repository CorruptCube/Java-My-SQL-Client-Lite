package wetsch.mysqlclient.objects.customuiobjects.jtable;



import javax.swing.table.DefaultTableModel;

import wetsch.mysqlclient.objects.customuiobjects.renderor.CustomJTableHeader;
import wetsch.mysqlclient.objects.customuiobjects.renderor.GenericJTableCellRender;
import wetsch.mysqlclient.objects.customuiobjects.renderor.TableDataHeaderCelllRenderor;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.JTableID;
public class TableDataJTable extends CustomJTable{
	private static final long serialVersionUID = 1L;
	private CustomJTableHeader header;
	public TableDataJTable(Tables table, DefaultTableModel model, JTableID ID){
		super(model, new GenericJTableCellRender(), ID);
		header = new CustomJTableHeader();
		header.setDefaultRenderer(new TableDataHeaderCelllRenderor(table));
		setTableHeader(header);
		header.setColumnModel(getColumnModel());
		
		}
	
	
}
