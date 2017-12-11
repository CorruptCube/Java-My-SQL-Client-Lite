package wetsch.mysqlclient.objects.customuiobjects.events;

public class SettingsEvent {

	public static final int gridStateChanged = 0;
	private Object componentID;
	private Object settingValue;
	private int SettingChanged;
	
	public static final int tableGridStateChanged = 0;
	public static final int labelColor = 1;
	public static final int tableSelectedRowColor = 2;
	public SettingsEvent(Object componentID , int SettingChanged, Object value) {
		this.componentID = componentID;
		this.SettingChanged = SettingChanged;
		this.settingValue = value;
		}
	
	public int getSettingStateChanged(){
		return SettingChanged;
	}
	
	public Object getComponentID(){
		return componentID;
	}
	
	public Object getValue(){
		return settingValue;
	}
	
	

}
