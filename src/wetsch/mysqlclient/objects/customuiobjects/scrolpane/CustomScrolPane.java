package wetsch.mysqlclient.objects.customuiobjects.scrolpane;


import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;


public class CustomScrolPane extends JScrollPane {
	private static final long serialVersionUID = 1L;

	public CustomScrolPane(Component c){
		super(c);
		setOpaque(false);
		getViewport().setOpaque(false);
		
		setSize(new Dimension(100,100));
	}
}
