package wetsch.mysqlclient.objects.customuiobjects.scrollpane;


import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;


public class CustomScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;

	public CustomScrollPane(Component c){
		super(c);
		setOpaque(false);
		getViewport().setOpaque(false);
		
		setSize(new Dimension(100,100));
	}
}
