package Pruning.Experiments;

import it.unimi.dsi.bits.LongArrayBitVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.benchmark.quality.QualityFilterParser;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.trec.TemporalTrecTopicsReader;
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

import Pruning.Methods.PruningMethod;

public class EvaluationMetrics {
	
	
	File  topicsFiletemporal;
	FileReader temporalfr ;
	BufferedReader temporalbr;
	IndexReader ir;
	IndexSearcher searcher;

	PruningMethod pruningmethod;
	int N = 86400000;// 1 day to milisecond as queries calculated by index date
	
	QualityQuery qqs[];
	QualityQueryParser qqParser;
	QualityFilterParser fParser;
	
	
	public EvaluationMetrics( PruningMethod p) throws IOException
	{
		
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
	}
	

	/** Calculate all evaluation metrics with given real and predicted rating matrices. 
	 * @throws IOException */
	public void run(Prune prune) throws IOException
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
				      NumericRangeFilter<Long> UnixTimefilter = NumericRangeFilter.newLongRange(Settings.datefield, (filter.getMin()*N)+ Settings.dateinit, (filter.getMax()*N)+ Settings.dateinit, true, true);
				      
				      
				      SetSimilarities(false, null);//no pruning
				      
				      TopDocs tdnotpruned = searcher.search(q,UnixTimefilter,1000);
				      ScoreDoc[] sdnotpruned = tdnotpruned.scoreDocs;
				      int[] nopruningkeys = new int[sdnotpruned.length];
				      double[]  nopruningvalues = new double[sdnotpruned.length];
				  	  
				  	  if(sdnotpruned.length > 0)
				  	  {
				  		  	  counter++;
				  		  		
						      for(int k=0;k<sdnotpruned.length;k++)
						      {
						    	  nopruningkeys[k] = sdnotpruned[k].doc;
						    	  nopruningvalues[k] = sdnotpruned[k].score;
						    	  
						      }
						      SetSimilarities(true, prune.maps);//pruned
						      TopDocs tdpruned = searcher.search(q,UnixTimefilter,1000);
						      ScoreDoc[] sdpruned = tdpruned.scoreDocs;

						      int[] prunedkeys = new int[sdpruned.length];
						      double[]  prunedvalues = new double[sdpruned.length];
						     
						      
						      for(int k=0;k<sdpruned.length;k++)
						      {
						    	  
						    	  prunedkeys[k] = sdpruned[k].doc;
						    	  prunedvalues[k] = sdpruned[k].score;
						    	  
						      }
						      
						      
						      
						  	ndcg += Distance.distanceNDCG(nopruningkeys.clone(), nopruningvalues.clone(), prunedkeys.clone(), prunedvalues.clone());
						  	kendallsTau += Distance.distanceKendall(nopruningkeys.clone(), nopruningvalues.clone(), prunedkeys.clone(), prunedvalues.clone(),nopruningkeys.length);
						  	spearman += Distance.distanceSpearman(nopruningkeys.clone(), nopruningvalues.clone(), prunedkeys.clone(), prunedvalues.clone(),nopruningkeys.length);
						  	
				  	  }

		    	}

		    	catch(Exception ex)
		    	{
		    		System.out.println(ex.toString());
		    	}
		    	
		    	
		    	
	
		    
	  }
	  

	  	System.out.println(" NDCG\t" + ndcg  + "\t" + counter +"\t" +nQueries ) ;
	  	System.out.println(" Kendall\t" + kendallsTau + "\t" + counter +"\t" +nQueries ) ;
	  	System.out.println(" Spear\t" + spearman  + "\t" + counter +"\t" +nQueries ) ;
  	
  	
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
}
