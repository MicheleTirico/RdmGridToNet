package dataAnalysis;

import java.io.FileWriter;
import java.io.IOException;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;

import RdmGridToNet.framework;

public class analysis extends framework {

	private Graph graphToAnalyze;
	private String 	header , nameFile  , path , pathNetAn ;
	private FileWriter fileWriter ;
	
	public enum indicator { 
		seedCount("sC" , false  ),
		gammaIndex("gI", false ) , 
		averageDegree("aD", false ) ,
		normalDegreeDistribution ("nDd", true );
		
		private Graph graphToAnalyze ;	
		private String id ;
		private boolean isList ;
		private String path ;
		private FileWriter fw ;
		
		private indicator ( String id , boolean  isList ) {
			this.id = id ;
			this.isList = isList ; 
		}
	
		public String getId ( ) {
			return id ;
		}
		public void setId ( String id ) {
			this.id = id ;
		}	
		public void setIsList ( boolean isList) {
			this.isList = isList ;
		}
		public boolean getIsList() {
			return isList ;
		}	
		
		public double  test () {
			System.out.println(graphToAnalyze);
			return 0 ;
		}
		public void setGraph ( Graph graph ) {
			this.graphToAnalyze = graph ;
		}
		
		public double getValue() { 
			
			double val = 0 ;
			switch (id) {
			case "gI":
				val = getGammaIndex(true);
				break;
			case "sC" : 
				val = getSeedCount();
				break ;
			case "aD" :
				val = getAverageDegree();
				break ;	
			
			}
			System.out.println(val);
			return val ;
		}
		public void setFw ( String path )throws IOException {
			fw = new FileWriter(path,  true) ;
			this.path = path ;
		}
		
		public FileWriter getFw ( ) {
			return fw ;
		}
		
		// get Seed Count
		private  double getSeedCount() {
			return lSeed.getNumSeeds() ;
		}
		
		// average degree
		private  double getAverageDegree ( ) {
			return Toolkit.averageDegree(graphToAnalyze) ;
		}
		
		// gamma index
		private  double getGammaIndex ( boolean isPlanar ) {	
			double  n = graphToAnalyze.getNodeCount() , 
					e = graphToAnalyze.getEdgeCount() ,
					eMax = 0 ;		
			System.out.println("pappa");
			if ( isPlanar )
				eMax = 3 * n - 6 ; 
			else 
				eMax = ( n - 1 ) * n / 2 ;
			
			if ( eMax == 0 || e == 0)	
				return 0 ;
			else 
				return e / eMax ;
		}
		
		// normal degree distribution 
		public  double[] getNDd (  int numberFrequency  ) {
			double [] vals = new double[numberFrequency] ;	
			int[] degreeDistribution = Toolkit.degreeDistribution(graphToAnalyze);
			double nodeCount = graphToAnalyze.getNodeCount() ;
			for ( int i = 0 ; i < degreeDistribution.length ; i++ )  
				vals[i] = (double) degreeDistribution[i] / nodeCount ;		
			return vals ;
		}
	}

	
	public double compute () {
		System.out.println(this.getClass());
		return 0 ;
	}
	
	public void  init (Graph graph ) {
		this.graphToAnalyze = graph ;
	}
	
// COMPUTE INDICATORS -------------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	
	
	
	
}
