
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Guy & Mark
 * This class contains the static function that is called when someone wants to use the SFT Algorithm on an
 * SFTFunction object. 
 *
 */
public class SFTAlgorithm {
	
	/**this is */
	public static List<Integer> SFT_Algorithm(SFTFunction function, double tau, double delta){
		double twoNormSqr = Math.pow(function.calcTwoNorm(), 2);
		double bigOConst = 1; //TODO Find constant
		int N = function.getN();
		double deltaForGQA = (delta) / (Math.pow(twoNormSqr/tau,1.5) * SFTUtils.log2(N) * bigOConst);

		List<Set<Integer>> listOfSubsets = Generate_Queries_Algorithm(function,tau/36,deltaForGQA);

		//map of q's and their values f(q)
		Map<Integer,Complex> map = CreateMapOfQueries(function, listOfSubsets);
		List<Interval> L = Fixed_Queries_SFT_Algorithm(N,tau,listOfSubsets,map);
		
		List<Integer> output = new LinkedList<Integer>();
		for(Interval a : L){
			output.add(a.lower);
		}
		//System.out.println("output list: ");
		//for(Integer i: output){
		//	System.out.println(i + " ");
		//}
		return output;
	}
	
	private static List<Set<Integer>> Generate_Queries_Algorithm(SFTFunction function, double gamma, double delta) {
   
		int N = function.getN();
		int MA=100;
        int MB=100;

         List<Set<Integer>> listOfSubsets = new LinkedList<Set<Integer>>();


         Set<Integer> A = SFTUtils.RandomSet(MA,N); //size MA (or all. check.), upto N, not including!!
         listOfSubsets.add(A);

         Set<Integer> B;
         for(int l=1; l<=Math.ceil(SFTUtils.log2(N));l++){ //create logN sets B_1,B_2,...B_logN
                 if(l==Math.ceil(SFTUtils.log2(N))){
                         B = SFTUtils.RandomSet(MB, N);
                 }
                 else{
                         B = SFTUtils.RandomSet(MB, (int) Math.min(Math.pow(2,l-1),N));
                 }
                 listOfSubsets.add(B);
         }

         return listOfSubsets;
	}
	
	private static Map<Integer, Complex> CreateMapOfQueries(SFTFunction function, List<Set<Integer>> listOfSubsets) {

        Map<Integer, Complex> map = new HashMap<Integer, Complex>();
        boolean flag = true;
        int N = function.getN();
        Set<Integer> A=null;
        for(Set<Integer> set : listOfSubsets){
                if(flag){ //set A
                        A = set;
                        flag = false;
                }
                else{ //B sets
                        for(Integer a : A){
                                for(Integer b : set){
                                        //System.out.println("inserting: " + (a-b));
                                        int key = (a-b+N)%N;
                                        if(!map.containsKey(key)){ //saves unwanted calls for function
                                                map.put(key,function.funcValue(key));
                                        }
                                }
                        }
                }
        }
        return map;
	}


	private static List<Interval> Fixed_Queries_SFT_Algorithm(int N, double tau, List<Set<Integer>> listOfSubsets,
			Map<Integer, Complex> map) {
		List<Interval> currCand = new LinkedList<Interval>();
        List<Interval> nextCand = new LinkedList<Interval>();
        currCand.add(new Interval(0, N-1));

        Set<Integer> A = listOfSubsets.get(0);

        for(int l=0;l < Math.ceil(SFTUtils.log2(N)); l++){
                Set<Integer> B = listOfSubsets.get(l+1); //B_l+1. (list is A,B1,B2...)
                for(Interval intrvl : currCand){
                        if(intrvl.isDegenerated()){ //if interval contains only 1, keep it (after checking again)
                                boolean c = Distinguish_Algorithm(intrvl,tau, map, A, B, N);
                                if(c){
                                        nextCand.add(intrvl);
                                }
                        }
                        else{
                                //do lower half.
                                boolean a = Distinguish_Algorithm(intrvl.getLowerHalf(),tau, map, A, B, N);
                                if(a){
                                        nextCand.add(intrvl.getLowerHalf());
                                }
                                boolean b = Distinguish_Algorithm(intrvl.getUpperHalf(),tau, map, A, B, N);
                                if(b){
                                        nextCand.add(intrvl.getUpperHalf());
                                }
                        }
                }
                currCand = nextCand;
                nextCand = new LinkedList<Interval>();
                //debug
                //System.out.println("end of "+ l + "'th round:");
                //System.out.println(currCand.size());
                //for(Interval a : currCand){
                //      print(a.toString());
                //}
                //System.out.println("end");
        }

        return currCand;
	}

	private static boolean Distinguish_Algorithm(Interval intrvl, double tau, Map<Integer, Complex> map,
            Set<Integer> A, Set<Integer> B, int N) {

    double est_a_b;
    double sumOverA=0;
    for(Integer a : A){
            Complex sumOverB = new Complex(0,0);
            Complex convolution;
            for(Integer b : B){
                    int key = (a-b+N)%N;
                    // a bit weird... needs a minus but actually works :|
                    Complex chi_exp = Complex.Chi((intrvl.lower+intrvl.upper)/2,b,N);
                    convolution = Complex.Mult(chi_exp, map.get(key));
                    //System.out.println("key: " + key);
                    //System.out.println("key_value: " + map.get(key));
                    //System.out.println("interval: " + intrvl.toString());
                    //System.out.println("chi_exp: " + chi_exp);
                    //System.out.println("convolution: " + convolution.toString());
                    sumOverB = Complex.Add(sumOverB, convolution);
            }
            //multiply by 1/|B| and normsqr it. then add to Asum.
            sumOverB = Complex.Mult(new Complex(1.0/B.size(),0), sumOverB);
            //System.out.println("hello " + sumOverB);
            sumOverA += sumOverB.sqrdNorm();
    }
    //System.out.println("hello " + sumOverA);
    est_a_b = (1.0/A.size())*sumOverA;

	if(intrvl.isDegenerated()){
		//System.out.println("interval: " + intrvl.toString() + " estab is: "+ est_a_b);
	}
    if(est_a_b>=(5.0/36.0)*tau){
            return true;
    }
    else{
            return false;
    }
}
}