package wetsch.mysqlclient.guilayout.newtablewindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import wetsch.mysqlclient.objects.customuiobjects.adapters.DocumentAdapter;
import wetsch.mysqlclient.objects.database.Column;
import wetsch.mysqlclient.objects.database.Database;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.DataTypeAttribiutes;

public class NewTableWindow extends NewTableWindowLayout implements ActionListener, ItemListener{

	private static final long serialVersionUID = 1L;
	private String[] invColumnNameValues = new String[]{"null", "NULL", ""};
	private ArrayList<Column> columns = new ArrayList<Column>();
	private QueryGenerator threadQueryBuild = null;
	private Thread cbLoaderThread = null;
	private Database schema;
	

	public NewTableWindow(Database schema) {
		super(schema);
		this.schema = schema;
		setupActionListeners();
	}
	
	//This method sets up the action listeners.
	private void setupActionListeners(){
		btnAttributeAppend.addActionListener(this);
		btnCreateTable.addActionListener(this);
		btnResetTable.addActionListener(this);
		btnAddColumn.addActionListener(this);
		btnRemoveColumn.addActionListener(this);
		
		jcbEngineTypes.addItemListener(this);
		jcbAttributeCategory.addItemListener(this);
		jcbTables.addItemListener(this);
		jtfTableName.getDocument().addDocumentListener(new MyDocumentListener());
		jtfAttributes.getDocument().addDocumentListener(new MyDocumentListener());
		
		tblNewColumns.addMouseListener(new MyMouseListener());

		tblNewColumns.getModel().addTableModelListener(new MyTableEditorListener());

		chkbPrimaryKey.addActionListener(this);
		chkbForeign.addActionListener(this);
	}
	
