package wetsch.mysqlclient.objects.customuiobjects.renderor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class schemaTableCellRendor extends JLabel implements TableCellRenderer{
	private static final long serialVersionUID = 1L;
	ImageIcon icon = new ImageIcon(getClass().getResource("/icons/database.png"));// prepared before
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
			setOpaque(true); 
			
			if(column == 0){
				setIcon(icon);
				setHorizontalAlignment(SwingConstants.LEFT);
			}else{
				setIcon(null);
				setHorizontalAlignment(SwingConstants.CENTER);
			}
			
			setText(value.toString());
			setForeground(Color.white);
			
			
			
			if(isSelected){
				setBackground(table.getSelectionBackground());
				setForeground(Color.black);
			}else{
				setBackground(table.getBackground());
				setForeground(table.getForeground());

			}
	

		return this;
	}

	
}

