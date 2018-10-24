package dataAnalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import dataAnalysis.analysis.indicator;
import dataAnalysis_old_01.expCsv;
import dataAnalysis_old_01.handleFolder;

public class analyzeNetwork  {
	
	private ArrayList<indicator> listIndicators = new ArrayList<indicator> ();
	private Graph graphToAnalyze ;
	boolean run ;
	double stepToAnalyze ;
	private String 	header , nameFile  , path , pathNetAn ;
	private FileWriter fileWriter ;
	private handleFolder hF ;
	
	public analyzeNetwork () throws IOException {
		this(false ,0 ,null, null, null, null);
	}
	
	public analyzeNetwork (boolean run , double stepToAnalyze ,Graph graphToAnalyze, String path , String nameFolder , String nameFile ) throws IOException {
		this.run = run ;
		this.stepToAnalyze = stepToAnalyze ;
		this.graphToAnalyze = graphToAnalyze ;
		this.path = path +"\\"+ nameFolder + "\\";
		this.nameFile = nameFile;
		hF = new handleFolder(path) ;
		pathNetAn = hF.createNewGenericFolder(nameFolder); 
		analysis an = new analysis();
		an.init(graphToAnalyze);
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
	
	public void compute (int t) throws IOException {
		for ( indicator in : listIndicators ) {
			in.setGraph(graphToAnalyze);
			System.out.println(graphToAnalyze + " " + in.getValue() + " " + in);
			System.out.println(graphToAnalyze);		
	
			fileWriter = in.getFw() ; 
		
		
			double val = in.getValue() ;
			expCsv.writeLine(fileWriter, Arrays.asList( Double.toString(t) , Double.toString(val) ) , ';' ) ;		
		
		}
	}
	
	public void compute () throws IOException {
		for ( indicator in : listIndicators ) {
			in.setGraph(graphToAnalyze);
			System.out.println(graphToAnalyze + " " + in.getValue() + " " + in);
			System.out.println(graphToAnalyze);		
		
		}
	}
	
	// close file writer
		public void closeFileWriter () throws IOException {
			if  ( run ) 
				for ( indicator in : listIndicators )
					in.getFw().close();
		}
		
		
// SET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	public void setIndicators ( indicator indicator ){	
		listIndicators.add(indicator);
	}
	
	public void setIndicators ( Collection<? extends indicator> list ) {
		listIndicators.addAll(list) ;
	}
	


}