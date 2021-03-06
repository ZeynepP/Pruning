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
	public static int quantilestermssize; // 

	public static String machine ; // "/home/pehlivanz/";//
	public static String noPruningIndex ;
	public static int collectiontype ;

	public static String topicPathtemporal ;
	public static String qrelsPath ;
	public static String qrelsPathtemporal 	;
	public static String topicTREC; // topicsall
	public static String qrelsPathtemporaltrue;
	public static String GMMfile;
	public static String maxminfile;
	public static int querynumber;
	public static double alpha;
	public static int totalquerynumer;
	
	public static int datecount; // depends on test data ( trec la times 2 years)
	public static String windtext;
	public static int windowsize;  // 3 mois
	public static int slidingsize ; // 2 semaines
	public static String workspace ;
	public static String rangefield ; // 
	public static int maxdocs;
	public static String termsfolder ;
	public static void InstallSettings(String path) throws IOException
	{
		Properties configFile = new Properties();
		configFile.load(new FileInputStream(new File(path)));		

		datecount = Integer.parseInt(configFile.getProperty("datecount"));
		docNameField = configFile.getProperty("docNameField");
		content = configFile.getProperty("content");
		System.out.println(content + " == " +  configFile.getProperty("content"));
		datefield = configFile.getProperty("datefield");
		dateinit = Long.parseLong( configFile.getProperty("dateinit"));
		rangefield = configFile.getProperty("rangefield");
		machine = configFile.getProperty("machine"); 
		noPruningIndex =  configFile.getProperty("noPruningIndex");  
		collectiontype = Integer.parseInt(configFile.getProperty("collectiontype"));   
		totalquerynumer = Integer.parseInt(configFile.getProperty("totalquerynumer"));
		workspace = configFile.getProperty("workspace");  
		querynumber = Integer.parseInt(configFile.getProperty("querynumber"));
		windowsize = Integer.parseInt(configFile.getProperty("windowsize"));
		slidingsize = Integer.parseInt(configFile.getProperty("slidingsize")); 
		topicPathtemporal = configFile.getProperty("topicPathtemporal");
		maxdocs =  Integer.parseInt(configFile.getProperty("maxdocs")); 
		GMMfile = configFile.getProperty("GMMfile");
		maxminfile = configFile.getProperty("maxminfile");
		//quantilestermssize =  Integer.parseInt(configFile.getProperty("quantilestermssize")); 
	}
		

	public static String[] STANDARD_QUERIES=// {"voyager"}
		{
			"ac milan",
			"academy award",
			"babe ruth ",
			"berlin",
			"boston red sox ",
			"chicago bulls", 
			"george bush",
			"internet",
			"iraq",
			"italian national soccer team ", 
			"jazz music ",
			"keith harring ",
			"kurt cobain  ",
			"la lakers  ",
			"mac os x ", 
			"michael jackson ", 
			"michael jordan   ",
			"mickey mouse  ",
			"microsoft halo   ",
			"monica lewinsky", 
			"muhammed  ",
			"musket  ",
			"new york yankees ",
			"nixon ",
			"pearl harbor ",
			"pink floyd  ",
			"poland",
			"queen victoria ", 
			"rocky horror picture show  ",
			"roentgen",
			"sewing machine",
			"siemens",
			"soccer", 
			"sound of music ",
			"stefan edberg  ",
			"thomas edison  ",
			"vietnam",
			"voyager ",
			"woodstock ",
			"wright brothers",
			"may 23 2007",
			"21st century",
			"1921",
			"1961",
			"27 2004",
			"1991", 
			"january 18",
			"2006", 
			"21st",
			"february 16 1990",
			"april 5 1994 ",
			"21st ",
			"march 24 2001", 
			"1982", 
			"1990s ",
			"1930s ",
			"june 2000 ",
		
			"7th ",
			"16th ",
			"1910s",
			"1970",
			"december 1941 ",
			"1973 ",
			"1975 ",
			"1895",
			"1850s ",
			"19th ",
			"21st ", 
			"1960s ",
			"july ",
			"1891",
			
			"1977 ",
			"1994 ",
			"1905 "
			
			
			
			
			};
	
	
}
