package wetsch.mysqlclient.objects.customuiobjects.button;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class CustomToggleButton extends JToggleButton{

	private static final long serialVersionUID = 1L;

	public CustomToggleButton(boolean enabled) {
		setIcon(new ImageIcon(getClass().getResource("/button-icons/togglebutton-off.png")));
		setSelectedIcon(new ImageIcon(getClass().getResource("/button-icons/togglebutton-on.png")));
		setBorderPainted(false);
		setContentAreaFilled(false);
		setEnabled(enabled);

	}

}
