package Pruning.Methods;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class  DiversificationBased_Utils {
// FIle strcutre 
// 'term', fd (optimal win size, start1, end1, weight1, mean1 , sd1,  start2, end2, weight2, mean2 , sd2, ...
	
	public static Multimap<String, Integer> ReadResultsMaxMin(int type, String file)
	{
		Multimap<String, Integer> results= ArrayListMultimap.create();
		
		try
		{
			System.out.println("Start reading maxmin");
			
			File out = new File(file);

			String temp = "";
			String term;
			List<String> lines =  FileUtils.readLines(out);
		    String[] values;
		    
			
			for(int i=0; i< lines.size(); i++)
			{
				
			  temp = lines.get(i);

			  values = temp.split(",");
			  term =  values[0].replace("\'","");
			  
			  if(results.get(term)!=null )
			  {
			    results.put(term,Integer.valueOf(values[1].trim())  );
			    if(type == 0)
			    	results.put(term,Integer.valueOf(values[2].trim())  );
			  }
			  
			 values = null;
			  

			  
			}
			
		}
		 catch (Exception e)
		 {
			  System.out.println("Error: " + e.getMessage());
		 }
		 System.out.println("OVER reading max min");
		return results;
	}
	
	public static Map<String, String> ReadResultsFromGMM(int type, String GMMfile) // type for GMM or quantile or another type
	{
		Map<String, String> GMMresults= new HashMap<String, String>();
		
		try
		{
			System.out.println("Start reading GMM");

			File out ;
	
			out = new File(GMMfile);
			
			System.out.println(out.getPath());
			
			String temp = "";
			String term;
			List<String> lines =  FileUtils.readLines(out);
		    String[] values;
	
			for(int i=0; i< lines.size(); i++)
			{
			
			  temp = lines.get(i).replaceAll("\'","");
			  values = temp.split(",");
			  term = values[0].replaceAll("\'","");
			  
			  temp = temp.substring(temp.indexOf(",") + 1);
			  temp = temp.replace(values[1]+",","");  // after adding FD into gmm result  I did not do it for LA 
				 
			  GMMresults.put(term.substring(1),temp ); 


			  
			}
			
		}
		 catch (Exception e)
		 {
			  System.out.println("Error: " + e.getMessage());
		 }
		 System.out.println("OVER reading GMM");
		return GMMresults;
	}
	
	public static int GetIndexDate(String date, long dateinit, int datecount)
	{

		try{
			int result = (int) (((Long.parseLong(date) ) - dateinit)/(86400000L ));
			if(result >  datecount )//516200
				result = datecount;
			if(result <0 ) 
			{
				result = 0;//it can happen for the queries smaller than 6th century in wiki collection
			}
			return result;
		}
		catch(Exception ex)
		{
			System.out.println("Exception in GetIndexDate for "  + date + " " + ex.toString());
		}

		return 0;
		
	}
	
}
