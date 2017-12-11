package wetsch.mysqlclient.guilayout.settingswindow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import wetsch.mysqlclient.objects.customuiobjects.Console;

public class ConsolePanel extends JPanel{
	GridBagConstraints jplc = new GridBagConstraints();
	private JTextArea jtaMessages;
	private JScrollPane scrMessages;
	private static final long serialVersionUID = 1L;

	public ConsolePanel() {
		
		setOpaque(false);
		setLayout(new GridBagLayout());
		LayoutSetup();
		setVisible(true);
	}
	
	
	
	private void LayoutSetup(){
		jtaMessages = new JTextArea();
		scrMessages = new JScrollPane(jtaMessages);

		jtaMessages.setLineWrap(true);
		jtaMessages.setEditable(false);
		jtaMessages.setText(Console.getConsoleMessages());

		addComp(this, scrMessages, 0, 0, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, 1, 1);

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
	
	public void updateConsole(){
		jtaMessages.setText(Console.getConsoleMessages());
		
	}
}
