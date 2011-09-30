

import java.util.ArrayList;
import java.util.List;

//sealed

/**
 * @author Guy & Mark
 *  This is the output object that is returned from learnWavFile, our main library function.
 *  It contains simple data about the .wav file, and a list of ChannelInfos, that holds the interesting data
 *  about the music/speech itself.
 *
 */


public class WavFileInfo {
	private String wavFilename; // The name of the .wav file processed in program 
	
	private String PeriodSearch; // "PEAKS" or "PATTERNS", as explained in WavHandler
	
	private String FT; // "SFT" or "DET", as explained in WavHandler
	
	private String channels; // "FIRST" or "ALL", as explained in WavHandler
	
	private int numOfChannels; // The number of channels in the file.
	
	private long sampleRate; //  The number of frames per second.
	
	private long numOfFrames; // The total number of frames in the file
	
	private double duration; // The duration of the file in seconds
	
	private int validBits; // The number of valid bits used for storing a single sample. This is the sample resolution.
		
	private List<ChannelInfo> channelList; // A list containing the information for every channel in the file
	
	public WavFileInfo(){
		this.setWavFilename(null);
		this.setPeriodSearch(null);
		setFT(null);
		setChannels(null);
		this.numOfChannels=-1;
		this.sampleRate=-1;
		this.numOfFrames=-1;
		this.duration=-1;
		this.validBits=-1;
		channelList=new ArrayList<ChannelInfo>();
	}
	
	/**
	 *  Getters and setters: a getter returns -1/NULL if and only if the actual value is unknown
	 */

	public void setWavFilename(String wavFilename) {
		this.wavFilename = wavFilename;
	}

	public String getWavFilename() {
		return wavFilename;
	}
	
	public void setPeriodSearch(String periodSearch) {
		PeriodSearch = periodSearch;
	}

	public String getPeriodSearch() {
		return PeriodSearch;
	}
	
	public void setFT(String fT) {
		FT = fT;
	}

	public String getFT() {
		return FT;
	}
	
	public void setChannels(String channels) {
		this.channels = channels;
	}

	public String getChannels() {
		return channels;
	}
	
	public void setNumOfChannels(int noc){
		this.numOfChannels=noc;
	}
	
	public int getNumOfChannels(){
		return this.numOfChannels;
	}
	
	public long getSampleRate(){
		return this.sampleRate;
	}
	
	public void setSampleRate(long sr){
		this.sampleRate=sr;
	}
	
	public double getDuration(){
		return this.duration;
	}
	
	public void setDuration(double d){
		this.duration=d;
	}
	
	public int getValidBits(){
		return this.validBits;
	}
	
	public void setValidBits(int vb){
		this.validBits=vb;
	}
	
	public long getNumOfFrames(){
		return this.numOfFrames;
	}
	
	public void setNumofFrames(long nof){
		this.numOfFrames=nof;
	}

	public List<ChannelInfo> getChannelist(){
		return channelList;
	}

}