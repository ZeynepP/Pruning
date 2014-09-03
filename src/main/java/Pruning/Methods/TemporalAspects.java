package Pruning.Methods;


import Pruning.Experiments.Settings;
import Pruning.Methods.PruningMethod.PruningType;
import cern.jet.random.Normal;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class TemporalAspects {
	 
	 
	 RangeMap rangeSet = TreeRangeMap.create(); // list of temporal aspects
	 double lambdakalus =  0.75;
	 double N = 10000;
	 int type;
	 String term;
	 Normal normal ;
	 double mean;
	 int datecount;
	 int slidingsize;
	 int windowssize;
	 
	 public TemporalAspects(int type, String tempterm, double alpha, int datecount, int sizeslide, int windowssize)
	 {
		 this.type = type;
		 this.datecount = datecount;
		 this.slidingsize = sizeslide;
		 this.windowssize = windowssize;
		 
		 term = tempterm;
		 // i START TO USE RANGSET TO GET INTERSECTION
		 rangeSet.put( Range.closed(0,  this.datecount), alpha); // global range
		 rangeSet.put( Range.closed(0, 0), 0.00000000001); // joker range
		 
		 
		 if(tempterm != null)
		 {
			 GetWindows();
		 }
		 else
		 {
			 GetSWindows(windowssize);//general windows for all terms the same 
		 }
		 
	 }
	 public void GetWindows()
	{
		 int temp1=0;
	     int temp2= datecount;
	     int max=datecount;
	     int min=0;;
	     int windowsize ;
	    
	    	 
    	 try
	     {
		     temp1 =Integer.valueOf((DiversificationBased.maxminlist.get(term)).toArray()[0].toString());
		     temp2 = Integer.valueOf((DiversificationBased.maxminlist.get(term)).toArray()[1].toString());
		     
		     max = Math.max(temp1, temp2);
		     min = Math.min(temp1, temp2);
		     
		     windowsize = (Integer) DiversificationBased.fdlist.get(term).toArray()[0];// optimal window FD optimal bin size generated by python script
		     
		     
	     }
	     catch(Exception ex)
	     {
	    	 System.out.println(term + " winsize wiki not in list");
	    	 windowsize = windowssize;
	 
	     }

	     
	     if(type== PruningType.DYNAMIC.getPruningTypeValue())
	     {
	    	 GetGMMWindows(); 
	     }
	     else  GetSWindows(windowsize);
 
		
	}
	 
	 
	public  void  GetSWindows(int wlenght) // type simple = 0 sliding = 1
 	{

	  	int start;
 		int hsize;int size = 0;
 		hsize =  this.datecount;
 		double weight = 0;
 		int slidesize =  (int)Math.round(slidingsize);
 		
 		
 		if(type == PruningType.SIMPLE.getPruningTypeValue() )
 		{
 			
 			size = hsize/wlenght;
 			weight =  (double)( 1/(double)(size)) ;
 			
 		}
 		else
 		{
 			size = (hsize/slidesize) - (wlenght / slidesize) + 1;
 			weight = (double) (1 /(double)( ( (hsize - wlenght) /slidesize) ));
 		}

 		
 		for(int i=0; i< size;i++)
 		{	 			
 			if(type == PruningType.SIMPLE.getPruningTypeValue())
 				start = (i * wlenght);
 			else
 				start = (i*slidesize);
 				
 			rangeSet.put( Range.closed(start,start + wlenght ),weight);// 2+ because of 0 = global 1 = joker window ids
 			
 		
 			
 		}
 		

 	}
	
	
	private  void GetGMMWindows() 
	{
		String[] temp = null;
		String result ="";
		int n = 0;
		int index = 0;
		if(Settings.collectiontype == 0) // LATIMES 
		{
			n = 3;// start, end, weight,
			index = 2;
		}
		else
			n = 5	; // start, end, weight, mean ,sd
		try{
			
				System.out.println(term);
			 	result =  DiversificationBased.GMMresults.get(term);
				result = result.replaceAll("\'", "");
			 	temp = result.split(",");
			 	System.out.println(temp);
				if(temp.length !=0)
				{
					 int start = 0;
				     int end =0;
				     double weight;
				    
			
				     
					for(int i=index;i<temp.length;i=i+n)
					{
							start = (int) Double.parseDouble(temp[i+0].trim());
							end = (int) Double.parseDouble(temp[i+1].trim());
							weight = Double.parseDouble(temp[i+2].trim());
							System.out.println(start + " " + end);
							rangeSet.put( Range.closed(start , end), weight);//Double.parseDouble(temp[i+4].trim()));
							
							//index++;
					}
				}
		}
		catch(Exception ex)
		{
			
			System.out.println(result);
			System.out.println(term + " In GetGMMWindows " +  ex.toString() + " type " + type);
	
		}

		
	
	}

		
		
}
