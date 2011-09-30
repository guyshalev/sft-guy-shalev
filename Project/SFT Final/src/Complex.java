/**
 * Complex.java 
 * @author Matan (Smut) Ginzburg - with small changes - we added a small function.
 * if you want to use are project, just add it too:
 * public double sqrdNorm(){
 * 		return (Re*Re + Im*Im);
 * }
 */
//sealed

public class Complex {

        public static final Complex ZERO = new Complex();
        public static final Complex REAL_UNIT = new Complex(1,0);
        public static final Complex IMAGINARY_UNIT = new Complex(0,1);
        
        private double Re;
        private double Im;
        
        public Complex() {
                this.Re = this.Im = 0;
        }
        
        public Complex(double Re, double Im) {
                
                this.Re = Re;
                this.Im = Im;
        }
        
        public Complex(Complex arg) {
                this.Re = arg.Re;
                this.Im = arg.Im;
        }
        
        public double Re() {return Re;}
        public double Im() {return Im;}
        
        public void Add(Complex other) {
                this.Re += other.Re;
                this.Im += other.Im;
        }
        
        public void Mult(Complex other) {
                this.Re = this.Re*other.Re - this.Im*other.Im;
                this.Im = this.Re*other.Im + other.Re*this.Im;
        }
        
        public void Sub(Complex other) {
                this.Add(Complex.Opp(other));
        }
        
        public void Div(Complex other) {
                this.Mult(Complex.Inv(other));
        }
        
        public static Complex Add(Complex arg1, Complex arg2) {
                return new Complex(arg1.Re+arg2.Re, arg1.Im+arg2.Im);
        }
        
        public static Complex Mult(Complex arg1, Complex arg2) {
                return new Complex(arg1.Re*arg2.Re-arg1.Im*arg2.Im, arg1.Re*arg2.Im+arg2.Re*arg1.Im);
        }
        
        public static Complex Sub(Complex arg1, Complex arg2) {
                return Add(arg1, Opp(arg2));
        }
        
        public static Complex Div(Complex arg1, Complex arg2) {
                return Mult(arg1, Inv(arg2));
        }
        
        /*public static Complex Div(Complex arg1, double arg2) {
                return new Complex(arg1.Re/arg2, arg1.Im/arg2);
        }*/
        
        public static Complex Opp(Complex arg){
                return Mult(arg,new Complex(-1,0));
        }
        
        public static Complex Inv(Complex arg) {
                Complex ret = Conjecture(arg);
                double abs = Abs(arg)*Abs(arg);
                ret.Re = ret.Re/abs;
                ret.Im = ret.Im/abs;
                return ret;
        }
        
        public static Complex Conjecture(Complex arg) {
                return new Complex(arg.Re,-arg.Im);
        }
        
        public static double Abs(Complex arg){
                return Math.sqrt(Mult(arg,Conjecture(arg)).Re);
        }
        
        public static Complex Chi(double r, double y, int N) {
                double x = (2*Math.PI*r*y)/N;
                return new Complex(Math.cos(x), Math.sin(x));
        }
        
        public static Complex parseComplex(String arg){
                String t = arg.trim();
                int place;
                double Re, Im;
                
                if(t.equals(""))
                        return new Complex();
                
                place = t.indexOf("+");
                if(place==-1) {
                        if(t.charAt(t.length()-1)=='i'){
                                Re = 0;
                                if(t.length()==1)
                                        Im = 1;
                                else
                                        Im = Double.parseDouble(t.substring(0, t.length()-1));
                        }
                        else {
                                Re = Double.parseDouble(t);
                                Im = 0;
                        }
                }
                else {
                        Re = Double.parseDouble(t.substring(0, place));
                        if(t.charAt(place+1)=='i')
                                Im = 1;
                        else
                                Im = Double.parseDouble(t.substring(place+1, t.length()-1));
                }
                return new Complex(Re,Im);
        }
        
        public boolean equals(Object o){
                Complex c;
                if(!(o instanceof Complex))
                        return false;
                c = (Complex) o;
                return this.Re == c.Re && this.Im == c.Im;
        }
        
        public String toString() {
                if(this.Re==0 && this.Im==0)
                        return "0";
                if(this.Re==0)
                        return this.Im+"i";
                if(this.Im==0)
                        return this.Re+"";
                if(this.Im<0)
                        return this.Re+""+this.Im+"i";
                return this.Re+"+"+this.Im+"i";
        }
        
    	public double sqrdNorm(){
    		return (Re*Re + Im*Im);
    	}
}



