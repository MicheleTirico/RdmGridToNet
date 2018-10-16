package dataAnalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import RdmGridToNet.layerNet;
import RdmGridToNet.layerSeed;
import RdmGsaNetExport.expCsv;
import scala.Array;

public class analyzeNetwork extends analyze {
	
	private Graph graph = new SingleGraph("grToAn");	
	private String 	header , nameFile  , path , pathNetAn ;
	private FileWriter fileWriter ;
	private handleFolder hF ;
	private boolean run ; 
	
	public analyzeNetwork () throws IOException {
		this(false ,null, null, null, null);
	}
	
	public analyzeNetwork(boolean run ,Graph graph, String path , String nameFolder , String nameFile ) throws IOException {
		this.run = run ;
		this.graph = graph ;
		this.path = path +"\\"+ nameFolder + "\\";
		this.nameFile = nameFile;
		hF = new handleFolder(path) ;
		pathNetAn = hF.createNewGenericFolder(nameFolder);
	}
	
	public void test ( ) throws IOException {	
	}
	
	public void initAnalysis ( ) throws IOException {
		if ( run )
			for ( indicator in : listIndicators ) {
				in.setId(in.toString());
				in.setFw(path +nameFile+"_"+ in + ".csv" );
				header = in.toString();
				expCsv.addCsv_header( in.getFw(), header ) ;
			}
	}
		
// COMPUTE SINGLE INDICATOR -------------------------------------------------------------------------------------------------------------------------
	public void computeSeedCount (int step) throws IOException {
		indicator in = indicator.seedCount ;
		fileWriter = in.getFw() ;
		double val = getValIndicator(in) ;
		expCsv.writeLine(fileWriter, Arrays.asList( Double.toString(step) , Double.toString(val) ) , ';' ) ;
	}
	
	public void computeAverageDegree (int step) throws IOException {
		indicator in = indicator.averageDegree ;
		fileWriter = in.getFw() ;
		double val = getValIndicator(in) ;
		expCsv.writeLine(fileWriter, Arrays.asList( Double.toString(step) , Double.toString(val) ) , ';' ) ;
	}
	
	public void computeGammaIndex (int step) throws IOException {
		indicator in = indicator.gammaIndex ;
		fileWriter = in.getFw() ;
		double val = getValIndicator(in) ;
		expCsv.writeLine(fileWriter, Arrays.asList( Double.toString(step) , Double.toString(val) ) , ';' ) ;
	}
		
	// compute all indicators
	public void computeIndicators ( int t ) throws IOException {
		if ( run ) 
			for ( indicator in : listIndicators ) {
				fileWriter = in.getFw() ; 
				if ( ! in.getIsList() ) {
					double val = getValIndicator(in) ;
					expCsv.writeLine(fileWriter, Arrays.asList( Double.toString(t) , Double.toString(val) ) , ';' ) ;		
				} 
				else {	
					String[] valList = getListValsIndicatorSTR(t,in, 10);
					expCsv.writeLine(fileWriter, Arrays.asList( valList ) , ';' ) ;	
				}			
			}		
	}
	
	// close file writer
	public void closeFileWriter () throws IOException {
		if  ( run ) 
			for ( indicator in : listIndicators)
				in.getFw().close();
	}

	// get list of values for indicator 
	public double[] getListValsIndicator ( indicator in , int numVals) {
		double[] listVal = new double[numVals] ;		
		switch (in) {
			case normalDegreeDistribution :
				listVal = getNDd(numVals) ;
				break ;			
		}
		return listVal ;
	}
	
	// get list ( string ) of values for indicator 
	public String [] getListValsIndicatorSTR ( int t , indicator in , int numVals) {
		double[] listVal = new double[numVals] ;	
		String[] listString  = new String[numVals] ;
		
		switch (in) {
			case normalDegreeDistribution :
				listVal = getNDd(numVals) ;
				break ;			
		}
		int pos = 1 ;
		listString[0] = Integer.toString(t) ; 
		while ( pos< listVal.length ) {
			listString[pos]= Double.toString(listVal[pos]);
			pos++;
		}
		return listString ;
	}
	
	// get single value of indicator 
	public double getValIndicator ( indicator in ) {
		double val = 0 ;	
		switch (in) {
			case seedCount: 
				val = lSeed.getNumSeeds();
				break;
			case averageDegree : 
				val = getAverageDegree(false) ;
				break ;
			case averageDegreeWoD2 : 
				val = getAverageDegree(true) ;
				break ;
			case gammaIndex:
				val = getGammaIndex(graph, true) ;
				break;	
		}
		return val ;
	}
	
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public void setIndicators (indicator indicator ){	
		listIndicators.add(indicator);
	}
	
	public void setIndicators (ArrayList<indicator> list ) {
		listIndicators.addAll(list) ;
	}
// COMPUTE INDICATORS -------------------------------------------------------------------------------------------------------------------------------
	
	// average degree
	private double getAverageDegree ( boolean woD2) {
		
		if ( ! woD2 ) 
			return Toolkit.averageDegree(graph) ;
		else {
			
			return 0 ;
		}
	}
	
	// gamma index
	private static double getGammaIndex ( Graph graph , boolean isPlanar ) {	
		double  n = graph.getNodeCount() , 
				e = graph.getEdgeCount() ,
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
	
	// normal degree distribution 
	public double[] getNDd (  int numberFrequency  ) {
		
		double [] vals = new double[numberFrequency] ;
		
		int[] degreeDistribution = Toolkit.degreeDistribution(graph);
		double nodeCount = graph.getNodeCount() ;
		
		for ( int i = 0 ; i < degreeDistribution.length ; i++ )  {
			vals[i] = (double) degreeDistribution[i] / nodeCount ;	
		}
		return vals ;
	}

}
