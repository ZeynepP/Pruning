package Pruning.Methods;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.FieldCache.DocTerms;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;

import com.google.common.collect.Multimap;


public class DiversificationBased  extends PruningMethod{

	// Settings from files optimal values etc.
	public static Map<String, String> GMMresults;
	public static  Multimap<String, Integer> maxminlist;
	public static  Multimap<String, Integer> fdlist;
	String GmmFile;
	String maxminfile;
	DocTerms dates = null ;
	FieldCache f = 	FieldCache.DEFAULT;
	String temporalfield;
	int type, collectiontype;
	TemporalAspects aspects = null;
	double alpha = 0.25;
	int datecount;
	long dateinit;
	int sizeslide;
	int windowssize;
	DiversificationBased_Scoring scoring;
	Map<Integer,String> date_docid = new HashMap<Integer, String>();

	public DiversificationBased(boolean isfortest,boolean isforquantiles, String indexdir,String gmm, String maxmin, String rangefield,int type, int collectiontype, long dateinit, int dc, int wsize, int slidesize, String content, int maxdoc) throws IOException
	{

		super(isfortest,isforquantiles,indexdir,content,maxdoc,collectiontype);
		maxminfile = maxmin;
		GmmFile = gmm;
		this.temporalfield = rangefield;
		this.type = type;
		this.collectiontype = collectiontype;
		this.dateinit = dateinit;
		this.datecount = dc;
		windowssize =wsize;
		sizeslide = slidesize;
		
		if(!isfortest)
		{
		
			Initialize();
		
		
			for (AtomicReaderContext ctx : irc.leaves())
			{
				try {
					dates = f.getTerms(ctx.reader(),rangefield );	
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(this.type != PruningType.DYNAMIC.getPruningTypeValue() )//&& this.collectiontype!=1) // no wiki no need optimal windows size etc.
			{
				aspects = new TemporalAspects(type, null, alpha, datecount, sizeslide, windowssize);
				
			}
		}
	}
	


	
	void Initialize()
	{
		if(type == PruningType.DYNAMIC.getPruningTypeValue())
		{
			GMMresults = DiversificationBased_Utils.ReadResultsFromGMM(type, GmmFile);
		}
		else 
		{
			if(collectiontype == 1) // Just for WIKI
			{
				maxminlist = DiversificationBased_Utils.ReadResultsMaxMin(0,maxminfile);
			    fdlist = DiversificationBased_Utils.ReadResultsMaxMin(1,GmmFile);
			}

		}
		
	}

	@Override
	OpenIntDoubleHashMap GetPostingsScores(DocsEnum docsAndPositionsEnum, Term tempterm) throws IOException
	{
		
		final OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		String date;
		int docid;
		BytesRef test = new BytesRef();
		float doclen;
		int freq;
		float docscore;
		
		if(this.type == PruningType.DYNAMIC.getPruningTypeValue())
		{
			aspects = new TemporalAspects(type, tempterm.text(), alpha, datecount, sizeslide, windowssize);
		}
		
		TermContext termState = TermContext.build(irc, tempterm, true); // cache term lookups!
		TermStatistics termStats= searcher.termStatistics(tempterm, termState);
	
		float idf = RankingFunctions.idf(termStats.docFreq()  ,collectionStats.maxDoc());
		IntArrayList keys = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		double maxscore = 0;

		scoring = new DiversificationBased_Scoring(aspects.rangeSet, collectiontype);
		//TopDocs d= searcher.search(new TermQuery(tempterm), null, 1000);
		//System.out.println(tempterm.text() + " == " + d.scoreDocs.length);
		while ((docid = docsAndPositionsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

			if(date_docid.containsKey(docid))
				date = date_docid.get(docid);
			else // TODO UPDATE JUST FOR PWA FOR WIKI MULTIMAP SHOULD BE USED
			{
				if(collectiontype == 1)
				{
						date = dates.getTerm((int)docid, test).utf8ToString();
			
						if(date.equals("")) // I do not know why but sometimes it returns ""
							date = ir.document(docid).get(temporalfield);
				}
				else
						date = ir.document(docid).get(temporalfield);
				
				date_docid.put(docid, date);
			}
				doclen =  NORM_TABLE[ norms[(int) docid] & 0xFF];
		 	    freq =  docsAndPositionsEnum.freq();
				docscore = RankingFunctions.BM25(avgdl, freq,  doclen, idf);
			
				
				scoring.GetDocumentsWindowsGuava(docid, date, docscore, alpha, dateinit,datecount); // to map docid to windows

				if(maxscore < docscore)
					maxscore = docscore;
				
			
				map.put(docid, docscore);
				
		}	

		
		return scoring.DistanceMethod(super.isForQuantiles, type, tempterm.text(), map, maxscore);
	}
	
	@Override
	OpenIntDoubleHashMap GetPostingsScores(Term term, DocsEnum docsAndPositionsEnum, ScoreDoc[] scoredocs) throws IOException
	{
		
		
		final OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		String date;
		int docid;
		BytesRef test = new BytesRef();
		float doclen;
		int freq;
		float docscore;
		
		if(this.type == PruningType.DYNAMIC.getPruningTypeValue())
		{
			aspects = new TemporalAspects(type, term.text(), alpha, datecount, sizeslide, windowssize);
		}

		double maxscore = 0;

		scoring = new DiversificationBased_Scoring(aspects.rangeSet, collectiontype);
		for(int i=0;i<scoredocs.length;i++)
		{
				docid = scoredocs[i].doc;
				docscore = scoredocs[i].score;
				if(collectiontype == 1)
				{
						date = dates.getTerm((int)docid, test).utf8ToString();
			
						if(date.equals("")) // I do not know why but sometimes it returns ""
							date = ir.document(docid).get(temporalfield);
				}
				else
						date = ir.document(docid).get(temporalfield);
	

				scoring.GetDocumentsWindowsGuava(docid, date, docscore, alpha, dateinit,datecount); // to map docid to windows
	
					if(maxscore < docscore)
						maxscore = docscore;
					
				
					map.put(docid, docscore);
				
				
		}	

		
		return scoring.DistanceMethod(super.isForQuantiles,type, term.text(), map, maxscore);
	}







	

	
	
}
