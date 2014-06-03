package Pruning.Quantiles;


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
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import Pruning_StateoftheArt.PruningMethod;
import cern.colt.list.DoubleArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class Init {
	
	
	public MetricSampleQuantiles metrics ;
	static final MetricQuantile[] quantiles = {new MetricQuantile(0.90, 0.010),
	      new MetricQuantile(0.80, 0.020), new MetricQuantile(0.70, 0.030),
	      new MetricQuantile(0.60, 0.040),new MetricQuantile(0.50, 0.050),
	      new MetricQuantile(0.40, 0.060),new MetricQuantile(0.30, 0.070),
	      new MetricQuantile(0.20, 0.010),new MetricQuantile(0.10, 0.090)};
	
	
 	
/******************GENERAL DECLERATIONS **************************/
	
	
	public static List<String> terms = new ArrayList<String>();
	public static String filename;
	public static PruningMethod pruningmethod;
	int type;
	String outputdir;
	@SuppressWarnings("deprecation")
	public Init(String ts, int type, String output) throws Exception 
	{
	
		metrics = new MetricSampleQuantiles(quantiles);
		this.type= type;
		filename = Utils.SetPruneTypeandFilename(type,ts,true); // will set also prune type 
		outputdir = output;
		GetVocabulary();
	}


	public void UpdateSettings(String ts,int type) throws IOException
	{
		this.type= type;
		filename = Utils.SetPruneTypeandFilename(type,ts,true); // will set also prune type 
		metrics.clear();
	}
	
	 
	 public void run() throws IOException, InterruptedException, ExecutionException
	 {
		 	List<Callable< ArrayList>> tasks = new LinkedList<Callable<ArrayList>>();
			ExecutorService executor =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			String term;
					
			for(int i=0;i<terms.size();i++)
			{ 
				term = terms.get(i);
				final Term tempterm =  new Term( Settings.content,term);
				tasks.add(new Callable< ArrayList>(){

							public ArrayList call() throws Exception {
								Map<Term, OpenIntDoubleHashMap> r = pruningmethod.GetPostingsForTerm(tempterm);
								if(r.size() > 0)
								{
									OpenIntDoubleHashMap h = (OpenIntDoubleHashMap) (r.values()).toArray()[0];//only one
									DoubleArrayList list = h.values();	
									return list.toList();
								}
								return null;

							}
							
				  	  });
			}

			
			List<Future<ArrayList>> list = executor.invokeAll(tasks);
			for (Future<ArrayList> fut :list ) 
			{
				ArrayList<Double> r = (ArrayList<Double>) fut.get();
				if(r!=null&r.size()>0 )
				{
					for(int i=0;i<r.size();i++)
					{
						metrics.insert((double)r.get(i));
						
					}
					
				}
			}
			

			executor.shutdown(); 

			GetThreshold(true);

	}

	  
	
	public void GetVocabulary() throws CorruptIndexException, IOException, DataFormatException, ParseException
	{
		
		
		Set<String> tempterms = new HashSet<String>(100000000);
		String[] temp ;
		List<String> termssampling = new ArrayList<String>();
		Pattern p = Pattern.compile("-?\\d+");
		Pattern p2 =  Pattern.compile("[^\\p{L}\\p{Nd}]");
		String term;
		TermsEnum termEnum ;
		Terms allterms ;
			
		allterms = pruningmethod.fields.terms(Settings.content);
		termEnum = allterms.iterator(null);
			
			
		while (termEnum.next()!=null)
		{ 
			//if(!termEnum.term().utf8ToString().contains("_") &&!termEnum.term().utf8ToString().contains("?") &&!termEnum.term().utf8ToString().contains(":")&&!termEnum.term().utf8ToString().contains("."))
			
			//if(!termEnum.term().utf8ToString().matches("[^A-Za-z]"));//matches(".*(\\.|\\_|\\?|:|;|!|\\').*"))
			term = termEnum.term().utf8ToString().trim().intern();
			if(!p.matcher(term).find()&& !p2.matcher(term).find() && term.length()>2 &&term.length()<10&& !term.matches(".*(\\.|\\_|\\?|:|;|!|\\').*"))
			{
				
				tempterms.add(term);
				if(tempterms.size()>1000000)
				{
					termssampling.addAll(tempterms);
					Collections.shuffle(termssampling);
					terms.addAll(termssampling.subList(0, 50000));
					termssampling.clear();
					tempterms.clear();
					System.out.println("Number of total terms : " + terms.size());
					
				}
				
				
			}
			
		}
			
			
		/*	
		terms.addAll(tempterms);// because the other one is set no duplicates
		Collections.shuffle(terms);
		terms = terms.subList(0, 2000000);*/
		
		
		System.out.println("Number of total terms : " + terms.size());
	}
	
	 public void GetThreshold(boolean writofile) throws IOException
	  {
		  	System.out.println( "Getting thresholds");
		  	String s="";

			Map<MetricQuantile, Double> results = metrics.snapshot();
			for(Entry<MetricQuantile, Double> r: results.entrySet())
			{
				s += 1-((MetricQuantile)r.getKey()).quantile +  ","+ r.getValue()+ "\n" ;
				System.out.println("For " +  (((MetricQuantile)r.getKey()).quantile) + " result " +	r.getValue());
			}
			
		    if(writofile)
		    	FileUtils.writeStringToFile(new File(outputdir + Init.filename), s);
		
		  
	  }

 
	
}
	 
