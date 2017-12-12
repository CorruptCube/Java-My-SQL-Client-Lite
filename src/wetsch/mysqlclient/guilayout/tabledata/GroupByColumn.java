package wetsch.mysqlclient.guilayout.tabledata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;


import wetsch.mysqlclient.objects.database.Tables;

public class GroupByColumn extends GroupByColumnLayout implements ActionListener{

	private static final long serialVersionUID = 1L;

	public GroupByColumn(Tables table) {
		super(table);
		populatelstColumnsBox();
		setupActionListeners();

	}
	
	private void setupActionListeners(){
		btnAddColumns.addActionListener(this);
		btnRemoveColumns.addActionListener(this);
		btnQuery.addActionListener(this);
	}
	
	private void populatelstColumnsBox() {
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (String value : table.getColumnNamesArray())
			model.addElement(value);
		lstColumns.setModel(model);
		lstDisplayedColuns.setModel(new DefaultListModel<String>());
	}
	
	private void btnAddColumns(){
		if(lstDisplayedColuns == null){
			lstDisplayedColuns = new JList<String>();
		}
		DefaultListModel<String> model = (DefaultListModel<String>) lstDisplayedColuns.getModel();
		if(lstColumns.getSelectedValuesList().size() == 0){
			JOptionPane.showMessageDialog(this, "You do not have any columns selected.");
			return;
		}
		for (Object value : lstColumns.getSelectedValuesList()) {
			if (model.contains(value))
				continue;
			else
				model.addElement((String) value);
	}		}
	
	private void btnRemoveColumns(){
		DefaultListModel<String> model = (DefaultListModel<String>) lstDisplayedColuns.getModel();
		if(lstDisplayedColuns.getSelectedValuesList().size() == 0){
			JOptionPane.showMessageDialog(this, "You do not have any columns selected.");
			return;

		}
		for (Object value : lstDisplayedColuns.getSelectedValuesList())
			model.removeElement(value);
	}
	
	private void btnQuery(){
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()== btnAddColumns)
			btnAddColumns();
		else if(e.getSource() == btnRemoveColumns)
			btnRemoveColumns();
		else if(e.getSource() == btnQuery)
			btnQuery();
	}
	
	

}
