package Main;

public class Monom {
	Complex coef;
	double alpha;
	public static int N;
	
	public Monom(double real, double im, double alpha){
		this.coef = new Complex(real,im);
		this.alpha=alpha;
	}
	
	public Complex calc(int x){
		double real = Math.cos(2*Math.PI*alpha*x/(double)Monom.N);    //2PI*A*X/N
		double im = Math.sin(2*Math.PI*alpha*x/(double)Monom.N);
		
		return Complex.multiply(coef,new Complex(real,im));
	}
	
	public String toString(){
		return "coef: " + coef.toString() + ", chi_" + new Double(alpha).toString();
	}
}
