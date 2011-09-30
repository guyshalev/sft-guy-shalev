
import java.util.Map;
import java.util.Set;
//sealed

/**
 * This class is a Tester for the project.
 */

public class TestUtility {

	public static void main(String[] args) {
		System.out.println("processing...");
		String inputFileName = "C:/Sadna/PianoNote.wav";
		String outputFileName = "C:/Sadna/garb2.wav";
		
		//create a WavFileInfo
		WavFileInfo result = WavHandler.learnWavFile(inputFileName, "PEAKS", "DET", "First", 0.1, 20);
		//Display info
		for (ChannelInfo cinf : result.getChannelist()) {
			System.out.println("This is channel number : " + cinf.getNumber());
			for (int periodvar=0; periodvar<cinf.howManyPeriods(); periodvar++) {
				System.out.print("Periodicity info number: " + periodvar);
				displayInfo(cinf.getPeriod(periodvar));
			}
			System.out.println("This was channel number : " + cinf.getNumber());
		}
		System.out.println("now make a wav file.");
		//create the "mimic wav file"
		WavHandler.WavFileInfoToWavFile(result, outputFileName);
	}

	/**
	 * An elegant way to display a PeriodicityInfo object.
	 */
	private static void displayInfo(PeriodicityInfo perinf) {
		if (!perinf.getStatus()) {
			System.out.println(" No periodicity was found");
			return;
		}
		if(perinf.isSilence()){
			System.out.print(" Silence ");
		} else{
			System.out.print(" NOT Silence ");
		}
		System.out.print(" N: " + perinf.getPeriodLength() +  " heavy charcters: ");
		Map<Integer,Complex> heavyChars=perinf.getHeavyCharacters();
		if(heavyChars==null){
			System.out.println("no heavy characters list.");
			return;
		}
		Set<Integer> integers=heavyChars.keySet();
		for (Integer integer : integers) {
			System.out.print(" , " + integer + " coeff: " + heavyChars.get(integer));
		}
		System.out.println();
	}
	
}

