import java.util.HashMap;
import java.util.Map;

//sealed
/**
 * This class represents an atomic part of the output - it contains all relevant data about one segment.
 */

public class PeriodicityInfo {
	
	private boolean isSilence; // if segment is silent (no sound), it is true. if true - all other data is worthless.

	private double[] period;  // the recurring period of the current segment
	
	private int periodLength; //  the length of the period mentioned above
	
	private int numOfAppearances; // how much times does the period appear in the segment? 
	
	private double startTime; // the beginning time of the segment
	
	private double endTime; // the end time of the segment
	
	private boolean status; // indicates whether the segment is periodic or not (true <--> periodic). if true - all other data is worthless.
	
	private Map<Integer,Complex> heavyCharacters; // the list of heavy characters for this period.
	
	public PeriodicityInfo(boolean isSilence, double[] period,
			int numOfAppearances, boolean status, double startTime, double endTime){
		this.isSilence = isSilence;
		this.status=status;
		this.startTime=startTime;
		this.endTime=endTime;
		if((!status) || isSilence) return;
		this.period=period;
		if(period!=null) periodLength=period.length;
		this.numOfAppearances=numOfAppearances;
		this.heavyCharacters=new HashMap<Integer,Complex>();
	}
	
	public boolean isSilence(){
		return isSilence;
	}
	
	public double[] getPeriod(){
		return period;
	}
	
	public int getNumOfAppearances(){
		return this.numOfAppearances;
	}

	public double getStartTime() {
		return this.startTime;
	}
	
	public double getEndTime() {
		return this.endTime;
	}
	
	public boolean getStatus(){
		return this.status;
	}
	
	public void putHeavyCharacters(Map<Integer,Complex> heavyChars){
		if(heavyChars!=null){
			heavyCharacters.putAll(heavyChars);
		}
	}
	
	public Map<Integer,Complex> getHeavyCharacters(){
		return this.heavyCharacters;
	}

	public int getPeriodLength() {
		return this.periodLength;
	}
	
}
