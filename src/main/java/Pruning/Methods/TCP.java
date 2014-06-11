package Pruning.Methods;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.TermStatistics;


import cern.colt.function.IntDoubleProcedure;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public class TCP  extends PruningMethod {

	int topk = 10;
	public TCP(boolean isforquantiles, String indexdir, int topk) throws IOException {
		
		super(isforquantiles,indexdir);
		this.topk=topk;
		
	}

	OpenIntDoubleHashMap GetPostingsScores(DocsAndPositionsEnum docsAndPositionsEnum, Term tempterm) throws IOException
	{
		
		final OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		int docid;
		float doclen;
		int freq;
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		double docscore;
		TermContext termState = TermContext.build(irc, tempterm, true); // cache term lookups!
		TermStatistics termStats= searcher.termStatistics(tempterm, termState);
		double topkscore;
		
		float idf = RankingFunctions.idf(termStats.docFreq()  , collectionStats.maxDoc());
	
			 
		while ((docid = docsAndPositionsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

			    doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
		 	    freq =  docsAndPositionsEnum.freq();
				docscore = RankingFunctions.BM25(avgdl, freq,  doclen, idf);

				map.put(docid, docscore);
				
			}
				
					
		
		
		
		map.pairsSortedByValue(keys, values);
		if(values.size()-topk>0) // no need to sort etc. 
		{
			topkscore = values.get(values.size()-topk);
		}
		else
			topkscore = values.get(0);
		
			final double top = topkscore;
			
			IntDoubleProcedure procedure = new IntDoubleProcedure() {
				public boolean apply(int arg0, double arg1) {
					// TODO Auto-generated method stub

					double tempscore = 0;
					tempscore =arg1/top;
					
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
