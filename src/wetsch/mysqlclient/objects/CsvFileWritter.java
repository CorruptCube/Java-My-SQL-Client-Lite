package wetsch.mysqlclient.objects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.naming.directory.InvalidAttributesException;

/*
 *Last modified 2/8/2016
 */

/**
 * This class writes data to a CSV file.  
 * The data is red in though a multidimensional string array.  
 * The data needs to be organized in a table format of rows and columns.
 * @author kevin
 *
 */
public class CsvFileWritter {

	private ResultSet data = null;//Data to write to CSV file.
	private String delimiter = null;//CSV delimiter.
	private String[] invalidStringValues = null;//Invalid string values.
	
	/**
	 * This constructor takes in the ResultSet holding the data, 
	 * and the delimiter to be used to generate the CSV file.
	 * @param tableData Data to be used.
	 * @param delimiter The delimiter to separate the data.
	 * @throws InvalidAttributesException Thrown if the data array or delimiter are equal to null or invalid.
	 * @throws SQLException 
	 */
	public CsvFileWritter(ResultSet tableData, String delimiter) throws InvalidAttributesException, SQLException {
		invalidStringValues = new String[]{null, ""};
		if(tableData == null)
			throw new InvalidAttributesException("The data array can not be null;");
		if(Arrays.asList(invalidStringValues).contains(delimiter))
			throw new InvalidAttributesException("The delimiter is invalid.");
		this.data = tableData;
		this.delimiter = delimiter;
		data.beforeFirst();
	}

	/**
	 * Sets the delimiter that will be used to separate the data.<br>
	 * The following examples can be used as delimiters.<br>
	 * <li>|
	 * 	<li>,<br>
	 * Note that a , is not a good delimiter in the following example.  
	 *For an entry ("John,Smith") the , does not work well in this example.  Using a 
	 * | would be a better delimiter.
	 * 
	 * @param delimiter
	 * @throws InvalidAttributesException
	 */
	public void setDelimiter(String delimiter) throws InvalidAttributesException{
		if(Arrays.asList(invalidStringValues).contains(delimiter))
			throw new InvalidAttributesException("The delimiter is invalid.");
		this.delimiter = delimiter;
	}
	
	/**
	 * Returns the delimiter that is used to separate the data.
	 * @return String
	 */
	public String getDelimiter(){
		return delimiter;
	}
	
	/**
	 * Returns the ResultSet that is to be used to generate the CSV file.
	 * @return
	 * String[][]
	 */
	public ResultSet getData(){
		return data;
	}
	
	/**
	 * Sets the data that will be used to generate the CSV file.
	 * @param data Data to store in the CSV file.
	 * @throws InvalidAttributesException Throws exception if parameter is equal to null.
	 */
	public void setData(ResultSet data) throws InvalidAttributesException{
		if(data == null)
				throw new InvalidAttributesException("The data array can not be null;");
		this.data = data;
	}
	
	/**
	 * Writes the data passed in from the ResultSet to a CSV file.  
	 * The method will throw an exception if the ResultSet 
	 *  equals null, the file name is improperly formated, or 
	 *  the delimiter is null or invalid.
	 * @param fileName The absolute path of the file name.
	 * @throws InvalidAttributesException Thrown if file name is null or an empty string.
	 * @throws IOException Thrown if the file can not be written.
	 * @throws SQLException 
	 */
	public void writeCsvFile(String fileName) throws InvalidAttributesException, IOException, SQLException{
		if(Arrays.asList(invalidStringValues).contains(fileName) || Arrays.asList(invalidStringValues).contains(delimiter))
			throw new InvalidAttributesException("The filename or delimiter are invalid.");
		if(data == null)
			throw new InvalidAttributesException("The data array can not be null");
		BufferedWriter  bw = new BufferedWriter(new FileWriter(fileName, true));
		for (int c = 0; c < data.getMetaData().getColumnCount(); c++){
			if(c < data.getMetaData().getColumnCount()-1)
				 bw.write(data.getMetaData().getColumnName(c+1) + delimiter);
			else
				 bw.write(data.getMetaData().getColumnName(c+1) + "\n");
		}
		
		while(data.next()){
			for(int c = 0; c < data.getMetaData().getColumnCount(); c++){
				if(c < data.getMetaData().getColumnCount()-1)
					 bw.write(data.getString(c+1).toString() + delimiter);
				else
					 bw.write(data.getString(c+1).toString() + "\n");
			}
			bw.flush();
		}
	    bw.close();
	}
}