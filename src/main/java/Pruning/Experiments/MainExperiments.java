package Pruning.Experiments;


public class MainExperiments {

	/**
	 * @param args
	 * @throws Exception 
	 */


	public static void main(String[] args) throws Exception {
	
		String config ="configpruneWIKI71tf0";
		
		int withthread = 0;
	 

		config = args[0];
		Settings.InstallSettings(config);
		
		Settings.prunetype =  Integer.valueOf(args[1]);
		Settings.percent = Integer.valueOf(args[2]);
	    Settings.termsfolder = args[3];
	    Settings.similarity = Integer.valueOf(args[4]);
	    withthread = Integer.valueOf(args[5]);
	    int fortests =  Integer.valueOf(args[6]);
	    
	    for(int i=-1;i<=5;i++)
	    {
	    	Settings.prunetype = i;
	    	Experiments in = new Experiments(Settings.prunetype);
	    if(Settings.prunetype<3 || fortests==0)	
	    {
			if(withthread == 1)
				in.Initialize();
			else 
				in.InitializeNoThread();

	    }	
			in.InitPruneRatios();
			
			in.StartPruning();

	    }

		System.out.println("OVER OVER");
	
		
		
		
	}
	

}
