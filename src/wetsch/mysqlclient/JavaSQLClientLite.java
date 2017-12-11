package wetsch.mysqlclient;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import wetsch.mysqlclient.guilayout.LoginWindow;

public class JavaSQLClientLite {

	public static void main(String[] args) {
		if(System.getProperty("os.name").equals("Mac OS X"))
			nativeAppleMenuBar();

		new LoginWindow();
	}
	
	private static void nativeAppleMenuBar(){
        try {
   		 System.setProperty("apple.laf.useScreenMenuBar", "true");
   	    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Java Mysql Client");
   	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}


}
