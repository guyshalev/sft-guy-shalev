import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 
 * @author Guy, Mark.
 * This library contains the "PEAKS" algorithm and the helper functions it uses.
 * In my humble opinion, it is a great algorithm, and the best code I've ever written.
 * Every line and parameter are a product of intensive, well-planned and enduring research which I enjoyed very much,
 * and I hope that whoever reads this code will appreciate and enjoy it as I do.
 */

//sealed
public class PeriodSearch {
	
	static double DEFAULT_RATIO = 0.5; //for leaps
	static int DEFAULT_AREA = 30; //for finding significant "extreme points"
	//this Default is used for development. in real usage, area will change according to situation, ranging from 15 to 50 as needed.
	
	
	/**
	 * the main function, used by wavHandler. finds period length (a lot of very hardwork)
	 * and takes from the beginning of the segment (we are assuming all of the segment is good).
	 * @param buffer - a segment of doubles from the .wav file.
	 * @return a shorter array, representing the period. if no periodicity was found, return null.
	 */
	public static double[] SegmentToPeriodUsingPeaks(double[] buffer){
		int periodLength = findPeriodFinal(buffer);
		if(periodLength<0){
			return null;
		}
		double[] period = new double[periodLength];
		//System.out.println("in SegmentToPeriodUsingPeaks: period length is: " + periodLength);
		for(int k=0;k<periodLength;k++){
			period[k]=buffer[k];
		}
		return period;
	}
	
	
	/**
	 * 
	 * @param an array in which we search for a period to operate on with the SFT Algorithm.
	 * @return a short array of attributes that will help other functions find the period.
	 * the attributes returned are the following, in this order:
	 * 0 - minimum (value)
	 * 1 - maximum (value)
	 * 2 - average of values (NOTE - if always close to zero, good)
	 * 3 - variance (CHANGE ALGORITHM)
	 * 4 - WHATEVER
	 */
	public static double[] ArrayAttributes(double[] arr){
		double[] result = new double[4];
		
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;
		double sum=0, abssum=0;
		for(int i=0; i<arr.length; i++){
			min = (arr[i]<min) ? arr[i] : min;
			max = (arr[i]>max) ? arr[i] : max;
			sum += arr[i];
			abssum += arr[i]*arr[i];
		}
		result[0]= min;
		result[1]= max;
		result[2]= sum/(double)arr.length;
		result[3]= abssum/(double)arr.length;
		return result;
	}
	/**
	 * 
	 * @param atr - attribute array of some (segment) array
	 * @return true iff segment is considered silence
	 */
	public static boolean isSilence(double[] attr){
		return (attr[0]>-0.01 || attr[1]<0.01 || attr[3]<0.0001);
	}
	/**
	 * 
	 * @return list of local (area to both sides) maximums/minimums/positive maximums/negative minimums
	 * peaks might be found between "area" to N-"area". (exclude the edges)
	 */
	public static List<Integer> findLocalMaxs(double[] arr){
		return findLocalMaxs(arr, DEFAULT_AREA);
	}
	public static List<Integer> findLocalMins(double[] arr){
		return findLocalMins(arr, DEFAULT_AREA);
	}
	public static List<Integer> findLocalPosMaxs(double[] arr){
		return findLocalPosMaxs(arr, DEFAULT_AREA);
	}
	public static List<Integer> findLocalNegMins(double[] arr){
		return findLocalNegMins(arr, DEFAULT_AREA);
	}
	
	//overload!!! does the same, but gets a parameter area - how far aside should an extreme point rule.
	public static List<Integer> findLocalNegMins(double[] arr, int area){
		List<Integer> all = findLocalMins(arr, area);
		List<Integer> neg = new ArrayList<Integer>();
		for(Integer i : all){
			if(arr[i]<0){
				neg.add(i);
			}
		}
		return neg;
	}
	public static List<Integer> findLocalMins(double[] arr, int area){
		double[] tempArr = new double[arr.length];
		for(int i=0;i<arr.length; i++){
			tempArr[i] = -arr[i];
		}
		return findLocalMaxs(tempArr, area);
	}
	public static List<Integer> findLocalMaxs(double[] arr, int area){
		boolean maxFlag;
		
		List<Integer> maxList = new ArrayList<Integer>();

		for(int i=area; i<arr.length-area; i++){ //go over all possible maximums
			maxFlag = true; //maximum unless proven otherwise 
			for(int j=0; j<area; j++){
				if(arr[i-area+j]>arr[i] || arr[i+area-j]>=arr[i]){ // if someone is bigger than me.
					maxFlag = false;
					break;
				}
			}
			if(maxFlag){ //we didn't break - i is a local maximum.
				maxList.add(i);
			}
		}
		return maxList;
	}
	public static List<Integer> findLocalPosMaxs(double[] arr, int area){
		List<Integer> all = findLocalMaxs(arr, area);
		List<Integer> pos = new ArrayList<Integer>();
		for(Integer i : all){
			if(arr[i]>0){
				pos.add(i);
			}
		}
		return pos;
	}
	
