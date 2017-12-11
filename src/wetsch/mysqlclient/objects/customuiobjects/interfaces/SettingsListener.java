package wetsch.mysqlclient.objects.customuiobjects.interfaces;

import wetsch.mysqlclient.objects.customuiobjects.events.SettingsEvent;

public interface SettingsListener {

	void SettingStateChanged(SettingsEvent e);
}
