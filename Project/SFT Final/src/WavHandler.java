

import java.io.File;
import java.util.List;
import java.util.Map;


import wavPackage.WavFile;
//sealed

/**
 * @author Guy & Mark
 *  This class contains our main library function, learnWavFile, which is used by users of the project.
 *  It also contains a reverse of the function, that takes a WavFileInfo and returns a .wav file.
 */

public class WavHandler {

	//fixed arguments.
	public static double singleQueryDuration=0.1; // in seconds. 0.1 is default, but other options are OK.
	final static double weight = 0.01; // characters with coefficients^2>weight are considered heavy.
	public static int defaultNumOfChars = 20;

	
	/**
	 * The core function in learning an audio file.
	 * @param wavFilename: A .wav file name that the algorithm processes.
	 * @param PeriodSearch: determines which of the two smart algorithm the program should use to find periodicity:
	 * for the algorithm that uses peaks, "PEAKS", and for the algorithm that uses patterns, "PATTERNS".
	 * default is "PEAKS".
	 * @param FT: determines which of the two algorithms should be used to learn the segment.
	 * for the probabilistic SFT, "SFT", for the deterministic FT, "DET".
	 * default is SFT.
	 * @param channels: determines whether to work only on first channel or on all of them. "FIRST", or "ALL".
	 * if neither of them is chosen, no learning will be done.
	 * @param queryDuration: the duration, in seconds, of a single query. (recommended: 0.1)
	 * @param numOfChars - an even integer used when DET option is used. determines how many heavy characters the user wants to get.
	 * Algorithm doen't promise this number exactly, but does its best to be close. when using SFT, unimportant.
	 * @return a WavFileInfo object that contains data concerning the input .wav file.
	 */
	public static WavFileInfo learnWavFile(String wavFilename, String PeriodSearch, String FT,
			String channels, double queryDuration, int numOfChars){
		singleQueryDuration = queryDuration; //update global
		defaultNumOfChars = numOfChars;
		WavFileInfo output=new WavFileInfo();  // creating the output object
		try{
			WavFile wavFile = WavFile.openWavFile(new File(wavFilename)); // opening the file 
			// filling the output with basic file properties
			fillWavStuff(output, wavFile, wavFilename, PeriodSearch, FT, channels);
			
			int framesToRead=(int) (output.getSampleRate()*singleQueryDuration);
			double[][] buffer=new double[output.getNumOfChannels()][framesToRead]; // a buffer for reading from the file
			//decide how many channels to process.
			int channelsToLearn=0;
			if(channels.equalsIgnoreCase("FIRST")){
				channelsToLearn = 1;
			}
			if(channels.equalsIgnoreCase("ALL")){
				channelsToLearn = output.getNumOfChannels();
			}
			
			for(int channelvar=0;channelvar<channelsToLearn;channelvar++){
				output.getChannelist().add(new ChannelInfo(channelvar,output)); // creating a Channelinfo for every channel
			}
			long serialNumber=0; // the number of the read i.e how many times the loop below was repeated.
			while(wavFile.readFrames(buffer,framesToRead)>0){ // while there are frames left to process
				for(int channelvar=0;channelvar<channelsToLearn;channelvar++){// for every channel requested.
					//add to the channelInfo object (output.getChannelist().get(channelvar))
					//a periodicityInfo Object
					output.getChannelist().get(channelvar). //up to here we are pointing to the channel object.
					addPeriod(learnSegment(buffer[channelvar],serialNumber,PeriodSearch,FT)); //period is learned the way the user asked for.
				}
				serialNumber++;
			}
		}

		catch (Exception e){
			System.err.println(e);
		}
		return output;
	}


