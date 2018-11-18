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
import wetsch.mysqlclient.objects.customuiobjects.scrolpane.CustomListBoxScrollPane;
import wetsch.mysqlclient.objects.customuiobjects.scrolpane.CustomScrolPane;
import wetsch.mysqlclient.objects.database.Tables;
import wetsch.mysqlclient.objects.enums.JTableID;

public class GroupByColumnLayout extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	JFrame frame = new JFrame("Group By Column");
	private GridBagConstraints jplc;

	protected JList<String> lstColumns = new JList<String>();
	protected JList<String> lstDisplayedColuns = new JList<>();

	private CustomListBoxScrollPane scrpLstColumns;
	private CustomListBoxScrollPane scrplstDisplayedColuns;

	protected CustomJButton btnAddColumns;
	protected CustomJButton btnRemoveColumns;
	protected CustomJButton btnQuery;
	
	protected JTextField jtfRowCountColumnName;
	
	//Popup Menus
	protected GroupByDataTablePopUpMenu dtMenu = new GroupByDataTablePopUpMenu();

	//Scrol panes
	private CustomScrolPane scrpDataTable;//Holds the JTable that holds the data of the record set.
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

		JLabel L1 = new JLabel("Columns");
		L1.setForeground(Color.white);
		addComp(this, L1, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		scrpLstColumns = new  CustomListBoxScrollPane(lstColumns);
		addComp(this, scrpLstColumns, 1, 2, 1, 5, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, 0, 0.5);

		jplc.insets = new Insets(0, 50, 0, 0);
		btnAddColumns = new CustomJButton("Add Column");
		addComp(this, btnAddColumns, 2, 2, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);
		
		btnRemoveColumns = new CustomJButton("Remove Colun");
		addComp(this, btnRemoveColumns, 2, 3, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);
		
		JLabel L2 = new JLabel("Column name for COUNT(*) column:");
		L2.setForeground(Color.white);
		addComp(this, L2, 2, 4, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		
		jtfRowCountColumnName = new JTextField("Record Count");
		addComp(this, jtfRowCountColumnName, 2, 5, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		btnQuery = new CustomJButton("Query");
		addComp(this, btnQuery, 2, 6, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(0, 10, 0, 0);
		JLabel L3 = new JLabel("Filtered Columns");
		L3.setForeground(Color.white);
		addComp(this, L3, 3, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.NONE, 0, 0);

		scrplstDisplayedColuns = new CustomListBoxScrollPane(lstDisplayedColuns);
		addComp(this, scrplstDisplayedColuns, 3, 2, 1, 5, GridBagConstraints.LINE_START, GridBagConstraints.VERTICAL, 0, 0.5);

		jplc.insets = new Insets(0, 0, 0, 0);
		tblData = new TableDataJTable(table, new DefaultTableModel(ColumnNames, 0), JTableID.DataTable);
		scrpDataTable = new CustomScrolPane(tblData);
		addComp(this, scrpDataTable, 1, 7, 4, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, 1, 1);

		
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
