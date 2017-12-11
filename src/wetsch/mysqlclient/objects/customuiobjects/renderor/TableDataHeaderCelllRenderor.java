package wetsch.mysqlclient.objects.customuiobjects.renderor;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import wetsch.mysqlclient.objects.database.Tables;


public class TableDataHeaderCelllRenderor extends JLabel implements TableCellRenderer{
	
	private static final long serialVersionUID = 1L;
	private String[] primaryKeys;
	private ImageIcon icon = new ImageIcon(getClass().getResource("/icons/key.png"));// prepared before

	public TableDataHeaderCelllRenderor(Tables table){
		int i = 0;
		primaryKeys = new String[table.getPrimaryKeyColumnNumbers().size()];
		for(int v : table.getPrimaryKeyColumnNumbers())
			primaryKeys[i++] = table.getColumnNamesArray()[v];
		
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		
		if(Arrays.asList(primaryKeys).contains(value.toString()))
			setIcon(icon);
		else 
			setIcon(null);
		setText(value.toString());
		setForeground(Color.white);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorder(BorderFactory.createLineBorder(Color.white));
		return this;
	}


	
}

