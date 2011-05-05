package Main;

import java.util.*;

//Many problems with constants...
//worked well with: N=any, tau=2,delta = O(0.2), inf norm 2
//<monom>	
//<real>2</real>
//<image>0</image>
//<alpha>any</alpha>
//</monom>
//<monom>
//<real>0</real>
//<image>2</image>
//<alpha>any</alpha>
//</monom>


public class MainClass {

	//for the usage of the "oracle access" function
	private static Polynom widelyKnownPolynom;
	private static int widelyKnownN;
	private static boolean xmlFlag;

	private static Scanner mainScanner = new Scanner(System.in);

	public static void main(String[] args) {

		//greetings
		print("Greetings kind user!");
		print("do you wish to use an xml file? (y/n)");
		String s = scanstring();
		xmlFlag=s.equalsIgnoreCase("y"); //if true,work with xml.
		if(xmlFlag){
			print("please insert file path:");
			String fileName = scanstring();
			widelyKnownPolynom = xmlReader.xmlToPolynom(fileName);
		}

		print("Enter N (size of group):");
		int N = scanint();
		Monom.N=N;
		widelyKnownN=N;
		print("Enter tau (threshold):");
		double tau = scandouble();
		print("Enter delta (failure prob.):");
		double delta = scandouble();
		print("Enter inf. norm of f (max abs. value of f):");
		double infNorm = scandouble();


		//call algorithm
		while(true){
			List<Interval> outputList = SFT_Algorithm(N,tau,delta,infNorm);
			if(outputList.size()>100){
				print("The algorithm returned more than a 100 values.");
				print("this unfortunate mistake might happen if delta is to big");
				print("or tau is too small. please try again with other arguments.");
			}
			else{
				print("Output List:");
				for(Interval I : outputList){
					print(I.lower);
				}
			}
			//algorithm call ends
			
			print("Do you want to run the algorithm again? (y/n)");
			s = scanstring();
			if(s.equalsIgnoreCase("n")){
				print("Thank you and good day!");
				return;
			}
			else{
				print("Do you want to run with the same arguments? (y/n)");
				s = scanstring();
				if(s.equalsIgnoreCase("y")){
					continue;
				}
				else{
					print("Enter N (size of group):");
					N = scanint();
					Monom.N=N;
					widelyKnownN=N;
					print("Enter tau (threshold):");
					tau = scandouble();
					print("Enter delta (failure prob.):");
					delta = scandouble();
				}
			}
		}
	}

	private static List<Interval> SFT_Algorithm(int N, double tau, double delta, double infNorm) {

		double twoNormSqr = approximateTwoNormSqr();
		double bigOConst = 0.001;
		double deltaForGQA = (delta) / (Math.pow(twoNormSqr/tau,1.5) * log2(N) * bigOConst);

		List<Set<Integer>> listOfSubsets = Generate_Queries_Algorithm(N,tau/36,infNorm,deltaForGQA);

		//debug
		//print("printing list of subsets:");
		//for(Set<Integer> a : listOfSubsets){
		//	print("set of size: "+ a.size() + " containing:");
		//	for(Integer b : a){
		//		print(b);
		//	}
		//}
		//print("lala end");

		//map of q's and their values f(q)
		Map<Integer,Complex> map = CreateMapOfQueries(listOfSubsets);
		List<Interval> L = Fixed_Queries_SFT_Algorithm(N,tau,listOfSubsets,map);

		return L;
	}



	private static List<Set<Integer>> Generate_Queries_Algorithm(int N, double gamma,
			double infNorm, double delta) {
		double TethaA = 0.00003;
		double TethaB = 0.00007;
		double eta = Math.min(Math.min(gamma,Math.sqrt(gamma)), gamma/infNorm);
		eta = Math.pow(infNorm/eta, 2);
		int MA = (int) (eta * TethaA * (1/Math.pow(gamma, 2)) * Math.log(1/delta));
		int MB = (int) (eta * TethaB * (1/Math.pow(gamma, 2)) * Math.log(1/(delta*gamma)));
		if(MA<=1){
			MA=2; //minimum MA, otherwise algorithm goes nuts...
			if(N>=1000){
				MA=3;
			}
		}
		if(MB<=1){
			MB=2; //minimum MB, otherwise algorithm goes nuts...
			if(N>=1000){
				MB=3;
			}
		}
		//print("MA: " + MA);
		//print("MB: " + MB);
		//print("delta" + delta);
		//for debug

		List<Set<Integer>> listOfSubsets = new LinkedList<Set<Integer>>();


		Set<Integer> A = RandomSet(MA,N); //size MA (or all. check.), upto N, not including!!
		listOfSubsets.add(A);

		Set<Integer> B;
		for(int l=1; l<=Math.ceil(log2(N));l++){ //create logN sets B_1,B_2,...B_logN
			B = RandomSet(MB, (int) Math.min(Math.pow(2,l-1),N));
			listOfSubsets.add(B);
		}

		return listOfSubsets;
	}

	//returns a set with random values from {0,1,...,limit-1} containing min(numELEM,limit)
	//possible improvement: if we see in advance we need all, generate all (no random).
	private static Set<Integer> RandomSet(int numElem, int limit) {

		Set<Integer> set = new HashSet<Integer>();
		Random generator = new Random();
		while(set.size()<numElem && set.size()<limit){
			set.add( Math.abs(generator.nextInt()) % limit );
		}
		return set;
	}

