package wetsch.mysqlclient.objects.customuiobjects.renderor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class schemaTablesTableCellRendor extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	ImageIcon icon = new ImageIcon(getClass().getResource("/icons/table.png"));// prepared
																				// before
	/*
	 * When using value.tostring() when setting the text for the label, This caused a null pointer exception
	 * when using the Mac OS magnifier.  This exception would be thrown when selecting a different row the the JTable.
	 * To try to correct this problem, the code has been changed to (String) value when setting the text for the label.
	 * In several trials this appears to have fixed the problem.
	 *
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setOpaque(true);

		if (column == 0) {
			setIcon(icon);
			setHorizontalAlignment(SwingConstants.LEFT);

		} else {
			setIcon(null);
			setHorizontalAlignment(SwingConstants.CENTER);
		}
		setText((String) value);

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(Color.black);
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());

		}

		return this;
	}

}
