package Pruning.Methods;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.SimilarityBase;

import Pruning.Quantiles.MainQuantiles;
import cern.colt.function.IntDoubleProcedure;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class IPU extends PruningMethod {

	public IPU(boolean isfortest,boolean isforquantiles, String indexdir, String content, int maxdoc, int type) throws IOException {
		
		super(isfortest,isforquantiles,indexdir,content,maxdoc,type);
		searcher.setSimilarity(new LMJelinekMercerSimilarity(0.6f));

	}
	
	@Override
	OpenIntDoubleHashMap GetPostingsScores(
			DocsEnum docsAndPositionsEnum,
			Term tempterm) throws IOException 
	{
		TermsEnum termEnum2  = allterms.iterator(null);
		termEnum2.seekExact(tempterm.bytes(), true);
		final OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		float  sumDocFreq = termEnum2.totalTermFreq();
		float collectionprobab= (sumDocFreq) / (sumTotalTermFreq);
		float alldocscore =0;
	
		while ((docid = docsAndPositionsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

				float doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
				float freq =  docsAndPositionsEnum.freq();
				float docscore = RankingFunctions.LMJM(0.6f, freq, doclen, collectionprobab) ;
				alldocscore+=docscore;

				map.put(docid, docscore);
				
		}	

		
		
		
			map.pairsSortedByValue(keys, values);

			final double allscore = alldocscore;
			IntDoubleProcedure procedure = new IntDoubleProcedure() {
				public boolean apply(int arg0, double arg1) {
					
					double tempscore = 0;
					tempscore = RankingFunctions.ScoreCIKM2012(allscore,arg1);
					
					//System.out.println(arg0 + " , " + tempscore );
					map.put(arg0,tempscore);
					return true;
				}
			};

		//	System.out.println(map.get(id) + ", " + map.size());
			map.forEachPair(procedure);
		//	System.out.println(map.get(id) + ", " + map.size());
			
		
		
		
		
		return map;
		
		
		
	}


	@Override
	OpenIntDoubleHashMap GetPostingsScores(Term term,DocsEnum docsAndPositionsEnum, ScoreDoc[] scoredocs) throws IOException 
	{

		final OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		float alldocscore =0;
	
		for(int i=0;i<scoredocs.length;i++)
		{
				docid = scoredocs[i].doc;
				float docscore = scoredocs[i].score;
				
				alldocscore+=docscore;

				map.put(docid, docscore);
				
				
		}	

		
		
		
			map.pairsSortedByValue(keys, values);

			final double allscore = alldocscore;
			IntDoubleProcedure procedure = new IntDoubleProcedure() {
				public boolean apply(int arg0, double arg1) {
					
					double tempscore = 0;
					tempscore = RankingFunctions.ScoreCIKM2012(allscore,arg1);
					
					//System.out.println(arg0 + " , " + tempscore );
					map.put(arg0,tempscore);
					return true;
				}
			};

		//	System.out.println(map.get(id) + ", " + map.size());
			map.forEachPair(procedure);
		//	System.out.println(map.get(id) + ", " + map.size());
			
		
		
		
		
		return map;
		
		
		
	}


	
}
