import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//sealed

/**
 * A useful library that includes fast deterministic versions of FT, and other related helpful functions.
 */

public class FTLibrary {
	
	/**
	 * A short implementation of the Fourier Transform. returns an array of length N, the k'th character coefficient
	 * is in the k'th cell.
	 */
	public static Complex[] ArrayOfAllCharacters(SFTFunction func) {
		Complex[] result = new Complex[func.getN()];
		
		for(int k=0;k<func.getN();k++){
			Complex kweight = new Complex(0,0);
			Complex temp = new Complex(0,0);
			for(int j=0;j<func.getN();j++){
				temp = Complex.Mult(func.funcValue(j), exp(new Complex(0,-2*Math.PI*j*k/func.getN())));
				kweight= Complex.Add(kweight, temp);
			}
			kweight.Div(new Complex(func.getN(),0)); //kweight is now the coef.
			result[k] = kweight;
		}
		return result;
	}
	
	/**
	 * @param func - an SFTFunction, hopefully Fourier-concentrated.
	 * @param num - the number of heaviest coefficients we want. for better results, USE EVEN NUMBERS.
	 * @return a map of the "num" heaviest characters to their coefficients.
	 * there is no need for large "num"s (misses the point) so complexity is OK.
	 */
	public static Map<Integer,Complex> TopNumCharacters(SFTFunction func, int num){
		
		Map<Integer,Complex> map = new HashMap<Integer,Complex>();
		if(num<=0){
			return map;
		}
		Complex[] array = ArrayOfAllCharacters(func);
		
		for(int i=0;i<(int)(num/2);i++){ //find the (i+1)'th heaviest.
			int index=-1;
			double size=-1;
			for(int j=0; j<array.length; j++){
				if(Complex.Abs(array[j])>size){ //new maximum
					size = Complex.Abs(array[j]);
					index = j;
				}
			}
			if(index==0){
				array[index] = Complex.ZERO;
			}
			if(!map.containsKey(index) && index!=0){
				map.put(index, array[index]);
				map.put(func.getN()-index,array[func.getN()-index]); //also take conjugate, for real part.
				array[index] = Complex.ZERO; //so it won't get picked again.
				array[func.getN()-index] = Complex.ZERO;
			}
		}
		return map;
	}
	
	
	
	
	/**
	 * A short implementation of the Fourier Transform. returns a map of the characters k for which:
	 * abs(coef(k))^2 > weight.
	 */
	public static Map<Integer,Complex> getHeavyCharacters(SFTFunction func, double weight) {
		Map<Integer,Complex> result = new HashMap<Integer,Complex>();
		
		for(int k=0;k<func.getN();k++){
			Complex kweight = new Complex(0,0);
			Complex temp = new Complex(0,0);
			for(int j=0;j<func.getN();j++){
				temp = Complex.Mult(func.funcValue(j), exp(new Complex(0,-2*Math.PI*j*k/func.getN())));
				kweight= Complex.Add(kweight, temp);
			}
			kweight.Div(new Complex(func.getN(),0)); //kweight is now the coef.
			if(Math.pow(Complex.Abs(kweight),2)>weight){
				result.put(k,kweight);
			}
		}
		return result;
	}
	
	/**
	 * @param z - some complex.
	 * @return e^z. function is here because Complex class is already sealed with other teams.
	 */
	public static Complex exp(Complex z){
		return new Complex(Math.exp(z.Re())*Math.cos(z.Im()),Math.exp(z.Re())*Math.sin(z.Im()));
	}


	/**
	 * 
	 * @param placeModN - the place in the group we want to calculate - polynom(placeModN)
	 * @param N - size of the group
	 * @param heavyCharacters - the polynom we want to calculate.
	 * @return the value of the calculation.
	 */
	public static Complex getValue(int placeModN, int N, Map<Integer,Complex> heavyCharacters) {
		Complex result = new Complex(0,0);
		if(heavyCharacters==null){
			//System.out.println("in library: get value : problem!!! no CharacterMap in PeriodicityInfo.");
			return result;
		}
		Set<Integer> chars = heavyCharacters.keySet(); //get the integers in the group
		Complex temp;
		for(Integer i : chars){
			temp = Complex.Mult(Complex.Chi(placeModN, i, N), heavyCharacters.get(i));
			result.Add(temp);
		}
		
		return result;
	}

}