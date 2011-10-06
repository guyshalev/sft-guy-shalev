//sealed

/**
 * This class is not used in the project itself - it was part of the research.
 * The class was used to develop the PeriodSearch algorithm.
 */
import java.io.*;

import wavPackage.WavFile;
	public class PeriodExperiment {
		
	public static int oneInA = 0; //used for blue dots in graph.
	public static void main(String[] args)
	{
		try
		{
			//System.out.println("hello world");
			// Open the wav file specified as the first argument
			String Name = "C:/Sadna/DET Success/HeyJude.wav";
			
			WavFile wavFile = WavFile.openWavFile(new File(Name));
			// Display information about the wav file
			wavFile.display();
			// Get the number of audio channels in the wav file
			int numChannels = wavFile.getNumChannels();

			// Create a buffer of XXXX frames
			
			int buffersize= 4410; //0.1 sec
			
			double[][] channelBuffers = new double[numChannels][buffersize]; //when we read, read into this.
			double[] buffer = channelBuffers[0]; //the first channel. meanwhile, ignore the rest.

			int framesRead = wavFile.readFrames(channelBuffers, 0); //offset from beginning
			int segment = (int) Math.ceil((double)wavFile.getNumFrames()/buffersize); //which segment? 1 to...
			
			System.out.println("number of frames: " + wavFile.getNumFrames());
			System.out.println("number of frames in a segment: " + buffersize);
			System.out.println("number of segments: " + segment);
			segment = 137; //number of segment we want to see.

			for(int i=0; i<segment;i++){ //how much further in the wav
				framesRead = wavFile.readFrames(channelBuffers, buffersize);
				System.out.println("\nsegment number " + (i+1));
				int length = PeriodSearch.findPeriodFinal(buffer);
				System.out.println("Length of the period is: " + length);
				oneInA = length;
			
			}
			
			//print data

			double[] attr = PeriodSearch.ArrayAttributes(buffer);
			System.out.println("Silence? " + PeriodSearch.isSilence(attr));
			
			//adapt buffer to fit graph, and plot
			java.util.List<Integer> a = PeriodSearch.findLocalPosMaxs(buffer);
			for(int k=0;k<buffersize;k++){
				buffer[k]=(buffer[k]*0.5+0.5);
			}
			GraphingData.plotArrayDots(buffer,a);
	
			// Close the wavFile
			wavFile.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
}
