package wetsch.mysqlclient.guilayout.settingswindow.backuprestorepanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import wetsch.mysqlclient.objects.DataBaseBackupTool;

public class BackupRestoreDatabasesPanel extends BackupRestoreDatabasePanelLayout implements ActionListener{

	
	private static final long serialVersionUID = 1L;

	public BackupRestoreDatabasesPanel(){
		prepControls();
		setupActionListeners();
	}
	
	//Set the panel controls configuration.
	private void prepControls(){
		jtfBinaryFile.setEnabled(false);
		btnSelectBinaryFile.setEnabled(false);
		chbEventsFlag.setVisible(false);
	}
	
	//Adding action listeners.
	private void setupActionListeners(){
		btnSelectBinaryFile.addActionListener(this);
		btnSaveDbFileTo.addActionListener(this);
		btnRestoreDbFrom.addActionListener(this);
		btnDatabaseActionExecute.addActionListener(this);
		jrbBackupDatabase.addActionListener(this);
		jrbRestoreDatabase.addActionListener(this);
	}
	

	private String getFileDialog(String dialogAction){
		JFileChooser fc = new JFileChooser();
		int action;
		switch (dialogAction) {
		case "Open":
			fc.setDialogTitle("Select file:");
			action = fc.showOpenDialog(this);
			if(action == JFileChooser.APPROVE_OPTION){
				return fc.getSelectedFile().getAbsolutePath().toString();
			}
			return null;
		case "Save":
			fc.setDialogTitle("Save Database To:");
			action = fc.showSaveDialog(this);
			if(action == JFileChooser.APPROVE_OPTION){
				return fc.getSelectedFile().getAbsolutePath().toString();
			}
			return null;
		}
		return null;
	}
	
	
	//Listener action call methods.
	
	private void BinaryFileSelectionButton(){
		String fileName = getFileDialog("Open");
		jtfBinaryFile.setText(fileName);
	}
	
	private void setSaveDatabaseToButton(){
		String fileName = getFileDialog("Save");
		jtfSaveDbFileTo.setText(fileName);
	}
	
	private void restoreDatabaseFromButton(){
		String fileName = getFileDialog("Open");
		jtfRestoreDbFileFrom.setText(fileName);
	}
	
	private void radioButtonBackupRestoreAction(){
		if(jrbBackupDatabase.isSelected()){
			if(!DataBaseBackupTool.isDefaultBinaryLocationFound(DataBaseBackupTool.mySqlDumpBinaryFile)){
				jtfBinaryFile.setEnabled(true);
				btnSelectBinaryFile.setEnabled(true);
				lblBinaryFileStatus.setText("MySQL dump binary is missing.");
			}
			else{
				jtfBinaryFile.setEnabled(false);
				btnSelectBinaryFile.setEnabled(false);
				lblBinaryFileStatus.setText("MySQL dump binary was found.");

			}
			jtfSaveDbFileTo.setEnabled(true);
			btnSaveDbFileTo.setEnabled(true);
			jtfRestoreDbFileFrom.setEnabled(false);
			btnRestoreDbFrom.setEnabled(false);
			chbEventsFlag.setVisible(true);
		}else if(jrbRestoreDatabase.isSelected()){
			if(!DataBaseBackupTool.isDefaultBinaryLocationFound(DataBaseBackupTool.mySqlBinaryFile)){
				jtfBinaryFile.setEnabled(true);
				btnSelectBinaryFile.setEnabled(true);
				lblBinaryFileStatus.setText("MySQL binary is missing.");
			}else{
				jtfBinaryFile.setEnabled(false);
				btnSelectBinaryFile.setEnabled(false);
				lblBinaryFileStatus.setText("MySQL binary was found.");
			}
			jtfSaveDbFileTo.setEnabled(false);
			btnSaveDbFileTo.setEnabled(false);
			jtfRestoreDbFileFrom.setEnabled(true);
			btnRestoreDbFrom.setEnabled(true);
			chbEventsFlag.setVisible(false);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void databaseBackupRestoreActionExecuteButton(){
		String host = null;
		String userName = null;
		String password = null;
		String FilePath = null;
		String pathToBinary = null;
		try {
			if(!DataBaseBackupTool.isOsSupported()){
				JOptionPane.showMessageDialog(this, "Your Os is not supported at this time.");
				return;
			}
			if(jtfDbHostAddress.getText().length() != 0)
				host = jtfDbHostAddress.getText().toString();
			if(jtfDbUserName.getText().length() != 0)
				userName = jtfDbUserName.getText().toString();
			if(jpfDbPassword.getPassword().length != 0)
				password = jpfDbPassword.getText().toString();
			if(jrbBackupDatabase.isSelected() && jtfSaveDbFileTo.getText().length() != 0)
				FilePath = jtfSaveDbFileTo.getText().toString();
			if(jrbRestoreDatabase.isSelected() && jtfRestoreDbFileFrom.getText().toString().length() != 0)
				FilePath = jtfRestoreDbFileFrom.getText().toLowerCase();
			if(jtfBinaryFile.getText().length() != 0)
				pathToBinary = jtfBinaryFile.getText().toString();
		if(jrbBackupDatabase.isSelected()){
			if(!jtfSaveDbFileTo.isEnabled() || jtfSaveDbFileTo.getText().length() == 0){
				JOptionPane.showMessageDialog(this, "You must set the action to execute, and specify a file.");
				return;
			}
			DataBaseBackupTool dbBackupTool = new DataBaseBackupTool(null, userName, password, host, FilePath, pathToBinary);	
			dbBackupTool.setEventsFlag(chbEventsFlag.isSelected());
			String runtimeMessages = dbBackupTool.backupDataBase();
			if(runtimeMessages != null)
				JOptionPane.showMessageDialog(this, runtimeMessages);
			else
				JOptionPane.showMessageDialog(this, "Backup is complete.");
		}else if(jrbRestoreDatabase.isSelected()){
			if(!jtfRestoreDbFileFrom.isEnabled() || jtfRestoreDbFileFrom.getText().length() == 0){
				JOptionPane.showMessageDialog(this, "You must set the action to execute, and specify a file.");
				return;
			}
			DataBaseBackupTool dbBackupTool = new DataBaseBackupTool(null,  userName, password, host, FilePath, pathToBinary);	
			String runtimeMessages = dbBackupTool.restoreDatabase();
			if(runtimeMessages != null)
				JOptionPane.showMessageDialog(this, runtimeMessages);
			else
				JOptionPane.showMessageDialog(this, "Restore is complete.");

		}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

	}


	//implemented Listener methods
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSelectBinaryFile){
			BinaryFileSelectionButton();
		}else if(e.getSource() == btnSaveDbFileTo){
			setSaveDatabaseToButton();
		}else if(e.getSource() == btnRestoreDbFrom){
			restoreDatabaseFromButton();
		}else if(e.getSource() == btnDatabaseActionExecute){
			databaseBackupRestoreActionExecuteButton();
		}else if(e.getSource() == jrbBackupDatabase){
			radioButtonBackupRestoreAction();
		}else if(e.getSource() == jrbRestoreDatabase){
			radioButtonBackupRestoreAction();
		}	
	}

}
