package Pruning.Quantiles;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Settings {
	

	/********************** variables based on test collection *******************************/ 

	public static String docNameField  ;//"docid";
	public static String content ; 
	public static String datefield; // 
	public static long dateinit; // 
	

	public static String machine ; // "/home/pehlivanz/";//
	public static String noPruningIndex ;


	
	
	public static void InstallSettings(String path) throws IOException
	{
		Properties configFile = new Properties();
		configFile.load(new FileInputStream(new File(path)));		

		docNameField = configFile.getProperty("docNameField");
		content = configFile.getProperty("content");
		datefield = configFile.getProperty("datefield");
		machine = configFile.getProperty("machine"); 
		noPruningIndex =  configFile.getProperty("noPruningIndex");  
		
	}
		

	
	
	
}
