package Pruning.Methods;

import org.apache.lucene.util.SmallFloat;


public class RankingFunctions {

	static float k1 = 1.2f;
	static float b  = 0.75f;
	
	/***************************** BM25***********************************************/
	

    
	public static float BM25( float avlen, float freq, float doclen, float idf)
	{
	   return idf * ((freq * (k1 + 1)) / (freq + k1 * (1 - b + ( b * doclen/avlen ))));
	}

	public static float idf(long docFreq, long numDocs)
	{
		//TFIDF// return  (float) Math.log( ( numDocs + 1 ) / ( docFreq + 0.5 ) );
	    return (float) Math.log(1+(numDocs - docFreq + 0.5)/(docFreq + 0.5));
		
	}
	
	
	
	/***************************************LM *******************************************/ 
	
	
	public static float LMJM(float lambda, float freq, float docLen, float collectionprob) {
		   
		   return        ((1 - lambda) * freq / docLen) +
		            (lambda * collectionprob);
		
		  }
	
	/******************************** CIKM******************************************/
	
	public static double ScoreCIKM2012( final double sum, final double score)
	{
		double result = 0;
		double basic = 0;
		
		basic = (score)/ sum;
		
		result = (float) (-1 * basic * (Math.log(basic)));
		if(result == -0.0f)
			result = 0.0f;
		
		return result;
		
	}
	
	/******************************** ECIR******************************************/
	
	public static  float ScoreECIR2011(float docLen, float colLen, float tf, float cf )
	{

		float result = 0;
		
		double P = (tf+cf)/(docLen + colLen);
		double error = Math.sqrt(P*(1-P)*(  1.0 / docLen + 1.0/colLen));
		result = (float) (((tf/docLen) - (cf / colLen)) / error);
		if(Double.isInfinite(result))
			System.out.println(error + " ** " + P);
		return result;
		
	}
	
	

	
}
