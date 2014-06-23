package Pruning.Quantiles;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;

import Pruning.Methods.ECIR2N2P;
import Pruning.Methods.IPU;
import Pruning.Methods.PRPP;
import Pruning.Methods.TCP;



public class Utils {
	
	
	
	public static float avgFieldLength(CollectionStatistics collectionStats) {
	    final long sumTotalTermFreq = collectionStats.sumTotalTermFreq();
	    if (sumTotalTermFreq <= 0) {
	      return 1f;       // field does not exist, or stat is unsupported
	    } else {
	      return (float) (sumTotalTermFreq / (double) collectionStats.maxDoc());
	    }
	  }
	
	
	public static String SetPruneTypeandFilename(int type, String indexdir, boolean isquantiles) throws IOException
	{
		String filename = "";
		if(type == 0)
		{
			filename = "TCP" ;
			Init.pruningmethod = new TCP(isquantiles,indexdir,10);
		}
		else if(type == 1)
		{
			filename = "IPU";
			Init.pruningmethod = new IPU(isquantiles,indexdir);
		}
		else if(type == 2)
		{
			filename = "2N2P";
			Init.pruningmethod = new ECIR2N2P(isquantiles,indexdir);
		}
		else if(type == -1)
		{
			filename = "PRP";
			Init.pruningmethod = new PRPP(isquantiles,indexdir);
		}


		
		return filename;
		
	
		
	}
	


}
