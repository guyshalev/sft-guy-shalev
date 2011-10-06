

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mark
 * This class contains the results of processing a channel in a .wav file.
 * the main attraction is a list of PeriodicityInfos, in chronological order.
 */

//sealed

public class ChannelInfo {
	
	private WavFileInfo parentFile; // The file with which the channel is associated
	
	private List<PeriodicityInfo> periods; // A list of the periods in the channel and their properties.
	
	private int number; // the number of this ChannelInfo in the list.
	
	public ChannelInfo(int number,WavFileInfo pFile){
		periods=new ArrayList<PeriodicityInfo>();
		this.number=number;
		this.parentFile=pFile;
	}
	
	public PeriodicityInfo getPeriod(int k){ // get the k-th period from the list.
		return periods.get(k);
	}
	
	public PeriodicityInfo getPeriodByTime(double time){ // get the period corresponding to the specified time
		for (PeriodicityInfo pinfo:periods) {
			if(pinfo.getStartTime()<=time&&pinfo.getEndTime()>=time) return pinfo;
		}
		return null;
	}
	
	public void addPeriod(PeriodicityInfo perinf){
		periods.add(perinf); // add a new period to the list of existing ones
	}
	
	public int getNumber(){
		return number;
	}
	
	public int howManyPeriods(){
		return periods.size();
	}
	
	public WavFileInfo getParentFile(){
		return this.parentFile;
	}
}


