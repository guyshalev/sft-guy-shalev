
//sealed
/**
 * 
 * @author Guy & Mark
 * This class represents an interval, from integer A to integer B (A<B) and is used in the SFT algorithm
 *
 */

public class Interval {
	public int lower;
	public int upper;
	
	public Interval (int lower, int upper)
	{
		this.lower = lower;
		this.upper = upper;
	}

	public Interval getLowerHalf()
	{
		if(upper==lower){
			return new Interval(lower,upper);
		}
		return (new Interval(lower,(int)Math.ceil((double)(upper - lower)/2) - 1 + lower));
	}
	
	public Interval getUpperHalf()
	{
		if(upper==lower){
			return new Interval(lower,upper);
		}
		return (new Interval((int)Math.ceil((double)(upper - lower)/2) + lower, upper));
	}
	
	public String toString()
	{

		Integer a = this.lower;
		Integer b = this.upper;
		return (a.toString() + "..." + b.toString());
	}
	
	public boolean isDegenerated(){
		return lower==upper;
	}
}