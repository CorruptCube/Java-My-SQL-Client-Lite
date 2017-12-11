package wetsch.mysqlclient.guilayout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import wetsch.mysqlclient.guilayout.mainwindowpane.MainFrame;
import wetsch.mysqlclient.objects.database.Database;
import wetsch.mysqlclient.objects.database.SqlConnection;
import wetsch.mysqlclient.guilayout.settingswindow.SettingsFrame;



public class LoginWindow extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	private SqlConnection sqlCon;
	private GridBagConstraints jplc;
	
	private JCheckBox useSSLConnection = new JCheckBox("Enable SSL/TSL");
	
	private JPanel jpMainPanel;
	
	private  JLabel lblServer = new JLabel("Server:", SwingConstants.RIGHT);
	private  JLabel lblUserName = new JLabel("UserName:", SwingConstants.RIGHT);
	private  JLabel lblPassword = new JLabel("Password:", SwingConstants.RIGHT);
	private  JLabel lblDatabaseName = new JLabel("Database:", SwingConstants.RIGHT);

	private  JTextField jtfServer = new JTextField(30);
	private  JTextField jtfUserName = new JTextField(30);
	private JTextField jtfDatabaseName = new JTextField(30);

	private  JPasswordField jpfPassword = new JPasswordField(30);
	
	private  JButton btnLogin = new JButton("Login",new ImageIcon(getClass().getResource("/button-icons/Check_mark.png")));
	private  JButton btnExit = new JButton("Exit");
	private JButton btnSettings = new JButton("Settings");

	public LoginWindow(){
			super("Login to MySQL");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setResizable(false);
			setSize(300,300);
            setLocationRelativeTo(null);
            getRootPane().setDefaultButton(btnLogin);
            layoutSetup();
			setVisible(true);

	}

	private void layoutSetup() {
		jplc = new GridBagConstraints();
		jpMainPanel = new JPanel(new GridBagLayout());
		
		addComp(jpMainPanel, lblServer,1 , 1, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, 0.3, 0);

		jplc.insets = new Insets(0, 0, 0, 50);
		addComp(jpMainPanel, jtfServer,2 , 1, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);

		jplc.insets = new Insets(20, 0, 0, 0);
		addComp(jpMainPanel, lblUserName, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0.3, 0);

		jplc.insets = new Insets(20, 0, 0, 50);
		addComp(jpMainPanel, jtfUserName,2 , 2, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);

		jplc.insets = new Insets(20, 0, 0, 0);
		addComp(jpMainPanel, lblPassword, 1, 3, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0.3, 0);
		
		jplc.insets = new Insets(20, 0, 0, 50);
		addComp(jpMainPanel, jpfPassword,2 , 3, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);

		jplc.insets = new Insets(20, 0, 0, 0);
		addComp(jpMainPanel, lblDatabaseName, 1, 4, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0.3, 0);

		jplc.insets = new Insets(20, 0, 0, 50);
		addComp(jpMainPanel, jtfDatabaseName,2 , 4, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0.7, 0);
		
		
		jplc.insets = new Insets(20, 0, 0, 0);
		addComp(jpMainPanel, useSSLConnection,2 , 5, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		jplc.insets = new Insets(20, 5, 0, 0);
		addComp(jpMainPanel, btnLogin,1 , 6, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		addComp(jpMainPanel, btnExit,2 , 6, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 0);

		addComp(jpMainPanel, btnSettings,3 , 6, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 0, 1);

		add(jpMainPanel);
		
		btnLogin.addActionListener(this);
		btnExit.addActionListener(this);
		btnSettings.addActionListener(this);
	}
	
	//Login button action.
	private void loginButtonAction(){
		String userName;
		String password;
		String schemaName;
		String serverAddress;
		boolean ssl = useSSLConnection.isSelected();
		serverAddress = jtfServer.getText().toString();
		userName = jtfUserName.getText().toString();
		password = new String(jpfPassword.getPassword());
		schemaName = jtfDatabaseName.getText().toString();
		try{
			if(schemaName.isEmpty())
				JOptionPane.showMessageDialog(this, "For better security, you must provide a schema name.");
			else{
				sqlCon = new SqlConnection(serverAddress, userName, password, schemaName, ssl);
				if(sqlCon.openConnection()){
					String query = "SHOW DATABASES";
					LinkedHashMap<String, Database> schemaMap = new LinkedHashMap<String, Database>();
					for(String[] r : sqlCon.getFromSelectStatement(query)){
						for(String s : r)
							schemaMap.put(s, new Database(sqlCon, s));
				}
					new MainFrame(serverAddress ,schemaName, schemaMap);
					dispose();
				}
			}
			}catch(Exception ex){
			JOptionPane.showMessageDialog(this, ex.getMessage()
					+ "\nContact your network DBA for assistance.");
			ex.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnLogin){
			loginButtonAction();
		}else if(e.getSource() == btnExit){
			System.exit(0);
		}else if(e.getSource() == btnSettings){
			new SettingsFrame(true);	
			}
	}
	
	/*
	 * Adds the objects to the panel.
	 * uses Grid bag layout constraints 
	 * that are passed in as parameters.
	 */
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