	private static Map<Integer, Complex> CreateMapOfQueries(List<Set<Integer>> listOfSubsets) {

		Map<Integer, Complex> map = new HashMap<Integer, Complex>();
		boolean flag = true;
		Set<Integer> A=null;
		for(Set<Integer> set : listOfSubsets){
			if(flag){ //set A
				A = set;
				flag = false;
			}
			else{ //B sets
				for(Integer a : A){
					for(Integer b : set){
						//print("inserting: " + (a-b));
						int key = (a-b+widelyKnownN)%widelyKnownN;
						if(!map.containsKey(key)){ //saves unwanted calls for function
							map.put(key,function(key)); //change function!!!!
						}
					}
				}
			}
		}
		//print("|Q|= " + map.size());
		//scanint();
		return map;
	}

	private static List<Interval> Fixed_Queries_SFT_Algorithm(int N,
			double tau, List<Set<Integer>> listOfSubsets, Map<Integer, Complex> map) {
		List<Interval> currCand = new LinkedList<Interval>();
		List<Interval> nextCand = new LinkedList<Interval>();
		currCand.add(new Interval(0, N-1));

		Set<Integer> A = listOfSubsets.get(0);

		for(int l=0;l < Math.ceil(log2(N)); l++){
			Set<Integer> B = listOfSubsets.get(l+1); //B_l+1. (list is A,B1,B2...)
			for(Interval intrvl : currCand){
				if(intrvl.isDegenerated()){ //if interval contains only 1, keep it.
					nextCand.add(intrvl);
				}
				else{
					//do lower half.
					boolean a = Distinguish_Algorithm(intrvl.getLowerHalf(),tau, map, A, B);
					if(a){
						nextCand.add(intrvl.getLowerHalf());
					}
					boolean b = Distinguish_Algorithm(intrvl.getUpperHalf(),tau, map, A, B);
					if(b){
						nextCand.add(intrvl.getUpperHalf());
					}
				}
			}
			currCand = nextCand;
			nextCand = new LinkedList<Interval>();
			//debug
			//print("end of "+ l + "'th round:");
			//print(currCand.size());
			//for(Interval a : currCand){
			//	print(a.toString());
			//}
			//print("end");
		}

		return currCand;
	}

	private static boolean Distinguish_Algorithm(Interval intrvl, double tau, Map<Integer, Complex> map,
			Set<Integer> A, Set<Integer> B) {

		double est_a_b;
		double sumOverA=0;
		for(Integer a : A){
			Complex sumOverB = new Complex(0,0);
			Complex convolution;
			for(Integer b : B){
				int key = (a-b+widelyKnownN)%widelyKnownN;
				// a bit weird... needs a minus but actually works :|
				Complex chi_exp = chi((intrvl.lower+intrvl.upper)/2,b,widelyKnownN);
				convolution = Complex.multiply(chi_exp, map.get(key));
				//print("key: " + key);
				//print("key_value: " + map.get(key));
				//print("interval: " + intrvl.toString());
				//print("chi_exp: " + chi_exp);
				//print("convolution: " + convolution.toString());
				sumOverB = Complex.add(sumOverB, convolution);
			}
			//multiply by 1/|B| and normsqr it. then add to Asum.
			sumOverB = Complex.multiply(new Complex(1.0/B.size(),0), sumOverB);
			//print("hello " + sumOverB);
			sumOverA += sumOverB.sqrdNorm();
		}
		//print("hello " + sumOverA);
		est_a_b = (1.0/A.size())*sumOverA;
		//if(intrvl.isDegenerated()){
		//	print("interval: " + intrvl.toString() + " estab is: "+ est_a_b);
		//}
		if(est_a_b>=(5.0/36.0)*tau){
			return true;
		}
		else{
			return false;
		}
	}

	//pick 10 integers and average to get two norm squared.
	private static double approximateTwoNormSqr() {
		Set<Integer> random_set = RandomSet(10, widelyKnownN);
		double twoNormSqr=0;
		for(Integer a : random_set){
			twoNormSqr += function(a).sqrdNorm();
		}
		twoNormSqr = twoNormSqr/random_set.size();

		//print("twoNormSqr is: " +twoNormSqr);

		return twoNormSqr;
	}

	private static Complex chi(double alpha, int x, int N){
		double real = Math.cos(2*Math.PI*alpha*x/(double)N);    //2PI*A*X/N
		double im = Math.sin(2*Math.PI*alpha*x/(double)N);

		return new Complex(real,im);
	}

	private static Complex function(int x){
		if(xmlFlag){
			return widelyKnownPolynom.calc(x);
		}
		else{
			print("please enter f(" + x + "): (real <space> im)");
			return scancomplex();
		}
	}



	//helpers and shortcuts:

	private static double log2(int n) {
		return (Math.log(n)/Math.log(2));
	}

	private static Complex scancomplex() {
		double real, im;
		real = scandouble();
		im = scandouble();
		//print("real " + real + ", im " + im);
		return new Complex(real,im);
	}

	private static double scandouble() {
		return (mainScanner.nextDouble());
	}

	private static String scanstring() {
		return (mainScanner.next());
		//return (mainScanner.nextLine());
	}

	private static int scanint() {

		return (mainScanner.nextInt());
	}

	private static void print(String string) {
		System.out.println(string);		
	}

	private static void print(int n) {
		System.out.println(n);		
	}


}