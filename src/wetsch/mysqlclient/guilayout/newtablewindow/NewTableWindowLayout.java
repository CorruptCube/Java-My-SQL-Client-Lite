package wetsch.mysqlclient.guilayout.newtablewindow;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import wetsch.mysqlclient.objects.customuiobjects.button.CustomJButton;
import wetsch.mysqlclient.objects.customuiobjects.editor.JComboboxCellEditor;
import wetsch.mysqlclient.objects.customuiobjects.jtable.NewTableColumnsJTable;
import wetsch.mysqlclient.objects.customuiobjects.renderor.GenericJTableCellRender;
import wetsch.mysqlclient.objects.customuiobjects.scrolpane.CustomScrolPane;
import wetsch.mysqlclient.objects.database.Database;
import wetsch.mysqlclient.objects.enums.DataType;
import wetsch.mysqlclient.objects.enums.DataTypeAttribiutes;
import wetsch.mysqlclient.objects.enums.JTableID;


public abstract class NewTableWindowLayout extends JPanel{
	private static final long serialVersionUID = 1L;
	private Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	private String[] ENGINES = new String[]{"FEDERATED", "CSV", "MEMORY", "BLACKHOLE", "MRG_MYISAM", "MyISAM", "ARCHIVE", "PERFORMANCE_SCHEMA", "InnoDB"};
	private String[] columnNames = new String[]{"Column Name", "Data Type", "Null"};
	
	private GridBagConstraints jplc;
	private Database schema;

	protected JFrame frame;

	private CustomScrolPane scrpColumTable;
	private JScrollPane scrpQueryAera;
	
	protected NewTableColumnsJTable tblNewColumns;
	
	private JLabel lblTableName;
	private JLabel lblEngineTypes;

	protected JCheckBox chkbPrimaryKey;
	protected JCheckBox chkbForeign;

	
	protected JComboBox<String> jcbDataTypes;
	protected JComboBox<String> jcbEngineTypes;
	protected JComboBox<String> jcbAttributeCategory;
	protected JComboBox<String> jcbAttributes;
	protected JComboBox<String> jcbTables;
	protected JComboBox<String> jcbColumns;
	
	protected JButton btnAddColumn;
	protected JButton btnRemoveColumn;
	protected CustomJButton btnAttributeAppend;
	protected CustomJButton btnCreateTable;
	protected CustomJButton btnResetTable;

	
	protected JTextField jtfTableName;
	protected JTextField jtfColumnName;
	protected JTextField jtfAttributes;
	
	protected final JTextArea jtaQueryAera = new JTextArea();
	private JPanel jpHeaderPanel = new JPanel(new GridBagLayout());
	private JPanel jpNewColumns = new JPanel(new GridBagLayout());
	private JPanel jpAttribute = new JPanel(new GridBagLayout());
	private JPanel jpPrimaryForiegnKeys = new JPanel(new GridBagLayout());
	private JPanel jpQueryString = new JPanel(new GridBagLayout());
	
	public NewTableWindowLayout(Database schema) {
		this.schema = schema;
		setOpaque(false);
		setLayout(new GridBagLayout());
		frame = new JFrame("Add new table in "+ this.schema.getSchemaName());
		frame.setResizable(false);
		frame.setSize(screenSize.width/2,screenSize.height/2+100);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.add(this);
		populateJComboBoxes();
		setDoubleBuffered(true);
		layoutSetup();
	}
	
