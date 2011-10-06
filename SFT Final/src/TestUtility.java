
import java.util.Map;
import java.util.Set;
//sealed

/**
 * This class is a Tester for the project.
 */

public class TestUtility {

	public static void main(String[] args) {
		System.out.println("processing...");
		String inputFileName = "C:/Sadna/DET Success/OrinSongNew.wav";
		String outputFileName = "C:/Sadna/Out.wav";
		
		//create a WavFileInfo
		WavFileInfo result = WavHandler.learnWavFile(inputFileName, "PEAKS", "SFT", "First", 0.1, 20);
		//Display info
		for (ChannelInfo cinf : result.getChannelist()) {
			System.out.println("This is channel number : " + cinf.getNumber());
			for (int periodvar=0; periodvar<cinf.howManyPeriods(); periodvar++) {
				System.out.print("PeriodicityInfo representing segment number: " + periodvar + ",");
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
			System.out.print(" Silence, ");
		} else{
			System.out.print(" NOT Silence, ");
		}
		System.out.println(" N: " + perinf.getPeriodLength() + " ");
		Map<Integer,Complex> heavyChars=perinf.getHeavyCharacters();
		if(heavyChars==null){
			System.out.println("no heavy characters list.");
			return;
		}
		System.out.println("\nThere are " + heavyChars.size() + " heavy characters: ");
		Set<Integer> integers=heavyChars.keySet();
		//if you want to see the list, run this:
		//for (Integer integer : integers) {
		//	System.out.print("" + integer + " coeff size: " + heavyChars.get(integer).sqrdNorm() + ", ");
		//}
		System.out.println();
	}
	
}

