package wetsch.mysqlclient.objects.enums;

import java.util.HashMap;
import java.util.LinkedHashMap;

public enum DataType {

	TINYINT("TINYINT(M)"),
	SMALLINT("SMALLINT(M)"),
	MEDIUMINT("MEDIUMINT(M)"),
	INT("INT(M)"),
	BIGINT("BIGINT(M)"),
	FLOAT("FLOAT(p)"),
	FLOATMD("FLOAT(M,D)"),
	DOUBLEMD("DOUBLE(M,D)"),
	DECIMAL("DECIMAL(M,D)"),
	BIT("BIT(M)"),
	CHAR("CHAR(M)"),
	VARCHAR("VARCHAR(M)"),
	TINYTEXT2("TINYTEXT"),
	TEXT("TEXT"),
	MEDIUMTEXT("MEDIUMTEXT"),
	LONGTEXT("LONGTEXT"),
	BINARY("BINARY(M)"),
	VARBINARY("VARBINARY(M)"),
	TINYBLOB("TINYBLOB"),
	BLOB("BLOB"),
	MEDIUMBLOB("MEDIUMBLOB"),
	LONGBLOB("LONGBLOB"),
	ENUM("ENUM(\"A1\",\"A2\",...)"),
	SET("SET(\"A1\",\"A2\",...)"),
	DATE("DATE"),
	DATETIME("DATETIME"),
	TIME("TIME"),
	TIMESTAMP("TIMESTAMP"),
	YEAR("YEAR");
	
	

	private final String dtName;
	private static final HashMap<String, DataType> rLookup = new LinkedHashMap<String, DataType>();
	
	private DataType(String dtName) {
		this.dtName = dtName;
	}
	

	
	public String getDataTypeName(){
		return dtName;
	}
	
	
	static{
		for(DataType dt : DataType.values()){
			rLookup.put(dt.getDataTypeName().split("\\(")[0].toLowerCase(), dt);

		}
	}

	public static DataType get(String name){
		return rLookup.get(name.split("\\(")[0]);
	}

	
	
	

}
