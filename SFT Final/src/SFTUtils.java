

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// SFT CENTRAL IS THE NEWEST VERSION
public class SFTUtils {
	
	public static double multTauForSFT = 0.1; //FOR USER: if you want more heavy characters, make tau SMALLER. (if less--> BIGGER)
	
	
	/**returns a set with random values from {0,1,...,limit-1} containing min(numELEM,limit)
	improvement: if we see in advance we need all, generate all (no random).*/
	//TODO DELETE FROM "MainClass"
	//TODO improve performance by using complement if numElem > (limit/2)
	public static Set<Integer> RandomSet(int numElem, int limit) {
		
		Set<Integer> set = new HashSet<Integer>();
		if(numElem >= limit){
			for(int i=0; i<limit; i++){
				set.add(i);
			}
		}
		else{
			Random generator = new Random();
			while(set.size()<numElem && set.size()<limit){
				set.add(generator.nextInt(limit));
			}
		}
			return set;
	}
	
	/**
	 * 
	 * @param function - an SFTFunction
	 * @return a map that represents the function
	 * this function works properly for SFTFunctions of type ArraySFTFunction.
	 */
	public static Map<Integer, Complex> HeavyCharaters(SFTFunction function) {
		Map<Integer,Complex> map = new HashMap<Integer,Complex>();
		
		double delta = 0.666;
		double tau = 0;
		for(int i=0; i<function.getN();i++){
			tau+= Math.pow(function.funcValue(i).Re(),2);
		}
		tau /= function.getN();
		tau *= multTauForSFT; //FOR USER: if you want more heavy characters, make tau SMALLER. (if less--> BIGGER)
		
		List<Integer> chars = SFTAlgorithm.SFT_Algorithm(function, tau, delta); //delta is meaningless
		for(Integer a : chars){
			map.put(a, getCoefficient(function,a));
		}
		return map;
	}
	
	//returns the n'th coefficient
	public static Complex getCoefficient(SFTFunction func, int place){
		Complex kweight = new Complex(0,0);
		Complex temp = new Complex(0,0);
		for(int j=0;j<func.getN();j++){
			temp = Complex.Mult(func.funcValue(j), FTLibrary.exp(new Complex(0,-2*Math.PI*j*place/func.getN())));
			kweight= Complex.Add(kweight, temp);
		}
		kweight.Div(new Complex(func.getN(),0)); //kweight is now the coef.
		return kweight;
	}
	
	
	
	
    /**
     * this function finds how good is the approximation an SFTFunction to another.
     * by the calculation of (2Norm(f-p)/2norm(f))^2. the closer to 0, the better the approximation!
     * assuming both agree on N...
     */
    public static double SFTFunctionVSApproximation(SFTFunction func1, SFTFunction func2, int sizeOfSample){
    	
        Set<Integer> A = RandomSet(sizeOfSample,func1.getN());
        
    	double numerator=0;
    	double denominator=0;
    	Complex temp;
    	for(Integer a : A){
    		
    		temp = Complex.Sub(func1.funcValue(a), func2.funcValue(a));
    		numerator += temp.sqrdNorm();
    		denominator += func1.funcValue(a).sqrdNorm();
    	}
    	
    	double weight_left = (denominator!=0) ? (numerator/denominator) : -1; //if denom 0, return -1.
    	return weight_left;
    }
	
    
    //helpers
	public static double log2(int n) {
		return (Math.log(n)/Math.log(2));
	}
}
