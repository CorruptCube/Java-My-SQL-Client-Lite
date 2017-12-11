package wetsch.mysqlclient.guilayout.tabledata;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import wetsch.mysqlclient.objects.customuiobjects.button.CustomJButton;
import wetsch.mysqlclient.objects.customuiobjects.button.CustomToggleButton;
import wetsch.mysqlclient.objects.customuiobjects.scrolpane.CustomListBoxScrollPane;

abstract class FiltersPanelLayout extends JPanel{
	private static final long serialVersionUID = 1L;
	protected int x;

	//JLabel
	private JLabel lblTableColumnsPanelTitle;
	private JLabel lblLSTColumns;
	private JLabel lblDisplayedColumns;
	private JLabel lblFilteredColumns;
	private JLabel lblFilterColumnValues;
	private JLabel lblColumnFiltersPanelTitle;
	private JPanel tableColumnsPanel;
	private JPanel ColumnFiltersPanel;
	//ScrollPane
	private CustomListBoxScrollPane scrpLstColumns;
	private CustomListBoxScrollPane scrplstDisplayedColuns;
	private CustomListBoxScrollPane scrplstFilteredColumns;
	private CustomListBoxScrollPane scrplstFilteredColumnValues;
	//JCheckBox
	protected CustomToggleButton filterEnabled;
	//JList
	protected JList<String> lstColumns;
	protected JList<String> lstDisplayedColuns;
	protected JList<String> lstFilteredColumns;
	protected JList<String> lstFilteredColumnValues;
	//JButtons
	protected CustomJButton btnAddColumns;
	protected CustomJButton btnRemoveColumns;
	protected CustomJButton btnAddFilterValue;
	protected CustomJButton btnRemoveFilterValue;


	public FiltersPanelLayout(int viewWidth, int viewHeight){
		setLayout(null);
		x = 0 - viewWidth;
		setBounds(x,130,viewWidth, viewHeight);
		setOpaque(false);
		setVisible(false);

		layoutSetup();
	}


