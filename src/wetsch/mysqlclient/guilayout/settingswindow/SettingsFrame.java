package wetsch.mysqlclient.guilayout.settingswindow;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import wetsch.mysqlclient.objects.configuration.Settings;
import wetsch.mysqlclient.objects.customuiobjects.button.CustomToggleButton;
import wetsch.mysqlclient.objects.customuiobjects.events.SettingsEvent;
import wetsch.mysqlclient.objects.customuiobjects.interfaces.SettingsListener;
import wetsch.mysqlclient.objects.enums.GlobalObjects;
import wetsch.mysqlclient.objects.enums.JTableID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SettingsFrame extends SettingsFrameLayout implements ActionListener, ChangeListener{

	private static final long serialVersionUID = 1L;
	private boolean settingsLoaded = false;
	
	private Set<SettingsListener> listeners = new HashSet<SettingsListener>();

	private final Settings settings = (Settings) GlobalObjects.settings.get();

	public SettingsFrame(boolean visable) {
		super(visable);
		loadSettings();
		setupActionListeners();
	}
	
	public SettingsFrame(boolean visable, SettingsListener listener) {
		super(visable);
		listeners.add(listener);
		loadSettings();
		setupActionListeners();
		settingsLoaded = true;
	}
	
	private void setupActionListeners(){
		btnApplyChanges.addActionListener(this);
		btnClose.addActionListener(this);
		tbtnAnimations.addActionListener(this);
		tbtnTableGrids.addActionListener(this);
		btnLabelColor.addActionListener(this);
		btnTableSelectedRowColor.addActionListener(this);
		for(Map.Entry<JTableID, CustomToggleButton> v : jTableGridToggleButtons.entrySet())
			v.getValue().addItemListener(new JTableGridControlsActionListener(v.getKey()));
		tabs.addChangeListener(this);
	}
	
	private void loadSettings(){
		tbtnAnimations.setSelected(settings.isAnimation());
		tbtnTableGrids.setSelected(settings.isTableGrids());
			for(Map.Entry<JTableID, CustomToggleButton> v : jTableGridToggleButtons.entrySet()){
				if(settings.isTableGrids())
					v.getValue().setEnabled(true);
				v.getValue().setSelected(settings.isTableGrids(v.getKey()));
			}
	}
	
	private void callSettingsChangedListenerLabelColor(){
		Color labelColor = JColorChooser.showDialog(null, "Select Label Color", Color.WHITE);
		settings.setLabelColor(labelColor);
		if(settingsLoaded && listeners.size() > 0){
			for(SettingsListener listener : listeners)
				listener.SettingStateChanged(new SettingsEvent(null, SettingsEvent.labelColor, labelColor));;
		}
	}
		
	private void callSettingsChangedListenerSelectedRowColor(){
		Color selectedRowColor = JColorChooser.showDialog(null, "Select Label Color", Color.WHITE);
		settings.setTableSelectedRowColor(selectedRowColor);
		if(settingsLoaded && listeners.size() > 0){
			for(SettingsListener listener : listeners)
				listener.SettingStateChanged(new SettingsEvent(null, SettingsEvent.tableSelectedRowColor, selectedRowColor));;
		}
	}
	
	private void saveSettings(){
		try{
		File homePath = (File) GlobalObjects.homeFolder.get();  
		File file = new File(homePath.getPath() + "/java-sql-client-settings.json");
	       if(!homePath.exists()){
	            System.out.println("Create home folder.");
	           	homePath.mkdir();
	        }
	        if(!file.exists())
	        	file.createNewFile();
		Gson s = new GsonBuilder().create();	
        FileWriter fw = new FileWriter(file);
        s.toJson(settings, fw);
        fw.close();
        JOptionPane.showMessageDialog(this, "Settings saved successfuly");
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	

	
	
	public void addSettingsStateChangedListener(SettingsListener listener){
		listeners.add(listener);
	}
	
	public void removeSettingsStateChangedListener(SettingsListener listener){
		listeners.remove(listener);
	}
	
	public void removeAllListeners(){
		listeners.clear();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnClose){
			dispose();
		}else if(e.getSource() == btnApplyChanges){
			saveSettings();
		}		if(e.getSource() == tbtnAnimations){
			settings.setAnimation(tbtnAnimations.isSelected());
		}else if(e.getSource() == tbtnTableGrids){
			settings.setTableGrids(tbtnTableGrids.isSelected());
			for(Map.Entry<JTableID, CustomToggleButton> v : jTableGridToggleButtons.entrySet()){
				if(tbtnTableGrids.isSelected()){
				v.getValue().setEnabled(true);
				}else{
					v.getValue().setEnabled(false);
					v.getValue().setSelected(false);
				}
			}
		}else if(e.getSource() == btnLabelColor){
			callSettingsChangedListenerLabelColor();
		}else if(e.getSource() == btnTableSelectedRowColor){
			callSettingsChangedListenerSelectedRowColor();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == tabs){
			if(tabs.getSelectedComponent() instanceof ConsolePanel){
				ConsolePanel p = (ConsolePanel) tabs.getSelectedComponent();
				p.updateConsole();
			}
		}
	}

private class JTableGridControlsActionListener implements ItemListener{
	private JTableID ID;
	public JTableGridControlsActionListener(JTableID ID) {
		this.ID = ID;
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		JToggleButton tb = (JToggleButton) e.getSource();
		settings.setTableGrids(ID, tb.isSelected());
		if(settingsLoaded && listeners.size() > 0){
			for(SettingsListener listener : listeners)
				listener.SettingStateChanged(new SettingsEvent(ID, SettingsEvent.tableGridStateChanged, tb.isSelected()));;
		}
	}
}
}