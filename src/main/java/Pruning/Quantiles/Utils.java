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

import Pruning_StateoftheArt.PruneMethod_ECIR2N2P;
import Pruning_StateoftheArt.PruneMethod_IPU;
import Pruning_StateoftheArt.PruneMethod_PRPP;
import Pruning_StateoftheArt.PruneMethod_TCP;



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
			Init.pruningmethod = new PruneMethod_TCP(isquantiles,indexdir,10);
		}
		else if(type == 1)
		{
			filename = "IPU";
			Init.pruningmethod = new PruneMethod_IPU(isquantiles,indexdir);
		}
		else if(type == 2)
		{
			filename = "2N2P";
			Init.pruningmethod = new PruneMethod_ECIR2N2P(isquantiles,indexdir);
		}
		/*else if(type == 3)
		{
			filename = "Simple"+MainQuantiles.alpha+Settings.withTF;
			Init.pruningmethod = new PruneMethod_DiversificationBased(type);
		}
		else  if(type ==4)
		{
			filename = "Sliding"+MainQuantiles.alpha+Settings.withTF;
			Init.pruningmethod = new PruneMethod_DiversificationBased(type);
		}
		else if(type == 5)
		{
			filename = "Dynamic"+MainQuantiles.alpha+Settings.withTF;
			Init.pruningmethod = new PruneMethod_DiversificationBased(type);
		}*/
		else if(type == -1)
		{
			filename = "PRP";
			Init.pruningmethod = new PruneMethod_PRPP(isquantiles,indexdir);
		}
		else if(type == -2)
			filename = "Random";
		
	
		
		return filename;
		
	
		
	}
	
	public static int GetIndexDate(String date)
	{

		try{
			int result = (int) (((Long.parseLong(date) / 1000L) - Settings.dateinit)/(86400L ));
			if(result >  Settings.datecount )//516200
				result = Settings.datecount;
			if(result <0 ) 
			{
				result = 0;//it can happen for the queries smaller than 6th century
			}
			return result;
		}
		catch(Exception ex)
		{
			System.out.println(date);
		}

		return 0;
		
	}
	
	

}
