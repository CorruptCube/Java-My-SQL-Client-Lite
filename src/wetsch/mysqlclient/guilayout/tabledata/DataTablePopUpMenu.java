package wetsch.mysqlclient.guilayout.tabledata;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class DataTablePopUpMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	
	//JMenus
	private JMenu jmImport = new JMenu("Import");
	private JMenu jmExport = new JMenu("Export");
	
	//JMenuItems
	public JMenuItem jmiCopy = new JMenuItem("Copy");
	public JMenuItem jmiGroupColumns = new JMenuItem("Group Columns By");
	public JMenuItem jmiExportPageCSV = new JMenuItem("Page to CSV");
	public JMenuItem jmiExportTableCSV = new JMenuItem("Table to CSV");
	public JMenuItem jmiImportCSV = new JMenuItem("From CSV");
	

	public DataTablePopUpMenu() {
		setupPopUpMenu();
	}
	
	//Adding components to pop-up menu.
	private void setupPopUpMenu(){
		add(jmiCopy);
		add(jmiGroupColumns);
		add(jmImport);
		jmImport.add(jmiImportCSV);
		add(jmExport);
		jmExport.add(jmiExportPageCSV);
		jmExport.add(jmiExportTableCSV);
	}
}