	/**
	 * @param locals - a list, such as the one returned by findLocalMaxs of indices of maximums.
	 * @param leap - the supposed distance in the list. 
	 * @return frame differences array. if not enough information, return null.
	 */
	public static int[] localsListToDifs(List<Integer> locals, int leap){
		if(locals.size()-leap <= 1){
			return null; //not enough information. I decided 1 sample is not enough information, for probability reasons.
		}
		int[] difs = new int[locals.size()-leap];
		for(int i=0; i<locals.size()-leap; i++){
			difs[i] =  locals.get(i+leap) - locals.get(i);
		}
		return difs;
	}
	
	/**
	 * 
	 * @param difs - differences of peaks made with correct leap size - optimally, most of the numbers in the array are very close.
	 * @return the suspected period length in frames. for now - it is the median. -1 means no answer (for now, if difs is null).
	 */
	public static int DifsToPeriodLength(int[] difs){
		if(difs==null){
			return -1;
		}
		Arrays.sort(difs);
		
		//System.out.print("sorted difs: ");
		//for(int k:difs)
		//	System.out.print(k + " ");
		//System.out.println();
		
		return difs[difs.length/2];
	}

	/**
	 * @param list - the significant points.
	 * @param arr - the segment.
	 * @param attr - the segment attributes
	 * @return an array of scores for the number of leaps, from 1 to maxLeaps.
	 */
	public static double[] LeapsRatios(List<Integer> list, double[] arr, double[] attr){
		int maxLeaps= 8;
		double[] result = new double[maxLeaps+1]; //leaps from 1 to 10 (1 to 10 in array)
		double threshold = 0.1 * attr[1]; //avoid division by zero or close. if "zero" ignore: do not add to sum & count,.
		
		for(int leap=1; leap<=maxLeaps; leap++){
			double sum=0;
			int count=0;
			for(int i=0; i<list.size()-leap; i++){
				if(Math.abs(arr[list.get(i)]) > threshold && Math.abs(arr[list.get(i+leap)]) > threshold){ //if second value is not too small; (0.0001 is very small with 0.1... usualy it will not happen because we are dealing with peaks.
					double proportion = arr[list.get(i+leap)] / arr[list.get(i)];
					if(proportion<1){
						proportion = 1/proportion; //we want proportion to be always larger than 1.
					}
					sum +=  Math.pow(proportion - 1,2); //variance from 1 (ratio~1 is good!)
					count++;
				}
				//else{
				//	System.out.println("in LeapsRatios: ignored!!! " + arr[list.get(i)]);
				//}
			}
			sum = sum / count;
			result[leap] = sum;
		}
		//print scores:
		//for(int k=0; k<result.length; k++)
		//	System.out.println(k + " : " + result[k]);
		
		return result;
	}

