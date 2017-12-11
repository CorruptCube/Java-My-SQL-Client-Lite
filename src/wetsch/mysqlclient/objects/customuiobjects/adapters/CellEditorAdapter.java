package wetsch.mysqlclient.objects.customuiobjects.adapters;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

public abstract class CellEditorAdapter implements CellEditorListener{

	@Override
	public void editingStopped(ChangeEvent e) {	}

	@Override
	public void editingCanceled(ChangeEvent e) {}

	
	
}
