package Pruning_StateoftheArt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.SmallFloat;

import Pruning.Quantiles.Init;
import Pruning.Quantiles.Settings;
import Pruning.Quantiles.Utils;
import cern.colt.list.DoubleArrayList;
import cern.colt.map.OpenIntDoubleHashMap;


public abstract class PruningMethod {

	IndexReader ir ;
	IndexSearcher searcher;
	Directory dir2 ;
    CollectionStatistics collectionStats ;
    public Fields fields;
    IndexReaderContext irc;
	
    
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
	
	TermsEnum termEnum ;
	Terms allterms ;
	long sumTotalTermFreq;
	static byte[] norms = null ;
	

	

	public PruningMethod(boolean isforquantiles, String indexfile) throws IOException 
	{
		dir2 = FSDirectory.open(new File(indexfile));
		ir =  IndexReader.open(dir2);
	    searcher = new IndexSearcher(ir);// false read only
	    irc = ir.getContext();
	    
	    
		this.isForQuantiles = isforquantiles;
		collectionStats = searcher.collectionStatistics(Settings.content);
		avgdl = Utils.avgFieldLength(collectionStats);
		fields = MultiFields.getFields(ir);
		
		
		try {
			allterms = fields.terms(Settings.content);
			termEnum = allterms.iterator(null);
			sumTotalTermFreq = allterms.getSumTotalTermFreq();//collection lenght |C|
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (AtomicReaderContext ctx : irc.leaves())
		{
			try {
				norms = ArrayUtils.addAll(norms,(byte[])ctx.reader().normValues(Settings.content).getSource().getArray());
				
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	
	public Map<Term,OpenIntDoubleHashMap> GetPostingsForTerm(Term tempterm) throws IOException
	{
		TermsEnum termEnum2  = allterms.iterator(null);

		Map<Term,OpenIntDoubleHashMap> result = new HashMap<Term, OpenIntDoubleHashMap>();
		OpenIntDoubleHashMap temp;
		DocsAndPositionsEnum docsAndPositionsEnum  = MultiFields.getTermPositionsEnum(ir,MultiFields.getLiveDocs(ir), Settings.content, tempterm.bytes());

		//System.out.println(tempterm.text());
		
	 	termEnum2.seekExact(tempterm.bytes(), true);
	 	overallcounter += termEnum2.docFreq();

	 	if(docsAndPositionsEnum!=null)
	 	{
	 		temp = GetPostingsScores(docsAndPositionsEnum,tempterm);
	 		if(temp!=null)
	 		{
		 			result.put(tempterm,temp);
	 		}
	 		
	 		
	 		
	 	}
		
		return result;
		
	}
	
	

	abstract OpenIntDoubleHashMap GetPostingsScores(DocsAndPositionsEnum docsAndPositionsEnum, Term tempterm) throws IOException;
	
	
	
}
