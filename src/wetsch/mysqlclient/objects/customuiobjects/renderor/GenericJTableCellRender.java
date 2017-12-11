package wetsch.mysqlclient.objects.customuiobjects.renderor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;


public class GenericJTableCellRender extends JLabel implements TableCellRenderer{
	private static final long serialVersionUID = 1L;
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
			setOpaque(true); 
			if(value == null)
				setText("NULL");
			else
				setText(value.toString());
			setHorizontalAlignment(SwingConstants.CENTER);
			if (table.isCellSelected(row, column)){
				if(table.isRowSelected(row)){
					setBackground(table.getSelectionBackground());
					setForeground(Color.black);
				}if(table.isColumnSelected(column))
					setForeground(Color.blue);
			}else{
				setBackground(table.getBackground());
				setForeground(table.getForeground());

			}
			
			
		return this;
	}
}