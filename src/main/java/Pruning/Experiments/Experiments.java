package Pruning.Experiments;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.DataFormatException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;

import cern.colt.map.OpenIntDoubleHashMap;

public class Experiments {

	static List<String> terms = new ArrayList<String>();
	
	public static long overallcounter=0;
	static public Pruning.Methods.PruningMethod pruningmethod;
	public static Map<Term, OpenIntDoubleHashMap > entries = new HashMap<Term,OpenIntDoubleHashMap>();
	float[] indexlist;
	float[] prunelist;
	String filename;
	static Map<String, Double> pruneratiobyterm = new HashMap<String, Double> ();
	int type;
	public Experiments(int type) throws IOException, DataFormatException, ParseException
	{
		this.type = type;
		filename = Utils.SetPruneTypeandFilename(type,Settings.noPruningIndex,false); 
		GetVocabulary();
	
		
	}
	
	
	 public void InitializeNoThread() throws IOException, InterruptedException, ExecutionException
	 {
		 	List<Callable< Map<Term,OpenIntDoubleHashMap>>> tasks = new LinkedList<Callable< Map<Term,OpenIntDoubleHashMap>>>();
			ExecutorService executor =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			String term;
			
			
			for(int i=0;i<terms.size();i++)
			{ 
				term = terms.get(i);
				System.out.println(term);
				final Term tempterm =  new Term( Settings.content,term);
		
				entries.putAll(pruningmethod.GetPostingsForTerm(tempterm));
			}	
	 }
	 
	 public void Initialize() throws IOException, InterruptedException, ExecutionException
	 {
		 	List<Callable< Map<Term,OpenIntDoubleHashMap>>> tasks = new LinkedList<Callable< Map<Term,OpenIntDoubleHashMap>>>();
			ExecutorService executor =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			String term;
			
			for(int i= 0;i<terms.size();i++)
			{ 
				term = terms.get(i);
			//	System.out.println(term);
				final Term tempterm =  new Term( Settings.content,term);
				tasks.add(new Callable< Map<Term,OpenIntDoubleHashMap>>(){

							@Override
							public Map<Term, OpenIntDoubleHashMap> call() throws Exception {
							
								
									return pruningmethod.GetPostingsForTerm(tempterm);

							}
							
				  	  });
			}

			
			List<Future<Map<Term,OpenIntDoubleHashMap>>> list = executor.invokeAll(tasks);
			for (Future<Map<Term,OpenIntDoubleHashMap>> fut :list ) {
					 entries.putAll(fut.get());
				}
			

			executor.shutdown(); 


	}
	
		public void StartPruning() throws Exception
		{
			Prune prune = new Prune();
			float pruneratio = 0;
    		float indexten ;
    		float pruneden ;
    		EvaluationMetrics ev = new EvaluationMetrics( pruningmethod);
	    	for(int i=0;i<indexlist.length;i++)
	    	{
	    	
	    		
	    		indexten = indexlist[i];
	    		pruneden = prunelist[i];


	    		if(Settings.prunetype >=3 ||Settings.prunetype==-2)
	    		{

	    			pruneratio = prune.PruningTemporal(indexten, Settings.percent==1, Settings.prunetype == -2);
	    			pruneratio =  pruneden;
	
	    		}
	    		else 
	    		{
    				pruneratio = prune.PruningByRatio(indexten);
					pruneratio = 1- pruneden;
					
						

	    		}
	    		
	  
				//System.out.println(" Ratio Pruning " + pruneratio);
						
				if(Settings.collectiontype == 1) // WIKI date validity is range
					ev.runRangeField(prune, pruneratio);
				else ev.runDateField(prune,pruneratio);

			   }
				
				   
		    	
			}
		
		
	
	public void InitPruneRatios()
	{
		try {
			indexlist =  Utils.InitializePruneRatios(Settings.prunetype, Settings.workspace+filename).get(0);
			prunelist =  Utils.InitializePruneRatios(Settings.prunetype,Settings.workspace+filename).get(1);
		} catch (IOException e) {
			System.out.println("In InitPruneRatios:" + e.toString());
		}
		
	}
	
	
	public void GetVocabulary() throws CorruptIndexException, IOException, DataFormatException, ParseException
	{
		
	
		Set<String> tempterms = new HashSet<String>();
		String[] temp ;

		// queries can be composed of different keys no need for PWA collection
		for(int i=0; i< Settings.STANDARD_QUERIES.length;i++)
		{
			temp = Settings.STANDARD_QUERIES[i].split(" ");
			for(int k=0; k< temp.length;k++)
			{
				if(!temp[k].trim().isEmpty())
				{
					//File f = new File(Settings.termsfolder + type + "_"+ temp[k].toLowerCase() + ".txt");
					
					//if(!f.exists())
					{
						//System.out.println( temp[k].toLowerCase());
						tempterms.add(temp[k].trim().toLowerCase());
					}
					
				}
				
				
			}
		}
			
		terms.addAll(tempterms); // to keep inscoring overall our terms 
		Collections.sort(terms, Collections.reverseOrder());// i NEED TO SORT because I use start end to work on multiple clusters
		System.out.println("Number of total terms : " + terms.size());
	}

	
}