	private void populateJComboBoxes(){
		jcbEngineTypes = new JComboBox<String>();
		jcbDataTypes = new JComboBox<String>();
		jcbAttributeCategory = new JComboBox<String>();
		jcbAttributes = new JComboBox<String>();
		jcbTables = new JComboBox<String>();
		jcbColumns = new JComboBox<String>();


		Thread populate = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//Table engine types.
				jcbEngineTypes.setModel(new DefaultComboBoxModel<String>(ENGINES));
				jcbEngineTypes.setSelectedIndex(jcbEngineTypes.getModel().getSize()-1);

				//Data types.
				int index = 0;
				String[] data = new String[DataType.values().length];
				for(DataType v : DataType.values())
					data[index++] = v.getDataTypeName();
				jcbDataTypes.setModel(new DefaultComboBoxModel<String>(data));
				
				//Data type attribute category.
				index = 0;
				data = new String[DataTypeAttribiutes.values().length];
				for(DataTypeAttribiutes v : DataTypeAttribiutes.values())
					data[index++] = v.name();
				jcbAttributeCategory.setModel(new DefaultComboBoxModel<String>(data));
				jcbAttributeCategory.setSelectedIndex(0);
				
				//Attributes
				jcbAttributes.setModel(new DefaultComboBoxModel<String>(DataTypeAttribiutes.Numeric.getAttributes()));

				//Table Names
				if(schema.getSchemaTables() != null){
					data = new String[schema.getSchemaTables().keySet().size()];
					schema.getSchemaTables().keySet().toArray(data);
					jcbTables.setModel(new DefaultComboBoxModel<String>(data));
					jcbTables.setSelectedIndex(-1);
				}
			}
		});
		populate.start();
	}
	
	private void layoutSetup(){
		jplc = new GridBagConstraints();
		jplc.insets = new Insets(0, 0, 0, 0);
		addComp(this, jpHeaderPanel, 1, 1, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0);
		jpHeaderPanelLayout();
		
		jplc.insets = new Insets(0, 0, 0, 0);
		addComp(this, jpNewColumns, 1, 2, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.4);
		jpNewColumnsLayout();		

		jplc.insets = new Insets(0, 0, 0, 0);
		addComp(this, jpAttribute, 1, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.2, 0.2);
		jpAttributeSetup();
		
		jplc.insets = new Insets(0, 0, 0, 0);
		addComp(this, jpPrimaryForiegnKeys, 2, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.8, 0.2);
		jpPrimaryForiegnKeysSetup();

		jplc.insets = new Insets(0, 0, 0, 0);
		addComp(this, jpQueryString, 1, 4, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.4);
		queryStringPanelSetup();


	}
	
	private void jpHeaderPanelLayout(){
		jpHeaderPanel.setOpaque(false);
		lblTableName =  new JLabel("New Table Name:");
		lblTableName.setForeground(Color.WHITE);
		addComp(jpHeaderPanel, lblTableName, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);
		
		jtfTableName = new JTextField();
		addComp(jpHeaderPanel, jtfTableName, 2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		lblEngineTypes = new JLabel("Engine:", SwingConstants.CENTER);
		lblEngineTypes.setForeground(Color.white);
		addComp(jpHeaderPanel, lblEngineTypes, 3, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);

		addComp(jpHeaderPanel, jcbEngineTypes, 4, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
	
	}
	
	private void jpNewColumnsLayout(){
		jpNewColumns.setOpaque(false);
		jpNewColumns.setBorder(BorderFactory.createLineBorder(Color.white));

		
		jplc.insets = new Insets(5, 5, 5, 5);
		btnAddColumn = new JButton(new ImageIcon(getClass().getResource("/button-icons/Add-Button.png")));
		btnAddColumn.setContentAreaFilled(false);
		btnAddColumn.setBorderPainted(false);
		
		addComp(jpNewColumns, btnAddColumn, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 1);
		
		btnRemoveColumn = new JButton(new ImageIcon(getClass().getResource("/button-icons/Remove-Button.png")));
		btnRemoveColumn.setBorderPainted(false);
		btnRemoveColumn.setContentAreaFilled(false);
		addComp(jpNewColumns, btnRemoveColumn, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 1);

		tblNewColumns = new NewTableColumnsJTable(new DefaultTableModel(columnNames, 0), new GenericJTableCellRender(), JTableID.NewTableColumns);
		tblNewColumns.getColumnModel().getColumn(0).setCellRenderer(new GenericJTableCellRender());
		tblNewColumns.getColumnModel().getColumn(1).setCellEditor(new JComboboxCellEditor(jcbDataTypes));
		scrpColumTable = new CustomScrolPane(tblNewColumns);
		DefaultTableModel m = (DefaultTableModel) tblNewColumns.getModel();
		m.addRow(new Object[]{null,null,false});
		addComp(jpNewColumns, scrpColumTable, 2, 1, 1, 2, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.BOTH, 1, 1);

		
	}
	
	private void jpAttributeSetup(){
		jpAttribute.setOpaque(false);
		jpAttribute.setDoubleBuffered(true);
		jpAttribute.setBorder(BorderFactory.createLineBorder(Color.white));
		
		btnAttributeAppend = new CustomJButton("<html><center>Add</center></html>");
		addComp(jpAttribute, btnAttributeAppend, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		
		JLabel l1 = new JLabel("Attributes");
		l1.setForeground(Color.white);
		addComp(jpAttribute, l1, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, 0, 0);


		JLabel l2 = new JLabel("Category:", SwingConstants.RIGHT);
		l2.setForeground(Color.white);
		jplc.insets = new Insets(20, 0, 0, 0);
		addComp(jpAttribute, l2, 1, 3, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, 0, 0);

		addComp(jpAttribute, jcbAttributeCategory, 2, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel l3 = new JLabel("Attribute:", SwingConstants.RIGHT);
		l3.setForeground(Color.white);
		addComp(jpAttribute, l3, 1, 4, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, 0, 0);

		addComp(jpAttribute, jcbAttributes, 2, 4, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel l4 = new JLabel("Appended:");
		l4.setForeground(Color.WHITE);
		addComp(jpAttribute, l4, 1, 5, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, 0, 0);

		jtfAttributes = new JTextField();
		addComp(jpAttribute, jtfAttributes, 2, 5, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, 1, 1);

	}
	
	private void jpPrimaryForiegnKeysSetup(){
		jpPrimaryForiegnKeys.setOpaque(false);
		jpPrimaryForiegnKeys.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		JLabel l1 = new JLabel("Key Types");
		l1.setForeground(Color.WHITE);
		addComp(jpPrimaryForiegnKeys, l1, 1, 1, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(20, 0, 0, 0);
		
		chkbPrimaryKey = new JCheckBox("Primary Key");
		chkbPrimaryKey.setOpaque(false);
		chkbPrimaryKey.setForeground(Color.WHITE);
		addComp(jpPrimaryForiegnKeys, chkbPrimaryKey, 1, 2, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);

		chkbForeign = new JCheckBox("Foreign Key");
		chkbForeign.setOpaque(false);
		chkbForeign.setForeground(Color.WHITE);
		addComp(jpPrimaryForiegnKeys, chkbForeign, 1, 3, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel l2 = new JLabel("Tables:");
		l2.setForeground(Color.WHITE);
		addComp(jpPrimaryForiegnKeys, l2, 1, 4, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 1);

		addComp(jpPrimaryForiegnKeys, jcbTables, 2, 4, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);
		
		JLabel l3 = new JLabel("Columns:");
		l3.setForeground(Color.white);
		addComp(jpPrimaryForiegnKeys, l3, 1, 5, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 1);

		addComp(jpPrimaryForiegnKeys, jcbColumns, 2, 5, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 1, 1);

	}
	
	private void queryStringPanelSetup(){
		addComp(jpQueryString, queryStringNote(), 1, 1, 4, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		
		jpQueryString.setOpaque(false);
		JLabel L1 = new JLabel("Query");
		L1.setForeground(Color.white);
		addComp(jpQueryString, L1, 1, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, 0.2, 0);

		scrpQueryAera = new JScrollPane(jtaQueryAera);
		jtaQueryAera.setLineWrap(true);
		jplc.insets = new Insets(10, 0, 10, 0);
		addComp(jpQueryString, scrpQueryAera, 2, 2, 1, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, 0.8, 0);

		btnCreateTable = new CustomJButton("<html>Create<br>Table</html>");
		jplc.insets = new Insets(0, 20, 0, 0);
		addComp(jpQueryString, btnCreateTable, 3, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 0.2, 0);

		btnResetTable = new CustomJButton("<html>Reset<br>Table</html>");
		addComp(jpQueryString, btnResetTable, 3, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 0, 1);

	}
	
	private JLabel queryStringNote(){
		JLabel l = new JLabel("<html>"
				+ "<center>Note:</center>"
				+ "All edits should be done once you have used the above controls to build the initial query."
				+ "</html>");
		l.setForeground(Color.WHITE);
	return l;
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
		BufferedImage bfi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bfi.createGraphics();
	   	Color COLOR_1 = new Color(0, 0,25);
		Color COLOR_2 = new Color(0,0,50);
		float SIDE = 40;
		GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_1, SIDE, SIDE, COLOR_2, true);
		g2.setPaint(gradientPaint);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(bfi, 0, 0, getWidth(), getHeight(), null);
		super.paintComponent(g);
	}
	
	

	 
	 

}
