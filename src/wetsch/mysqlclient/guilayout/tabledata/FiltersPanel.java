package wetsch.mysqlclient.guilayout.tabledata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wetsch.mysqlclient.objects.configuration.Settings;
import wetsch.mysqlclient.objects.database.Column;
import wetsch.mysqlclient.objects.enums.GlobalObjects;

public class FiltersPanel extends FiltersPanelLayout implements ActionListener,	ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private Settings settings = (Settings) GlobalObjects.settings.get();
	private boolean state = false;
	private boolean filtersUpdated = false;
	private boolean filterConstraints = false;
	private boolean animation = false;

	private String[] emptyValueChecks = new String[] { "", "NULL", "null" };
	private String[] columnNames = null;
	private LinkedHashMap<Integer, Column> filteredColumns = null;
	private Timer slideTimer = new Timer(2, this);

	public FiltersPanel(int viewWidth, int viewHeight, String[] columnNames) {
		super(viewWidth, viewHeight);
		this.columnNames = columnNames;
		setupActionListeners();
		populatelstColumnsBox();
		loadSettings();
	}
	
	private void loadSettings(){
		animation = settings.isAnimation();
		
	}

	private void setupActionListeners() {
		filterEnabled.addActionListener(this);
		btnAddColumns.addActionListener(this);
		btnRemoveColumns.addActionListener(this);
		btnAddFilterValue.addActionListener(this);
		btnRemoveFilterValue.addActionListener(this);
		lstFilteredColumns.addListSelectionListener(this);
	}

	private void populatelstColumnsBox() {
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (String value : columnNames)
			model.addElement(value);
		lstColumns.setModel(model);
		lstFilteredColumns.setModel(model);
		lstDisplayedColuns.setModel(new DefaultListModel<String>());
		lstFilteredColumnValues.setModel(new DefaultListModel<String>());
	}

	private void addColumsToDisplayedList() {
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
		}
		filtersUpdated = true;
	}

	private void removeColumnsFromDisplayList() {
		DefaultListModel<String> model = (DefaultListModel<String>) lstDisplayedColuns.getModel();
		if(lstDisplayedColuns.getSelectedValuesList().size() == 0){
			JOptionPane.showMessageDialog(this, "You do not have any columns selected.");
			return;

		}
		for (Object value : lstDisplayedColuns.getSelectedValuesList())
			model.removeElement(value);
		filtersUpdated = true;
	}

	private void addFilterValue() {
		try {
			if(lstFilteredColumns.getSelectedIndex() == -1){
				JOptionPane.showMessageDialog(this, "You do not have any columns selected.");
				return;
			}
				
			DefaultListModel<String> model = (DefaultListModel<String>) lstFilteredColumnValues.getModel();
			if (filteredColumns == null)
				filteredColumns = new LinkedHashMap<Integer, Column>();
			String value = JOptionPane.showInputDialog("What is the filter value?\n (Use % as a wildcard.)");
			int ColumnIndex = lstFilteredColumns.getSelectedIndex();
			String ColumnName = lstFilteredColumns.getSelectedValue().toString();
			if (Arrays.asList(emptyValueChecks).contains(value))
				value = null;
			if (!filteredColumns.containsKey(ColumnIndex)) {
				filteredColumns.put(ColumnIndex, new Column(ColumnName, null));
				filteredColumns.get(ColumnIndex).addFilterValues(value);
			} else
				filteredColumns.get(ColumnIndex).addFilterValues(value);
			if (value == null)
				model.addElement("NULL");
			else
				model.addElement(value);
			filterConstraints = true;
			filtersUpdated = true;
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}

	private void removeFilterValue() {
		try {
			if(lstFilteredColumnValues.getSelectedValuesList().size() == 0){
				JOptionPane.showMessageDialog(this, "You do not have any values selected.");
				return;
			}
			
			if (lstFilteredColumnValues.getModel().getSize() > 0) {
				int columnIndex = lstFilteredColumns.getSelectedIndex();
				DefaultListModel<String> model = (DefaultListModel<String>) lstFilteredColumnValues
						.getModel();
				for (Object value : lstFilteredColumnValues
						.getSelectedValuesList()) {
					if (value.equals("NULL"))
						value = null;
					if (filteredColumns.get(columnIndex).removeFilterValue(
							(String) value)) {
						if (value == (String) null)
							value = "NULL";
						model.removeElement(value);
					}
				}
				if (filteredColumns.get(columnIndex).getFilteredValues().size() == 0) {
					filteredColumns.remove((int) columnIndex);
					String column = lstFilteredColumns.getSelectedValue();
					System.out.println("Column " + column
							+ " removed from filters.");
				}
				if (filteredColumns.size() == 0) {
					clearFilterValues();
					System.out.println("Filtered colums cleared");
				}
				filtersUpdated = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearFilterValues() {
		filterConstraints = false;
		filteredColumns = null;
	}

	public String getQueryString(String schemaName, String tableName) {
		boolean firstValue = true;
		String basicQuery = "SELECT * FROM " + schemaName + "." + tableName;
		StringBuilder queryString = new StringBuilder();
		if (!filterEnabled.isSelected())
			return queryString.append(basicQuery).toString();
		else {
			if (lstDisplayedColuns.getModel().getSize() == columnNames.length)
				queryString.append(basicQuery);
			else {
				queryString.append("SELECT ");
				for (String value : getDisplayedColumns()) {
					if (firstValue) {
						queryString.append(value);
						firstValue = false;
					} else
						queryString.append("," + value);
				}
				queryString.append(" FROM " + schemaName + "." + tableName);
			}
			if (filterConstraints) {
				firstValue = true;
				queryString.append(" WHERE ");
				for (Map.Entry<Integer, Column> v : filteredColumns.entrySet()) {
					if (firstValue) {
						queryString.append("(");
					} else
						queryString.append(" AND (");
					firstValue = true;
					for (String s : v.getValue().getFilteredValues()) {
						if (firstValue) {
							if(s == null)
								queryString.append(v.getValue().getColumnName() + " IS NULL");
							else
								queryString.append(v.getValue().getColumnName() + " LIKE '" + s + "'");
							firstValue = false;
						} else
							if(s == null)
								queryString.append(" OR " + v.getValue().getColumnName() + " IS NULL");
							else
								queryString.append(" OR " + v.getValue().getColumnName() + " LIKE '" + s + "'");
					}
					queryString.append(")");
				}
			}
		}
		return queryString.toString();
	}

	public void slideOpen() {
		if(!animation){
			setLocation(0, getY());
			setVisible(true);
		}else{
			state = true;
			setVisible(true);
			slideTimer.start();
		}
	}

	public void slideClose() {
		if(!animation){
			setVisible(false);
			setLocation(0-getWidth(),getY());
		}else{
			state = false;
			slideTimer.start();
		}
	}

	public void resetFiltersUpdated() {
		filtersUpdated = false;
	}

	public boolean isFiltersUpdted() {
		return filtersUpdated;
	}

	public boolean isFilterEnabled() {
		return filterEnabled.isSelected();
	}

	public boolean isFilterConstraints() {
		return filterConstraints;
	}

	public String[] getDisplayedColumns() {
		if (lstDisplayedColuns.getModel().getSize() == 0)
			return null;
		ArrayList<Integer> columnIndex = new ArrayList<Integer>();
		if (lstDisplayedColuns.getModel().getSize() == columnNames.length)
			return columnNames;
		String columnNames[] = new String[lstDisplayedColuns.getModel()
				.getSize()];
		DefaultListModel<String> model = (DefaultListModel<String>) lstDisplayedColuns
				.getModel();
		model.copyInto(columnNames);
		for (String value : columnNames) {
			columnIndex.add(Arrays.asList(this.columnNames).indexOf((String) value));
		}
		Collections.sort(columnIndex);
		int counter = 0;
		for (int i : columnIndex) {
			columnNames[counter] = this.columnNames[i];
			counter++;
		}
		return columnNames;
	}

	// Listeners
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == slideTimer) {
			if (state) {
				if (x < 0) {
					setLocation(x += 10 , getY());
				} else {
					slideTimer.stop();
					setLocation(0, getY());
				}
			} else if(!state) {
				if (x > getWidth() * (-1))
					setLocation(x -= 10, getY());
				else {
					slideTimer.stop();
					setVisible(false);
				}
			}
		} else if (e.getSource() == filterEnabled) {
			if (!filterEnabled.isSelected()) {
				clearFilterValues();
				lstColumns.clearSelection();
				DefaultListModel<String> model = (DefaultListModel<String>) lstDisplayedColuns
						.getModel();
				model.removeAllElements();
				model = (DefaultListModel<String>) lstFilteredColumnValues
						.getModel();
				model.removeAllElements();
				filtersUpdated = true;
			} else {
				filtersUpdated = true;
			}
		} else if (e.getSource() == btnAddColumns) {
			addColumsToDisplayedList();
		} else if (e.getSource() == btnRemoveColumns) {
			removeColumnsFromDisplayList();
		} else if (e.getSource() == btnAddFilterValue) {
			addFilterValue();
		} else if (e.getSource() == btnRemoveFilterValue) {
			removeFilterValue();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == lstFilteredColumns && e.getValueIsAdjusting()) {
			int index = lstFilteredColumns.getSelectedIndex();
			if (filteredColumns != null) {
				if (filteredColumns.size() > 0) {
					DefaultListModel<String> model = (DefaultListModel<String>) lstFilteredColumnValues
							.getModel();
					model.removeAllElements();
					if (filteredColumns.containsKey(index))
						for (String value : filteredColumns.get(index)
								.getFilteredValues())
							model.addElement(value);
				}
			}
		}
	}
}
