package wetsch.mysqlclient.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.naming.directory.AttributeInUseException;
import javax.naming.directory.InvalidAttributesException;

import wetsch.mysqlclient.objects.customuiobjects.Console;

public class DataBaseBackupTool {
	public static final int mySqlDumpBinaryFile = 0;//The MySQL dump binary file.
	public static final int mySqlBinaryFile = 1;//The MySQL binary file.
	
	private boolean eventsFlag = false;//Set the events flag to be appends to the command during backup.
	private String pathToBinary = null;//The absolute path to the binary file.
	private String fileName = null;//The filename to save or read.
	private String host = null;//The host address or domain name.
	private String userName = null;//The username to connect to the MySQL database.
	private String password = null;//The password to connect to the MySQL database.
	private String[] schemaNames = null;//The schemas to backup.
	private final static String[] supportedOS = new String[]{"Linux"};//The operating systems that are supported.
	private final String[] invalidStringConstructorArgs = new String[]{null, ""};//Invalid string attributes for the constructor.

	/**
	 * Constructor to set the parameters for the SQL connection.
	  * @param schemaNames String array of schema names to backup. (set null for all schemas.)
	  * @param args The following arguments below are needed.  Make sure to put them in the order as they are 
	  * listed below.
	  * <ul>
	 *   <li> userName The user-name to login to Mysql.
	 *   <li> password The password to login to Mysql.
	 *   <li> host The host name or IP-address of the Mysql server. 
	 *   <li> fileName The absolute path of the file to save to or restore from.
	 *   <li> pathToBinary The absolute path to SQL binary. (Ignored if default path is found.) Set null to use default path to binary  
	 *   Only do this if you know the default binary is on the system and in the right location.
	 * </ul>
	 * @throws InvalidAttributesException 
	 * Thrown if there are to many string arguments after the string array, or user name, password, host or filename parameters are null or an empty string.  
	 * @throws AttributeInUseException
	 * Thrown if default binary is not found, and binary file attribute is not set.
	 * @throws FileNotFoundException
	 * Thrown if the path to the binary is set, but the file does not exist.
	 */
	public DataBaseBackupTool(String[] schemaNames, String... args) throws InvalidAttributesException, AttributeInUseException, FileNotFoundException {
		if(!getOS().equals("Linux"))
			throw new UnsupportedOperationException("OS not supported.");
		if(args.length != 5)
			throw new InvalidAttributesException("The number of arguments in the constructor is invalid");
		if(!isDefaultBinaryLocationFound(mySqlBinaryFile) && !isDefaultBinaryLocationFound(mySqlDumpBinaryFile) && Arrays.asList(invalidStringConstructorArgs).contains(args[4]))
			throw new AttributeInUseException("You must set the path to the binary.");
		if(args[4] != null && !new File(args[4]).exists())
			throw new FileNotFoundException("The path to the binary does not exist");
		for(int c = 0; c < args.length; c++){
			if(c < args.length - 1 && Arrays.asList(invalidStringConstructorArgs).contains(args[c]))
				throw new InvalidAttributesException("One or more of the parameters is missing.");
		}
		this.schemaNames = schemaNames;
		this.userName = args[0];
		this.password = args[1];
		this.host = args[2];
		this.fileName = args[3];
		this.pathToBinary = args[4];
	}

