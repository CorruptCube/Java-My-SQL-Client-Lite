package wetsch.mysqlclient.objects.enums;

public enum DataTypeAttribiutes {
	
	Numeric(new String[]{"AUTO_INCREMENT", "ZEROFILL", "UNSIGNED", "SERIAL DEFAULT VALUE"}),
	String(new String[]{"BINARY", "CHARACTER SET"}),
	Time_Stamp(new String[]{"DEFAULT CURRENT_TIMESTAMP", "ON UPDATE CURRENT_TIMESTAMP"});
	
	private final String[] dataTypeAttributes;

	private DataTypeAttribiutes(String[] dataTypeAttributes) {
		this.dataTypeAttributes = dataTypeAttributes;
	}
	
	public String[] getAttributes(){
		return dataTypeAttributes;
	}
}
