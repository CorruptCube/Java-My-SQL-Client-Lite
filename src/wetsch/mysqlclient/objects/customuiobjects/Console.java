package wetsch.mysqlclient.objects.customuiobjects;


public class Console{
	private static StringBuilder consoleMessages = new StringBuilder();

	

	public static String getConsoleMessages(){
		return consoleMessages.toString();
	}

	public static void appendMessage(String message){
		if(message != null || message != "")
		consoleMessages.append(message);
	}
	
}
