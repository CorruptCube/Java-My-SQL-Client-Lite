package wetsch.mysqlclient.objects.customuiobjects.renderor;

/*
 * This class handels the rendering for the check boxes that apper when the table is in delete record state.
 * The value is passed as type object, and is then cast to a boolion value.
 */


import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;


public class CheckBoxcJTableCellRender extends JCheckBox implements TableCellRenderer{
	private static final long serialVersionUID = 1L;
	String[] checkBoxText;
	public CheckBoxcJTableCellRender(String checkBoxText){
		this.checkBoxText = null;
		setText(checkBoxText);
		setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public CheckBoxcJTableCellRender(String[] checkBoxText){
		this.checkBoxText = checkBoxText;
	}
	
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if(checkBoxText != null)
			setText(checkBoxText[row]);
		if(value == null)
			value = false;
		setSelected((boolean)value);

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