	private void layoutSetup(){
		filterEnabled = new CustomToggleButton(true);
		filterEnabled.setBounds(0, 0, filterEnabled.getPreferredSize().width, filterEnabled.getPreferredSize().height);
		filterEnabled.setOpaque(false);
		filterEnabled.setSelected(false);

		lblTableColumnsPanelTitle = new JLabel("Table Columns",SwingConstants.CENTER);
		lblTableColumnsPanelTitle.setBounds(0, 10, getWidth(),10);
		lblTableColumnsPanelTitle.setForeground(Color.white);

		tableColumnsPanel = new JPanel();
		tableColumnsPanel.setLayout(null);
		tableColumnsPanel.setBounds(0,filterEnabled.getY()+filterEnabled.getHeight()+5,getWidth(),getHeight()/2);
		tableColumnsPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		tableColumnsPanel.setOpaque(false);

		lblLSTColumns = new JLabel("Columns");
		lblLSTColumns.setBounds(0,5,lblLSTColumns.getPreferredSize().width,10);
		lblLSTColumns.setForeground(Color.white);

		lstColumns = new JList<String>();
		scrpLstColumns  = new CustomListBoxScrollPane(lstColumns);
		scrpLstColumns.setBounds(0,lblLSTColumns.getY()+lblLSTColumns.getHeight()+5,getWidth()/3,tableColumnsPanel.getHeight()/2);

		lstDisplayedColuns = new JList<String>();
		scrplstDisplayedColuns = new CustomListBoxScrollPane(lstDisplayedColuns);
		scrplstDisplayedColuns.setBounds(getWidth()-scrpLstColumns.getWidth(),scrpLstColumns.getY(),scrpLstColumns.getWidth(),scrpLstColumns.getHeight());

		lblDisplayedColumns = new JLabel("Displayed Columns");
		lblDisplayedColumns.setBounds(scrplstDisplayedColuns.getX(),lblLSTColumns.getY(),lblDisplayedColumns.getPreferredSize().width,lblLSTColumns.getHeight());
		lblDisplayedColumns.setForeground(Color.white);

		lblColumnFiltersPanelTitle = new JLabel("Filtered Columns",SwingConstants.CENTER);
		lblColumnFiltersPanelTitle.setForeground(Color.white);
		lblColumnFiltersPanelTitle.setBounds(0,tableColumnsPanel.getY()+tableColumnsPanel.getHeight()+5,getWidth(),10);

		btnAddColumns = new CustomJButton("<html><center>Add<br>Columns</center></html>");
		btnAddColumns.setLocation(scrpLstColumns.getX()+scrpLstColumns.getWidth()+30,scrpLstColumns.getY()+scrpLstColumns.getHeight()/6);
		
		btnRemoveColumns = new CustomJButton("<html><center>Remove<br>Columns</center></html>");
		btnRemoveColumns.setLocation(btnAddColumns.getX(), btnAddColumns.getY()+btnAddColumns.getHeight()+20);
		
		ColumnFiltersPanel = new JPanel();
		ColumnFiltersPanel.setLayout(null);
		ColumnFiltersPanel.setOpaque(false);
		ColumnFiltersPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		ColumnFiltersPanel.setBounds(0,lblColumnFiltersPanelTitle.getY()+lblColumnFiltersPanelTitle.getHeight()+5,getWidth(),getHeight()-(lblColumnFiltersPanelTitle.getY()+lblColumnFiltersPanelTitle.getHeight()));

		lblFilteredColumns = new JLabel("Columns");
		lblFilteredColumns.setBounds(0,5,lblFilteredColumns.getPreferredSize().width,10);
		lblFilteredColumns.setForeground(Color.white);

		lstFilteredColumns = new JList<String>();
		lstFilteredColumns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrplstFilteredColumns = new CustomListBoxScrollPane(lstFilteredColumns);
		scrplstFilteredColumns.setBounds(0,lblFilteredColumns.getY()+lblFilteredColumns.getHeight()+5,getWidth()/3,tableColumnsPanel.getHeight()/2);

		lstFilteredColumnValues = new JList<String>();
		scrplstFilteredColumnValues = new CustomListBoxScrollPane(lstFilteredColumnValues);
		scrplstFilteredColumnValues.setBounds(getWidth()-scrplstFilteredColumns.getWidth(),scrplstFilteredColumns.getY(),scrplstFilteredColumns.getWidth(),scrplstFilteredColumns.getHeight());

		lblFilterColumnValues = new JLabel("Column Values");
		lblFilterColumnValues.setBounds(scrplstFilteredColumnValues.getX(),lblFilteredColumns.getY(),lblFilterColumnValues.getPreferredSize().width,10);
		lblFilterColumnValues.setForeground(Color.white);

		btnAddFilterValue = new CustomJButton("<html><center>Add<br>Values</center></html>");
		btnAddFilterValue.setLocation(scrplstFilteredColumns.getX()+scrplstFilteredColumns.getWidth()+30,scrplstFilteredColumns.getY()+scrplstFilteredColumns.getHeight()/6);
	
		btnRemoveFilterValue = new CustomJButton("<html><center>Remove<br>Values</center></html>");
		btnRemoveFilterValue.setLocation(btnAddFilterValue.getX(),btnAddFilterValue.getY()+btnAddFilterValue.getHeight()+20);

		//Add to panel
		add(filterEnabled);
		add(lblTableColumnsPanelTitle);
		add(tableColumnsPanel);
		tableColumnsPanel.add(lblLSTColumns);
		tableColumnsPanel.add(lblDisplayedColumns);
		tableColumnsPanel.add(scrpLstColumns);
		tableColumnsPanel.add(scrplstDisplayedColuns);
		tableColumnsPanel.add(btnAddColumns);
		tableColumnsPanel.add(btnRemoveColumns);
		add(lblColumnFiltersPanelTitle);
		add(ColumnFiltersPanel);
		ColumnFiltersPanel.add(lblFilteredColumns);
		ColumnFiltersPanel.add(scrplstFilteredColumns);
		ColumnFiltersPanel.add(lblFilterColumnValues);
		ColumnFiltersPanel.add(scrplstFilteredColumnValues);
		ColumnFiltersPanel.add(btnAddFilterValue);
		ColumnFiltersPanel.add(btnRemoveFilterValue);



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

