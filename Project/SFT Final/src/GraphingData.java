
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

/**
 * 
 * @author Guy & Mark
 * A SUPER-helpful class that allows us to SEE the wav file beautifully, and add colourful dots in strategic places.
 * It has been one of our main tools in development and debugging.
 *
 */
//sealed

public class GraphingData extends JPanel {
    double[] data;
    List<Integer> dots;
    final int PAD = 20;
    
    protected GraphingData(double[] data){
    	this.data=data;
    	this.dots=null;
    }

    public GraphingData(double[] data, List<Integer> dots) {
    	this.data=data;
    	this.dots=dots;
    }

	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = "data";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        // Abcissa label.
        s = "x axis";
        sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/(data.length-1);
        double scale = (double)(h - 2*PAD)/getMax();
        g2.setPaint(Color.green.darker());
        for(int i = 0; i < data.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*data[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*data[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Mark data points.
        g2.setPaint(Color.blue);
        if(PeriodExperiment.oneInA!=0){
	        int i=0;
	        while(i<data.length) {
	           double x = PAD + i*xInc;
	           double y = h - PAD - scale*data[i];
	           g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
	           i+=PeriodExperiment.oneInA;
	        }
        }
        g2.setPaint(Color.red);
        if(dots!=null){
        	for(Integer i : dots){
	            double x = PAD + i*xInc;
	            double y = h - PAD - scale*data[i];
	            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        	}
        }	
    }

    private double getMax() {
        double max = -Integer.MAX_VALUE;
        for(int i = 0; i < data.length; i++) {
           if(data[i] > max)
                max = data[i];
        }
        return max;
    }

    public static void plotArray(double[] data) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new GraphingData(data));
        f.setSize(1500,500);
        f.setLocation(200,200);
        f.setVisible(true);
    }
    
    public static void plotArrayDots(double[] data, java.util.List<Integer> dots) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new GraphingData(data, dots));
        f.setSize(1500,500);
        f.setLocation(200,200);
        f.setVisible(true);
    }
}