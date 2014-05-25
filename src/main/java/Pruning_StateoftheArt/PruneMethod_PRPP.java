package Pruning_StateoftheArt;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSetIterator;

import Pruning.Quantiles.MainQuantiles;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class PruneMethod_PRPP extends PruningMethod {

	double mean;
	double sd;

	public PruneMethod_PRPP(boolean isforquantiles,String indexdir) throws IOException {
		
		super(isforquantiles,indexdir);

		mean = mean(); 
		sd = standardDeviation();
		
		System.out.println("mean = " + mean + " :: sd = " + sd);
		
	}
	
	
	
	OpenIntDoubleHashMap GetPostingsScores(
			DocsAndPositionsEnum docsAndPositionsEnum,
			Term tempterm) throws IOException {
		
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