	/**
	 * This method uses your systems runtime environment to access mysqldump.  
	 * The method will check your OS for the needed executable and run the  
	 * necessary command to dump your MySQL databases into a file. 
	 * The method will return a String containing any messages 
	 * if the error stream from the runtime environment contains 
	 * any messages, otherwise, the method will return null.  
	 *@return String
	 * @throws Exception Thrown if mysqldump binary is not found, or 
	 * something goes wrong backing up your MySQL databases.
	 */
	public String backupDataBase() throws Exception{
		StringBuilder command = null;
		String line = null;
		StringBuilder messages = new StringBuilder();

		if(!new File("/usr/bin/mysqldump").exists())
			throw new FileNotFoundException("The file /usr/bin/mysqldump could not be found.");
		if(getOS().equals("Linux") && isDefaultBinaryLocationFound(mySqlDumpBinaryFile))
			command = new StringBuilder("/usr/bin/mysqldump -r " + fileName + " --host=" + host +" --user=" + userName + " --password="+ password);
		else if(getOS().equals("Linux") && !isDefaultBinaryLocationFound(mySqlDumpBinaryFile))
			command = new StringBuilder(pathToBinary + " -r " + fileName + " --host=" + host +" --user=" + userName + " --password="+ password);
		if(schemaNames != null){
			command.append(" --databases");
			for(String s : schemaNames)
				command.append(" " + s);
		}else
			command.append(" --all-databases");
		if(eventsFlag)
			command.append(" --events");

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec((command.toString()));
		pr.waitFor();
		BufferedReader brError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		if(brError.ready()){
			while((line = brError.readLine()) != null)
				messages.append(line + "\n");
			
		}
		brError.close();
		pr.destroy();
		Console.appendMessage(messages.toString());
		Console.appendMessage("Backup file created at " + fileName + "\n");
		if(messages.toString().length() != 0)
			return messages.toString();
		else
			return null;
	}
	
	/**
	 * This method uses your systems runtime environment to access mysql.  
	 * The method will check your OS for the needed executable and run the  
	 * necessary command to restore your MySQL databases
	 * The method will return a String containing any messages 
	 * if the error stream from the runtime environment contains 
	 * any messages, otherwise, the method will return null.   
	 * @throws IOException
	 * @throws InterruptedException
	 *@return String
	 * Thrown if MySQL binary is not found, or 
	 * something goes wrong restoring your MySQL databases.
	 */
	public String restoreDatabase() throws IOException, InterruptedException{
		 String[] command = null;
		 String line = null;
		StringBuilder messages = new StringBuilder();
		if(!new File("/usr/bin/mysqldump").exists())
			throw new FileNotFoundException("The file /usr/bin/mysqldump could not be found.");
		if(getOS().equals("Linux") && isDefaultBinaryLocationFound(mySqlBinaryFile))
			command = new String[]{"mysql", "--user=" + userName, "--password=" + password, "-e", " source " + fileName};
		else if(getOS().equals("Linux") && !isDefaultBinaryLocationFound(mySqlBinaryFile))
			command = new String[]{pathToBinary, "--user=" + userName, "--password=" + password, "-e", " source " + fileName};
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(command);
		pr.waitFor();
		BufferedReader brError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		if(brError.ready()){
			while((line = brError.readLine()) != null)
				messages.append(line + "\n");
		}
		brError.close();
		pr.destroy();
		if(messages.toString().length() != 0){
			Console.appendMessage(messages.toString());
			return messages.toString();
		}else{
			Console.appendMessage("Databases restored.\n");
			return null;
		}

	}
	
	
	/**
	 * Returns true if the installed OS is supported otherwise returns false.
	 * @return boolean
	 */
	public static boolean isOsSupported(){
		String os = System.getProperty("os.name");
		if(Arrays.asList(supportedOS).contains(os))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns true if the default path of the mysql binaries are found otherwise returns false.
	*@param binary The integer that represents the binary to check.
	 * @return boolean
	 */
	public static boolean isDefaultBinaryLocationFound(int binary){
		switch (binary) {
		case mySqlDumpBinaryFile:
			if(getOS().equals("Linux") && new File("/usr/bin/mysqldump").exists())
				return true;
			else
			return false;
		case mySqlBinaryFile:
			if(getOS().equals("Linux") && new File("/usr/bin/mysql").exists())
				return true;
			else
			return false;
		}
		return false;
	}
	
	//Returns the installed operating system type.
	public static String getOS(){
		return System.getProperty("os.name");
	}
	
	/**
	 * Sets to true to append events flag to the command, otherwise, set to false.
	 */
	public void setEventsFlag(boolean eventsFlag){
		this.eventsFlag = eventsFlag;
	}
	/**
	 * Returns true if the events flag is set to be appended to the command, otherwise, returns false.
	 *@return boolean
	 */
	public boolean getEventsFlag(){
		return eventsFlag;
	}
}
