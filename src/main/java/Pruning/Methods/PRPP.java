package Pruning.Methods;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;

import Pruning.Quantiles.MainQuantiles;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class PRPP extends PruningMethod {

	double mean;
	double sd;

	public PRPP(boolean isforquantiles,String indexdir, String content, int maxdoc, int type) throws IOException {
		
		super(isforquantiles,indexdir,content,maxdoc,type);
	

		mean = mean(); 
		sd = standardDeviation();
		searcher.setSimilarity(new LMJelinekMercerSimilarity(0.6f));
		System.out.println("mean = " + mean + " :: sd = " + sd);
		
	}
	
	
	
	OpenIntDoubleHashMap GetPostingsScores(
			DocsEnum docsAndPositionsEnum,
			Term tempterm) throws IOException {
		TermsEnum termEnum2  = allterms.iterator(null);
		termEnum2.seekExact(tempterm.bytes(), true);
		OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		float  sumDocFreq = termEnum2.totalTermFreq();
		float collectionprobab= (sumDocFreq) / (sumTotalTermFreq);
		

		while ((docid = docsAndPositionsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

				float doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
				float freq =  docsAndPositionsEnum.freq();
				float prelevancegivendocument = (float) (0.5 + (0.1 * Math.tanh((doclen - mean)/sd)));
				float pquerylikelihood = RankingFunctions.LMJM(0.6f, freq, doclen, collectionprobab) ;
				double docscore = (pquerylikelihood/collectionprobab) * (prelevancegivendocument / (1.0f- prelevancegivendocument));

				map.put(docid, docscore);
				
			}
		
		
			if(!isForQuantiles)
					map.pairsSortedByValue(keys, values);

		return map;
		
		
		
	}
	
	OpenIntDoubleHashMap GetPostingsScores(String term, DocsEnum docsAndPositionsEnum, ScoreDoc[] scoredocs) throws IOException {

		TermsEnum termEnum = allterms.iterator(null);
		OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		float  sumDocFreq = termEnum.totalTermFreq();
		float collectionprobab= (sumDocFreq) / (sumTotalTermFreq);
		

		for(int i=0;i<scoredocs.length;i++)
		{
				docid = scoredocs[i].doc;
				float pquerylikelihood = scoredocs[i].score; //RankingFunctions.LMJM(0.6f, freq, doclen, collectionprobab) ;
				float doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
				
				float prelevancegivendocument = (float) (0.5 + (0.1 * Math.tanh((doclen - mean)/sd)));

				double docscore = (pquerylikelihood/collectionprobab) * (prelevancegivendocument / (1.0f- prelevancegivendocument));
	
				map.put(docid, docscore);
				
				
			}
		
		
			if(!isForQuantiles)
					map.pairsSortedByValue(keys, values);

		return map;
	}
	
	float mean() {
		
		float sum=0f;
		for(int i=0;i<ir.maxDoc();i++)
		{
			sum+= NORM_TABLE[ norms[(int) i] & 0xFF];
			
		}
		
		
		return sum /( ir.maxDoc() * 1.0f);
	   
	  }
	
	
	float standardDeviation() {
		
		int sum = 0;

 
        for(int i=0;i<ir.maxDoc();i++)
		{
            sum += Math.pow(NORM_TABLE[norms[(int) i] & 0xFF] - mean, 2);
		}
        return (float) Math.sqrt( sum / ( ir.maxDoc() - 1.0 ) ); // sample
		

	   
	  }


}
