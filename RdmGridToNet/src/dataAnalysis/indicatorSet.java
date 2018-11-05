package dataAnalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import RdmGridToNet.framework;

public class indicatorSet extends framework {

	private Graph graphToAnalyze;
	private String  path  ;
	private FileWriter fwGI, fwAD,fwSC,fwNDD , fwPLD , fwDD;
	private int numFreqNND , numFreqPLD;
	private double minValFreqNND , maxValFreqPLD;
	private analyzeNetwork aN ;
	public enum indicator { 
		seedCount("seedCount" , false ),
		gammaIndex("gammaIndex", false ) , 
		averageDegree("averageDegree", false ) ,
		degreeDistribution ("degreeDistribution", true , 0,0,0) ,
		normalDegreeDistribution ("normalDegreeDistribution", true , 0 , 0 , 0 ),
		pathLengthDistribution("pathLengthDistribution", true , 0 , 0 , 0 );
	
		private String id ;
		private boolean isList ;
		private int numFreq;
		private double minVal, maxVal ; 
		private String header ;
		
		private indicator ( String id , boolean  isList ) {
			this.id = id ;
			this.isList = isList ;
		}
		
		private indicator ( String id , boolean  isList, int numFreq, double minVal, double maxVal ) {
			this.id = id ;
			this.isList = isList ; 
		}
	
		public void setFrequencyParameters ( int numFreq, double minVal, double maxVal ) {
			this.numFreq = numFreq;
			this.minVal = minVal;
			this.maxVal = maxVal ;	
		}
		
		public double[] getFrequencyParameters () {
			return new double[] {numFreq,minVal,maxVal};
		}
		
		public void setHeader ( ) {
			if ( isList) {
				// System.out.println(maxVal + " " + minVal + " " + numFreq);
				double gap = ( maxVal - minVal ) / numFreq ;
				header = getId() + ";"; 
				int x = 0 ;
				while ( x < numFreq ) {
					NumberFormat nf = NumberFormat.getNumberInstance();
					nf.setMaximumFractionDigits(1);
					String rounded = nf.format(gap * x );
					header = header + rounded + ";";
					x++ ;
					
				}
			}
			else 
				header = getId();
		}	
		public String getHeader ( ) {
			return header;
		}
		public String getId ( ) {
			return id ;
		}
		public void setId ( String id ) {
			this.id = id ;
		}	
		public void setIsList ( boolean isList ) {
			this.isList = isList ;
		}
		public boolean getIsList() {
			return isList ;
		}	
	}	

	public void setPath(String path) {	
		this.path = path;
	}
	
	public void setFw (indicator in) throws IOException {	
		System.out.println(path); 
		switch (in) {
			case gammaIndex:
				fwGI = new FileWriter(path , true);
			break;
			case seedCount : 
				fwSC = new FileWriter(path , true);
				break ;
			case averageDegree :
				fwAD = new FileWriter(path , true);
				break ;	
			case normalDegreeDistribution :
				fwNDD = new FileWriter(path, true);
				break ;
			case pathLengthDistribution :
				fwPLD = new FileWriter(path,true) ;
				break;
			case degreeDistribution :
				fwDD = new FileWriter(path, true) ;
		}
	}
	
	public FileWriter getFw (indicator in ) {
		FileWriter fw = null ;	
		switch (in) {
			case gammaIndex:
				fw = fwGI;
				break;
			case seedCount : 
				fw = fwSC;
				break ;
			case averageDegree :
				fw = fwAD;
				break ;	
			case normalDegreeDistribution :
				fw = fwNDD ;
				break ;
			case pathLengthDistribution :
				fw = fwPLD;
				break; 
			case degreeDistribution :
				fw = fwDD ;
				break ;
		}
		return fw ;
	}
	
