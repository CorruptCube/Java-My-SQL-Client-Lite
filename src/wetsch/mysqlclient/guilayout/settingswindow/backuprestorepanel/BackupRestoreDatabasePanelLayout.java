package wetsch.mysqlclient.guilayout.settingswindow.backuprestorepanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class BackupRestoreDatabasePanelLayout extends JPanel{
	
	private static final long serialVersionUID = 1L;

	private GridBagConstraints jplc = new GridBagConstraints();
	
	protected JLabel lblBinaryFileStatus; 
	protected JButton btnDatabaseActionExecute;
	protected JButton btnSelectBinaryFile;
	protected JButton btnSaveDbFileTo;
	protected JButton btnRestoreDbFrom;
	protected JRadioButton jrbBackupDatabase;
	protected JRadioButton jrbRestoreDatabase;
	protected ButtonGroup bgDatabaseAction;
	
	protected JCheckBox chbEventsFlag;
	
	protected JTextField jtfBinaryFile;
	protected JTextField jtfSaveDbFileTo;
	protected JTextField jtfRestoreDbFileFrom;
	protected JTextField jtfDbHostAddress;
	protected JTextField jtfDbUserName;
	protected JPasswordField jpfDbPassword;

	public BackupRestoreDatabasePanelLayout() {
		setLayout(new GridBagLayout());
		setupPanelLayout();
		setOpaque(true);
		setVisible(true);
	}
	
	private void setupPanelLayout(){
		loadBackupRestoreDatabasePanel();
	}
	
	private void loadBackupRestoreDatabasePanel(){
		jplc.insets = new  Insets(7,0, 0, 0);

		addComp(this, new JLabel("Backup/Restore Databases", SwingConstants.CENTER), 0, 0, 3, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);
		addComp(this, new JLabel("Binary:"), 0, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0, 0);
		lblBinaryFileStatus = new JLabel("N/A");
		addComp(this, lblBinaryFileStatus, 1, 1, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		addComp(this, new JSeparator(SwingConstants.HORIZONTAL), 0, 2, 3, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0, 0);
		addComp(this, new JLabel("Set binary:"), 0, 3, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.1, 0);
		jtfBinaryFile = new JTextField();
		jtfBinaryFile.setEnabled(false);
		addComp(this, jtfBinaryFile, 1, 3, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		btnSelectBinaryFile = new JButton("Browse");
		btnSelectBinaryFile.setEnabled(false);
		addComp(this, btnSelectBinaryFile, 2, 3, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0.2, 0);
		addComp(this, new JLabel("Host Address:"), 0, 4, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.1, 0);
		jtfDbHostAddress = new JTextField();
		addComp(this, jtfDbHostAddress, 1, 4, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);

		chbEventsFlag = new JCheckBox("--events", false);
		chbEventsFlag.setOpaque(false);
		addComp(this, chbEventsFlag, 2, 4, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0.2, 0);

		addComp(this, new JLabel("User Name:"), 0, 5, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.1, 0);
		jtfDbUserName = new JTextField();
		addComp(this, jtfDbUserName, 1, 5, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		addComp(this, new JLabel("Password:"), 0, 6, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.1, 0);
		jpfDbPassword = new JPasswordField();
		addComp(this, jpfDbPassword, 1, 6, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		addComp(this, new JSeparator(SwingConstants.HORIZONTAL), 0, 7, 3, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0, 0);
		addComp(this, new JLabel("Save DB To:"), 0, 8, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.1, 0);
		jtfSaveDbFileTo = new JTextField();
		jtfSaveDbFileTo.setEnabled(false);
		addComp(this, jtfSaveDbFileTo, 1, 8, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		btnSaveDbFileTo = new JButton("Browse");
		btnSaveDbFileTo.setEnabled(false);
		addComp(this, btnSaveDbFileTo, 2, 8, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0.2, 0);
		addComp(this, new JLabel("Restore DB From:"), 0, 9, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 0.1, 0);
		jtfRestoreDbFileFrom = new JTextField();
		jtfRestoreDbFileFrom.setEnabled(false);
		addComp(this, jtfRestoreDbFileFrom, 1, 9, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		btnRestoreDbFrom = new JButton("Browse");
		btnRestoreDbFrom.setEnabled(false);
		addComp(this, btnRestoreDbFrom, 2, 9, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		bgDatabaseAction = new ButtonGroup();
		jrbBackupDatabase = new JRadioButton("Backup Database", false);
		jrbBackupDatabase.setOpaque(false);
		jrbRestoreDatabase = new JRadioButton("Restore Database", false);
		jrbRestoreDatabase.setOpaque(false);
		bgDatabaseAction.add(jrbBackupDatabase);
		bgDatabaseAction.add(jrbRestoreDatabase);
		addComp(this, jrbBackupDatabase, 1, 10, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		addComp(this, jrbRestoreDatabase, 2, 10, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);
		btnDatabaseActionExecute = new JButton("Execute");
		addComp(this, btnDatabaseActionExecute, 1, 11, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 1);
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
