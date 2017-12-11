package wetsch.mysqlclient.guilayout.mainwindowpane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import wetsch.mysqlclient.guilayout.newtablewindow.NewTableWindow;
import wetsch.mysqlclient.guilayout.settingswindow.SettingsFrame;
import wetsch.mysqlclient.guilayout.tabledata.TableDataFrame;
import wetsch.mysqlclient.objects.customuiobjects.Console;
import wetsch.mysqlclient.objects.customuiobjects.events.SettingsEvent;
import wetsch.mysqlclient.objects.customuiobjects.interfaces.SchemaChangedListener;
import wetsch.mysqlclient.objects.customuiobjects.interfaces.SettingsListener;
import wetsch.mysqlclient.objects.customuiobjects.tablemodels.CustomJTableModel;
import wetsch.mysqlclient.objects.database.Database;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.JTableID;

public class MainFrame extends MainWindowFrameLayout implements ActionListener, SettingsListener{
	
	private static final long serialVersionUID = 1L;
	
	private MySchemaChangedListener schemaChangeListener = new MySchemaChangedListener();

	public MainFrame(String serverAddress, String loginSchema,
			LinkedHashMap<String, Database> schemaMap) {
		super(serverAddress);
		this.schemaMap = schemaMap;
		this.loginSchema = loginSchema;
		this.loginSchema = loginSchema;
		this.tblSchemas.setModel(createSchemasModel());
		this.tblSchemaTable.setModel(createSchemaTableModel(schemaMap.get(loginSchema)));
		//Setting the selected schema and getting table lists.
		int loginSchemaIndex = new ArrayList<String>(schemaMap.keySet()).indexOf(loginSchema);
		tblSchemas.changeSelection(loginSchemaIndex, 0, false, false);
		if(schemaMap.get(loginSchema).getTableCount() > 0)
			updateSchemaTablesJTable();
		else
			this.tblTableDescribe.setModel(createSchemaTableDescribtionModel(schemaMap.get(loginSchema)));
		setupActionListeners();
		}
	
	protected CustomJTableModel createSchemasModel(){
		int row = 0;
		String[] columnNames = new String[]{"Schemas", "Table"};
		String[][] data = new String[schemaMap.size()][2];
		for(Map.Entry<String, Database> value : schemaMap.entrySet()){
			data[row][0] = value.getKey();
			data[row][1] = Integer.toString(value.getValue().getTableCount());
			row++;
		}
		return new CustomJTableModel(data, columnNames);
	}
	