	//overrides the simple learn segment. 
	//TODO - change.
	public static PeriodicityInfo learnSegment(double[] buffer, long serialNumber, String periodSearch, String FT) {
		
		System.out.println("in learnSegment: SERIAL NUMBER: " + serialNumber);
		//first, check if segment is silent. if so, return a periodicity info that says so.
		boolean isSilence = PeriodSearch.isSilence(PeriodSearch.ArrayAttributes(buffer));
		if(isSilence){
			System.out.println("in learnSegment: Segment is Silent");
			return new PeriodicityInfo(isSilence,null,0,true,
					serialNumber*singleQueryDuration, (serialNumber+1)*singleQueryDuration);
		}
		//not silence. proceed.
		double[] period;
		if(periodSearch.equalsIgnoreCase("PATTERNS")){
			period = PatternMatching.SegmentToPeriodUsingPatterns(buffer);
		}
		else{ //"PEAKS", or other if user doesn't use properly...
			period = PeriodSearch.SegmentToPeriodUsingPeaks(buffer);
		}
		
		if(period == null){ //no periodicity was found.
			return new PeriodicityInfo(isSilence,null,0,false,
					serialNumber*singleQueryDuration,(serialNumber+1)*singleQueryDuration); 
		}
		//now we have a period. we need to run Fourier Transform on it and produce a PeriodicityInfo object. 
		SFTFunction function = new ArraySFTFunction(period,1);
		
		Map<Integer,Complex> heavyCoefs = null;
		if(FT.equalsIgnoreCase("DET")){
			heavyCoefs = FTLibrary.TopNumCharacters(function, defaultNumOfChars);
		}
		else{ //"SFT",or other if user doesn't use properly...
			//TODO - some algorithm that returns a Map<Integer,Complex>
		}
		
		PeriodicityInfo result = new PeriodicityInfo(isSilence,period,0,true,
				serialNumber*singleQueryDuration,(serialNumber+1)*singleQueryDuration);
		result.putHeavyCharacters(heavyCoefs);
		
		return result;

	}

	
	
	/**
	 * 
	 * @param wfi - an output object from the program.
	 * @param wavFileName - an address to write a new wav file from the information.
	 * if segment is silent - wav will be silent in that area.
	 * improvement: if segment status is false - insert the last buffer inserted again. (unless first, then insert silence).
	 * 
	 */
	public static void WavFileInfoToWavFile(WavFileInfo wfi, String output_file){
		try
		{			
			//for write:
			long sampleRate = wfi.getSampleRate();		// Samples per second
			long numFrames = wfi.getNumOfFrames();
			int numChannels = wfi.getNumOfChannels();
			if(wfi.getChannels().equalsIgnoreCase("FIRST")){//if "FIRST" option is used, create mono.
				numChannels = 1;
			}
			int validBits = wfi.getValidBits();
			WavFile outWavFile = WavFile.newWavFile(new File(output_file), numChannels, numFrames, validBits, sampleRate);


			
			int bufferSize = (int) (singleQueryDuration*sampleRate);
			List<ChannelInfo> channelList = wfi.getChannelist();
			double[][] buffer = new double[numChannels][bufferSize];
		    
			// Loop over all PeriodicityInfos (channels interleave)
	        int NumberOfSegments = channelList.get(0).howManyPeriods(); //assume all channels are of same length.
	        for(int i=0; i<NumberOfSegments; i++)
	        {
	        	//PeriodicityInfo[] perInfs = new PeriodicityInfo[numChannels]; //Parallel in time PeriodicityInfos
	        	for(int j=0; j<numChannels; j++){
	        		PeriodicityInfo currInfo = channelList.get(j).getPeriod(i);
		        	// Fill the i'th segment/buffer of the j'th channel.
	        		fillBufferFromPeriodicityInfo(buffer[j], currInfo);
	        	}
	           // Write the buffer
	        	outWavFile.writeFrames(buffer, bufferSize);
	        }
			
			System.out.println("End");
			// Close the wavFile
			outWavFile.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	
	/**
	 * 
	 * @param toFill - a buffer to fill, trying to mimic the parallel segment in original wav.
	 * @param perInf - the PeriodicityInfo used to fill the buffer.
	 */
	private static void fillBufferFromPeriodicityInfo(double[] toFill, PeriodicityInfo perInf) {
		if( (!perInf.getStatus()) || perInf.isSilence() ){ //no interesting information.
			for(int i=0;i<toFill.length;i++){
				toFill[i] = 0;
			}
			return;
		}
		
		for(int i=0; i<toFill.length; i++){
			int iModN = (i + perInf.getPeriodLength()) % perInf.getPeriodLength(); //simple mod, make sure to be positive.
			Complex value = FTLibrary.getValue(iModN, perInf.getPeriodLength(), perInf.getHeavyCharacters());
			toFill[i] = value.Re();
		}
		
	}

	// filling the output with basic file properties. so it will not sit in main function.
	private static void fillWavStuff(WavFileInfo output, WavFile wavFile, String wavFilename, String periodSearch, String FT, String channels){
		output.setWavFilename(wavFilename);
		output.setPeriodSearch(periodSearch);
		output.setFT(FT);
		output.setChannels(channels);
		output.setNumOfChannels(wavFile.getNumChannels()); 
		output.setSampleRate(wavFile.getSampleRate());
		output.setValidBits(wavFile.getValidBits());
		output.setNumofFrames(wavFile.getNumFrames());
		output.setDuration(output.getNumOfFrames()/output.getSampleRate());
	}

}
