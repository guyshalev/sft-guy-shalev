package Main;

//import java.util.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class xmlReader {
	public static Polynom xmlToPolynom(String filepath){
		try {
			  
			  // Creating file object
			  File file = new File(filepath);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);
			  doc.getDocumentElement().normalize();
			  
			  //parsing file
			  
			  Element ele = doc.getDocumentElement();
			  String functionID = ele.getAttribute("id");
			  //System.out.println("Root element " + functionID);
			  
			 
			  Polynom result = new Polynom(); //will be returned.

			  NodeList nodeLst = ele.getElementsByTagName("function");
			  
			  for (int s = 0; s < nodeLst.getLength(); s++) { //go over functions
				  
				  // Getting the current node
				  Node currNode = nodeLst.item(s);
				  
				  Element funcElem = (Element) currNode;
				  if(funcElem.getAttribute("id").equalsIgnoreCase(functionID)){//we have the right function
					  System.out.println("using function number: " + funcElem.getAttribute("id")+ " from xml file");
					  NodeList monomLst = funcElem.getElementsByTagName("monom");
					 			  
					  double[] realParts = new double[monomLst.getLength()];
					  double[] imParts = new double[monomLst.getLength()];
					  int[] alphaParts = new int[monomLst.getLength()];
					  for(int i=0;i<monomLst.getLength(); i++){
						  NodeList reals = funcElem.getElementsByTagName("real");
						  realParts[i] = new Double(reals.item(i).getTextContent());
						  NodeList ims = funcElem.getElementsByTagName("image");
						  imParts[i] = new Double(ims.item(i).getTextContent());
						  NodeList alphas = funcElem.getElementsByTagName("alpha");
						  alphaParts[i] = new Integer(alphas.item(i).getTextContent());
						  
						  result.addMonom(new Monom(realParts[i],imParts[i],alphaParts[i]));
					  }
				  }
			 }
			 return result;
			
				  
		   }catch (Exception e) {
			    e.printStackTrace();
		   }
		   
	return null;
	}
}
