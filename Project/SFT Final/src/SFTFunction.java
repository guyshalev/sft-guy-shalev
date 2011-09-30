/**
 * 
 * @author Guy & Mark
 * An interface of an SFTFunction - implemented by classes such as ArraySFTFunction and XmlSFTFunction.
 *
 */

//sealed

public interface SFTFunction {
	
	/** returns value of function for integer x in group */
	public Complex funcValue(int x);
	
	/** calculates approximation of 2-norm of the function */
	public double calcTwoNorm(); 
	
	/** gives approximation of infinite-norm (max value) of the function */
	public double calcInfNorm();
	
	/** gives the size of group - domain of the function */
	public int getN();
	
}