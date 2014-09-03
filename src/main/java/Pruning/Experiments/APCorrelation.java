package Pruning.Experiments;

import java.awt.List;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;

/**
 * JAVA VERSION OF THE APCORRELATION 
 * 
 * # Author: Mark D. Smucker
# Date: January 2013
# version: 1.0 (January, 2013)
# Most recent version: http://www.mansci.uwaterloo.ca/~msmucker/apcorr.r
#
#    This code was written for and used in the evaluation of the TREC
#    2013 Crowdsourcing Track.  If you use this code, please cite:
#
#    Mark D. Smucker, Gabriella Kazai, and Matthew Lease, "Overview of the
#    TREC 2013 Crowdsourcing Track," TREC 2013. 
#    http://trec.nist.gov/pubs/trec22/papers/CROWD.OVERVIEW.pdf
#

 * @author pehlivanz
 *
 */
/**
 * apcorr.nosampling <- function( truth, estimate )
{
  # we're going to say that the scores at rank i are for an
  # item with an ID of i.  Thus "ID" means the index into
  # the truth and estimate vectors.
  n <- length(truth) 
  if ( length(estimate) != n )
     stop( "must be same length" )
  truth.order <- order( truth, decreasing=TRUE ) 
  print(truth.order)
  estimate.order <- order( estimate, decreasing=TRUE ) 
   print(estimate.order)
  innerSum <- 0
  for ( i in 2:n )
  {
	currDocID <- estimate.order[i] 
	print(currDocID)
	estimate.rankedHigherIDs <- estimate.order[1:(i-1)] 
	print (estimate.rankedHigherIDs)
	# where is the current doc in the truth order?
	currDoc.truth.order.index <- which( truth.order == currDocID )
	truth.rankedHigherIDs <- vector()
	if ( currDoc.truth.order.index != 1 ) # top ranked doc, beware
	{
	    truth.rankedHigherIDs <- truth.order[1:(currDoc.truth.order.index-1)]
	}
	C_i <- length( intersect(estimate.rankedHigherIDs, truth.rankedHigherIDs) )
	innerSum <- innerSum + (C_i / (i-1))
  }
  result = 2 / (n-1) * innerSum - 1   
  return( result )
}

 * @author pehlivanz
 *
 */

public class APCorrelation {
	public APCorrelation(){}
	
	public double APCorrelation(final double[] truth,final  double[] estimate)
	{
		int n = truth.length;
		if(n!=estimate.length)
		{
			System.out.println("not equal ap");
			throw new DimensionMismatchException(truth.length, estimate.length);
		}
			 
		//n = 4;
		// truth = new double[]{1,2,3,4};
		// estimate = new double[]{4,3,2,1};
		 
		double innerSum = 0;
		double CurrentDocID;
		for(int i=1;i<n;i++)
		{

			try{
			
				CurrentDocID =  estimate[i];// this ID also gives the truth index as we did it before
				double[] estimaterankedHigherIDs = Arrays.copyOfRange(estimate, 0,i);//i - 1 + 1 for java
				double[] truthrankedHigherIDs = new double[(int) CurrentDocID];
				//int indextruthreverse = Arrays.asList(ArrayUtils.toObject(truth)).indexOf(CurrentDocID);
			
				if(CurrentDocID!=1) //top ranked doc, beware
					truthrankedHigherIDs = Arrays.copyOfRange(truth, 0, (int) CurrentDocID -1); // indextruhreservse - 1 but +1 for java
				
				int Ci = CollectionUtils.intersection( Arrays.asList( ArrayUtils.toObject(truthrankedHigherIDs)),Arrays.asList( ArrayUtils.toObject(estimaterankedHigherIDs))).size();
				innerSum += (Ci / (double)(i));//i - 1 + 1 for java
			}
			catch(Exception ex)
			{
				System.out.println(i);
				System.out.println(ex.toString());
				
			}
		}
		
		double result = (2 / (double)(n-1)) * innerSum - 1   ;
		return result;
		
		
		
	}

}
