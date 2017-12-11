package wetsch.mysqlclient.objects.customuiobjects.scrolpane;


import java.awt.Color;

import javax.swing.JList;
import javax.swing.JScrollPane;

public class CustomListBoxScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;

	public CustomListBoxScrollPane(JList<String> list){
		super(list);
		setOpaque(false);
		getViewport().setOpaque(false);
		list.setBackground(Color.black);
		list.setForeground(Color.white);


	}
}
