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
		
		//prunetype =  Integer.valueOf(args[2]);
	  // TO use same sampling for all types / same terms / I am doing all in once
	    
	    Settings.InstallSettings(config);
	    System.out.println("STARTING");
	    
	    prunetype = 1;
	    System.out.println(prunetype+ " ******** ");
	    Init init  = new Init(Settings.noPruningIndex, prunetype, args[1]);
	    init.run();
	    
	    prunetype = 2;
	    System.out.println(prunetype+ " ******** ");
	    init.UpdateSettings(Settings.noPruningIndex, prunetype);
	    init.run();
	    
	    prunetype = -1;
	    System.out.println(prunetype+ " ******** ");
	    init.UpdateSettings(Settings.noPruningIndex, prunetype);
	    init.run();
	    
	    prunetype = 0;
	    System.out.println(prunetype+ " ******** ");
	    init.UpdateSettings(Settings.noPruningIndex, prunetype);
	    init.run();

	   

		System.out.println("OVER OVER");
	
		
		
		
	}
	

}
