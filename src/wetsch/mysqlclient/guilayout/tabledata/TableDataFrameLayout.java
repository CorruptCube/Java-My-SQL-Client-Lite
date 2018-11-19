package wetsch.mysqlclient.guilayout.tabledata;

/*
 * This class handels the layout for the records table.
 * All the objects displayed on the frame are first set up here.
 * The TableDataFrame class extends this class to access the objects
 * that are made avalable to the user.
 * This class also holds the methods that allow seting lof a new Table model.
 * 
 */

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import wetsch.mysqlclient.objects.customuiobjects.button.CustomJButton;
import wetsch.mysqlclient.objects.customuiobjects.jtable.TableDataJTable;
import wetsch.mysqlclient.objects.customuiobjects.scrollpane.CustomScrollPane;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.JTableID;

abstract class TableDataFrameLayout extends JFrame {
	protected final String tableName;
	protected final String belongsToSchema;
	
	private static final long serialVersionUID = 1L;

	private GridBagConstraints jplc;
	
	//Pop-up Menu	
	protected DataTablePopUpMenu dtPopUpMenu = new DataTablePopUpMenu();
	
	//JLabels
	private JLabel lblRecordsPerPage;//Lable discribing the records per page field.
	protected JLabel lblTableInfo;// Holds the information show about the table name and schema location.
	protected JLabel lblRecordSet;// the current record set for the selected page.
	
	//ComboBox
	protected JComboBox<Integer> cbRecordsPerPage;

	//JButtons
	protected CustomJButton btnRefreshTable;//Refresh table data for the selected record set.
	protected CustomJButton btnBackPage;//Go back one page of the record set.
	protected CustomJButton btnNextPage;//Go forward one page of the record set.
	protected CustomJButton btnNewRecord;//Add a new record to the table.
	protected CustomJButton btnDropRecord;//Remove record from the table.
	protected CustomJButton btnUpdateRecords;//Update records in the current record set.
	protected CustomJButton btnCancelChanges;//Cancel the canges made to the Table.
	protected CustomJButton btnApplyChanges;//Apply the changes made to the table.
	protected CustomJButton btnTableFilters;//Open/Close table filters panel.

	//JScrolPanes
	private CustomScrollPane scrpDataTable;//Holds the JTable that holds the data of the record set.
	//JTables
	protected TableDataJTable tblData;//Jtable that ods the data for the record set.
	//JComboBox
	protected JComboBox<Integer> cbPage;//Holds the avalable pages numbers of the record set.
	
	//JPanels
	protected FiltersPanel filters;
	private JPanel jpMainPanel;
	
	//Object that gets available screen size
	private Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

	//Database Objects
	protected Tables table;//Object to access the schema table

	private int viewableWidth;//Holds the useable content pane width.
	private int viewableHeight;//Holds the viewable content panne height.
	//Image icon that holds background image.
	private ImageIcon wallpaper = new ImageIcon(getClass().getResource("/wallpaper.png"));
	private JLabel background = new JLabel(wallpaper);//Holds wallpaper image icon.
	
	public TableDataFrameLayout(String tableName, Tables table){
		super(tableName);
		setSize(screenSize.width, screenSize.height);
		setResizable(false);
		setLayout(null);

		this.tableName = tableName;
		belongsToSchema = table.getSchemaLocation();
		this.table = table;


	    setVisible(true);
	    viewableWidth = getContentPane().getWidth();
		viewableHeight = getContentPane().getHeight();

	    layoutSetup();

	}
	
	//Set up UI layout.
	private void layoutSetup(){
		jplc = new GridBagConstraints();
		background.setBounds(0,0,viewableWidth,viewableHeight);
		
		filters = new FiltersPanel(viewableWidth/3, viewableHeight-130, table.getColumnNamesArray());
		jpMainPanel = new JPanel(new GridBagLayout());
		jpMainPanel.setBounds(0,0,viewableWidth,viewableHeight);
		jpMainPanel.setOpaque(false);
		
		lblTableInfo = new JLabel();
		lblTableInfo.setForeground(Color.white);
		addComp(jpMainPanel, lblTableInfo, 0, 0, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(15, 0, 0, 0);
		lblRecordsPerPage = new JLabel("Number of records per page:");
		lblRecordsPerPage.setForeground(Color.white);
		addComp(jpMainPanel, lblRecordsPerPage, 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(10, 10, 0, 0);
		cbRecordsPerPage = new JComboBox<Integer>();
		addComp(jpMainPanel, cbRecordsPerPage, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(0, 10, 0, 0);
		btnRefreshTable = new CustomJButton("<html><center>Refresh Table</center></html>");
		addComp(jpMainPanel, btnRefreshTable, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(0, 100, 0, 0);
		btnBackPage = new CustomJButton("Back");
		addComp(jpMainPanel, btnBackPage, 3, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		btnCancelChanges = new CustomJButton("Cancel");
		btnCancelChanges.setVisible(false);
		addComp(jpMainPanel, btnCancelChanges, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		btnApplyChanges = new CustomJButton("<html><center>Apply Changes</center><html>");
		btnApplyChanges.setVisible(false);
		addComp(jpMainPanel, btnApplyChanges, 5, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(10, 90, 0, 0);
		cbPage = new JComboBox<Integer>();
		addComp(jpMainPanel, cbPage, 4, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(0, 10, 0, 0);
		btnNextPage = new CustomJButton("Next");
		addComp(jpMainPanel, btnNextPage, 5, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		
		jplc.insets = new Insets(0, 90, 0, 0);
		btnNewRecord = new CustomJButton("<html><center>New Record</center></html>");
		addComp(jpMainPanel, btnNewRecord, 6, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		btnDropRecord = new CustomJButton("<html><center>Dropp Records</center></html>");
		addComp(jpMainPanel, btnDropRecord, 7, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		
		btnUpdateRecords = new CustomJButton("<html><center>Update Records</center></html>");
		addComp(jpMainPanel, btnUpdateRecords, 8, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		
		btnTableFilters = new CustomJButton("<html><center>Apply >Filters</center></html>");
		addComp(jpMainPanel, btnTableFilters, 9, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(0, 0, 0, 0);
		lblRecordSet = new JLabel();
		lblRecordSet.setForeground(Color.white);
		addComp(jpMainPanel, lblRecordSet, 0, 2, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		tblData = new TableDataJTable(table, null, JTableID.DataTable);
		scrpDataTable = new CustomScrollPane(tblData);
		scrpDataTable.setBorder(BorderFactory.createLineBorder(Color.blue));
		addComp(jpMainPanel, scrpDataTable, 0, 3, 10, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, 1, 1);

		add(filters);
		add(jpMainPanel);
		add(background);
	}
	
	
	 private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch, double weightx, double weighty){
		 jplc.gridx = xPos;
		 jplc.gridy = yPos;
		 jplc.gridwidth = compWidth;
		 jplc.gridheight = compHeight;
		 jplc.weightx = weightx;
		 jplc.weighty = weighty;
		 jplc.anchor = place;
		 jplc.fill = stretch;
	     thePanel.add(comp, jplc);
	 }

	
	
}
