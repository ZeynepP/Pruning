package Pruning.Methods;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.SmallFloat;

import Pruning.Experiments.Utils;
import cern.colt.map.OpenIntDoubleHashMap;


public abstract class PruningMethod {

	public IndexReader ir ;
	IndexSearcher searcher;
	Directory dir2 ;
    CollectionStatistics collectionStats ;
    public Fields fields;
    IndexReaderContext irc;
	int MAC_DOC;
	int collectiontype;
	String content;
    
    public enum PruningType 
	{
		  Random(-2),
		  PRP(-1),
		  CARMEL(0),
		  CIKM_IPU(1),
		  ECIR_2N2P(2),
		  SIMPLE(3),
		  SLIDING(4),
		  DYNAMIC(5),
		  QUANTILES(6),
		  KLAUS(7),
		  CDFGMM(8),
		  ECIRWINDOW(9),
		  GREEDYOPT(10),
		  BM25WINDOW(11);
		  
		  private int type;
		 
		  private PruningType (int value)
		  {
		    this.type = value;
		  }
		 
		  public int getPruningTypeValue() {
		    return type;
		  }
		
		
	}
    
    public static final float[] NORM_TABLE = new float[256];

	  static {
	    for (int i = 0; i < 256; i++) {
	      float floatNorm = SmallFloat.byte315ToFloat((byte)i);
	      NORM_TABLE[i] = 1.0f / (floatNorm * floatNorm);
	    }
	  }
	
    
	
 	boolean isForQuantiles ;
    
	int overallcounter = 0;
	float avgdl ;
	
	
	Terms allterms ;
	long sumTotalTermFreq;
	static byte[] norms = null ;
	boolean isfortest;

	

	public PruningMethod(boolean isfortest,boolean isforquantiles, String indexfile, String body, int maxdoc, int collectiont) throws IOException 
	{
		this.isfortest = isfortest;
		dir2 = FSDirectory.open(new File(indexfile));
		ir =  IndexReader.open(dir2);
	    searcher = new IndexSearcher(ir);// false read only
	    
	    searcher.setSimilarity(new BM25Similarity());
	    MAC_DOC = maxdoc * 100;
	    System.out.println(MAC_DOC);
	    content = body;
	    
	    this.isForQuantiles = isforquantiles;
	    fields = MultiFields.getFields(ir);
		allterms = fields.terms(content);
		
		if(!isfortest)
		{
			
			 irc = ir.getContext();
			  System.out.println("MAXDOC " + ir.maxDoc());
			    System.out.println(body);
			Initialize(body, collectiont);
		}
	}
	
	public void Initialize(String body, int collectiont) throws IOException
	{	
		collectionStats = searcher.collectionStatistics(body);
		avgdl = Utils.avgFieldLength(collectionStats);
		
		collectiontype = collectiont;
		System.out.println("Num docs = " + ir.numDocs());
		try {
		
			sumTotalTermFreq = allterms.getSumTotalTermFreq();//collection lenght |C|
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (AtomicReaderContext ctx : irc.leaves())
		{
			try {
				norms = ArrayUtils.addAll(norms,(byte[])ctx.reader().normValues(content).getSource().getArray());
				
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}
	
	
	public IndexSearcher GetSearcher()
	{
		return searcher;
		
	}
	
	public Map<Term,OpenIntDoubleHashMap> GetPostingsForTerm(Term tempterm) throws IOException
	{

		Map<Term,OpenIntDoubleHashMap> result = new HashMap<Term, OpenIntDoubleHashMap>();
		OpenIntDoubleHashMap temp;
		
		TermsEnum termEnum = allterms.iterator(null);
		
		termEnum.seekExact(tempterm.bytes(), true);
		DocsEnum docsenum = termEnum.docs(MultiFields.getLiveDocs(ir), null);
		    
		//DocsAndPositionsEnum docsAndPositionsEnum  = MultiFields.getTermPositionsEnum(ir,MultiFields.getLiveDocs(ir), Settings.content, tempterm.bytes());

		
	 	if(docsenum!=null)
	 	{
	 		if(collectiontype != 2)
	 			temp = GetPostingsScores(docsenum,tempterm);
	 		else // PWA collection is too big to keep all postings for all term that's why I keep first max doc given in config as I do benchmarking for top 1000 
	 		{
	 			
	 			TermQuery q = new TermQuery(tempterm);
	 			TopDocs topdocs = searcher.search(q, MAC_DOC);
	 			//System.out.println(" total docs " + tempterm.text() + "  " + topdocs.scoreDocs.length);
	 			temp = GetPostingsScores(tempterm, docsenum, topdocs.scoreDocs);
	 		}
	 		if(temp!=null)
	 		{
		 			result.put(tempterm,temp);
	 		}
	 		
	 		
	 		
	 	}
		
		return result;
		
	}
	
	

	abstract OpenIntDoubleHashMap GetPostingsScores(DocsEnum docsEnum, Term tempterm) throws IOException;
	abstract OpenIntDoubleHashMap GetPostingsScores(Term term, DocsEnum docsEnum, ScoreDoc[] docs) throws IOException;	
	
	
}
