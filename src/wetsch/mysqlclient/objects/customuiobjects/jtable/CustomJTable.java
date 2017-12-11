package wetsch.mysqlclient.objects.customuiobjects.jtable;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import wetsch.mysqlclient.objects.configuration.Settings;
import wetsch.mysqlclient.objects.customuiobjects.CustomJTableHeader;
import wetsch.mysqlclient.objects.enums.GlobalObjects;
import wetsch.mysqlclient.objects.enums.JTableID;

public class CustomJTable extends JTable{
	private static final long serialVersionUID = -5943821983357684939L;
	private CustomJTableHeader header;
	private Settings settings;

	public CustomJTable(DefaultTableModel model, TableCellRenderer renderer, JTableID tableID) {
		super(model);
		settings = (Settings) GlobalObjects.settings.get();
		header = new CustomJTableHeader();
		setTableHeader(header);
		header.setColumnModel(getColumnModel());
		setDefaultRenderer(Object.class, renderer);

		setOpaque(false);
		setFillsViewportHeight(false);
		setBackground(new Color(0,0,0,0));
		setSelectionBackground(settings.getTablSelectedRowColor());
		setForeground(settings.getLabelColor());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowGrid(settings.isTableGrids(JTableID.valueOf(tableID.name())));
		
		getColumnModel().setColumnMargin(0);
		setRowMargin(0);
		setRowHeight(40);

	}

}
