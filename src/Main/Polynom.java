package Main;
import java.util.*;

public class Polynom {
	private List<Monom> monomList;
	
	public Polynom(){
		monomList = new LinkedList<Monom>();
	}
	
	public void addMonom(Monom a){
		monomList.add(a);
	}
	
	public Complex calc(int x){
		Complex result = new Complex(0,0);
		for(Monom mon : monomList){
			result = Complex.add(result,mon.calc(x));
		}
		return result;
	}
	
	public String toString(){
		String str="Polynom = ";
		for(Monom a : monomList){
			str = str + a.toString() + " + ";
		}
		return str;
	}
}
