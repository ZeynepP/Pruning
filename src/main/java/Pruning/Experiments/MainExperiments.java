package Pruning.Experiments;


public class MainExperiments {

	/**
	 * @param args
	 * @throws Exception 
	 */
	


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	//	String config = "configprune71tf0"; // la
		String config ="configpruneWIKI71tf0";
		int start =0; int end = 118;//118
		int withthread = 0;
	 

		config = args[0];
		Settings.InstallSettings(config);
		
		Settings.prunetype =  Integer.valueOf(args[1]);
		Settings.percent = Integer.valueOf(args[2]);
	    start = Integer.valueOf(args[3]);
	    end = Integer.valueOf(args[4]);
	    Settings.termsfolder = args[5];
	    Settings.similarity = Integer.valueOf(args[6]);
	    withthread = Integer.valueOf(args[7]);

	    
	    
	   Experiments in = new Experiments(Settings.prunetype);
 
	   
	
		if(withthread == 1)
			in.Initialize();
		else 
			in.InitializeNoThread();
		in.InitPruneRatios();
		in.StartPruning();

	

		System.out.println("OVER OVER");
	
		
		
		
	}
	

}