	public void setGraph ( Graph graph ) {
		this.graphToAnalyze = graph; 
	}
	
// GET VALUE ----------------------------------------------------------------------------------------------------------------------------------------
	public double[] getValueArr ( indicator in ) {
		double[] val = null ;
		double[] freqParams = in.getFrequencyParameters();
		
		switch (in) {
			case normalDegreeDistribution:
				val = getNDD ((int) freqParams[0] );
				break;
			case pathLengthDistribution:
				val = getPLD((int) freqParams[0] ,freqParams[1] ,freqParams[2] );
				break ;
			case degreeDistribution :
				val = getDD ((int)freqParams[0] ) ;
				break ;
		}
		System.out.println(graphToAnalyze + " "+ in.getId() + " "+ val);
		return val ;
	}

	public double getValue ( indicator in ) {
		double val = 0 ;	
		switch (in) {
			case gammaIndex:
				val = getGammaIndex(true);
			break;
			case seedCount : 
				val = getSeedCount();
				break ;
			case averageDegree :
				val = getAverageDegree();
				break ;	
		}
		System.out.println(graphToAnalyze + " "+ in.getId() + " "+ val);
		return val ;
	}

// COMPUTE INDICATOR --------------------------------------------------------------------------------------------------------------------------------	
	// get Seed Count
	private double getSeedCount() {
		return lSeed.getNumSeeds() ;
	}
	
	// average degree
	private double getAverageDegree ( ) {
		return Toolkit.averageDegree(graphToAnalyze) ;
	}
	
	// gamma index
	private double getGammaIndex ( boolean isPlanar ) {	
		double  n = graphToAnalyze.getNodeCount() , 
				e = graphToAnalyze.getEdgeCount() ,
				eMax = 0 ;		
		if ( isPlanar )
			eMax = 3 * n - 6 ; 
		else 
			eMax = ( n - 1 ) * n / 2 ;
		
		if ( eMax == 0 || e == 0)	
			return 0 ;
		else 
			return e / eMax ;
	}
	
	// degree distribution 
	private double[] getDD (  int numberFrequency  ) {
		double [] vals = new double[numberFrequency] ;	
		int[] degreeDistribution = Toolkit.degreeDistribution(graphToAnalyze);
		for (int i = 0 ; i < degreeDistribution.length ; i++ )
			vals[i] = (double) degreeDistribution[i];
	
		return vals ;
	}
	
	// normal degree distribution 
	private double[] getNDD (  int numberFrequency  ) {
		double [] vals = new double[numberFrequency] ;	
		int[] degreeDistribution = Toolkit.degreeDistribution(graphToAnalyze);
		double nodeCount = graphToAnalyze.getNodeCount() ;
		for ( int i = 0 ; i < degreeDistribution.length ; i++ )  
			try {
				vals[i] = (double) degreeDistribution[i] / nodeCount ;		
			} catch (ArrayIndexOutOfBoundsException e) {
				break ; 
			}	
		return vals ;
	}
	
	// path length distribution
	private double[] getPLD (  int numberFrequency , double valMin , double valMax ) {
		double [] vals = new double[numberFrequency] ;	

		Map<Edge, ArrayList<Double>> mapEdgeLen = getMapEdgeValue(graphToAnalyze, "listLen");
		ArrayList<Double> listLen = new ArrayList<Double>() ;

		for ( Edge e : mapEdgeLen.keySet()) 
			for ( double val : mapEdgeLen.get(e))
				listLen.add(val);
		
		Map<Double, Double> map = new TreeMap<>(getMapFrequencyAss(listLen, numberFrequency ,valMin , valMax)) ;
		int pos = 0 ;
		for ( double key : map.keySet()) {
			vals[pos] = map.get(key);
			pos++;
		}
		return vals;
	}

// GET VALUES FOR EACH GRAPH ELEMENT ---------------------------------------------------------------------------------------------------------------- 
	private Map getMapNodeValue ( Graph gr , String attr ){	
		Map map = new HashMap();
		for ( Node n : gr.getEachNode() ) {
			map.put(n, n.getAttribute(attr));
		}
		return map;
	}
	
