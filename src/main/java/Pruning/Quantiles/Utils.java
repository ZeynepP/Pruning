package Pruning.Quantiles;



import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CollectionStatistics;

import Pruning.Experiments.Experiments;
import Pruning.Experiments.Settings;
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
			Init.pruningmethod = new TCP(false,isquantiles,indexdir,10, Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else if(type == 1)
		{
			filename = "IPU";
			Init.pruningmethod = new IPU(false,isquantiles,indexdir,Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else if(type == 2)
		{
			filename = "2N2P";
			Init.pruningmethod = new ECIR2N2P(false,isquantiles,indexdir,Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else if(type == -1)
		{
			filename = "PRP";
			Init.pruningmethod = new PRPP(false,isquantiles,indexdir,Settings.content,Settings.maxdocs, Settings.collectiontype);
		}
		else 
		{ 
			if(type == 3) filename = "Simple";
			else  if(type ==4)  filename = "Sliding";
			else if(type == 5) filename = "Dynamic";
			
			Init.pruningmethod = new DiversificationBased(false,isquantiles,indexdir,Settings.GMMfile,Settings.maxminfile, Settings.rangefield,type, Settings.collectiontype, Settings.dateinit, Settings.datecount, Settings.windowsize,Settings.slidingsize,Settings.content, Settings.maxdocs);
		}

		return filename;
		
	
		
	}
	
}
