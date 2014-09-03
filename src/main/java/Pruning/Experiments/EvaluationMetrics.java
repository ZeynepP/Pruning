package Pruning.Experiments;


import it.unimi.dsi.bits.LongArrayBitVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.text.Document;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.benchmark.quality.QualityFilterParser;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.trec.TemporalTrecTopicsReader;
import org.apache.lucene.benchmark.quality.utils.DocNameExtractor;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.TemporalSimpleQQParser;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BM25SimilarityZP;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarityZP;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarityZP;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

import Pruning.Methods.PruningMethod;
import cern.colt.map.OpenIntIntHashMap;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class EvaluationMetrics {
	private NumberFormat nf;
	
	File  topicsFiletemporal;
	FileReader temporalfr ;
	BufferedReader temporalbr;
	IndexReader ir;
	IndexSearcher searcher;
	String sep = " \t ";
	PruningMethod pruningmethod;
	int N = 86400000;// 1 day to milisecond as queries calculated by index date
	
	QualityQuery qqs[];
	QualityQueryParser qqParser;
	QualityFilterParser fParser;
	KendallsCorrelation kendallcor ;
	//SpearmansCorrelation spearmancor;
	APCorrelation apcorr ;
	
	public EvaluationMetrics( PruningMethod p) throws IOException
	{
		kendallcor = new KendallsCorrelation();
		//spearmancor = new SpearmansCorrelation();
		apcorr = new APCorrelation();
		
		topicsFiletemporal = new File(Settings.topicPathtemporal);
		this.ir = p.ir;
		this.searcher = p.GetSearcher();
		temporalfr = new FileReader(topicsFiletemporal);
		temporalbr = new BufferedReader(temporalfr);
		pruningmethod = p;
		
		TemporalTrecTopicsReader qReader = new TemporalTrecTopicsReader();   
		qqs = qReader.readQueries(temporalbr); 
		qqParser = new SimpleQQParser("title", Settings.content);
		fParser = new TemporalSimpleQQParser("time",Settings.datefield );
		
		 nf = NumberFormat.getInstance();
		 nf.setMaximumFractionDigits(4);
		 nf.setMinimumFractionDigits(4);
	}
	

	/** Calculate all evaluation metrics with given real and predicted rating matrices. 
	 * @throws IOException */
	public void runDateField(Prune prune, float pruneratio) throws IOException
	{
		double kendallsTau=0;
		double ndcg=0;
		int debug = 0;
		int maxdoc = Settings.maxdocs;
	    int nQueries =  qqs.length;
	    int counter = 0;

	  for (int i=0; i<nQueries; i++) {
		  debug = 1;
		    	try{
		    		
		    	
				      QualityQuery qq = qqs[i];
				      Query q = qqParser.parse(qq);
				      debug = 2;
				      
				      
				      NumericRangeFilter<Long> filter = fParser.parseFilter(qq);
				      NumericRangeFilter<Long> UnixTimefilter = filter;
				      if(Settings.collectiontype == 2)
				      {
				    	  	UnixTimefilter = NumericRangeFilter.newLongRange(Settings.datefield, (filter.getMin()*N)+ Settings.dateinit, (filter.getMax()*N)+ Settings.dateinit, true, true);
				      }
				      if(Settings.isTemporalExperiment == 0)
				    	  UnixTimefilter = null;
				      debug = 3;
				      //System.out.println(UnixTimefilter.getMax() + "\t" + UnixTimefilter.getMin());
				      SetSimilarities(false, null);//no pruning
				      debug = 4;
				      TopDocs tdnotpruned = searcher.search(q,UnixTimefilter,maxdoc);
				      debug = 5;
				      ScoreDoc[] sdnotpruned = tdnotpruned.scoreDocs;
				      debug = 6;
				  	  OpenIntIntHashMap nopruned = new OpenIntIntHashMap();
				  	  
				  	  
				  	  Set<Term> terms = new HashSet<Term>();
					  q.extractTerms(terms);
					  debug = 7;
					  double  pruneratioterm = 0;
					  Iterator<Term> it = terms.iterator();
				      while(it.hasNext())
				      {
				    	  Term t = it.next();
				    	  try{
				    	  pruneratioterm+= Experiments.pruneratiobyterm.get(t.text());
				    	  }
				    	  catch(Exception ex)
				    	  {
				    		  System.out.println("From pruneratioterm");
				    	  }
				    	  
				      }
				    
					  pruneratioterm = pruneratioterm/(double)terms.size();
					  debug = 8;
					  
					  
					  
				  	  
				  	  if(sdnotpruned.length > 0)
				  	  {
				  		  	 
				  		debug = 9;
						      for(int k=0;k<sdnotpruned.length;k++)
						      {
						    	  nopruned.put( sdnotpruned[k].doc,k+1);
						      }
						      
						      
						      debug = 10;     
						      SetSimilarities(true, prune.maps);//pruned
						      debug = 11;  
						      TopDocs tdpruned = searcher.search(q,UnixTimefilter,maxdoc);
						      ScoreDoc[] sdpruned = tdpruned.scoreDocs;
						      debug = 12;  
						      double[] nopruningkeys = new double[nopruned.size()];
						      double[] prunedkeys = new double[nopruned.size()];
						      debug = 13;  
						      for(int k=0;k<sdpruned.length;k++)
						      {
						    	  int id = nopruned.get(sdpruned[k].doc);
							    	 if(id == 0)
							    		 id = maxdoc +k;
							    	 debug = 14; 
							    	 nopruningkeys[k] = k +1;
							    	 prunedkeys[k] = id;
							    	 debug = 15; 
						      }
						   
						    double k = 0;
						    double a = 0;
						   
						    debug = 16; 
						    if(nopruningkeys.length > 2)
						    {
						    	try{
						    		k = kendallcor.correlation(nopruningkeys.clone(), prunedkeys.clone(),nopruningkeys.length);	
						    	}
						    	catch(Exception ex)
						    	{
						    		System.out.println("From kendall " + ex.toString());
						    	}
						    	
							try{
								a = apcorr.APCorrelation(nopruningkeys.clone(), prunedkeys.clone());						    		
													    	}
						    	catch(Exception ex)
						    	{
						    		System.out.println("From AP " + ex.toString());
						    	}
						    	
 	
						    	 counter++;
							    System.out.println("APKENDALL" + "\t" + Settings.prunetype + "\t" + i + "\t" + pruneratioterm + "\t" + a + "\t" + k + "\t" + pruneratio  +"\t" + counter);
							    kendallsTau+=k;
							    ndcg+=a;
						    }
						
						// 	System.out.println("\tFor query " + i + "\t" + pruneratio + "\t" + k + "\t" + a + "\t" + counter + "\t" + nopruningkeys.length + "\t" + prunedkeys.length );
						    if(nopruningkeys.length!=prunedkeys.length)
						    		System.out.println("\tFor query " + i + "\t" + pruneratio + "\t" + nopruningkeys.length + "\t" + prunedkeys.length );
						    
						    
				  	  }
				  	  else
				  	  {
				  		System.out.println(q.toString());
				  	    System.out.println(filter.getMin() + " ** " + filter.getMax() + " ** " + sdnotpruned.length);
				  	   
				  	  }

		    	}

		    	catch(Exception ex)
		    	{
		    		System.out.println(ex.toString() + " )) " + debug);
		    	}    	
	  }
	  	System.out.println( "APKENDALL" + "\t" + Settings.prunetype + "\t" + nQueries + "\t" + pruneratio + "\t"  + ndcg/(double)counter + "\t" + kendallsTau/(double)counter + "\t" + pruneratio + "\t" + counter);
	  	//System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Kendall\t"  ) ;
	  	//System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Spear\t"  +spearman/(double)counter);
		    
	  }
	  
	  
	
	  public void runRangeField(Prune prune, float pruneratio) throws IOException
		{
		  StringWriter swnoprune = new StringWriter();
		  PrintWriter loggernoprune=  new PrintWriter(swnoprune); 
		  StringWriter swprune = new StringWriter();
		  PrintWriter loggerprune=  new PrintWriter(swprune); 
		  
		  
		  
			double kendallsTau=0;
			double ndcg=0;
			DocNameExtractor rangesxt =  new DocNameExtractor(Settings.rangefield);
			RangeMap tempmap = TreeRangeMap.create();
			String docrange;
		    int nQueries =  qqs.length;
		    int counter = 0;
		    int countertemporal = 0;
		    int maxdoc = Settings.maxdocs;
		    int countermaxdoc = 1000;
		    DocNameExtractor xt = new DocNameExtractor(Settings.docNameField);
		    for (int i=0; i<nQueries; i++) {
			    	
			    	try{
			    		
			    	
					      QualityQuery qq = qqs[i];
					      Query q = qqParser.parse(qq);
					     // q = new TermQuery(new Term(Settings.content, "milan"));
					      NumericRangeFilter<Long> filter = fParser.parseFilter(qq);
					      
					      Set<Term> terms = new HashSet<Term>();
						  q.extractTerms(terms);
						 
						  double  pruneratioterm = 0;
						  Iterator<Term> it = terms.iterator();
					      while(it.hasNext())
					      {
					    	  Term t = it.next();
					    	  pruneratioterm+= Experiments.pruneratiobyterm.get(t.text());
					    	  
					      }
					    
						  pruneratioterm = pruneratioterm/(double)terms.size();
					    
						  
						//  System.out.println(Settings.similarity);
					      SetSimilarities(false, null);//no pruning
					      
					      TopDocs tdnotpruned = searcher.search(q,null,Settings.maxdocs);
					      ScoreDoc[] sdnotpruned = tdnotpruned.scoreDocs;
					     
					      OpenIntIntHashMap nopruned = new OpenIntIntHashMap();
					  	  countertemporal = 0;
					  	  
					  	  if(sdnotpruned.length > 0 )
					  	  {
					  		  	  counter++;
					  		  	  if( Settings.isTemporalExperiment == 1)
					  		  	  {
								      for(int k=0;k<sdnotpruned.length;k++)
								      {
								    	 
								    	  docrange = rangesxt.docName(searcher,sdnotpruned[k].doc);
										  RangeMap rangeSet = parseTemporalDimension(docrange);
										  tempmap = rangeSet.subRangeMap(Range.closed(filter.getMin(), filter.getMax()));
										    	 
										
										    if(tempmap.asMapOfRanges().size() > 0)
										    {
										    	
										    	nopruned.put( sdnotpruned[k].doc,countertemporal+1);
										    	
										    	//System.out.println("NoPruned\t" + i + "\t" + sdnotpruned[k].doc + "\t" + sdnotpruned[k].score + "\t" + countertemporal );
										    	countertemporal++;
										    	
										    	
										    	if(Settings.fortrec == 1 && sdnotpruned[k].score != 0)
										    	{
										    		String docName = xt.docName(searcher,sdnotpruned[k].doc);
											    	loggernoprune.println(
											    	          qq.getQueryID()       + sep +
											    	          "Q0"                   + sep +
											    	          format(docName.replaceAll("_0", ""),20)    + sep +
											    	          format(""+i,7)        + sep +
											    	          nf.format(sdnotpruned[k].score) + sep +
											    	          "nopruned"
											    	          );
										    	}
										    	
										    }
								    	  
										    if(countertemporal == countermaxdoc)
										    	break;
								    	 
								    	  
								      }
								     
					  		  	  }
					  		  	  else
					  		  	  {
					  		  		 for(int k=0;k<sdnotpruned.length;k++)
								      {	
					  		  			nopruned.put( sdnotpruned[k].doc,k+1);
					  		  		if(Settings.fortrec == 1  && sdnotpruned[k].score != 0)
							    	{
							    		String docName = xt.docName(searcher,sdnotpruned[k].doc);
								    	loggernoprune.println(
								    	          qq.getQueryID()       + sep +
								    	          "Q0"                   + sep +
								    	          format(docName.replaceAll("_0", ""),20)    + sep +
								    	          format(""+i,7)        + sep +
								    	          nf.format(sdnotpruned[k].score) + sep +
								    	          "nopruned_notemporal"
								    	          );
							    	}							    	
								      }  
					  		  	  }
					  		  	  
					  		  	  
							      SetSimilarities(true, prune.maps);//pruned
							      TopDocs tdpruned = searcher.search(q,null,Settings.maxdocs);
							      ScoreDoc[] sdpruned = tdpruned.scoreDocs;
							
							      
							      double[] nopruningkeys = new double[Math.min(nopruned.size(),maxdoc)];
							      double[] prunedkeys =  new double[Math.min(nopruned.size(),maxdoc)];
							     
							      countertemporal = 0;
							      if(Settings.isTemporalExperiment == 1)
							      {
								      for(int k=0;k<sdpruned.length;k++) 
								      {
									    	docrange = rangesxt.docName(searcher,sdpruned[k].doc);
										    RangeMap rangeSet = parseTemporalDimension(docrange);
										    tempmap = rangeSet.subRangeMap(Range.closed(filter.getMin(), filter.getMax()));
										    	 
										    
										    if(tempmap.asMapOfRanges().size() > 0)
										    {
										    	 int id = nopruned.get(sdpruned[k].doc);
										    	 if(id == 0)
										    		 id = maxdoc +countertemporal;
										    	 nopruningkeys[countertemporal] = countertemporal +1;
										    	 prunedkeys[countertemporal] = id;
										    	// prunedkeys[countertemporal] = sdpruned[k].doc;
										    	 countertemporal++;
										    	 
										    	 
										    	 if(Settings.fortrec == 1  && sdnotpruned[k].score != 0)
										    	 {
										    		 String docName = xt.docName(searcher,sdpruned[k].doc);
											    	 loggerprune.println(
											    	          qq.getQueryID()       + sep +
											    	          "Q0"                   + sep +
											    	          format(docName.replaceAll("_0", ""),20)    + sep +
											    	          format(""+i,7)        + sep +
											    	          nf.format(sdpruned[k].score) + sep +
											    	          Settings.prunetype
											    	          );
										    		 
										    	 }
										    	
										    }
								    	  
										    if(countertemporal == countermaxdoc)
										    	break;
								    	 
								    	  
								      }
							      }
							      else
							      {
							    	  for(int k=0;k<Math.min(maxdoc,sdpruned.length);k++) 
								      {
							    		  prunedkeys[k] = nopruned.get(sdpruned[k].doc);
							    		  nopruningkeys[k] = k +1;
							    		  
							    		  if(Settings.fortrec == 1  && sdnotpruned[k].score != 0)
							    		  {
							    			  String docName = xt.docName(searcher,sdpruned[k].doc);
										    	 loggerprune.println(
										    	          qq.getQueryID()       + sep +
										    	          "Q0"                   + sep +
										    	          format(docName.replaceAll("_0", ""),20)    + sep +
										    	          format(""+i,7)        + sep +
										    	          nf.format(sdpruned[k].score) + sep +
										    	          Settings.prunetype + "notemporal"
										    	          );
							    		  }
								      }
							    	  
							      }
							      
							   
							    double k = kendallcor.correlation(nopruningkeys.clone(), prunedkeys.clone(),nopruningkeys.length);
							    double a = apcorr.APCorrelation(nopruningkeys.clone(), prunedkeys.clone());
							    
							    
							    
							    System.out.println("APKENDALL" + "\t" + Settings.prunetype + "\t" + i + "\t" + pruneratioterm + "\t" + a + "\t" + k  + "\t" + pruneratio);
							    kendallsTau+= k;
							    ndcg+= a;
								    //spearman+= (temp==-1?spearmancor.correlation(nopruningkeys.clone(), prunedkeys.clone()):0); 
						  	
					  	  }

			    	}

			    	catch(Exception ex)
			    	{
			    		System.out.println(ex.toString());
			    	}
			    	
			    	
			    	
		
			    
		  }
		    
		    if(Settings.fortrec == 1)
		    {
		    
			     FileUtils.writeStringToFile(new File("NoprunedWIKI" + Settings.isTemporalExperiment ), swnoprune.toString());
			     FileUtils.writeStringToFile(new File(Settings.prunetype + "_" + pruneratio + Settings.isTemporalExperiment), swprune.toString());
			     
		    	
		    }
		  System.out.println("APKENDALL" + "\t" + Settings.prunetype + "\t" + nQueries + "\t" + pruneratio + "\t" + ndcg/(double)counter + "\t" +  kendallsTau/(double)counter + "\t" + pruneratio);
	  	//System.out.println( Settings.prunetype + "\t" + pruneratio + "\t" + "AP\t"  +ndcg/(double)counter ) ;
	  //	System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Kendall\t"  +kendallsTau/(double)counter ) ;
	  	//System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Spear\t"  +spearman/(double)counter);
	
	
	}
	
  RangeMap parseTemporalDimension(String docrange)
  {
	  	String[] temp = docrange.split("\\$");
	  	RangeMap rangeSet = TreeRangeMap.create();
		
		for(int i=0;i<temp.length;i++)
		{
			
			 String[] dates= temp[i].split("_");
			 rangeSet.put(Range.closed(Long.parseLong(dates[0]), Long.parseLong(dates[3])),i);
		}
		return rangeSet;
	  
  }
	
	
	void SetSimilarities(boolean forprune, Map<BytesRef, LongArrayBitVector> maps)
	{
		Similarity sim = null;
	
		if(forprune)
		{
			switch (Settings.similarity) {
			case 1:
				sim = new BM25SimilarityZP(1.2f,0.75f,maps); 
				break;
			case 2:
				sim = new LMDirichletSimilarityZP(maps); 
				break;
			case 3:
				sim = new LMJelinekMercerSimilarityZP(0.25f, maps); 
				break;
			default:
				break;
			};
			
		}
		else
		{
			switch (Settings.similarity) {
			
			case 1:
				sim = new BM25Similarity(); 
				break;
			case 2:
				sim = new LMDirichletSimilarity(); 
				break;
			case 3:
				sim = new LMJelinekMercerSimilarity(0.25f); 
				break;
			default:
				break;
			};
			
			
			
		}
		
		pruningmethod.GetSearcher().setSimilarity(sim);
		
	}
	
	 private static String padd = "                                    ";
	  private String format(String s, int minLen) {
	    s = (s==null ? "" : s);
	    int n = Math.max(minLen,s.length());
	    return (s+padd).substring(0,n);
	  }
}
