package Pruning.Experiments;

import it.unimi.dsi.bits.LongArrayBitVector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;





public class Prune {
	
	
	Map<BytesRef, LongArrayBitVector> maps = new HashMap<BytesRef, LongArrayBitVector>();

	
	public float  PruningByRatio(final float index) throws IOException
	{
		Term tempterm;
		OpenIntDoubleHashMap map;
		maps.clear();
	
		

		
		int removedcounter = 0;
		double score   = 0;
		Experiments.overallcounter = 0;
		for(String term: Experiments.terms)
	    {
		
				removedcounter = 0;
				tempterm =  new Term( Settings.content,term);
				final LongArrayBitVector ltr =  LongArrayBitVector.getInstance().length(Experiments.pruningmethod.ir.maxDoc());
				ltr.fill(false);

				map =	Experiments.entries.get(tempterm);
				if(map!=null)
				{
					Experiments.overallcounter = map.size();
					
					IntArrayList keys = new IntArrayList() ;
					
					DoubleArrayList vals = new DoubleArrayList() ;
					
					map.pairsSortedByValue(keys, vals);
			
	
					for(int i = 0; i<keys.size();i++)
					{
						if(Settings.prunetype == 0)
						{
							if(vals.get(i)  <= index)
							{
						
								ltr.set((long) keys.get(i), true );
								removedcounter++;
							}
							else break;
						}
						else
						{
							if(Settings.prunetype < 3)
							{
								if(vals.get(i)  < index)
								{
							
									ltr.set((long) keys.get(i), true );
									removedcounter++;
								}
								else break;
							}
							else
							{
								if(vals.get(i) >= index)
								{
							
									ltr.set((long) keys.get(i), true );
									removedcounter++;
								}
								
								
							}
							
						}
						
						
					}
					//System.out.println("For term " + term + " at- "+ index +   " removed " + removedcounter + " total " + keys.size());
					Experiments.pruneratiobyterm.put(term, (double)ltr.count()/(double)Experiments.overallcounter);
					maps.put(tempterm.bytes(), ltr);
				}
				
			}

		
		return Experiments.overallcounter;
		
		
	}
	

	// To test top k + percent 
	public float  PruningTemporal(double index, boolean percent, boolean isRandom) throws IOException
	{
		Term tempterm;
	
		maps.clear();
			
		LongArrayBitVector ltr ;

		//System.out.println(index);
		Experiments.overallcounter = 0;
	    int topk = 0;
	    int removedcounter = 0;

		File termfile;
		File termscorefile;
		String s ;
		List<String> intList2;
	    int ptype = Settings.prunetype;
	    if(isRandom)
	    	ptype = 3;
		
		for(String term: Experiments.terms)
	    {
			
			tempterm =  new Term( Settings.content,term);
			termfile = new File( Settings.termsfolder + ptype + "_"+ term.toLowerCase() + ".txt" );
			
				
			
			if( termfile.exists())
			{
			
					s = FileUtils.readFileToString(termfile);
					
					
					ltr=  LongArrayBitVector.getInstance().length(Experiments.pruningmethod.ir.maxDoc());
					ltr.fill(false);
					
					String[] ids = s.replace("[", "").replace("]", "").split(",");

			
					Experiments.overallcounter=ids.length;
					
					 	if(percent )
							topk = (int) (ids.length * index);
						else topk= Math.min( (int) index, ids.length) ;
					 
					 	if(isRandom)
					 	{	
					 			intList2 = Arrays.asList(ids);
					 		
					 			Collections.shuffle(intList2);
					 			
					 			ids = (String[]) intList2.toArray();
					 		
					 	}
					 	
					 
					 	 for(int i=1; i< ids.length;i++)//0 i emty
					    //for(int i=topk; i< ids.length;i++)
						{
						 
					    	 String[] docscore = ids[i].split("->");
				
					    	if( Float.valueOf( docscore[1].trim() ) >= index )
						 	{
						    	try{
									//ltr.set( Long.valueOf( ids[i]).longValue(), true );
						    		ltr.set( Long.valueOf(docscore[0].trim()).longValue(), true );
									removedcounter++;
						    	}
						    	catch(Exception ex){
						    		
						    		System.out.println( termfile);
						    	}
						 	}
							
						
					}
					
					
			    }
			else 
			{
				ltr=  LongArrayBitVector.getInstance().length(Experiments.pruningmethod.ir.maxDoc());
				ltr.fill(true);
			//	System.out.println("No file for " + ptype+ "_" +Main41.alpha + Settings.withTF+ tempterm.text() + ".txt");
			}
			
			Experiments.pruneratiobyterm.put(term, (double) (1- index));
			maps.put(tempterm.bytes(), ltr);
	    }
	//	System.out.println(index +   " removed " + removedcounter + " total " + (float)removedcounter/(float)Experiments.overallcounter);
		return (float)removedcounter/(float)Experiments.overallcounter;
		
	}
	
	
}