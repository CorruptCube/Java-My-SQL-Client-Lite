package wetsch.mysqlclient.objects.customuiobjects.renderor;

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
		setOpaque(false);
		setText(value.toString());
		setHorizontalAlignment(SwingConstants.CENTER);
		 setForeground(Color.WHITE);
	     return this;
	}
}
	
