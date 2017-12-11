package wetsch.mysqlclient.objects.enums;

public enum ColumnKeyType {
	primary("PRI"),
	forign(""),
	none("");
	
	private final String type;
	
	ColumnKeyType(String name){
		this.type = name;
	}
	
	public String getName(){
		return type;
	}

	public static void main(String[] args) {
	}
	
}
