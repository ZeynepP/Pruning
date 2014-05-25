package Pruning.Quantiles;




public class MainQuantiles {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static int prunetype = 0;
	public static int collectiontype;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	      

   	    String config = args[0];
		
		prunetype =  Integer.valueOf(args[1]);
	  
	    
	    Settings.InstallSettings(config);
	   
	    Init init  = new Init(Settings.noPruningIndex, prunetype, Settings.machine);
	    init.run();
		

		System.out.println("OVER OVER");
	
		
		
		
	}
	

}
