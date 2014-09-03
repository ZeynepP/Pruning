package Pruning.Methods;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.SimilarityBase;

import Pruning.Quantiles.MainQuantiles;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class ECIR2N2P extends PruningMethod {
	

	public ECIR2N2P(boolean isfortest,boolean isForQuantiles, String indexdir, String content, int maxdoc, int type) throws IOException {
		
		super(isfortest, isForQuantiles,indexdir,content,maxdoc,type);
		
		
	}

	@Override
	OpenIntDoubleHashMap GetPostingsScores( DocsEnum docsAndPositionsEnum,Term tempterm) throws IOException {
		// TODO Auto-generated method stub
		
		OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		TermsEnum termEnum = allterms.iterator(null);
		termEnum.seekExact(tempterm.bytes(), true);
		float  sumDocFreq = termEnum.totalTermFreq();

			 	
		while ((docid = docsAndPositionsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

				float doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
				float freq =  docsAndPositionsEnum.freq();
				double docscore = RankingFunctions.ScoreECIR2011(doclen, sumTotalTermFreq, freq, sumDocFreq) ;
				//System.out.println(docscore + " " + docid + " for term " + tempterm.text());
				map.put(docid, docscore);
				
			}
		
	
		return map;
		
		
	}
	
	@Override
	OpenIntDoubleHashMap GetPostingsScores(Term term,DocsEnum docsAndPositionsEnum, ScoreDoc[] scoredocs) throws IOException {
		// TODO Auto-generated method stub
	
		OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		TermsEnum termEnum = allterms.iterator(null);
		termEnum.seekExact(term.bytes(), true);
		
		float  sumDocFreq = termEnum.totalTermFreq();

			 	
		for(int i=0;i<scoredocs.length;i++)
		{
				docsAndPositionsEnum = termEnum.docs(MultiFields.getLiveDocs(ir), null);
				// STUPID BUT INEED TO DO THIS TO HAVE THE SAME ID BECAUSE The behavior of this method is undefined when called with target ≤ current, or after the iterator has exhausted. Both cases may result in unpredicted behavior.
				// THAT IS WHY I USED TO HAVE DIFFERENT IDS ANYWAY I WILL KEEP THE IF STATEMENT FOR A WHILE
				docid = scoredocs[i].doc;
				int tempdocid = docsAndPositionsEnum.advance(docid);
				if(tempdocid==docid)//it should be always the case but checking anyway PROBLEM TODO:CHECK IT IMPORTANT
				{

					float doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
					float freq =  docsAndPositionsEnum.freq();
					double docscore = RankingFunctions.ScoreECIR2011(doclen, sumTotalTermFreq, freq, sumDocFreq) ;
					map.put(docid, docscore);
				}
				else{System.out.println(tempdocid + " " + docid);}
				
			}
		
		
		if(!isForQuantiles)
				map.pairsSortedByValue(keys, values);

		
		return map;
		
		
	}

	
}
