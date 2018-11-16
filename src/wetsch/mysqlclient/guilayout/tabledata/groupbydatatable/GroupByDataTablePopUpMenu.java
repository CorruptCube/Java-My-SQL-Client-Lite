package wetsch.mysqlclient.guilayout.tabledata.groupbydatatable;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Pop up menu for the Group by column data table.
 * @author kevin
 *
 */
public class GroupByDataTablePopUpMenu extends JPopupMenu{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//JMenuItems
		public JMenuItem jmiCopy = new JMenuItem("Copy");
		
		public GroupByDataTablePopUpMenu(){
			setupPopUpMenu();
		}

		private void setupPopUpMenu(){
			add(jmiCopy);
		}
}
