package Pruning.Experiments;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.util.Version;


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
	    Settings.isfortest =  Integer.valueOf(args[6])==1?true:false;
	    Settings.isTemporalExperiment = Integer.valueOf(args[7]);
	    Settings.fortrec =  Integer.valueOf(args[8]);
	    Settings.start =  Integer.valueOf(args[9]);
	    Settings.end =  Integer.valueOf(args[10]);

	    Experiments in = new Experiments(Settings.prunetype);
	  /*  Analyzer basicana = new StandardAnalyzer(Version.LUCENE_CURRENT);
	    IndexWriterConfig configwriter = new IndexWriterConfig(Version.LUCENE_CURRENT, basicana);
		IndexWriter indexWriter = new IndexWriter(in.pruningmethod.dir2,configwriter);
		indexWriter.forceMerge(1);
		indexWriter.commit();
		indexWriter.close();
	    */
	    if(Settings.prunetype<3|| Settings.isfortest==false)	
	    {
			if(withthread == 1)
				in.Initialize();
			else 
				in.InitializeNoThread();

	    }	
		in.InitPruneRatios();
		
		in.StartPruning();

	    

		System.out.println("OVER OVER");
	
		
		
		
	}
	

}

