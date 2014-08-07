package Pruning.Experiments;


import it.unimi.dsi.bits.LongArrayBitVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.lucene.benchmark.quality.QualityFilterParser;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.trec.TemporalTrecTopicsReader;
import org.apache.lucene.benchmark.quality.utils.DocNameExtractor;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.TemporalSimpleQQParser;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BM25SimilarityZP;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarityZP;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarityZP;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

import cern.colt.map.OpenIntIntHashMap;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import Pruning.Methods.PruningMethod;

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
	SpearmansCorrelation spearmancor;
	APCorrelation apcorr ;
	
	public EvaluationMetrics( PruningMethod p) throws IOException
	{
		kendallcor = new KendallsCorrelation();
		spearmancor = new SpearmansCorrelation();
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
		double spearman=0;
		double ndcg=0;

	   
	    int nQueries =  qqs.length;
	    int counter = 0;

	  for (int i=0; i<nQueries; i++) {
		    	
		    	try{
		    		
		    	
				      QualityQuery qq = qqs[i];
				      Query q = qqParser.parse(qq);
				      NumericRangeFilter<Long> filter = fParser.parseFilter(qq);
				      NumericRangeFilter<Long> UnixTimefilter = filter;
				      if(Settings.collectiontype == 2)
				      {
				    	  	UnixTimefilter = NumericRangeFilter.newLongRange(Settings.datefield, (filter.getMin()*N)+ Settings.dateinit, (filter.getMax()*N)+ Settings.dateinit, true, true);
				      }
				      
				      SetSimilarities(false, null);//no pruning
				      
				      TopDocs tdnotpruned = searcher.search(q,UnixTimefilter,1000);
				      ScoreDoc[] sdnotpruned = tdnotpruned.scoreDocs;
				      double[] nopruningkeys = new double[sdnotpruned.length];
				      double[]  nopruningvalues = new double[sdnotpruned.length];
				  	  OpenIntIntHashMap nopruned = new OpenIntIntHashMap();
				  	  OpenIntIntHashMap pruned = new OpenIntIntHashMap();
				  	  
				  	  if(sdnotpruned.length > 0)
				  	  {
				  		  	  counter++;
				  		  		
						      for(int k=0;k<sdnotpruned.length;k++)
						      {
						    	  nopruned.put( sdnotpruned[k].doc,k+1);
						    	  nopruningkeys[k] = k+1;//sdnotpruned[k].doc;
						    	 // nopruningvalues[k] = sdnotpruned[k].doc;//sdnotpruned[k].score;
						    	  
						      }
						      SetSimilarities(true, prune.maps);//pruned
						      TopDocs tdpruned = searcher.search(q,UnixTimefilter,1000);
						      ScoreDoc[] sdpruned = tdpruned.scoreDocs;

						      double[] prunedkeys = new double[sdpruned.length];
						      double[]  prunedvalues = new double[sdpruned.length];
						     
						      
						      for(int k=0;k<sdpruned.length;k++)
						      {
						    	  prunedkeys[k] = nopruned.get(sdpruned[k].doc);
						    	 // prunedkeys[k] = sdpruned[k].doc;
						    	 // prunedvalues[k] = sdpruned[k].score;
						    	  
						      }
						    
						    double temp = -1;
						    if(nopruningkeys.length <3)
						    {
						    	counter--;
						    	
						    }
						    if(nopruningkeys.length >=3)
						    {
						    	kendallsTau+= (temp==-1?kendallcor.correlation(nopruningkeys.clone(), prunedkeys.clone(),nopruningkeys.length):0);
						    	ndcg+= (temp==-1?apcorr.APCorrelation(nopruningkeys.clone(), prunedkeys.clone()):0);
						    	spearman+= (temp==-1?spearmancor.correlation(nopruningkeys.clone(), prunedkeys.clone()):0);
						    }
				  	
				  	  }

		    	}

		    	catch(Exception ex)
		    	{
		    		System.out.println(ex.toString());
		    	}    	
	  }
	  	System.out.println( Settings.prunetype + "\t" + pruneratio + "\t" + "AP\t"  +ndcg/(double)counter ) ;
	  	System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Kendall\t"  +kendallsTau/(double)counter ) ;
	  	//System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Spear\t"  +spearman/(double)counter);
		    
	  }
	  
	  
	  public void runRangeFieldWithLogger(Prune prune, float pruneratio) throws IOException
		{
		  
		  StringWriter swnoprune = new StringWriter();
		  PrintWriter loggernoprune=  new PrintWriter(swnoprune); 
		  StringWriter swprune = new StringWriter();
		  PrintWriter loggerprune=  new PrintWriter(swprune); 
		  
	      
			double kendallsTau=0;
			double spearman=0;
			double ndcg=0;
			DocNameExtractor rangesxt =  new DocNameExtractor(Settings.rangefield);
			RangeMap tempmap = TreeRangeMap.create();
			String docrange;
		    int nQueries =  qqs.length;
		    int counter = 0;
		    int countertemporal = 0;
		    DocNameExtractor xt = new DocNameExtractor(Settings.docNameField);
		  for (int i=0; i<nQueries; i++) {
			    	
			    	try{
			    		
			    	
					      QualityQuery qq = qqs[i];
					      Query q = qqParser.parse(qq);
					      NumericRangeFilter<Long> filter = fParser.parseFilter(qq);
					      
					      
					      SetSimilarities(false, null);//no pruning
					      
					      TopDocs tdnotpruned = searcher.search(q,null,Settings.maxdocs);
					      ScoreDoc[] sdnotpruned = tdnotpruned.scoreDocs;
					      double[] nopruningkeys = new double[1000];
					      double[]  nopruningvalues = new double[1000];
					  	  countertemporal = 0;
					  	  
					  	  if(sdnotpruned.length > 0)
					  	  {
					  		  	  counter++;
					  		  	  
							      for(int k=0;k<sdnotpruned.length;k++)
							      {
							    	 
							    	  docrange = rangesxt.docName(searcher,sdnotpruned[k].doc);
									  RangeMap rangeSet = parseTemporalDimension(docrange);
									  tempmap = rangeSet.subRangeMap(Range.closed(filter.getMin(), filter.getMax()));
									    	 
									    	
									    if(tempmap.asMapOfRanges().size() > 0)
									    {
									    	nopruningkeys[countertemporal] = sdnotpruned[k].doc;
									    	nopruningvalues[countertemporal] = sdnotpruned[k].score;
									    	countertemporal++;
									    	String docName = xt.docName(searcher,sdnotpruned[k].doc);
									    	loggernoprune.println(
									    	          qq.getQueryID()       + sep +
									    	          "Q0"                   + sep +
									    	          format(docName.replaceAll("_0", ""),20)    + sep +
									    	          format(""+i,7)        + sep +
									    	          nf.format(sdnotpruned[k].score) + sep +
									    	          "lucene"
									    	          );
									    }
							    	  
									    if(countertemporal == 1000)
									    	break;
							    	 
							    	  
							      }
							     
							   
							      SetSimilarities(true, prune.maps);//pruned
							      TopDocs tdpruned = searcher.search(q,null,Settings.maxdocs);
							      ScoreDoc[] sdpruned = tdpruned.scoreDocs;
							      
							      
							      
							      double[] prunedkeys = new double[nopruningkeys.length];
							      double[]  prunedvalues = new double[nopruningkeys.length];
							      countertemporal = 0;
							      
							      for(int k=0;k<sdpruned.length;k++) 
							      {
								    	docrange = rangesxt.docName(searcher,sdpruned[k].doc);
									    RangeMap rangeSet = parseTemporalDimension(docrange);
									    tempmap = rangeSet.subRangeMap(Range.closed(filter.getMin(), filter.getMax()));
									    	 
									    	
									    if(tempmap.asMapOfRanges().size() > 0)
									    {
									    	 prunedkeys[countertemporal] = sdpruned[k].doc;
									    	 prunedvalues[countertemporal] = sdpruned[k].score;
									    	 countertemporal++;
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
							    	  
									    if(countertemporal == 1000)
									    	break;
							    	 
							    	  
							      }
							      
							      
								    double temp = -1;
								    if(nopruningkeys.length == 1)
								    {
								    	if(nopruningkeys[0] == prunedkeys[0])
								    		temp=0;
								    	else temp = 1;
								    	
								    }
								      
								    kendallsTau+= (temp==-1?kendallcor.correlation(nopruningkeys.clone(), prunedkeys.clone(),nopruningkeys.length):0);
								    ndcg+= (temp==-1?apcorr.APCorrelation(nopruningkeys.clone(), prunedkeys.clone()):0);
								    spearman+= (temp==-1?spearmancor.correlation(nopruningkeys.clone(), prunedkeys.clone()):0); 
						  	
					  	  }

			    	}

			    	catch(Exception ex)
			    	{
			    		System.out.println(ex.toString());
			    	}
			    	
			    	
			    	
		
			    
		  }

		     
	     BufferedReader resultsnopruning = new BufferedReader(new StringReader(swnoprune.toString()));
	     BufferedReader resultspruned = new BufferedReader(new StringReader(swprune.toString()));
	     
	     FileUtils.writeStringToFile(new File("NoprunedWIKI"), swnoprune.toString());
	     FileUtils.writeStringToFile(new File(Settings.prunetype + "_" + pruneratio), swprune.toString());
	     

	      
		  
	  	System.out.println( Settings.prunetype + "\t" + pruneratio + "\t" + "NDCG\t"  +ndcg/(double)counter ) ;
	  	System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Kendall\t"  +kendallsTau/(double)counter ) ;
	  	System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Spear\t"  +spearman/(double)counter);
  	
  	
	}
	  public void runRangeField(Prune prune, float pruneratio) throws IOException
		{
		
			double kendallsTau=0;
			double spearman=0;
			double ndcg=0;
			DocNameExtractor rangesxt =  new DocNameExtractor(Settings.rangefield);
			RangeMap tempmap = TreeRangeMap.create();
			String docrange;
		    int nQueries =  qqs.length;
		    int counter = 0;
		    int countertemporal = 0;
		  for (int i=0; i<nQueries; i++) {
			    	
			    	try{
			    		
			    	
					      QualityQuery qq = qqs[i];
					      Query q = qqParser.parse(qq);
					      NumericRangeFilter<Long> filter = fParser.parseFilter(qq);
					      
					      
					      SetSimilarities(false, null);//no pruning
					      
					      TopDocs tdnotpruned = searcher.search(q,null,Settings.maxdocs);
					      ScoreDoc[] sdnotpruned = tdnotpruned.scoreDocs;
					      double[] nopruningkeys = new double[1000];
					      
					  	  countertemporal = 0;
					  	  
					  	  if(sdnotpruned.length > 0)
					  	  {
					  		  	  counter++;
					  		  	  
							      for(int k=0;k<sdnotpruned.length;k++)
							      {
							    	 
							    	  docrange = rangesxt.docName(searcher,sdnotpruned[k].doc);
									  RangeMap rangeSet = parseTemporalDimension(docrange);
									  tempmap = rangeSet.subRangeMap(Range.closed(filter.getMin(), filter.getMax()));
									    	 
									    	
									    if(tempmap.asMapOfRanges().size() > 0)
									    {
									    	nopruningkeys[countertemporal] = sdnotpruned[k].doc;
									    	
									    	//System.out.println("NoPruned\t" + i + "\t" + sdnotpruned[k].doc + "\t" + sdnotpruned[k].score + "\t" + countertemporal );
									    	countertemporal++;
									    	
									    }
							    	  
									    if(countertemporal == 1000)
									    	break;
							    	 
							    	  
							      }
							     
							   
							      SetSimilarities(true, prune.maps);//pruned
							      TopDocs tdpruned = searcher.search(q,null,Settings.maxdocs);
							      ScoreDoc[] sdpruned = tdpruned.scoreDocs;
							      
							      
							      
							      double[] prunedkeys = new double[nopruningkeys.length];
							     
							      countertemporal = 0;
							      
							      for(int k=0;k<sdpruned.length;k++) 
							      {
								    	docrange = rangesxt.docName(searcher,sdpruned[k].doc);
									    RangeMap rangeSet = parseTemporalDimension(docrange);
									    tempmap = rangeSet.subRangeMap(Range.closed(filter.getMin(), filter.getMax()));
									    	 
									    	
									    if(tempmap.asMapOfRanges().size() > 0)
									    {
									    	 prunedkeys[countertemporal] = sdpruned[k].doc;
									    	 countertemporal++;
									    	
									    }
							    	  
									    if(countertemporal == 1000)
									    	break;
							    	 
							    	  
							      }
							      
							      
							      double temp = -1;
								    if(nopruningkeys.length == 1)
								    {
								    	if(nopruningkeys[0] == prunedkeys[0])
								    		temp=0;
								    	else temp = 1;
								    	
								    }
								      
								    kendallsTau+= (temp==-1?kendallcor.correlation(nopruningkeys.clone(), prunedkeys.clone(),nopruningkeys.length):0);
								    ndcg+= (temp==-1?apcorr.APCorrelation(nopruningkeys.clone(), prunedkeys.clone()):0);
								    spearman+= (temp==-1?spearmancor.correlation(nopruningkeys.clone(), prunedkeys.clone()):0); 
						  	
					  	  }

			    	}

			    	catch(Exception ex)
			    	{
			    		System.out.println(ex.toString());
			    	}
			    	
			    	
			    	
		
			    
		  }
	      
		  
	  	System.out.println( Settings.prunetype + "\t" + pruneratio + "\t" + "NDCG\t"  +ndcg/(double)counter ) ;
	  	System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Kendall\t"  +kendallsTau/(double)counter ) ;
	  	System.out.println(Settings.prunetype + "\t" + pruneratio + "\t" + "Spear\t"  +spearman/(double)counter);
	
	
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
