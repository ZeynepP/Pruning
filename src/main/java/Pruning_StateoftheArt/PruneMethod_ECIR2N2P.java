package Pruning_StateoftheArt;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.similarities.SimilarityBase;

import Pruning.Quantiles.MainQuantiles;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class PruneMethod_ECIR2N2P extends PruningMethod {
	
	float  sumTotalTermFreq;

	public PruneMethod_ECIR2N2P(boolean isForQuantiles, String indexdir) throws IOException {
		
		super(isForQuantiles,indexdir);
		try {
			sumTotalTermFreq = allterms.getSumTotalTermFreq();//collection lenght |C|
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	OpenIntDoubleHashMap GetPostingsScores( DocsAndPositionsEnum docsAndPositionsEnum,Term tempterm) throws IOException {
		// TODO Auto-generated method stub
		
		OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		float  sumDocFreq = termEnum2.totalTermFreq();

			 	
		while ((docid = docsAndPositionsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

				float doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
				float freq =  docsAndPositionsEnum.freq();
				double docscore = RankingFunctions.ScoreECIR2011(doclen, sumTotalTermFreq, freq, sumDocFreq) ;
				map.put(docid, docscore);
				
			}
		
		
		if(!isForQuantiles)
				map.pairsSortedByValue(keys, values);

		
		return map;
		
		
	}

	
}