	protected CustomJTableModel createSchemaTableModel(Database db){
		int row = 0;
		int tableCount = db.getTableCount();
		String[] columnNames = new String[]{"Table Name", "Columns", "Rows" };
		String[][] data = null;
		if(db.getSchemaTables() == null)
			try {
				if(!db.populateTablesHmap())
					return new CustomJTableModel(columnNames, 0);
			} catch (java.sql.SQLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		data = new String[tableCount][3];
		for(Map.Entry<String, Tables> value : db.getSchemaTables().entrySet()){
			data[row][0] = value.getKey().toString();
			data[row][1] = Integer.toString(value.getValue().getColumnCount());
			data[row][2] = Integer.toString(value.getValue().getRowCount());
			row++;
		}
		return new CustomJTableModel(data, columnNames);

	}
	
	protected CustomJTableModel createSchemaTableDescribtionModel(Database db){
		String[] columnNames = new String[]{"Field", "Type", "Null", "Key", "Default", "Extra"};
		int selectedRow = tblSchemaTable.getSelectedRow();
		if(selectedRow < 0){
			return new CustomJTableModel(columnNames, 0);
		}
		String selectedTable = tblSchemaTable.getModel().getValueAt(selectedRow, 0).toString();
		Tables table = db.getSchemaTables().get(selectedTable);
		if(table.getTableDescribe() == null){
			try {
				if(!table.populseTableDescribtion())
					return new CustomJTableModel(columnNames, 0);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		ArrayList<String[]> data = db.getSchemaTables().get(selectedTable).getTableDescribe();
		return new CustomJTableModel(data, columnNames);
	}
	
	private void setupActionListeners(){
		tblSchemas.addMouseListener(new MyMouseAdapter());
		tblSchemaTable.addMouseListener(new MyMouseAdapter());
		tblTableDescribe.addMouseListener(new MyMouseAdapter());
		menuItemSettings.addActionListener(this);
		menuItemExit.addActionListener(this);
		menuItemAddSchema.addActionListener(this);
		menuItemDropSchema.addActionListener(this);
		menuItemRefreshSchemas.addActionListener(this);
		menuItemCreateTable.addActionListener(this);
		menuItemViewEditTable.addActionListener(this);
		menuItemDropTable.addActionListener(this);

	}
	//Updates the JTable that holds the list of tables in the selected schema.
	private void updateSchemaTablesJTable(){
		String schema = tblSchemas.getValueAt(tblSchemas.getSelectedRow(), 0).toString();
		tblSchemaTable.setModel(createSchemaTableModel(schemaMap.get(schema)));
		if(tblSchemaTable.getRowCount() == 0){
			if(tblTableDescribe.getRowCount() > 0){
				DefaultTableModel model = (DefaultTableModel) tblTableDescribe.getModel();
				model.getDataVector().clear();
				model.fireTableDataChanged();
			}
			return;

		}
		tblSchemaTable.changeSelection(0, 0, false, false);
		updateTableDescribeJTable();
	}
	
	public void updateTableDescribeJTable(){
		int selectedRow = tblSchemas.getSelectedRow();
		String schema = null;
		schema = tblSchemas.getValueAt(selectedRow, 0).toString();
		tblTableDescribe.setModel(createSchemaTableDescribtionModel(schemaMap.get(schema)));
	}
	
	private void openSettingsWindow(){
		new SettingsFrame(true, this);
		
	}

	private void addNewSchema(){
		String newSchemaName = JOptionPane.showInputDialog("Enter the name of your new schema.");
		try {
			if(newSchemaName.isEmpty())
				JOptionPane.showMessageDialog(this, "No name was given for the new schema."
						+ " Operation canceled.");
			else if(schemaMap.get(loginSchema).createNewSchema(newSchemaName, schemaMap)){
				tblSchemas.setModel(createSchemasModel());
				JOptionPane.showMessageDialog(this, "The new schema was created successfuly.");
			}
		} catch (java.sql.SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	//Deletes the selected schema from the database.
	private void dropSchemaFromDatabase(){
		int selectedRow = -1;
		try{
			selectedRow = tblSchemas.getSelectedRow();
			String selectedSchema = tblSchemas.getModel().getValueAt(selectedRow, 0).toString();
			int action = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete this schema:" + selectedSchema,
					"Delete Schema", JOptionPane.YES_NO_OPTION);
				if(action == JOptionPane.YES_OPTION){
					if(schemaMap.get(selectedSchema).dropSchema()){
						schemaMap.remove(selectedSchema);
						tblSchemas.setModel(createSchemasModel());
						JOptionPane.showMessageDialog(this, "Sechema " + selectedSchema +" removed successfuly");
					}
				}

		}catch(IndexOutOfBoundsException | java.sql.SQLException ex){
			if(selectedRow < 0)
				JOptionPane.showMessageDialog(this, "You must select a schema first.");
			else
				JOptionPane.showMessageDialog(this, ex.getMessage());
		}
	}

	public LinkedHashMap<String, Database> refreshSchemas(){
		LinkedHashMap<String, Database> schemas = null;
		try {
			
			schemas = schemaMap.get(loginSchema).refreshSchemas();
			schemaMap = null;
		} catch (java.sql.SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
		return schemas;
	}
	
	private void createNewTableMenuItem(){
		int selectedRow = tblSchemas.getSelectedRow();
		String selectedSchema = tblSchemas.getValueAt(selectedRow, 0).toString();
		new NewTableWindow(schemaMap.get(selectedSchema));
		if(!schemaMap.get(selectedSchema).containsTableCreatedListener(schemaChangeListener))
				schemaMap.get(selectedSchema).addTableCreatedListener(schemaChangeListener);
	}
	
	private void viewEditTableMenuItem(){
		final int selectedTableRow;
		int selectedSchemaRow;
		final String selectedSchemaName;
		final String selectedTableName;
		Tables selectedTable;
		try {
			selectedSchemaRow = tblSchemas.getSelectedRow();
			selectedSchemaName = tblSchemas.getModel().getValueAt(selectedSchemaRow, 0).toString();
			selectedTableRow = tblSchemaTable.getSelectedRow();
			selectedTableName = tblSchemaTable.getModel().getValueAt(selectedTableRow, 0).toString();
			selectedTable = schemaMap.get(selectedSchemaName).getSchemaTables().get(selectedTableName);
			selectedTable.addTableListener(new MySchemaChangedListener());

			new TableDataFrame(selectedTableName, selectedTable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void dropTableFromSchema(){
		int selectedSchemaRow;
		int selectedTableRow;
		String selectedSchemaName;
		String selectedTableName;
		try{
			selectedSchemaRow = tblSchemas.getSelectedRow();
			selectedTableRow = tblSchemaTable.getSelectedRow();
			if(selectedSchemaRow < 0 || selectedTableRow < 0){
				JOptionPane.showMessageDialog(this, "You must select a schema and/or table first.");
				return;
			}
			int userConfirm =	JOptionPane.showConfirmDialog(this, "Are you sure you want to drop this Table?", "Drop Table Confirm", JOptionPane.YES_NO_OPTION);
			if(userConfirm == JOptionPane.YES_OPTION){
				selectedSchemaName = tblSchemas.getModel().getValueAt(selectedSchemaRow, 0).toString();
				selectedTableName = tblSchemaTable.getModel().getValueAt(selectedTableRow, 0).toString();
				if(!schemaMap.get(selectedSchemaName).containsTableCreatedListener(schemaChangeListener))
					schemaMap.get(selectedSchemaName).addTableCreatedListener(schemaChangeListener);
				if(schemaMap.get(selectedSchemaName).dropTable(selectedTableName)){
					JOptionPane.showMessageDialog(this, "Table " + selectedTableName + " dropped successfuly.");
				}
			}
		}catch(NumberFormatException | SQLException e){
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	//Disconnect from the data base.
	private void disconnectFromDatabase(){
		try {
			schemaMap.get(loginSchema).closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Listeners.
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuItemSettings){
			openSettingsWindow();
		}else if(e.getSource() == menuItemExit){
			disconnectFromDatabase();
			System.exit(0);
		}else if(e.getSource() == menuItemAddSchema){
			addNewSchema();
		}else if(e.getSource() == menuItemDropSchema ){
			dropSchemaFromDatabase();
		}else if(e.getSource() == menuItemRefreshSchemas){
			DefaultTableModel model = (DefaultTableModel)tblSchemaTable.getModel();
			model.getDataVector().clear();
			model.fireTableDataChanged();;
			model = (DefaultTableModel) tblTableDescribe.getModel();
			model.getDataVector().clear();
			model.fireTableDataChanged();
			schemaMap = refreshSchemas();
			tblSchemas.setModel(createSchemasModel());
		}else if(e.getSource() == menuItemCreateTable){
			createNewTableMenuItem();
		}else if(e.getSource() == menuItemViewEditTable){
			viewEditTableMenuItem();
		}else if(e.getSource() == menuItemDropTable){
			dropTableFromSchema();
		}
	}
	
	@Override
	public void SettingStateChanged(SettingsEvent e) {
		if(e.getComponentID() == JTableID.Schemas){
			switch (e.getSettingStateChanged()) {
			case SettingsEvent.tableGridStateChanged:
				tblSchemas.setShowGrid((boolean) e.getValue());
				break;
			}
		}else if(e.getComponentID() == JTableID.SchemaTables){
			switch (e.getSettingStateChanged()) {
			case SettingsEvent.tableGridStateChanged:
				tblSchemaTable.setShowGrid((boolean)e.getValue());
				break;
			}
		}else if(e.getComponentID() == JTableID.SchemaTableDiscription){
			switch (e.getSettingStateChanged()) {
			case SettingsEvent.tableGridStateChanged:
				tblTableDescribe.setShowGrid((boolean)e.getValue());
				break;

			}
		}else if(e.getSettingStateChanged() == SettingsEvent.labelColor){
			tblSchemas.setForeground((Color)e.getValue());
			tblSchemaTable.setForeground((Color)e.getValue());
			tblTableDescribe.setForeground((Color)e.getValue());

		}else if(e.getSettingStateChanged() == SettingsEvent.tableSelectedRowColor){
			tblSchemas.setSelectionBackground((Color)e.getValue());
			tblSchemaTable.setSelectionBackground((Color)e.getValue());
			tblTableDescribe.setSelectionBackground((Color)e.getValue());

		}
	}
	
	//This class listens for changes to the schemas.
private class MySchemaChangedListener implements SchemaChangedListener{
	@Override
	public void newTableAdded(String tableName, Database schema) {
		CustomJTableModel model = (CustomJTableModel) tblSchemaTable.getModel();
		int selectedSchemaIndex = tblSchemas.getSelectedRow();
		String columnCount = Integer.toString(schema.getSchemaTables().get(tableName).getColumnCount());
		String rowCount = Integer.toString(schema.getSchemaTables().get(tableName).getRowCount());
		String[] data = new String[]{tableName, columnCount, rowCount};
		model.insertRow(0, data);
		tblSchemas.setValueAt(schema.getTableCount(),selectedSchemaIndex , 1);
		Console.appendMessage("Table " + tableName + " added to " + schema.getSchemaName() + ".");
	}

	@Override
	public void tableRemoved(String tableName, Database schema) {
		int schemaIndex = tblSchemas.getSelectedRow();
		int schemaTableIndex = tblSchemaTable.getSelectedRow();
		CustomJTableModel model = (CustomJTableModel) tblSchemaTable.getModel();
		model.removeRow(schemaTableIndex);
		tblSchemas.setValueAt(schema.getTableCount(), schemaIndex, 1);
		Console.appendMessage("Table " + tableName + " removed from " +schema.getSchemaName() + ".");
	}

	@Override
	public void tableRowCountUpdated(String tableName, String schemaName, int rowCount) {
		int tableIndex = Arrays.asList(schemaMap.get(schemaName).getSchemaTables().keySet().toArray()).indexOf(tableName);
		int selectedSchemaIndex = tblSchemas.getSelectedRow();
		String selectedSchemaName = (String) tblSchemas.getValueAt(selectedSchemaIndex, 0);
		if(selectedSchemaName.equals(schemaName)){
			tblSchemaTable.setValueAt(rowCount, tableIndex, 2);
		}
	}
}
	//This class is used for the mouse listener on the JTables.
private class MyMouseAdapter extends MouseAdapter{
	public void mouseReleased(MouseEvent e) {
		try {
			
		if(e.getSource() == tblSchemaTable && e.getButton() == MouseEvent.BUTTON1  && e.getClickCount() == 2){
			viewEditTableMenuItem();
		}else if(e.getSource() == tblSchemaTable && e.getButton() == MouseEvent.BUTTON1){
			updateTableDescribeJTable();
		}else if(e.getSource() == tblSchemas && e.getButton() == MouseEvent.BUTTON1){
			updateSchemaTablesJTable();
		}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(MainFrame.this, e1.getMessage());
			e1.printStackTrace();
		}
	}
}

}