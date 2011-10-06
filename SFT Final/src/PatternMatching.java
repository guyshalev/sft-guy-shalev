import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mark
 * This library contains the "PATTERNS" algorithm and the helper functions it uses.
 * Much alike PeriodSearch, this class was created with much thought and experiments.
 * The algorithm is quite sensitive to parameters, which may vary from file to file, 
 * causing it to malfunction from time to time.
 * This is the reason (besides research) we came up with two completely different algorithms -
 * each one with its ups and downs, so if one fails the other can be used.
 */

//sealed
public class PatternMatching {
	
	//fixed arguments.
	final static double sqd=WavHandler.singleQueryDuration; //initials, shorter.
	final static int minPatternLength=50;
	final static double smallDifference=0.01;
	final static double matchingQuotient=0.9;
	
	/**
	 * 
	 * @param buffer - a segment of doubles from the .wav file.
	 * @return a shorter array, representing the period. if no periodicity was found, return null.
	 */
	public static double[] SegmentToPeriodUsingPatterns(double[] buffer){
		List<Integer> periodPoints;
		int numOfShifts=buffer.length/10;  // the number of different subsequent patterns to be checked.
		int[] grades=new int[numOfShifts]; // a grade associated to each checked pattern - how many repetitions.
		
		double[] pattern=new double[minPatternLength]; 
		for (int offset=0;offset<numOfShifts;offset++) { // filling up the pattern.
			for (int i=0;i<pattern.length;i++) {
				pattern[i]=buffer[i+offset];
			}
			periodPoints=PatternMatching.patternMatching(buffer,pattern,smallDifference,matchingQuotient,offset+minPatternLength);
			grades[offset]=periodPoints.size();
		}
		int maximum=maxindex(grades);
		if (grades[maximum]<3){
			return null; //the case no periodicity is found
		}
		for (int i=0;i<pattern.length;i++) {
			pattern[i]=buffer[i+maximum];
		}
		periodPoints=PatternMatching.patternMatching(buffer,pattern,smallDifference,matchingQuotient,maximum+minPatternLength);
		List<Integer> diffList=differenceList(periodPoints); // the list of periods' lengths.
		int avg=average(diffList); // the average length of a period
		int avgLocation=closestTo(avg,diffList); // find the period whose length is closest to the average.
		// System.out.println("avg: " + avg + " size : " +  periodPoints.size() + " closest: " + diffList.get(avgLocation));
		double[] period=new double[periodPoints.get(avgLocation+1)-periodPoints.get(avgLocation)]; // the period describing the segment
		for(int periodvar=0;periodvar<period.length;periodvar++){
			period[periodvar]=buffer[periodPoints.get(avgLocation)+periodvar];
		}
		return period;
	}
	
	// returns the index of the greatest integer in the input array.
	private static int maxindex(int[] grades) {
		int maxindex=0;
		for (int i=0;i<grades.length;i++) {
			if(grades[i]>grades[maxindex]) maxindex=i;
		}
		return maxindex;
	}

	
	// returns pattern's appearances in the buffer with tolerance up to smallDifference in every frame 
	// and matchingQuotient for the whole pattern	
	private static List<Integer> patternMatching(double[] buffer,double[] pattern,double smallDifference,double matchingQuotient,
			int offset) {
		List<Integer> appearances=new ArrayList<Integer>(); // shall contain the locations of the pattern in the header
		for(int buffervar=offset;buffervar<=buffer.length-pattern.length;buffervar++){ // loop over the possible starting places of a match
			int matchings=0; // re-initialise the number of counted matchings to zero 
			for(int patternvar=0;patternvar<pattern.length;patternvar++){
				if(Math.abs(pattern[patternvar]-buffer[buffervar+patternvar])<smallDifference) matchings++;
			}
			if(matchings/pattern.length>=matchingQuotient) {
				appearances.add(new Integer(buffervar));
				buffervar+=pattern.length;
			}
		}
		return appearances;
	}

	
	// closestTo returns the place in the list of the element closest to the input number.
	private static int closestTo(int number, List<Integer> lst) {
		int closest=0;
		for (int lstvar=1;lstvar<lst.size();lstvar++) {
			if(Math.abs(number-lst.get(lstvar))<Math.abs(number-lst.get(closest))) closest=lstvar;
		}
		return closest;
	}

	// the following method returns the average of the numbers in the input list.
	private static int average(List<Integer> lst) {
		int sum=0;
		for (Integer integer : lst) {
			sum+=integer;
		}
		return (sum/lst.size());
	}
	
	// returns the list of differences of neighbouring elements in the input i.e result[i]=input[i+1]-input[i]
	private static List<Integer> differenceList(List<Integer> lst) {
		List<Integer> result=new ArrayList<Integer>();
		for(int i=0;i<lst.size()-1;i++){
			result.add(lst.get(i+1)-lst.get(i));
		}
		return result;
	}

	
}