	private Map getMapEdgeValue ( Graph gr , String attr ){
		Map map = new HashMap();
		for ( Edge e : gr.getEachEdge() ) {
			map.put(e, e.getAttribute(attr));
		}
		return map;
	}

// FREQUENCY DISTRIBUTION ---------------------------------------------------------------------------------------------------------------------------
	private Map<Double, Double> getMapFrequencyRel ( ArrayList<Double> listVal, int numberFrequency ) {
		Map<Double, Double> map = new HashMap<>();
		double  maxVal = listVal.stream().mapToDouble(valstat -> valstat).max().getAsDouble(),
				minVal = listVal.stream().mapToDouble(valstat -> valstat).min().getAsDouble(),
				gap = maxVal - minVal,
				increm = minVal + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);		
		for ( int x = 0 ; x < numberFrequency ; x++) {
			double  key = minVal + gap * x / numberFrequency ,
					minFreq = minVal + x* increm ,
					maxFreq = minVal + (x + 1)* increm ,
					freq = 	listVal.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();		
			map.put(  key  ,  freq );
		}	
		return map ;
	}
	
	private static Map<Double, Double>  getMapFrequencyAss (ArrayList<Double> listVal , int numberFrequency , double valMin , double valMax  ) {
		Map<Double, Double> mapFrequency = new HashMap<Double, Double>();
		double gap = valMax - valMin ,
				increm = valMin + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);	
		for ( int x = 0 ; x < numberFrequency ; x++) {
			double  key = valMin + gap * x / numberFrequency,
					minFreq = valMin + x* increm ,
					maxFreq = valMin + (x + 1)* increm ,
					freq = 	listVal.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();	
			mapFrequency.put( key  ,  freq );
		}
		return mapFrequency;		
	}
	
	public static Map<Double, Double> getMapFrequencyRel ( Graph graph , String attribute , int numberFrequency ) {
		
		Map<Double, Double> mapFrequency = new HashMap<>();
		Map <Node, Double> mapIdAtr = new HashMap<>();
		ArrayList<Double> listAtr = new ArrayList<>();
		double val;																						//	System.out.println("listAtr " + listAtr);
		
		double maxAtr = listAtr.stream().mapToDouble(valstat -> valstat).max().getAsDouble();
		double minAtr = listAtr.stream().mapToDouble(valstat -> valstat).min().getAsDouble(); 		//			System.out.println("maxAtr " + maxAtr);		System.out.println("minAtr " + minAtr);
		
		double gap = maxAtr - minAtr;
		double increm = minAtr + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);
		
		for ( int x = 0 ; x < numberFrequency ; x++) {
			double key = minAtr + gap * x / numberFrequency ;
			
			double minFreq = minAtr + x* increm ;
			double maxFreq = minAtr + (x + 1)* increm ;
			
			double freq = 	listAtr.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();
			
			mapFrequency.put(  key  ,  freq );
		}
		return mapFrequency;	
	}
	
	public static Map getMapFrequencyAss ( Graph graph , String attribute , int numberFrequency ) {
		
		Map<Double, Double> mapFrequency = new HashMap<>();
		Map <Node, Double> mapIdAtr = new HashMap<>();
		ArrayList<Double> listAtr = new ArrayList<>();
																							//	System.out.println("listAtr " + listAtr);	
		double maxAtr = 1;
		double minAtr = 0;
		
		double gap = maxAtr - minAtr;
		double increm = minAtr + gap / numberFrequency;												//	System.out.println("gap " + gap);	System.out.println("increm " + increm);
		
		for ( int x = 0 ; x < numberFrequency ; x++) {
			double key = minAtr + gap * x / numberFrequency ;		
			double minFreq = minAtr + x* increm ;
			double maxFreq = minAtr + (x + 1)* increm ;			
			double freq = 	listAtr.stream()
							.filter(p -> p >=  minFreq && p < maxFreq )
							.count();
			
			mapFrequency.put( key  ,  freq );
		}
		return mapFrequency;		
	}
}