	/**  
	 * @param leapScores - contains data on how well did every leap do.
	 * @param ratio - is in [0,1]. small ratio - higher standard: a leap is considered "absolute best"
	 * if it is at least 1/ratio times smaller than every other leap it doesn't divide. (example: if 4 is minimal, it is not compared to 8).
	 * @return the absolute best leap (say, from 1 to 8), or -1 if there is no absolute best leap.
	 */
	public static int GetAbsoluteBestLeap(double[] leapScores, double ratio) {
		double min = leapScores[1]; //ignore arr[0]!!!! no such thing zero leap. cell is never used.
		int index=1;
		for(int i=2; i<leapScores.length; i++){
			if(leapScores[i]<min){ //it is not enough to be minimal: if 3 is minimum, 6 needs to be half to conquer the minimum.
				if( (index ==1) || (i%index)!=0 || leapScores[i]<0.5*min){
					//explanation: a new minimum needs to be: a) first (after 1). b) not divisible by current min, or c) divisible but much better than current minimum.
					min = leapScores[i];
					index = i;
				}
			}
		}
		if(min > 10){
			//System.out.println("minimum is not good enough");
			return -1;
		}
		if(leapScores[1]<0.00001){ //perfect audio file.
			return 1; 
		}
		
		//now index is the place of the "minimum", the value is stored in min.
		//check if better than 
		min /= ratio; //make min larger - to give a higher standard.
		boolean flag = false;
		for(int i=1; i<leapScores.length; i++){
			if (((i%index) != 0) && ((i%index) != 0) && (leapScores[i] <= min)){
				//second condition added due to the 3/6 bug. essentially - don't check if gcd(a,b) = a || b.
				flag = true; //we found someone that is smaller than minimum/ratio. there is no absolute.
			}
		}
		if(flag){	//minimum is not good enough. maybe because correct index is 1, and then all are small.
			if(leapScores[1]*0.4 < min*ratio){
				index = 1;
			}
			if(leapScores[1]*0.8 < min*ratio){ //restore min, and check if it is only 1.25 times (or less) bigger than the 1 index.
				index = 2; //option: even though probably 1, maybe better to return 2 (for SFT).
			}
			else{ //no solution.
				index = -1;
			}
		}
		
		return index;
	}

	/**
	 * a function that finds the period length in a periodic function.
	 * @param arr - presumably part of a wav file, hopefully periodic.
	 * @return if function is periodic, the length of the period. if segment is considered silence, returns 0.
	 * if it cannot decide for some reason, the function will return -1 (it is better to admit lack of knowledge than return crap).
	 */
	public static int findPeriodFinal(double[] arr){
		double[] attr = ArrayAttributes(arr);
		if(isSilence(attr)){
			//System.out.println("Segment is silent");
			return 0;
		}
		//System.out.println("Segment is NOT silent");
		//not silence. proceed.
		
		//produce a list of peaks, and try to get from it the period length. if you are not sure, try with another list.
		//lists are determined by choosing one of - Min, Max, posMax, negMin, combined with an argument "area". larger area --> the fewer peaks chosen.
		//current version: posMax30, negMins30, posMax45, negMins45.
		
		List<Integer> currList;
		int PeriodLength=-1;
		
		//try1
		//System.out.println("try1 - posMax30");
		currList= findLocalPosMaxs(arr, 30);
		PeriodLength = localListToPeriodLength(currList,arr,attr);
		if(PeriodLength<=0){ //try2
			//System.out.println("try2 - negMin30");
			currList = findLocalNegMins(arr, 30);
			PeriodLength = localListToPeriodLength(currList,arr,attr);
		}
		if(PeriodLength<=0){ //try3
			//System.out.println("try3 - posMax45");
			currList = findLocalPosMaxs(arr, 45);
			PeriodLength = localListToPeriodLength(currList,arr,attr);
		}
		if(PeriodLength<=0){ //try4
			//System.out.println("try4 - negMin45");
			currList = findLocalNegMins(arr, 45);
			PeriodLength = localListToPeriodLength(currList,arr,attr);
		}
		//if one of the procedures succeeded, the next won't be called. we will now have the length and we can return it.
		if(PeriodLength<=0){
			//System.out.println("Problematic segment, algorithm unable to retrieve period length.");
		}
		//last shiftzur - periodLenght is bounded by 2000, algorithm actually failed.
		if(PeriodLength>2000){
			PeriodLength = -1;
		}
		return PeriodLength;
	}
	/**
	 * 
	 * @param currList - a list of peaks, as usual.
	 * @return if possible, the length of the period in frames. else, returns -1.
	 */
	private static int localListToPeriodLength(List<Integer> currList, double[] arr, double[] attr) {
		
		double[] scores = LeapsRatios(currList, arr, attr);
		int bestLeap = GetAbsoluteBestLeap(scores,DEFAULT_RATIO);
		//System.out.println("leap chosen is: " + bestLeap);
		if(bestLeap==-1){
			return -1;
		}
		//else, we have an absolute best leap. this is great!!! transform it to length of period.
		int[] difs = localsListToDifs(currList,bestLeap);
		
		//if(difs!=null){
		//	System.out.print("difs: ");
		//	for(int k:difs)
		//		System.out.print(k + " ");
		//	System.out.println();
		//}
		
		int result = DifsToPeriodLength(difs);
		
		return result;
	}
	
	//helper, prints a list.
	public static void printList(List<Integer> list){
		System.out.println("Print List: ");
		for(Integer i : list)
			System.out.print(i + " ");
		System.out.println();
	}
	
	
	
}

