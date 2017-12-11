package wetsch.mysqlclient.guilayout.settingswindow;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import wetsch.mysqlclient.guilayout.settingswindow.backuprestorepanel.BackupRestoreDatabasesPanel;
import wetsch.mysqlclient.objects.customuiobjects.button.CustomJButton;
import wetsch.mysqlclient.objects.customuiobjects.button.CustomToggleButton;
import wetsch.mysqlclient.objects.customuiobjects.jtabbedpane.CustomJTabbedPane;
import wetsch.mysqlclient.objects.customuiobjects.scrolpane.CustomScrolPane;
import wetsch.mysqlclient.objects.enums.JTableID;

public abstract class SettingsFrameLayout extends JFrame {
	private static final long serialVersionUID = 1L;

	private Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
//	private int viewableWidth;;
	private int viewableHeight;
	
	private GridBagConstraints jplc = new GridBagConstraints();
	
	protected CustomJTabbedPane tabs;
	
	private CustomScrolPane jspLookAndFeel;
	
	private JPanel jpLookAndFeel = new JPanel(new GridBagLayout());
	private JPanel jpAbout = new JPanel();
	
	private JLabel lblAnimationsWarning = new JLabel();
	
	protected CustomToggleButton tbtnAnimations;
	protected CustomToggleButton tbtnTableGrids;
	protected LinkedHashMap<JTableID, CustomToggleButton> jTableGridToggleButtons;
	protected CustomJButton btnApplyChanges;
	protected CustomJButton btnClose;
	protected JButton btnLabelColor;
	protected JButton btnTableSelectedRowColor;
	
	public SettingsFrameLayout(boolean visable) {
		super("Settings");
		setResizable(false);
		setSize(screenSize.width/2, screenSize.height/2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(visable);


		//viewableWidth = getContentPane().getWidth();
		viewableHeight = getContentPane().getHeight();
		layoutSetup();
		
	}
	
	private void layoutSetup(){
		tabs = new CustomJTabbedPane();
		tabs.setForeground(Color.white);
		jpLookAndFeel.setOpaque(true);

		jspLookAndFeel = new CustomScrolPane(jpLookAndFeel);

		JLabel l1 = new JLabel("Animations");
		addComp(jpLookAndFeel, l1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 0.7, 0);

		tbtnAnimations = new CustomToggleButton(true);
		addComp(jpLookAndFeel, tbtnAnimations, 2, jplc.gridy, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0.3, 0);

		addComp(jpLookAndFeel, new JSeparator(), 1, ++jplc.gridy, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		

		lblAnimationsWarning.setText(getAnimationsWarningText());
		addComp(jpLookAndFeel,lblAnimationsWarning, 1, ++jplc.gridy, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);

		addComp(jpLookAndFeel, new JSeparator(), 1, ++jplc.gridy, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0.0);
		addComp(jpLookAndFeel, new JLabel("Table Grids"), 1, ++jplc.gridy, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		
		tbtnTableGrids = new CustomToggleButton(true);
		addComp(jpLookAndFeel, tbtnTableGrids, 2, jplc.gridy, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NORTHEAST, 0.3, 0);
		
		addComp(jpLookAndFeel, new JSeparator(), 1, ++jplc.gridy, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);
		
		placeJTableGridControls();
		placeLookAndFeelColorControls();
		
		addComp(jpLookAndFeel, new JSeparator(), 1, ++jplc.gridy, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 1);
		LoadAboutPanelContent();


		btnApplyChanges =  new CustomJButton("Apply Changes");
		btnApplyChanges.setBounds(10,viewableHeight-110,100,20);
		

		btnClose = new CustomJButton("Close");
		btnClose.setBounds(10,viewableHeight-60,100,20);
		add(btnClose);
		add(btnApplyChanges);
		add(tabs);

		tabs.addTab("Look and feel", jspLookAndFeel);
		tabs.addTab("Backup/Restore DB", new BackupRestoreDatabasesPanel());
		tabs.add("Console", new ConsolePanel());
		tabs.addTab("About", jpAbout);

	}
	
	private void placeJTableGridControls(){
		jTableGridToggleButtons = new LinkedHashMap<JTableID, CustomToggleButton>();
		for(JTableID v : JTableID.values())
			jTableGridToggleButtons.put(v, new CustomToggleButton(false));

		for(JTableID v : jTableGridToggleButtons.keySet()){
			addComp(jpLookAndFeel, new JLabel(v.getTableName()), 1, ++jplc.gridy, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
			addComp(jpLookAndFeel, jTableGridToggleButtons.get(v), 2, jplc.gridy, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.3, 0);
			jplc.insets = new  Insets(20,0, 0, 0);
		}
		jplc.insets = new  Insets(0,0, 0, 0);

	}
	
	private void placeLookAndFeelColorControls(){
		addComp(jpLookAndFeel, new JSeparator(), 1, ++jplc.gridy, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);
		
		addComp(jpLookAndFeel, new JLabel("Colors"), 1, ++jplc.gridy, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new  Insets(10,0, 0, 0);
		addComp(jpLookAndFeel, new JLabel("Label Text:"), 1, ++jplc.gridy, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 0, 0);
		
		btnLabelColor = new JButton("Set Color");
		addComp(jpLookAndFeel, btnLabelColor,2 , jplc.gridy, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, 0, 0);
		
		addComp(jpLookAndFeel, new JLabel("Selected Row::"), 1, ++jplc.gridy, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, 0, 0);

		btnTableSelectedRowColor = new JButton("Set Color");
		addComp(jpLookAndFeel, btnTableSelectedRowColor,2 , jplc.gridy, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, 0, 0);
	}
	
	
	private void LoadAboutPanelContent() {
		jpAbout.setLayout(new GridLayout(1,1));
		jpAbout.setOpaque(true);

		try {
			URL url = getClass().getResource("/about-html/about.html");
			JEditorPane webView = new JEditorPane(url);
			webView.setEditable(false);
			webView.setOpaque(false);
			jpAbout.add(webView);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String getAnimationsWarningText(){
		int width = 480;
		StringBuilder s = new StringBuilder("<html>");
		s.append("<body width=\"" + width + "\"");
		s.append("<center>Warrning:</center><br>");
		s.append("<p>Using animations on older systems may result in performance issues.  "
				+ "It is recommended that this feature be turned off on older systems.</p>");
		s.append("</body>");
		s.append("</html>");
		
		return s.toString();
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
