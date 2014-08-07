package Pruning.Quantiles;



import java.io.IOException;

import org.apache.lucene.search.CollectionStatistics;

import Pruning.Methods.DiversificationBased;
import Pruning.Methods.ECIR2N2P;
import Pruning.Methods.IPU;
import Pruning.Methods.PRPP;
import Pruning.Methods.TCP;



public class Utils {
	
	
	
	
	
	public static String SetPruneTypeandFilename(int type, String indexdir, boolean isquantiles) throws IOException
	{
		String filename = "";
		if(type == 0)
		{
			filename = "TCP" ;
			Init.pruningmethod = new TCP(isquantiles,indexdir,10, Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else if(type == 1)
		{
			filename = "IPU";
			Init.pruningmethod = new IPU(isquantiles,indexdir,Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else if(type == 2)
		{
			filename = "2N2P";
			Init.pruningmethod = new ECIR2N2P(isquantiles,indexdir,Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else if(type == -1)
		{
			filename = "PRP";
			Init.pruningmethod = new PRPP(isquantiles,indexdir,Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else 
		{ 
			if(type == 3) filename = "Simple";
			else  if(type ==4)  filename = "Sliding";
			else if(type == 5) filename = "Dynamic";
			
			Init.pruningmethod = new DiversificationBased(isquantiles,indexdir,Settings.GMMfile,Settings.maxminfile, Settings.rangefield,type, Settings.collectiontype, Settings.dateinit, Settings.datecount, Settings.windowsize,Settings.slidingsize,Settings.content, Settings.maxdocs);
		}

		return filename;
		
	
		
	}
	


}
