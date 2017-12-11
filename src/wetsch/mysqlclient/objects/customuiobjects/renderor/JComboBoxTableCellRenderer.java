package wetsch.mysqlclient.objects.customuiobjects.renderor;

/*
 * This class handels the rendering for the check boxes that apper when the table is in delete record state.
 * The value is passed as type object, and is then cast to a boolion value.
 */


import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class JComboBoxTableCellRenderer extends JComboBox<String> implements TableCellRenderer{

	private static final long serialVersionUID = 1L;

	public JComboBoxTableCellRenderer(JComboBox<String> comboBox){
		
		for(int i = 0; i < comboBox.getModel().getSize(); i++)
			addItem(comboBox.getItemAt(i).toString());
		setEditable(true);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setSelectedItem(value);
		return this;
	}

	
}

