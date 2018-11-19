package wetsch.mysqlclient.guilayout.mainwindowpane;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import wetsch.mysqlclient.objects.customuiobjects.jtable.CustomJTable;
import wetsch.mysqlclient.objects.customuiobjects.renderor.GenericJTableCellRender;
import wetsch.mysqlclient.objects.customuiobjects.renderor.schemaTableCellRendor;
import wetsch.mysqlclient.objects.customuiobjects.renderor.schemaTablesTableCellRendor;
import wetsch.mysqlclient.objects.customuiobjects.scrollpane.CustomScrollPane;
import wetsch.mysqlclient.objects.database.Database;
import wetsch.mysqlclient.objects.enums.JTableID;

abstract class MainWindowFrameLayout extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private GridBagConstraints jplc;
	private Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	//JButtons
	private	JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile;
	private JMenu menuSchema;
	private JMenu menuTable;
	//JmenuItems for file menu
	protected JMenuItem menuItemSettings;
	protected JMenuItem menuItemExit;
	//JMenuItems for schema menu. 
	protected JMenuItem menuItemAddSchema;
	protected JMenuItem menuItemDropSchema;
	protected JMenuItem menuItemRefreshSchemas;
	//JMenuItems for tables menu.
	protected JMenuItem menuItemCreateTable;
	protected JMenuItem menuItemViewEditTable;
	protected JMenuItem menuItemDropTable;
	
	private JFrame mainFrame;
	
	protected LinkedHashMap<String, Database> schemaMap;
	//JTables
	protected CustomJTable tblSchemas;
	protected CustomJTable tblSchemaTable;
	protected CustomJTable tblTableDescribe;
	//ScrolPanes
	private CustomScrollPane scrpSchemas;
	private CustomScrollPane scrpSchemaTables;
	private  CustomScrollPane scrpTableDescribe;
	
	protected String loginSchema;
	public MainWindowFrameLayout(String serverAddress){
		mainFrame = new JFrame("Connected to:" + serverAddress);
		mainFrame.setSize(new Dimension(screenSize.width,screenSize.height));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setJMenuBar(menuBar);
		mainFrame.setVisible(true);
		layoutSetup();
	}
	
	private void layoutSetup(){
		setOpaque(false);
		setLayout(new GridBagLayout());
		jplc  = new GridBagConstraints();

		//MenuBar
		menuFile = new JMenu("File");
		menuSchema = new JMenu("Schemas");
		menuSchema.setIcon(new ImageIcon(getClass().getResource("/menu-icons/database.png")));
		menuTable = new JMenu("Tables");
		menuTable.setIcon(new ImageIcon(getClass().getResource("/menu-icons/table.png")));

		menuItemSettings = new JMenuItem("Settings");
		menuItemExit = new JMenuItem("Exit");
		menuItemAddSchema = new JMenuItem("Add new schema", new ImageIcon(getClass().getResource("/menu-icons/add.png")));
		menuItemDropSchema = new JMenuItem("Drop selected schema", new ImageIcon(getClass().getResource("/menu-icons/remove.png")));
		menuItemRefreshSchemas = new JMenuItem("Refresh schemas", new ImageIcon(getClass().getResource("/menu-icons/refresh-table.png")));
		menuItemCreateTable =  new JMenuItem("Create New Table");
		menuItemViewEditTable = new JMenuItem("View/Edit Table");
		menuItemDropTable = new JMenuItem("Drop Table");
		
		tblSchemas = new CustomJTable(null, new schemaTableCellRendor(), JTableID.Schemas);
		scrpSchemas = new CustomScrollPane(tblSchemas);
		addComp(this, scrpSchemas, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.4, 0.5);
		
		tblSchemaTable = new CustomJTable(null, new schemaTablesTableCellRendor(), JTableID.SchemaTables);
		scrpSchemaTables = new CustomScrollPane(tblSchemaTable);
		addComp(this, scrpSchemaTables, 2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.6, 0.5);
		
		tblTableDescribe = new CustomJTable(null, new GenericJTableCellRender(), JTableID.SchemaTableDiscription);
		scrpTableDescribe = new CustomScrollPane(tblTableDescribe);
		addComp(this, scrpTableDescribe, 1, 2, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.5);

		
		//Add objects
		menuBar.add(menuFile);
		menuBar.add(menuSchema);
		menuBar.add(menuTable);
		menuFile.add(menuItemSettings);
		menuFile.add(menuItemExit);
		menuSchema.add(menuItemAddSchema);
		menuSchema.add(menuItemDropSchema);
		menuSchema.add(menuItemRefreshSchemas);
		menuTable.add(menuItemCreateTable);
		menuTable.add(menuItemViewEditTable);
		menuTable.add(menuItemDropTable);
		mainFrame.add(this);
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

	@Override
	protected void paintComponent(Graphics g) {
		ImageIcon wallpaper = new ImageIcon(getClass().getResource("/wallpaper.png"));
		g.drawImage(wallpaper.getImage(), 0, 0, wallpaper.getIconWidth(), wallpaper.getIconHeight(), null);
		super.paintComponent(g);
	}

	 
	 
	 

}