	/*
	 * This method uses takes in a combo box and a data String array.
	 * The method uses a thread to load the data into the combo box.
	 */
	private void LoadComboBoxData(JComboBox<String> comboBox, String[] data){
		cbLoaderThread = new Thread(new Runnable() {
			public void run() {
				try{
					if(data == null){
						DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
						model.removeAllElements();
					}else
						comboBox.setModel(new DefaultComboBoxModel<String>(data));
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		cbLoaderThread.start();
	}
	
	/*
	 * This method is responsible for starting the query generation thread.
	 * Once the tread is started, it is signaled to regenerate the query string
	 * when called.
	 */
	private void updateQuery(){
		try {
			if(threadQueryBuild == null){
				threadQueryBuild = new QueryGenerator();
				threadQueryBuild.start();
			}
			threadQueryBuild.updateQueryString();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	
	//listener method for the add column button.
	private void AddNewColumnButtonListener(){
		
		DefaultTableModel m = (DefaultTableModel) tblNewColumns.getModel();
		for(int c = 0; c < 2; c++){
			if(m.getValueAt(0, c).toString().equals("NULL")){
				return;
			}
		}
		m.addRow(new Object[]{"NULL", "NULL", false});
		tblNewColumns.setRowSelectionInterval(tblNewColumns.getRowCount()-1, tblNewColumns.getRowCount()-1);
		jtfAttributes.setText("");
		chkbPrimaryKey.setSelected(false);
		chkbForeign.setSelected(false);
	}
	
	//Lister method for the remove column button.
	private void removeColumnButtonListener(){
		String key = null;
		try{
		int selectedRow = tblNewColumns.getSelectedRow();
		if(selectedRow == -1)
			return;
		DefaultTableModel model = (DefaultTableModel) tblNewColumns.getModel();
		if(!model.getValueAt(selectedRow, 0).toString().equals("NULL"))
			key = tblNewColumns.getValueAt(selectedRow, 0).toString();
		model.removeRow(selectedRow);
		if(key != null ||columns.contains(key)){
			columns.remove(key);
			jtfAttributes.setText("");
			chkbPrimaryKey.setSelected(false);
			chkbForeign.setSelected(false);
			jcbTables.setSelectedIndex(-1);
			updateQuery();
			
		}
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	//Listener method for the append attributes button.
	private void appendAttributeListener(){
		String attribute = jcbAttributes.getSelectedItem().toString();
		if(jtfAttributes.getText().contains(attribute))
			JOptionPane.showMessageDialog(this, "Attribute already added.");
		else{
			String value = jcbAttributes.getSelectedItem().toString();
			if(jtfAttributes.getText().length() == 0)
				jtfAttributes.setText(value);
			else{
				jtfAttributes.setText(jtfAttributes.getText() + " " + value);
			}
		}
	}
	/*
	 * This method is the listener method for when the selected item is changed
	 * on the tables combo box.  It is responsible for loading the column names
	 * into the columns combo box of the selected table. 
	 * This method takes in a string representing the table name from which it
	 * should load the columns. 
	 */
	private void tablesComboBoxItemListener(String tableName){
		try{
			if(jcbTables.getSelectedIndex() == -1){
				LoadComboBoxData(jcbColumns, null);
				return;
			}
			Tables table = schema.getSchemaTables().get(tableName);
			if(table.getTableDescribe() == null)
				table.populseTableDescribtion();
			LoadComboBoxData(jcbColumns, schema.getSchemaTables().get(tableName).getColumnNamesArray());
		}catch(SQLException e1){
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}
	}
	//Build the table inside the schema.
	private boolean buildTableInSchema(){
		try {
			if(threadQueryBuild == null)
				return false;
			updateQuery();
			schema.createNewTable(jtfTableName.getText().toString(), jtaQueryAera.getText().toString());
			return true;
		} catch (SQLException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}
		return false;
	}
	
	//Listener method for the primary key check box.
	private void chkbPrimaryKeyAction(){
		int selectedRow = tblNewColumns.getSelectedRow();
		if(selectedRow == -1){
			JOptionPane.showMessageDialog(this, "YOu must have a selected column");
			return;
		}
		String columnName = tblNewColumns.getValueAt(selectedRow, 0).toString();
		if(Arrays.asList(invColumnNameValues).contains(columnName)){
			JOptionPane.showMessageDialog(this, "The column name of the selected column can not be empty or null");
			return;
		}
		if(chkbPrimaryKey.isSelected()){
			columns.get(selectedRow).setPrimaryKey(true);
		}else{
			columns.get(selectedRow).setPrimaryKey(false);
		}
		updateQuery();
	}
	
	//Listener method for the foreign key check box.
	private void chkbForeignKeyAction(){
		if(jcbTables.getSelectedIndex() == -1){
			JOptionPane.showMessageDialog(this, "you must select a table and column first.");
			chkbForeign.setSelected(false);
			return;
		}
		int selectedRow = tblNewColumns.getSelectedRow();
			if(chkbForeign.isSelected()){
				if(columns.contains(selectedRow)){
					columns.get(selectedRow).setForeignKey(jcbTables.getSelectedItem().toString(), jcbColumns.getSelectedItem().toString());
				}
			}else{
				if(columns.contains(selectedRow)){
					columns.get(selectedRow).setForeignKey(null, null);
				}
			}
			updateQuery();
	}
	
	//Listener method for the create table button.
	private void createTableListener(){
		if(buildTableInSchema()){
			try {
				threadQueryBuild.shutdownThread();
				threadQueryBuild.join();
				JOptionPane.showMessageDialog(this, "Table created successfully");
				frame.dispose();
			} catch (InterruptedException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
	
	private void resetTableListener(){
		try{
			columns.clear();
			jtfTableName.setText("");
			jtfAttributes.setText("");
			chkbPrimaryKey.setSelected(false);
			chkbForeign.setSelected(false);
			jcbTables.setSelectedIndex(-1);
			jcbColumns.setSelectedIndex(-1);
			jcbEngineTypes.setSelectedIndex(0);
			jcbAttributeCategory.setSelectedIndex(0);
			jcbAttributes.setSelectedIndex(0);
			DefaultTableModel model = (DefaultTableModel) tblNewColumns.getModel();
			while(tblNewColumns.getRowCount() > 0)
				model.removeRow(0);
			model.addRow(new Object[]{"NULL", "NULL", false});
			
			jtaQueryAera.setText("");
			if(threadQueryBuild.isAlive())
				threadQueryBuild.shutdownThread();
			threadQueryBuild = null;

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//listeners
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnAttributeAppend){
			appendAttributeListener();
		}else if(e.getSource() == btnAddColumn){
			AddNewColumnButtonListener();
		
		}else if(e.getSource() == btnRemoveColumn){
			removeColumnButtonListener();
		}else if(e.getSource() == btnCreateTable){
			createTableListener();
		}else if(e.getSource() == btnResetTable){
			resetTableListener();
		}else if(e.getSource() == chkbPrimaryKey){
			chkbPrimaryKeyAction();
		}else if(e.getSource() == chkbForeign){
			chkbForeignKeyAction();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == jcbAttributeCategory && e.getStateChange() == ItemEvent.SELECTED){
			LoadComboBoxData(jcbAttributes, DataTypeAttribiutes.valueOf(e.getItem().toString()).getAttributes());
		}else if(e.getSource() == jcbEngineTypes){
			updateQuery();
		}else if(e.getSource() == jcbTables){
			tablesComboBoxItemListener(e.getItem().toString());
		}
	}
//Used for the mouse on the columns table.	
private class MyMouseListener extends MouseAdapter{
	@Override
	public void mouseReleased(MouseEvent e) {
		int selectedRow = tblNewColumns.getSelectedRow();
		if(selectedRow > columns.size())
			return;
		Column column = columns.get(selectedRow);
		jtfAttributes.setText(column.getAttributes());
		chkbPrimaryKey.setSelected(column.isPrimaryKey());
		if(column.getForeignKey() != null){
			chkbForeign.setSelected(true);
			String[] foreignKey = column.getForeignKey().split("\\(|\\)");
			jcbTables.setSelectedItem(foreignKey[0]);
			try {
				cbLoaderThread.join();
				jcbColumns.setSelectedItem(foreignKey[1]);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, e1.getMessage());
			}
		}else{
			chkbForeign.setSelected(false);
			jcbTables.setSelectedIndex(-1);
		}
		super.mouseReleased(e);
	}
}
	

private class MyTableEditorListener implements TableModelListener{

	@Override
	public void tableChanged(TableModelEvent e) {
		try{
			if(columns.size() < tblNewColumns.getRowCount())
				columns.add(new Column(null, null, null, false, null));
			switch (e.getColumn()) {
			case 0:
				String value = tblNewColumns.getValueAt(e.getFirstRow(), e.getColumn()).toString();
				if(Arrays.asList(invColumnNameValues).contains(value)){
					JOptionPane.showMessageDialog(null, "Column Name can not be null or empty.");
					tblNewColumns.editCellAt(e.getFirstRow(), e.getColumn());
					tblNewColumns.transferFocus();
					return;
				}
				columns.get(e.getFirstRow()).setColumnName(value);
				break;
			case 1:
				String dataType = tblNewColumns.getValueAt(e.getFirstRow(), e.getColumn()).toString();
				if(dataType.equals("")){
					JOptionPane.showMessageDialog(null, "Data type must be set");
					return;
				}
				columns.get(e.getFirstRow()).setDataType(dataType);
				break;
			case 2:
				boolean isNull = (boolean) tblNewColumns.getValueAt(e.getFirstRow(), e.getColumn());
				columns.get(e.getFirstRow()).setNull(isNull);
				break;
			}
			tblNewColumns.putClientProperty("terminateEditOnFocusLost", true);
			updateQuery();
		}catch(Exception e1){
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
		
	}
}
/*
 * This class is used to update the attributes text field.
 * 
 */
private class MyDocumentListener extends DocumentAdapter{
	/*
	 * This method is called by the document listener.
	 * It clears the attributes text field when called
	 * by the document listener.
	 */
	private void clearAttributesFromDocumentListener(){
				SwingUtilities.invokeLater(new Runnable(){
				    public void run(){
				        jtfAttributes.setText("");
				    }
				});
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if(e.getDocument() == jtfAttributes.getDocument()){
			int selectedRow = tblNewColumns.getSelectedRow();
			if(columns.isEmpty() || tblNewColumns.getRowCount() > columns.size()){
				JOptionPane.showMessageDialog(null, "You must first set a column name and data type first");
				clearAttributesFromDocumentListener();
				return;
			}
			if(jtfAttributes.getText().length() > 0)
				columns.get(selectedRow).setAttributes(jtfAttributes.getText().toString());
		}
		updateQuery();
		super.insertUpdate(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if(e.getDocument() == jtfAttributes.getDocument()){
			int selectedRow = tblNewColumns.getSelectedRow();
			if(columns.isEmpty() || !columns.contains(selectedRow)){
				clearAttributesFromDocumentListener();
				return;
			}
			columns.get(selectedRow).setAttributes(jtfAttributes.getText().toString());
		}
		updateQuery();
		super.removeUpdate(e);
	}
}
	

/**
 * This class runs on it's own thread.  This class is used to update the 
 * create table query string. when changes are made to any of the components, 
 * The components call the query update method and sets the update flag.  The 
 * thread then sees that there is an update and regenerates the query string.  
 * <br><br><b>Note:</b> This thread checks fore updates every second.  If there are no 
 * updates, the thread sleeps fore one second. 
 * @author kevin
 *
 */
private class QueryGenerator extends Thread implements Runnable{
	private boolean running = false;//Keep the thread alive.
	private boolean update = false;//Signals the thread to regenerate a new query string.
	private boolean firstRow = false;//Keep track of the first object in the loop.
	private ArrayList<String> primaryKeys = new ArrayList<String>();

	private final StringBuilder queryString = new StringBuilder();//Query string to be generated.
	private StringBuilder primaryKeyString = new StringBuilder(",PRIMARY KEY(");//Primary key section.
	private StringBuilder foreignKeyString = new StringBuilder();//Foreign key section.
	
	/**
	 * Signals the thread to finish and shutdown. 
	 */
	public void shutdownThread(){
		this.running = false;
	}
	
	/**
	 * Signals the thread to update the query string.

	 */
	public void updateQueryString(){
		this.update = true;
	}
	
	//Clear children.
	private void reset(){
		primaryKeys.clear();
		primaryKeyString.setLength(0);
		primaryKeyString.append(",PRIMARY KEY(");
		foreignKeyString.setLength(0);
		queryString.setLength(0);
		update = false;
	}

	@Override
	public void run() {
		try{
			running = true;
			do{
				if(!update){
					sleep(1000);
				}else{
					queryString.append("CREATE TABLE " +schema.getSchemaName() + "." + jtfTableName.getText() + "(");
					firstRow = true;
					for(Column v : columns){
						if(firstRow){
							queryString.append(v.getColumnName() + " ");
							firstRow = false;
						}else
							queryString.append(", " + v.getColumnName() + " ");
						queryString.append(v.getDataType() + " ");
						if(v.getAttributes() != null)
							queryString.append(v.getAttributes() + " ");
						
						if(v.isNull())
							queryString.append("NULL");
						else
							queryString.append("NOT NULL");
						if(v.isPrimaryKey())
							primaryKeys.add(v.getColumnName());
						if(v.getForeignKey() != null){
							foreignKeyString.append(",FOREIGN KEY(" + v.getColumnName() + ")" + " REFERENCES " + v.getForeignKey());
						}
					}
					if(!primaryKeys.isEmpty()){
						firstRow = true;
						for(String s : primaryKeys){
							if(firstRow){
								primaryKeyString.append(s);
								firstRow = false;
							}
							else
								primaryKeyString.append("," + s);
						}
						primaryKeyString.append(")");
						queryString.append(primaryKeyString.toString());
					}
					queryString.append(foreignKeyString.toString());
					queryString.append(") ENGINE=" + jcbEngineTypes.getSelectedItem().toString());
				
					jtaQueryAera.setText(queryString.toString());
					reset();
				}
			}while(running);
			}catch(Exception e){
				e.printStackTrace();
			}
			super.run();
		}
	}

}
