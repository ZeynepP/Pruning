package Pruning.Experiments;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.search.CollectionStatistics;

import Pruning.Methods.DiversificationBased;
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
	
	
	public static ArrayList<float[]>  InitializePruneRatios(int indextype, String file) throws IOException
	{
		
		ArrayList<float[]> returnlist = new ArrayList<float[]>();
		
		File out = new File(file);
		System.out.println(file);

		if(indextype >=3) //indextype<9 && 
		{
			returnlist.add(0, new float[]{ 0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f});
			returnlist.add(1,new float[]{0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f});
		}
		else
		{
			float[] indexlist = new float[9];
			float[] prunelist = new float[9];
			if(out.exists())
			{
				List<String> lines =  FileUtils.readLines(out);
				String[] values = null;
				for(int i=0; i< lines.size(); i++)
				{
					values = lines.get(i).split(",");
					indexlist[i] = Float.valueOf(values[1]);
					prunelist[i] = Float.valueOf(values[0]);
				}
				
				returnlist.add(0,indexlist);
				returnlist.add(1,prunelist);
			}
			else
				System.out.println("FILE DOES NOT EXIST "+ file);
			
		}

			
			
			return returnlist;
	}

	

	public static String SetPruneTypeandFilename(int type, String indexdir, boolean isquantiles) throws IOException
	{
		String filename = "";
		if(type == 0)
		{
			filename = "TCP" ;
			Experiments.pruningmethod = new TCP(isquantiles,indexdir,10);
		}
		else if(type == 1)
		{
			filename = "IPU";
			Experiments.pruningmethod = new IPU(isquantiles,indexdir);
		}
		else if(type == 2)
		{
			filename = "2N2P";
			Experiments.pruningmethod = new ECIR2N2P(isquantiles,indexdir);
		}
		else if(type == -1)
		{
			filename = "PRP";
			Experiments.pruningmethod = new PRPP(isquantiles,indexdir);
		}
		else 
		{ 
			if(type == 3) filename = "Simple";
			else  if(type ==4)  filename = "Sliding";
			else if(type == 5) filename = "Dynamic";
			
			Experiments.pruningmethod = new DiversificationBased(isquantiles,indexdir,Settings.GMMfile,Settings.GMMfile, Settings.rangefield,type, Settings.collectiontype, Settings.dateinit, Settings.datecount);
		}

		return filename;
		
	
		
	}
	


}
