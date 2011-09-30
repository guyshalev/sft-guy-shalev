

public class zegarbage {

	/**		
	// The core function in learning an audio file. It takes as an argument a short audio segment and explores it for 
	// periodicity.
	public static WavFileInfo learnWavFile(String wavFilename){
		WavFileInfo output=new WavFileInfo();  // creating the output object
		try{
			WavFile wavFile = WavFile.openWavFile(new File(wavFilename)); // opening the file 
			output.setNumOfChannels(wavFile.getNumChannels()); // filling the output with basic file properties
			output.setSampleRate(wavFile.getSampleRate());
			output.setValidBits(wavFile.getValidBits());
			output.setNumofFrames(wavFile.getNumFrames());
			output.setDuration(output.getNumOfFrames()/output.getSampleRate());
			int framesToRead=(int) (output.getSampleRate()*singleQueryDuration);
			double[][] buffer=new double[output.getNumOfChannels()][framesToRead]; // a buffer for reading from the file
			for(int channelvar=0;channelvar<output.getNumOfChannels();channelvar++){
				output.getChannelist().add(new ChannelInfo(channelvar,output)); // creating a Channelinfo for every channel
			}
			long serialNumber=0; // the number of the read i.e how many times the loop below was repeated.
			while(wavFile.readFrames(buffer,framesToRead)>0){ // while there are frames left to process
				for(int channelvar=0;channelvar<output.getNumOfChannels();channelvar++){// for every channel
					output.getChannelist().get(channelvar).addPeriod(learnSegment(buffer[channelvar],serialNumber));
				}
				serialNumber++;	
			}
		}

		catch (Exception e){
			System.err.println(e);
		}
		return output;
	}
*/
	
	
	/**
	 * 
	 * @param arr - an array of doubles.
	 * @return an array of Complexes - same numbers, different object type. (will be used to create ArraySFTFunction)
	 */
	/**public static Complex[] doublestoComplexes(double[] arr){
		Complex[] result = new Complex[arr.length];
		for(int i=0; i<arr.length; i++){
			result[i] = new Complex(arr[i],0);
		}
		return result;
	}
	*/
	
	/**
	public static Map<Integer,Complex> getAllCharacters(SFTFunction func) {
		Map<Integer,Complex> result = new HashMap<Integer,Complex>();
		
		for(int k=0;k<func.getN();k++){
			Complex kweight = new Complex(0,0);
			Complex temp = new Complex(0,0);
			for(int j=0;j<func.getN();j++){
				temp = Complex.Mult(func.funcValue(j), exp(new Complex(0,-2*Math.PI*j*k/func.getN())));
				kweight= Complex.Add(kweight, temp);
			}
			result.put(k,kweight);
		}
		return result;
	}
	*/
	

	/**
	public static PeriodicityInfo learnSegmentish(double[] buffer,long serialNumber) {
		List<Integer> periodPoints;
		//maybe change to /20 or more
		int numOfShifts=buffer.length/10;  // the number of different subsequent patterns to be checked.
		int[] grades=new int[numOfShifts]; // a grade associated to each checked pattern - how many repetitions.
		
		boolean isSilence = PeriodSearch.isSilence(PeriodSearch.ArrayAttributes(buffer));
		PeriodicityInfo negativePerInf=new PeriodicityInfo(isSilence,null,0,false,serialNumber*sqd,(serialNumber+1)*sqd);
		
		double[] pattern=new double[minPatternLength]; 
		for (int offset=0;offset<numOfShifts;offset++) { // filling up the pattern.
			for (int i=0;i<pattern.length;i++) {
				pattern[i]=buffer[i+offset];
			}
			periodPoints=PatternMatching.patternMatching(buffer,pattern,smallDifference,matchingQuotient,offset+minPatternLength);
			grades[offset]=periodPoints.size();
		}
		int maximum=maxindex(grades);
		if (grades[maximum]<3) return negativePerInf; //the case no periodicity is found
		for (int i=0;i<pattern.length;i++) {
			pattern[i]=buffer[i+maximum];
		}
		periodPoints=PatternMatching.patternMatching(buffer,pattern,smallDifference,matchingQuotient,maximum+minPatternLength);
		return PatternMatching.createInfo(buffer,periodPoints,serialNumber);
	}
	
	
	
	
	//create Info
	public static PeriodicityInfo createInfo(double[] buffer,List<Integer> periodPoints,long serialNumber){
		List<Integer> diffList=differenceList(periodPoints); // the list of periods' lengths.
		int avg=average(diffList); // the average length of a period
		int avgLocation=closestTo(avg,diffList); // find the period whose length is closest to the average.
		// System.out.println("avg: " + avg + " size : " +  periodPoints.size() + " closest: " + diffList.get(avgLocation));
		double[] period=new double[periodPoints.get(avgLocation+1)-periodPoints.get(avgLocation)]; // the period describing the segment
		//size we want is in double[***] -|
		
		for(int periodvar=0;periodvar<period.length;periodvar++){
			period[periodvar]=buffer[periodPoints.get(avgLocation)+periodvar];
		}
		boolean isSilence = PeriodSearch.isSilence(PeriodSearch.ArrayAttributes(buffer));
		
		PeriodicityInfo perinf=new PeriodicityInfo(isSilence, period,periodPoints.size(),true,(serialNumber)*sqd,(serialNumber+1)*sqd);
		perinf.putHeavyCharacters(Library.getHeavyCharacters(perinf,WavHandler.weight));
		return perinf;
	}
	*/
	
	/**
	 * 
	 * this is a very simple function that finds the period length, assuming a rather "nice segment"

	public static List<Integer> findPeriodTake1(double[] arr){
		if(isSilence(ArrayAttributes(arr))){
			System.out.println("Segment is silent");
			return null;
		}
		System.out.println("Segment is NOT silent");
		//not silence. proceed.
		List<Integer> posMaxs = findLocalPosMaxs(arr);
		
		//take guesses. jump 1, jump 2, jump 3... up to 5 local maximums per period.
		for(int leap=1; leap<=5; leap++){
			System.out.println("leap size: " + leap);
			int[] difs = localsListToDifs(posMaxs,leap);
			if(difs!=null){
				for(Integer i : difs){
					System.out.print(i + " ");
				}
			}
			System.out.println();
			System.out.println("period length: " + DifsToPeriodLength(difs));
			
		}
		return null;
	}
	**/

}
