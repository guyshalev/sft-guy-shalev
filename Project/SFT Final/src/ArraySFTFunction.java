//sealed

import java.util.Set;
public class ArraySFTFunction implements SFTFunction{

	private Complex[] values;
	private double infNorm; //Uninitialised
	

	/**if user somehow knows infNorm, he will kindly tell us.
	 * default should be 1.0, for WAV files values are bounded by -1 & 1. */
	public ArraySFTFunction(Complex[] values, double infNorm){
		this.values=values;
		this.infNorm=infNorm;
	}
	//the same, for users who don't like complexes...
	public ArraySFTFunction(double[] values, double infNorm){
		this.values= new Complex[values.length];
		for(int i=0;i<values.length;i++){
			this.values[i] = new Complex(values[i],0);
		}
		this.infNorm=infNorm;
	}
	
	public Complex funcValue(int x) {
		return values[x];
	}

	@Override
	public double calcTwoNorm() {
		Set<Integer> random_set = FTLibrary.TempRandomSet(10, values.length);
		double twoNormSqr=0;
		for(Integer a : random_set){
			twoNormSqr += funcValue(a).sqrdNorm();
		}
		twoNormSqr = twoNormSqr/random_set.size(); //average
		//System.out.println("twoNormSqr is: " +twoNormSqr); //DEBUG

		return Math.sqrt(twoNormSqr);
	}

	@Override
	public double calcInfNorm() {
		return infNorm;
	}

	@Override
	public int getN() {
		return values.length;
	}

}
