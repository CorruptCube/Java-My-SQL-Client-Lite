package wetsch.mysqlclient.guilayout.tabledata.groupbydatatable;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;


import wetsch.mysqlclient.objects.customuiobjects.tablemodels.CustomJTableModel;
import wetsch.mysqlclient.objects.database.Tables;

public class GroupByColumn extends GroupByColumnLayout implements ActionListener{

	private static final long serialVersionUID = 1L;
	private MouseAdapter myMouseAdapter = new MyMouseAdapter();//Class for the mouse listener.

	public GroupByColumn(Tables table) {
		super(table);
		populatelstColumnsBox();
		setupActionListeners();

	}
	
	//Method called to setup action listeners for GUI.
	private void setupActionListeners(){
		btnAddColumns.addActionListener(this);
		btnRemoveColumns.addActionListener(this);
		btnQuery.addActionListener(this);
		
		tblData.addMouseListener(myMouseAdapter);
		//Pop up menu listeners.
		dtMenu.jmiCopy.addActionListener(this);
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
		ArrayList<String> selectedColumns = new ArrayList<>();
		ArrayList<String[]> data = null;
		for(int i = 0; i < lstDisplayedColuns.getModel().getSize(); i++)
			selectedColumns.add(lstDisplayedColuns.getModel().getElementAt(i));
		try {
			data = table.SelectGroupByColumn(selectedColumns);
			selectedColumns.add("Record Count");
			String[] c = new String[selectedColumns.size()];//Temporary Variable
			selectedColumns.toArray(c);
			tblData.setModel(new CustomJTableModel(data,c));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void jmiCopyToClipBoardHandler(){
		int selectedRow = tblData.getSelectedRow();
		int seleCtedColumn = tblData.getSelectedColumn();
		if(selectedRow == -1 || seleCtedColumn == -1){
			JOptionPane.showMessageDialog(this, "You must first select a row and column.");
			return;
		}
		String value = tblData.getValueAt(selectedRow, seleCtedColumn).toString();
		StringSelection stringSelection = new StringSelection(value);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()== btnAddColumns)
			btnAddColumns();
		else if(e.getSource() == btnRemoveColumns)
			btnRemoveColumns();
		else if(e.getSource() == btnQuery)
			btnQuery();
	
		//Popup menu actions.
		else if(e.getSource() == dtMenu.jmiCopy)
			jmiCopyToClipBoardHandler();
	
	}
	

	//This MouseAdapter class is used to grab actions from ouse clicks.
	private class MyMouseAdapter  extends MouseAdapter {
	
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == tblData && e.getButton() == MouseEvent.BUTTON3){
		        dtMenu.show(tblData, e.getX(), e.getY());
			}
		}
	};
	
	

}
