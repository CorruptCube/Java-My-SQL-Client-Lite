package wetsch.mysqlclient.objects.customuiobjects;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;


public class GenericJTableHeaderCellRenderor extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setOpaque(true);
		setText(value.toString());
		setHorizontalAlignment(SwingConstants.CENTER);
		 setForeground(Color.black);
	     return this;
	}
}
	
