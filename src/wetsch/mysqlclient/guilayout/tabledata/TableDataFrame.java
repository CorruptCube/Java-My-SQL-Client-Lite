package wetsch.mysqlclient.guilayout.tabledata;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;

import wetsch.mysqlclient.guilayout.tabledata.groupbydatatable.GroupByColumn;
import wetsch.mysqlclient.objects.CsvFileWritter;
import wetsch.mysqlclient.objects.customuiobjects.renderor.CheckBoxcJTableCellRender;
import wetsch.mysqlclient.objects.customuiobjects.tablemodels.DataTableJTableModel;
import wetsch.mysqlclient.objects.database.Column;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.DataTableState;

public class TableDataFrame extends TableDataFrameLayout implements
		ActionListener, ItemListener, CellEditorListener {

	private static final long serialVersionUID = 1L;
	private final String checkBoxColumnName = "Delete";// Holds the column name
	private ArrayList<Integer> pkColumnNumbers;
	private String[] tblCellNull = new String[] { "null", "NULL" };// Checks if
	private ArrayList<String[]> newRecords;// Holds the row data for new records
	private ArrayList<Integer> bulkDeletedRecords = null;// Holds the row index
	private LinkedHashMap<String, Column> updateRecords = null;// Holds the data
	private DataTableState tableState;// Holds the current table state.
	private MouseAdapter myMouseAdapter = new MyMouseAdapter();

	public TableDataFrame(String tableName, Tables table) {
		super(tableName, table);
		pkColumnNumbers = table.getPrimaryKeyColumnNumbers();
		checkForSystemSchemaTables();
		lblTableInfo.setText(populateTableInfoLable());
		setupListeners();
		populateCBRecordsPerPage();
		if (checkEmptyTable())
			return;
		populatePagesComboBox();
	}

	private boolean checkEmptyTable() {
		if (table.getRowCount() == 0) {
			lblRecordSet.setText("Table is empty.");
			tblData.setModel(dataTableModel(0, 0, false, false));
			return true;
		}
		return false;

	}

	/*
	 * Checks if the bable is from one of teh system schems for mysql. The
	 * method will hide any buttons that allow editing of the table.
	 */
	private void checkForSystemSchemaTables() {
		String[] ReadOnly = new String[] { "mysql", "information_schema",
				"performance_schema" };
		if (Arrays.asList(ReadOnly).contains(table.getSchemaLocation())) {
			btnNewRecord.setVisible(false);
			btnDropRecord.setVisible(false);
			btnUpdateRecords.setVisible(false);
		}
	}

	// Sets up the listeners for the buttons and JTable.
	private void setupListeners() {
		btnRefreshTable.addActionListener(this);
		btnBackPage.addActionListener(this);
		btnNextPage.addActionListener(this);
		btnNewRecord.addActionListener(this);
		btnDropRecord.addActionListener(this);
		btnUpdateRecords.addActionListener(this);
		btnCancelChanges.addActionListener(this);
		btnApplyChanges.addActionListener(this);
		btnTableFilters.addActionListener(this);
		cbPage.addItemListener(this);
		tblData.getDefaultEditor(String.class).addCellEditorListener(this);
		filters.addComponentListener(new myComponentListener());
		tblData.addMouseListener(myMouseAdapter);
		dtPopUpMenu.jmiCopy.addActionListener(this);
		dtPopUpMenu.jmiGroupColumns.addActionListener(this);
		dtPopUpMenu.jmiExportPageCSV.addActionListener(this);
		dtPopUpMenu.jmiExportTableCSV.addActionListener(this);
	}

	/*
	 * Sets up the label that holds the general information about the table. The
	 * info lable contains the table schema location and table name.
	 */
	private String populateTableInfoLable() {
		return "<html>" + "Table name: " + tableName + "<br>" + "Schema: "
				+ belongsToSchema + "</html>";
	}
	/*
	 * This method opens a file chooser to open/save a file.
	 * Returns the absalute file path. 
	 */
	private String getFileDialog(String dialogAction){
		JFileChooser fc = new JFileChooser();
		int action;
		switch (dialogAction) {
		case "Open":
			fc.setDialogTitle("Select file:");
			action = fc.showOpenDialog(this);
			if(action == JFileChooser.APPROVE_OPTION){
				return fc.getSelectedFile().getAbsolutePath().toString();
			}
			return null;
		case "Save":
			fc.setDialogTitle("Save Database To:");
			action = fc.showSaveDialog(this);
			if(action == JFileChooser.APPROVE_OPTION){
				return fc.getSelectedFile().getAbsolutePath().toString();
			}
			return null;
		}
		return null;
	}

	/*
	 * This method calculates the current record set based on the selected page.
	 * The record set shows the record range shown out of all records avalable.
	 */
	private void updateRecordSetInfo() {
		if (cbPage.getModel().getSize() == 0) {
			lblRecordSet.setText("Table is empty.");
			return;
		}
		int recordFirst;
		int recordLast;
		int tRecords;

		int recordsPerPage = (int) cbRecordsPerPage.getSelectedItem();
		if (filters.isFilterEnabled())
			tRecords = table.getFilteredRowCount();
		else
			tRecords = table.getRowCount();
		String recordSetInfo = null;
		int pageNumber = (int) cbPage.getSelectedItem();

		recordFirst = pageNumber * recordsPerPage - recordsPerPage + 1;
		recordLast = recordFirst + tblData.getRowCount() - 1;

		recordSetInfo = "record set " + recordFirst + "-" + recordLast + " of "
				+ tRecords;
		lblRecordSet.setText(recordSetInfo);
	}

	// Populates the combo-box with pre calculated number of records per page.
	private void populateCBRecordsPerPage() {
		DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();
		for (int i = 0; i < 100; i += 5)
			model.addElement(i + 5);
		cbRecordsPerPage.setModel(model);
		cbRecordsPerPage.setSelectedIndex(3);
	}

	// populates the combo box that holds the page numbers for pagination.
	private void populatePagesComboBox() {
		int numberOfPages;
		int recordsPerPage;
		int maxRecords;
		DefaultComboBoxModel<Integer> model;
		try {
			if (table.getRowCount() == 0)
				return;
			maxRecords = table.getRowCount();
			model = new DefaultComboBoxModel<Integer>();
			recordsPerPage = (int) cbRecordsPerPage.getSelectedItem();
			if (filters.isFilterEnabled()){
				if(table.getFilteredRowCount() == 0){
					cbPage.setModel(model);
					updateRecordSetInfo();
					JOptionPane.showMessageDialog(this, "No records were found");
					return;
				}
				numberOfPages = (int) Math.ceil((double) table
						.getFilteredRowCount() / recordsPerPage);
			}else
				numberOfPages = (int) Math.ceil((double) maxRecords
						/ recordsPerPage);

			cbPage.setModel(model);
			for (int i = 0; i < numberOfPages; i++)
				model.addElement(i + 1);
			cbPage.setSelectedIndex(0);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	// Setup the table model layout that is used for the data table.
	private DataTableJTableModel dataTableModel(int page, int numberOfRows,
			boolean primaryKeyEditable, boolean tableIsEditable) {
		boolean filterEnabled = filters.isFilterEnabled();
		String queryString = filters.getQueryString(table.getSchemaLocation(),
				table.getTableName());
		boolean filterConstraints = filters.isFilterConstraints();
		boolean filtersUpdated = filters.isFiltersUpdted();
		String[] columnNames;
		DataTableJTableModel model = null;
		try {
			if (filterEnabled)
				columnNames = filters.getDisplayedColumns();
			else
				columnNames = table.getColumnNamesArray();

			if (page == 0 && numberOfRows == 0)
				return model = new DataTableJTableModel(columnNames, 0,
						primaryKeyEditable, tableIsEditable, pkColumnNumbers);
			table.selectFromTablePagination(page, numberOfRows, queryString,
					filterEnabled, filtersUpdated, filterConstraints);
			if (table.getTableData().size() == 0)
				return model = new DataTableJTableModel(columnNames, 0,
						primaryKeyEditable, tableIsEditable,
						table.getPrimaryKeyColumnNumbers());
			model = new DataTableJTableModel(table.getResultSet(),
					primaryKeyEditable, tableIsEditable, pkColumnNumbers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}


	/*
	 * This method sets the tables available states. The sate passed in enables
	 * or disables controls, and makes the table editable or read only.
	 */
	private void setTableState(DataTableState state) {
		DataTableJTableModel model = (DataTableJTableModel) tblData.getModel();
		switch (state) {
		case editAddNewRecord:
			cbRecordsPerPage.setVisible(false);
			btnRefreshTable.setVisible(false);
			btnBackPage.setVisible(false);
			btnNextPage.setVisible(false);
			cbPage.setVisible(false);
			btnDropRecord.setVisible(false);
			btnUpdateRecords.setVisible(false);
			btnTableFilters.setVisible(false);
			btnCancelChanges.setVisible(true);
			btnApplyChanges.setVisible(true);
			model.setEditable(true);
			model.setPrimaryKeyEditable(true);
			tblData.getTableHeader().setReorderingAllowed(false);
			break;
		case editUpdateRecord:
			btnRefreshTable.setVisible(false);
			btnBackPage.setVisible(false);
			btnNextPage.setVisible(false);
			btnNewRecord.setVisible(false);
			cbPage.setVisible(false);
			btnDropRecord.setVisible(false);
			btnUpdateRecords.setVisible(false);
			btnTableFilters.setVisible(false);
			btnCancelChanges.setVisible(true);
			btnApplyChanges.setVisible(true);
			model.setEditable(true);
			model.setPrimaryKeyEditable(false);
			break;
		case editDelete:
			btnRefreshTable.setVisible(false);
			btnBackPage.setVisible(false);
			btnNextPage.setVisible(false);
			cbPage.setVisible(false);
			btnNewRecord.setVisible(false);
			btnDropRecord.setVisible(false);
			btnUpdateRecords.setVisible(false);
			btnDropRecord.setVisible(false);
			btnTableFilters.setVisible(false);
			btnCancelChanges.setVisible(true);
			btnApplyChanges.setVisible(true);
			break;
		case viewing:
			cbRecordsPerPage.setVisible(true);
			btnRefreshTable.setVisible(true);
			btnBackPage.setVisible(true);
			btnNextPage.setVisible(true);
			cbPage.setVisible(true);
			btnNewRecord.setVisible(true);
			btnDropRecord.setVisible(true);
			btnUpdateRecords.setVisible(true);
			btnTableFilters.setVisible(true);
			btnCancelChanges.setVisible(false);
			btnApplyChanges.setVisible(false);
			model.setEditable(false);
			model.setPrimaryKeyEditable(false);
			tblData.getTableHeader().setReorderingAllowed(true);
			
			break;
		}
		tableState = state;
	}
	
	//Handlers 
	
	//Data table pop-up menu Handlers
	/*
	 * Handles the action triggered when the copy menu item is clicked.
	 */
	private void miCopyToClipBoardHandler(){
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
	
	/*
	 * Handles writing the current page from the table data to a CSV file.
	 */
	private void miExportPageToCSV(){
		String path = null;
		try{
			if(tblData.getRowCount() == 0){
				JOptionPane.showMessageDialog(this, "There is no data to write.");
				return;
			}
			path = getFileDialog("Save");
			String[][] data = new String[tblData.getRowCount()+1][tblData.getColumnCount()];
			for(int c = 0; c < tblData.getColumnCount(); c++)
				data[0][c] = tblData.getColumnModel().getColumn(c).getHeaderValue().toString();
			for(int r = 1; r < data.length; r++){
				for(int c = 0; c < data[r].length; c++){
					data[r][c] = tblData.getValueAt(r-1, c).toString();
				}
			}
		CsvFileWritter cfw = new CsvFileWritter(data, "|");
		cfw.writeCsvFile(path);
		JOptionPane.showMessageDialog(this, "File written to " + path);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void miExportTable(){
		String path = null;
		int row = 1;
		int column = 0;
		try{
			if(tblData.getRowCount() == 0){
				JOptionPane.showMessageDialog(this, "There is no data to write.");
				return;
			}
			path = getFileDialog("Save");
			ArrayList<String[]> tableData = table.getAllRecords();
			String[][] csvData = new String[table.getRowCount()+1][table.getColumnCount()];
			for(int c = 0; c < tblData.getColumnCount(); c++)
				csvData[0][c] = tblData.getColumnModel().getColumn(c).getHeaderValue().toString();
			for(String[] cData : tableData){
				for(String value : cData){
					csvData[row][column] = value;
					column++;
				}
				row++;
				column = 0;
			}
			CsvFileWritter cfw = new CsvFileWritter(csvData, "|");
			cfw.writeCsvFile(path);
			JOptionPane.showMessageDialog(this, "File written to " + path);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}
	
	//Button handlers
	// Handler for the refresh button.
	private void refreshTableData() {
		if (filters.isFiltersUpdted()) {
			tblData.setModel(dataTableModel(1,
					(int) cbRecordsPerPage.getSelectedItem(), false, false));
		}
		populatePagesComboBox();
	}

	// Handler for the prevous record set button.
	private void getPreviousPage() {
		int index = 0;
		try {
			index = (int) cbPage.getSelectedIndex();
			if (index == 0)
				return;
			cbPage.setSelectedIndex(index - 1);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	// Handler for the next record set button.
	private void getNextPage() {
		int index = 0;
		try {
			index = (int) cbPage.getSelectedIndex();
			if (index == cbPage.getModel().getSize() - 1)
				return;
			cbPage.setSelectedIndex(index + 1);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	// Gets the record set for the given page that is selected from the page
	// combo box.
	private void getSelectedPage() {
		int page = 0;
		int numberOfRows = 0;
		String queryString = filters.getQueryString(table.getSchemaLocation(),
				table.getTableName());
		boolean filtersEnabled = filters.isFilterEnabled();
		boolean filtersUpdated = filters.isFiltersUpdted();
		boolean filterConstraints = filters.isFilterConstraints();
		DataTableJTableModel model;
		try {
			if (cbPage.getModel().getSize() == 0)
				return;
			page = (int) cbPage.getSelectedItem();
			numberOfRows = (int) cbRecordsPerPage.getSelectedItem();
			if (filtersUpdated) {
				updateRecordSetInfo();
				filters.resetFiltersUpdated();
				return;
			}
			if (tblData.getModel().getColumnCount() == 0) {
				tblData.setModel(dataTableModel(page, numberOfRows, false,
						false));
				updateRecordSetInfo();
				filters.resetFiltersUpdated();
				return;
			}
			model = (DataTableJTableModel) tblData.getModel();
			model = (DataTableJTableModel) tblData.getModel();
			model.getDataVector().clear();
			table.selectFromTablePagination(page, numberOfRows, queryString,
					filtersEnabled, filtersUpdated, filterConstraints);
			model.loadModelData(table.getTableData());
			updateRecordSetInfo();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Handler for the new record button. The method first clears the table
	 * model. Once cleared, It appends on a new row that is editable and holds
	 * the data for the new record.
	 */
	private void addNewRecord() {

		DataTableJTableModel model;
		String[] data;
		if (tableState != DataTableState.editAddNewRecord) {
			tblData.setModel(dataTableModel(0, 0, false, false));
			setTableState(DataTableState.editAddNewRecord);
		}
		model = (DataTableJTableModel) tblData.getModel();
		data = new String[model.getColumnCount()];
		for (int i = 0; i < model.getColumnCount() - 1; i++)
			data[i] = null;
		model.addRow(data);
	}

	/*
	 * Handler for the update record button. Sets the table state to edit
	 * update so as to allow editing of the current record set.
	 */
	public void updateTableData() {
		setTableState(DataTableState.editUpdateRecord);
	}

	/*
	 * Handler for the delete record button. Sets the table state into edit
	 * delete record state. The method also appends on a column that shows a
	 * check box. The Check box determins if the row is taged for deletion.
	 */
	public void deleteRecordFromTable() {
		TableColumn DelRowChkBoxColumn;
		DataTableJTableModel model = (DataTableJTableModel) tblData.getModel();
		setTableState(DataTableState.editDelete);
		Object[] selected = new Object[model.getRowCount()];
		try {
			for (int i = 0; i < model.getRowCount(); i++)
				selected[i] = false;
			model.addColumn(checkBoxColumnName, selected);
			DelRowChkBoxColumn = tblData.getColumn(checkBoxColumnName);
			DelRowChkBoxColumn.setCellRenderer(new CheckBoxcJTableCellRender("Delete"));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	/*
	 * Handler for the calcel button. This method will undo any changes made to
	 * the record set and reloads the prier data.
	 */
	private void cancelChanges() {
		DataTableJTableModel model = (DataTableJTableModel) tblData.getModel();

		if (tableState == DataTableState.editDelete) {
			if (bulkDeletedRecords != null) {
				bulkDeletedRecords.clear();
				bulkDeletedRecords = null;
			}
			model.removeColumn(tblData.getColumnModel().getColumnIndex(
					checkBoxColumnName));

		} else if (tableState == DataTableState.editAddNewRecord) {
			if (newRecords != null)
				newRecords = null;

		} else if (tableState == DataTableState.editUpdateRecord) {
			if (updateRecords != null)
				updateRecords = null;
		}
		setTableState(DataTableState.viewing);

		if (tblData.isEditing())
			tblData.getCellEditor().stopCellEditing();
		model.loadModelData(table.getTableData());
	}

	/*
	 * Handler for the apply changes button. The method sends off the updated
	 * changes to the current record set. Once these changes have been applyed,
	 * the data can not be revirced.
	 */
	public void applyTableChangs() {
		DataTableJTableModel model = (DataTableJTableModel) tblData.getModel();
		try {
			if (tableState == DataTableState.editAddNewRecord) {
				if (table.addNewRecords(newRecords)) {
					setTableState(DataTableState.viewing);
					btnRefreshTable.doClick();
					JOptionPane.showMessageDialog(this,
							"Record insertion completed successfuly.");
				}
			} else if (tableState == DataTableState.editUpdateRecord) {
				if (table.updateRecords(updateRecords)) {
					setTableState(DataTableState.viewing);
					updateRecords.clear();
					btnRefreshTable.doClick();
					JOptionPane.showMessageDialog(this,
							"Records updated successfuly.");
				}
			} else if (tableState == DataTableState.editDelete) {
				if (bulkDeletedRecords == null || bulkDeletedRecords.isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"You do not have any records selected.");
					return;
				}

				int userSelection = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to delete these records?",
						"Confirm Record Deletesion", JOptionPane.YES_NO_OPTION);
				if (userSelection == JOptionPane.YES_OPTION) {
					if (table.deleteRecordFromTable(bulkDeletedRecords))
						;
					setTableState(DataTableState.viewing);
					if (model.getRowCount() == bulkDeletedRecords.size()) {
						DefaultComboBoxModel<Integer> cbModel = (DefaultComboBoxModel<Integer>) cbPage
								.getModel();
						model.getDataVector().clear();
						model.fireTableDataChanged();
						;
						cbModel.removeAllElements();
						updateRecordSetInfo();

					} else
						refreshTableData();
					bulkDeletedRecords.clear();
					bulkDeletedRecords = null;
					model.removeColumn(tblData.getColumnModel().getColumnIndex(
							checkBoxColumnName));
					JOptionPane.showMessageDialog(this,
							"All selected records were removed successfuly.");
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	// Tigers the filter panel to open or close.
	private void triggerFilterPanel() {
		if (!filters.isVisible()) 
			filters.slideOpen();
		else if (filters.isVisible())
			filters.slideClose();
	}

	// Action Listener.
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRefreshTable) {
			refreshTableData();
		} else if (e.getSource() == btnBackPage) {
			getPreviousPage();
		} else if (e.getSource() == btnNextPage) {
			getNextPage();
		} else if (e.getSource() == btnNewRecord) {
			addNewRecord();
		} else if (e.getSource() == btnDropRecord) {
			deleteRecordFromTable();
		} else if (e.getSource() == btnUpdateRecords) {
			updateTableData();
		} else if (e.getSource() == btnCancelChanges) {
			cancelChanges();

		} else if (e.getSource() == btnApplyChanges) {
			applyTableChangs();
		} else if (e.getSource() == btnTableFilters) {
			triggerFilterPanel();
		}else if(e.getSource() == dtPopUpMenu.jmiCopy){
			miCopyToClipBoardHandler();
		}else if(e.getSource() == dtPopUpMenu.jmiGroupColumns){
			new GroupByColumn(table);
		}else if(e.getSource() == dtPopUpMenu.jmiExportPageCSV){
			miExportPageToCSV();
		}else if(e.getSource() == dtPopUpMenu.jmiExportTableCSV){
			miExportTable();
		}
	}

	@Override
	// Listener for the page combo box.
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == cbPage && e.getStateChange() == ItemEvent.SELECTED) {
			getSelectedPage();
		}
	}

	// Default table model editer listeners.
	@Override
	public void editingStopped(ChangeEvent e) {
		int selectedRow;
		int selectedColumn;
		String value;
		try {
			selectedRow = tblData.getSelectedRow();
			selectedColumn = tblData.getSelectedColumn();
			value = tblData.getValueAt(selectedRow, selectedColumn).toString();

			if (Arrays.asList(tblCellNull).contains(value))
				value = null;

			if (tableState == DataTableState.editAddNewRecord) {
				if (newRecords == null) {
					newRecords = new ArrayList<String[]>();
				}
				if (newRecords.size() == selectedRow) {
					newRecords.add(new String[table.getColumnCount()]);
					newRecords.get(selectedRow)[selectedColumn] = value;
				} else
					newRecords.get(selectedRow)[selectedColumn] = value;
			} else if (tableState == DataTableState.editUpdateRecord) {
				String key = selectedRow + "," + selectedColumn;
				String ColumnName = tblData.getColumnName(selectedColumn);

				if (updateRecords == null)
					updateRecords = new LinkedHashMap<String, Column>();
				if (!updateRecords.containsKey(key))
					updateRecords.put(key, new Column(selectedRow, ColumnName,
							value));
				else {
					updateRecords.get(key).setDtaValue(value);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());

		}
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
	}

	// Mouse listeners.
	private class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			int selectedRow;
			int column;
			boolean value;
			try {
				if (tableState == DataTableState.editDelete) {
					if (bulkDeletedRecords == null)
						bulkDeletedRecords = new ArrayList<Integer>();
					selectedRow = tblData.getSelectedRow();
					column = tblData.getColumnModel().getColumnIndex(
							checkBoxColumnName);
					value = (boolean) tblData.getValueAt(selectedRow, column);
					if (value == false) {
						tblData.setValueAt(true, selectedRow, column);
						bulkDeletedRecords.add(selectedRow);
					} else {
						tblData.setValueAt(false, selectedRow, column);
						bulkDeletedRecords.remove((Object) selectedRow);
					}
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
			if(e.getSource() == tblData && e.getButton() == MouseEvent.BUTTON3){
		        dtPopUpMenu.show(tblData, e.getX(), e.getY());
			}
		}
		
	}
	
	private class myComponentListener extends ComponentAdapter{

		@Override
		public void componentShown(ComponentEvent e) {
			tblData.setEnabled(false);
			super.componentShown(e);
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			tblData.setEnabled(true);
			if(filters.isFiltersUpdted())
				refreshTableData();
			super.componentHidden(e);
		}
		
	}
}
