package Main;

public class Complex {
	public double real;
	public double im;
	
	public Complex (double real, double im)
	{
		this.real = real;
		this.im = im;
	}
	
	public String toString()
	{

		Double a = this.real;
		Double b = this.im;
		return (a.toString() + " + i* " + b.toString());
	}
	
	public static Complex multiply(Complex one, Complex other){
		return (new Complex(one.real*other.real - one.im*other.im,
				one.real*other.im+one.im*other.real));
	}
	
	public static Complex add(Complex one, Complex other){
		return (new Complex(one.real+other.real, one.im+other.im));
	}
	
	public double sqrdNorm(){
		return (real*real + im*im);
	}
}
