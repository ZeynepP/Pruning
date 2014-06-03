package Pruning.Quantiles;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Settings {
	
	public enum PruningType 
	{
		  Random(-2),
		  PRP(-1),
		  CARMEL(0),
		  CIKM_IPU(1),
		  ECIR_2N2P(2),
		  SIMPLE(3),
		  SLIDING(4),
		  DYNAMIC(5),
		  QUANTILES(6),
		  KLAUS(7),
		  CDFGMM(8),
		  ECIRWINDOW(9),
		  GREEDYOPT(10),
		  BM25WINDOW(11);
		  
		  private int type;
		 
		  private PruningType (int value)
		  {
		    this.type = value;
		  }
		 
		  public int getPruningTypeValue() {
		    return type;
		  }
		
		
	}
	
	/********************** variables based on test collection *******************************/ 
	
	// wiki end 1247011200
	public static int datecount; // depends on test data ( trec la times 2 years)
	public static String docNameField  ;//"docid";
	public static String content ; 
	public static String datefield; // 
	public static String rangefield ; // 
	public static long dateinit; // 
	
	
	public static enum WindowType {  
		
		SIMPLE, SLIDING, DYNAMIC, GMM
		
		};
	
	public static String topicPathtemporal ;
	public static String qrelsPath ;
	public static String qrelsPathtemporal 	;
	public static String topicTREC; // topicsall
	public static String machine ; // "/home/pehlivanz/";//
	public static String noPruningIndex ;
	public static String qrelsPathtemporaltrue;
	public static String GMMfile;
	public static String Quantilefile;
	public static String subBasic;
	// LA TIMES
	
	public static String[] STANDARD_QUERIES_LA= //{"cosmic"};
		{"International of Organized Crime","Poliomyelitis Post Polio","Hubble and Telescope Achievements"," Endangered Species Mammals","Most Dangerous Vehicles","African Civilian Deaths","New Hydroelectric Projects","Implant Dentistry","Rap  Crime","Radio Waves  Brain Cancer","Industrial Espionage","Hydroponics","Magnetic Levitation Maglev","Marine Vegetation","Unexplained Highway Accidents","Polygamy Polyandry Polygyny","Unsolicited Faxes","Best Retirement Country","New Fuel Sources","Undersea Fiber Optic Cable","Women Parliaments","International Art Crime","Literary Journalistic Plagiarism","Argentine British Relations","Cult Lifestyles","Ferry Sinkings","Modern Slavery","Pope Beatifications","Mexican Air Pollution","Iran Iraq Cooperation","World Bank Criticism","Income Tax Evasion","Antibiotics Bacteria Disease","Export Controls Cryptography","Adoptive Biological Parents","Black Bear Attacks","Viral Hepatitis","Risk Aspirin","Alzheimer's Drug Treatment","L Mine Ban","Airport Security","Diplomatic Expulsion","Police Deaths","Abuses E Mail","Overseas Tobacco Sales","Educational Standards","Wildlife Extinction","Agoraphobia","Metabolism","Health Computer Terminals", // 301 350
		"Land Mine Ban ","Falkland petroleum exploration","British Chunnel impact","Antarctica exploration","journalist risks","ocean remote sensing","postmenopausal estrogen Britain","territorial waters dispute","blood alcohol fatalities","mutual fund predictors","drug legalization benefits","clothing sweatshops","human smuggling","transportation tunnel disasters","rabies","El Nino","commercial cyanide uses","piracy","in vitro fertilization","anorexia nervosa bulimia","food drug laws","health insurance holistic","Native American casino","encryption equipment export","Nobel prize winners","hydrogen energy","World Court","cigar smoking","euro opposition","mainstreaming","obesity medical treatment","alternative medicine","hydrogen fuel automobiles","mental illness drugs","space station moon","hybrid fuel cars","teaching disabled children","radioactive waste","organic soil enhancement","illegal technology transfer","orphan drugs","R D drug prices","robotics","mercy killing","home schooling","tourism","sick building syndrome","automobile recalls","dismantling Europe's arsenal","oceanographic vessels","Amazon rain forest",// 351 400
		"foreign minorities Germany","behavioral genetics","osteoporosis","Ireland peace talks","cosmic events","Parkinson's disease","poaching wildlife preserves","tropical storms","legal Pan Am 103","Schengen agreement","salvaging shipwreck treasure","airport security","steel production","Cuba sugar exports","drugs Golden Triangle","Three Gorges Project","creativity","quilts income","recycle automobile tires","carbon monoxide poisoning","industrial waste disposal","art stolen forged","Milosevic Mirjana Markovic","suicides","counterfeiting money","law enforcement dogs","UV damage eyes","declining birth rates","Legionnaires disease","killer bee attacks","robotic technology","profiling motorists police","Greek philosophy stoicism","Estonia economy","curbing population growth","railway accidents","deregulation gas electric","tourism increase","inventions scientific discoveries","child labor","Lyme disease","heroic acts","U.S investment Africa","supercritical fluids","women clergy","tourists violence","Stirling engine","ship losses","antibiotics ineffectiveness","King Hussein peace"};  // 401 405
	
	public static String[] STANDARD_QUERIES;
	
	public static String[] STANDARD_QUERIES_PWA= 
		{"público","expo 98","benfica","cavaco silva","universidade lisboa","josé saramago","câmara municipal marco canaveses","primeiro ministro","festival da canção","finanças","federação de kickboxing","volkswagen","telepizza","hospital barreiro","fernando pessoa","google","adelaide ferreira","porto aveiro","porto de lisboa","embaixada do brasil","teatro dona maria segunda","notícias de leiria","horários soflusa","restaurantes público","icat projectos","centro Jacques Delors","Santana Lopes","assembleia da república","feup","pedro abrunhosa","marinha","rock rio","cavalo lusitano","moviflor","giatsi isel","instituto ricardo jorge","dns","olx","ordem dos engenheiros","net surf","escola profissional chaves","misericórdia de fafe","bolsa emprego sapo","timeout arte","serviços consulado angola","embaixada da estónia","lpcpcc","arquidiocese évora paróquias oeste","rádio douro sul","instituto energia"};
	
	
	public static String[] STANDARD_QUERIES_WIKI=// {"voyager"};
			
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
		"wright brothers "
		
		
		
		};
	
	public static String[] STANDARD_QUERIES_DATE= 
		{
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
	
	
	public static String windtext;
	public static int windowsize;  // 3 mois
	public static int slidingsize ; // 2 semaines
	public static int greedymax ; 
	public static int withTF ;
	
	
	public static void InstallSettings(String path) throws IOException
	{
		Properties configFile = new Properties();
		configFile.load(new FileInputStream(new File(path)));		
		
		//datecount = Integer.parseInt(configFile.getProperty("datecount"));
		docNameField = configFile.getProperty("docNameField");
		content = configFile.getProperty("content");
		datefield = configFile.getProperty("datefield");
		//dateinit = Long.parseLong( configFile.getProperty("dateinit"));
		//rangefield = configFile.getProperty("rangefield");
		machine = configFile.getProperty("machine"); 
		noPruningIndex =  configFile.getProperty("noPruningIndex");  
	
		
	
		if(MainQuantiles.collectiontype == 0)
		{
			STANDARD_QUERIES = new String[STANDARD_QUERIES_LA.length];
			System.arraycopy(STANDARD_QUERIES_LA, 0, STANDARD_QUERIES, 0, STANDARD_QUERIES_LA.length);
		}
		else  if(MainQuantiles.collectiontype == 1)
		{
			STANDARD_QUERIES = new String[STANDARD_QUERIES_WIKI.length +STANDARD_QUERIES_DATE.length]; //+ 
			System.arraycopy(STANDARD_QUERIES_WIKI, 0, STANDARD_QUERIES, 0, STANDARD_QUERIES_WIKI.length);
			System.arraycopy(STANDARD_QUERIES_DATE, 0, STANDARD_QUERIES,STANDARD_QUERIES_WIKI.length, STANDARD_QUERIES_DATE.length);
		}
		else  if(MainQuantiles.collectiontype == 2)
		{
			STANDARD_QUERIES = new String[STANDARD_QUERIES_PWA.length];
			System.arraycopy(STANDARD_QUERIES_PWA, 0, STANDARD_QUERIES, 0, STANDARD_QUERIES_PWA.length);
		}
		
	}
		

	
	
	
}
