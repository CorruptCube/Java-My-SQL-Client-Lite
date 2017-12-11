package wetsch.mysqlclient.objects.customuiobjects.editor;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class JComboboxCellEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

	private static final long serialVersionUID = 1L;

	   private JComboBox<String> jcb;
	   private String[] items;
	 
	   public JComboboxCellEditor(JComboBox<String> combobox){
		   items = new String[combobox.getModel().getSize()];
		for(int i = 0; i < combobox.getModel().getSize(); i++)
			items[i] = combobox.getItemAt(i).toString();
	   }
	 
	   @Override
	   public Object getCellEditorValue() {
	      return jcb.getEditor().getItem();
	   }
	   
	   


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		jcb = new JComboBox<String>(items);
		jcb.addItemListener(this);
		jcb.setEditable(true);
		jcb.setSelectedItem(value);

	      return jcb;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			stopCellEditing();
		}

	}
}
