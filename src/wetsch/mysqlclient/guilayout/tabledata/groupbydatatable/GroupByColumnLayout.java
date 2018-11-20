package wetsch.mysqlclient.guilayout.tabledata.groupbydatatable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import wetsch.mysqlclient.objects.customuiobjects.button.CustomJButton;
import wetsch.mysqlclient.objects.customuiobjects.jtable.TableDataJTable;
import wetsch.mysqlclient.objects.customuiobjects.scrollpane.CustomListBoxScrollPane;
import wetsch.mysqlclient.objects.customuiobjects.scrollpane.CustomScrollPane;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.JTableID;

public class GroupByColumnLayout extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected int columnsSelected = 0;//The number of columns selected for the filter.
	protected int rowsFound = 0;//Rows returned by the ResultSet.
	
	private Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	JFrame frame = new JFrame("Group By Column");
	
	private GridBagConstraints jplc;
	
	private JPanel jplFilterFields = new JPanel(new GridBagLayout());

	protected JList<String> lstColumns = new JList<String>();
	protected JList<String> lstDisplayedColuns = new JList<>();

	private CustomListBoxScrollPane scrpLstColumns;
	private CustomListBoxScrollPane scrplstDisplayedColuns;

	protected CustomJButton btnAddColumns;
	protected CustomJButton btnRemoveColumns;
	protected CustomJButton btnQuery;
	
	protected JTextField jtfRowCountColumnName;
	protected JTextField jtfRegxFilter1;//Text filed to filter table data.
	protected JTextField jtfRegxFilter2;//Text filed to filter table data.
	protected JTextField jtfRegxFilter3;//Text filed to filter table data.

	protected JLabel lblQueryInfo;
	
	//Pop-up Menus
	protected GroupByDataTablePopUpMenu dtMenu = new GroupByDataTablePopUpMenu();

	//Scroll panes
	private CustomScrollPane scrpDataTable;//Holds the JTable that holds the data of the record set.
	//JTables
	protected TableDataJTable tblData;//Jtable that holds the data for the record set.

	//Database Objects
	protected Tables table;//Object to access the schema table
	
	private String[] ColumnNames;

	public GroupByColumnLayout(Tables table){
		frame.add(this);
		frame.setSize(new Dimension(screenSize.width,screenSize.height));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		setOpaque(false);
		setLayout(new GridBagLayout());
		this.table = table;
		this.ColumnNames = table.getColumnNamesArray();
		layoutSetup();
	}
	
	private void layoutSetup(){
		jplc = new GridBagConstraints();
		jplc.insets = new Insets(0, 0, 0, 0);

		lblQueryInfo = new JLabel();
		lblQueryInfo.setForeground(Color.WHITE);
		updateQueryInfoLabel();
		addComp(this, lblQueryInfo, 1, 1, 1, 2, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, 0, 0);

		jplc.insets = new Insets(0, 20, 0, 0);
		JLabel L1 = new JLabel("Columns");
		L1.setForeground(Color.white);
		addComp(this, L1, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		scrpLstColumns = new  CustomListBoxScrollPane(lstColumns);
		addComp(this, scrpLstColumns, 2, 2, 1, 5, GridBagConstraints.LINE_START, GridBagConstraints.VERTICAL, 0, 0);
		
		
		jplc.insets = new Insets(0, 50, 0, 0);
		btnAddColumns = new CustomJButton("Add Column");
		addComp(this, btnAddColumns, 3, 2, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);
		
		btnRemoveColumns = new CustomJButton("Remove Colun");
		addComp(this, btnRemoveColumns, 3, 3, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);
		
		JLabel L2 = new JLabel("Alias name for COUNT(*) column:");
		L2.setForeground(Color.white);
		addComp(this, L2, 3, 4, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		
		jtfRowCountColumnName = new JTextField("Record Count");
		addComp(this, jtfRowCountColumnName, 3, 5, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		btnQuery = new CustomJButton("Query");
		addComp(this, btnQuery, 3, 6, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(0, 10, 0, 0);
		JLabel L3 = new JLabel("Filtered Columns");
		L3.setForeground(Color.white);
		addComp(this, L3, 4, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		scrplstDisplayedColuns = new CustomListBoxScrollPane(lstDisplayedColuns);
		addComp(this, scrplstDisplayedColuns, 4, 2, 1, 5, GridBagConstraints.LINE_START, GridBagConstraints.VERTICAL, 0, 0);
		scrplstDisplayedColuns.setPreferredSize(scrplstDisplayedColuns.getPreferredSize());
		
		JLabel L4 = new JLabel("Regx Filters");
		L4.setForeground(Color.WHITE);
		addComp(this, L4, 5, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);
		
		setUpRegxFieldsPanel();
		addComp(this, jplFilterFields, 5, 2, 3, 4, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		jplc.insets = new Insets(0, 0, 0, 0);
		tblData = new TableDataJTable(table, new DefaultTableModel(ColumnNames, 0), JTableID.DataTable);
		scrpDataTable = new CustomScrollPane(tblData);
		addComp(this, scrpDataTable, 1, 7, 5, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, 1, 1);
		
	}
	
	private void setUpRegxFieldsPanel(){
		jplFilterFields.setOpaque(false);
		jtfRegxFilter1 = new JTextField();
		jtfRegxFilter2 = new JTextField();
		jtfRegxFilter3 = new JTextField();

		jplc.insets = new Insets(0, 0, 20, 0);

		JLabel L1 = new JLabel("Field 1:");
		L1.setForeground(Color.WHITE);
		addComp(jplFilterFields, L1, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		addComp(jplFilterFields, jtfRegxFilter1, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel L2 = new JLabel("Field 2:");
		L2.setForeground(Color.WHITE);
		addComp(jplFilterFields, L2, 1, 2, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);
		
		addComp(jplFilterFields, jtfRegxFilter2, 2, 2, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel L3 = new JLabel("Field 3");
		L3.setForeground(Color.WHITE);
		addComp(jplFilterFields, L3, 1, 3, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		addComp(jplFilterFields, jtfRegxFilter3, 2, 3, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 1, 1);

		
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
	 
	 /**
	  * This method updates the query information label.  The column count does not account for the COUNT(*) 
	  * Column that gets returned by the ResultSet.  The column count is only the number of columns that are filtered.
	  */
	 protected void updateQueryInfoLabel(){
		 String text = "<html>Number of columns: " + columnsSelected + "<br><br>";
		 text += "Number of rows found: " + rowsFound + "</html>";
		 lblQueryInfo.setText(text);

	 }
	 
	 
	@Override
	protected void paintComponent(Graphics g) {
		Color COLOR_1 = new Color(0, 0,25);
		Color COLOR_2 = new Color(0,0,50);
		float SIDE = 40;
		GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_1, SIDE, SIDE, COLOR_2, true);
		BufferedImage bfi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bfi.createGraphics();
		g2.setPaint(gradientPaint);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(bfi, 0, 0, getWidth(), getHeight(), null);
		super.paintComponent(g);
	}
	
}
