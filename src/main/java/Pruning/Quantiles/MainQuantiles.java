package Pruning.Quantiles;

public class MainQuantiles {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static int prunetype = 0;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	      

   	    String config = args[0];
		
		prunetype =  Integer.valueOf(args[2]);
	  // TO use same sampling for all types / same terms / I am doing all in once
	    
	    Settings.InstallSettings(config);
	    Settings.termsfolder = args[3];
	    System.out.println("STARTING");
//	    Settings.termsfolder = args[6];
	    Init init  = new Init(Settings.noPruningIndex, prunetype, args[1]);
	    init.readfromfile();
	    //init.run();
//	    if(Integer.valueOf(args[5]) == 1)
//	    	init.readfromfile();
	    /*
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
*/
	   

		System.out.println("OVER OVER");
	
		
		
		
	}
	

}
