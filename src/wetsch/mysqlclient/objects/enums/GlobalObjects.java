package wetsch.mysqlclient.objects.enums;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import wetsch.mysqlclient.objects.configuration.Settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public enum GlobalObjects {

	settings(getSettings()),
	homeFolder(getHomeFolder());
	
	private final Object object;
	
	GlobalObjects(Object obj){
		object = obj;
	}
	
	public Object get(){
		return object;
	}
	
	private static File getHomeFolder(){
		String homeDir = System.getProperty("user.home");
		return new File(homeDir + "/J-SQL-Lite"); 
		

	}
	
	private static Object getSettings(){
		try {
		 File file = new File(getHomeFolder().getPath() + "/java-sql-client-settings.json");
		 if(!file.exists())
			 return new Settings();
		 BufferedReader br;
			br = new BufferedReader(new FileReader(file));
		 Gson s = new GsonBuilder().create();
		 return s.fromJson(br, Settings.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Settings();
		
	}
	
	
}

		
