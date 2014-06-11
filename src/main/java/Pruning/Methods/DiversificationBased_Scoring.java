package Pruning.Methods;



import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import cern.colt.function.IntDoubleProcedure;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class DiversificationBased_Scoring{

	RangeMap aspects;
	int collectiontype;
	OpenIntDoubleHashMap mapsdocidw = new OpenIntDoubleHashMap();
/*********************************************** get windows for each document ******************/
	
	public DiversificationBased_Scoring(RangeMap las, int collectiontype)
	{
		aspects = las;
		this.collectiontype = collectiontype;
		
	}
	
	@SuppressWarnings("unchecked")
	public void GetDocumentsWindowsGuava(int docid, String date, double docscore, double alpha,long dateinit, int datecount) 
	{

			//Set tempwindows = new HashSet();
			String[] temp ;
			String[] dates = null;
			RangeMap tempmap = TreeRangeMap.create();
			int start ,end;
			double score = 0;
			temp = date.split("\\$");
			
			for(int i=0;i<temp.length;i++)
			{	
					dates= temp[i].split("_");
					start = DiversificationBased_Utils.GetIndexDate(dates[0],dateinit,datecount);
					end =  DiversificationBased_Utils.GetIndexDate(dates[1],dateinit,datecount);
					if(end<start) end = start+1;
					
					if(collectiontype == 1)
					{
						tempmap =aspects.subRangeMap(Range.closed(start,end));
					}
					else
					{
						tempmap =aspects.subRangeMap(Range.closed(Long.parseLong( dates[0]),Long.parseLong( dates[0])));//3 months
					}

					Iterator it = tempmap.asMapOfRanges().values().iterator();
					while(it.hasNext())
						score+= Double.parseDouble( it.next().toString());
					
			}

			if(score == 0)
				score+=0.000000001;// joker window
			
			score+=alpha;
			mapsdocidw.put(docid, score);
			

			
		
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public  OpenIntDoubleHashMap  DistanceMethod(int type,String term,OpenIntDoubleHashMap mapdocscore, final double maxscore) throws IOException
	{
		int docid = 0;
		double docscore = 0;
		Iterator windowsiterator ;
		int testwid = 0;
		//instead of windows objet 

		//final OpenIntDoubleHashMap map = new  OpenIntDoubleHashMap();
		final OpenIntDoubleHashMap mapdistance = new  OpenIntDoubleHashMap();

		double x = 0;
		double score = 0;
		double overallx = 0;

		IntDoubleProcedure p = new IntDoubleProcedure() {
			
			@Override
			public boolean apply(int arg0, double arg1 ) {
				// TODO Auto-generated method stub
				double x = mapsdocidw.get(arg0);
				double score = x * (arg1/maxscore);
				//map.put(arg0, score); 
				double dist =  calculateDistance(1, 1, x , (arg1/maxscore));
			    mapdistance.put(arg0, dist );
			    return true;
			}
		};

		
		IntArrayList keys = new IntArrayList();
		String result = "";
		mapdistance.keysSortedByValue(keys);
		
		
		for(int i =0;i<keys.size();i++)
			result = result + "," + keys.get(i);
		FileUtils.writeStringToFile(new File("/home/pehlivanz/PWA/terms/"+ type + "_"+ term + ".txt"), result);
		return mapdistance;
		

	}
	
	
	 double calculateDistance(double x1, double y1, double x2, double y2){
	    double xDistance = Math.abs(x1 - x2);
	    double yDistance = Math.abs(y1 - y2);
	    double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );

	    return distance;
	}
	
	
	
	
}
