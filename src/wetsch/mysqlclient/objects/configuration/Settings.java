package wetsch.mysqlclient.objects.configuration;

import java.awt.Color;
import java.util.HashMap;
import wetsch.mysqlclient.objects.enums.JTableID;



public class Settings {
	private boolean tableGrids = false;
	private HashMap<JTableID, Boolean> tableGrid = new HashMap<JTableID, Boolean>();
	private Color labelColor = Color.WHITE;
	private Color tableSelectedRowColor = Color.WHITE;
	private boolean useSSL = false;
	private boolean animation = false;
	
	public Settings(){
		for(JTableID v : JTableID.values())
			tableGrid.put(v, false);
	}
	
	public Color getTablSelectedRowColor(){
		return tableSelectedRowColor;
	}
	
	public void setTableSelectedRowColor(Color selectedRowColor){
		this.tableSelectedRowColor = selectedRowColor;
	}
	
	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public Color getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	public boolean isTableGrids(JTableID ID) {
		return tableGrid.get(ID);
	}

	public void setTableGrids(JTableID ID,  boolean grid) {
		tableGrid.put(ID, grid);
	}

	public boolean isTableGrids() {
		return tableGrids;
	}

	public void setTableGrids(boolean tableGrids) {
		this.tableGrids = tableGrids;
	}



}
