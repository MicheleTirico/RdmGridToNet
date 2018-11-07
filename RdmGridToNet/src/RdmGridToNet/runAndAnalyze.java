package RdmGridToNet;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import RdmGridToNet.framework.RdmType;
import RdmGridToNet.framework.morphogen;
import RdmGridToNet.framework.typeVectorField;
import RdmGridToNet.layerMaxLoc.typeComp;
import RdmGridToNet.layerMaxLoc.typeInit;
import RdmGridToNet.layerRd.typeDiffusion;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.storeRd.whichMorpToStore;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;
import viz.*;

public class runAndAnalyze extends framework {
	// common parameters
		private static int stepToStore = 10 , 
				stepToAnalyze = 10 ,
				stepToPrint = 100 ,
				stepMax = 50 ;
		
		private static  String  path = "D:\\ownCloud\\RdmGrid_exp\\test" ;
		
	// store and analysis parameters 
	private static boolean  runStoreRd = false ,
			runStoreSimNet = false , 
			runStoreNet = false ,
			runSimNet = true , 
			runAnalysisNet = true,
			runAnalysisSimNet = true ;
	
	// layer Rd
	private static int sizeGridX = 200, 
			sizeGridY = 200 ;
	private static double Da = 0.2 ,
			Db = 0.1 ,
			initVal0 = 1 ,
			initVal1 = 0 ,
			perturVal0 = 1 ,
			perturVal1 = 1 ;	
	private static typeDiffusion tyDif = typeDiffusion.mooreCost ;
	
	// layer Local Max
	
	// layer seed and vector field
	private static morphogen m = morphogen.b;
	private static double r = 2,
			minDistSeed = 1 , 
			alfa = 2 ;
	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;

	public static void main(String[] args) throws Exception {	
		// bucket set
		bks = new bucketSet(1, 1, sizeGridX, sizeGridY);
		bks.initializeBukets();

		// layer Rd
		lRd = new layerRd(1, 1, sizeGridX, sizeGridY, typeRadius.circle);		
		lRd.initializeCostVal(1,0);	
		
		// set Rd classical pattern
		setRdType ( RdmType.solitions) ;	
		f = 0.01 ; k = 0.025 ; 
		lRd.setGsParameters(f , k , Da, Db, typeDiffusion.mooreCost );
		
		lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.wholeGrid, m );
		lMl.initializeLayer();
		
		lNet = new layerNet("net") ;	
		Graph netGr = lNet.getGraph();
		Graph locGr = lMl.getGraph() ;
		
		lSeed = new layerSeed( r , m , alfa  );
		initMultiCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );		
		
		lNet.setLengthEdges("length" , true );
				
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		
		String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
		System.out.println(nameFile);
		
		// initialize rd store csv
		storeRd storeRd = new storeRd(runStoreRd, whichMorpToStore.a, 10 , path, "storeRd", nameFile + "Rd") ;
		storeRd.initStore();
		
		// Initialize simplify network
		symplifyNetwork simNet = new symplifyNetwork(runSimNet, netGr);
		simNet.init( stepToAnalyze);
		Graph simNetGr = simNet.getGraph() ;
		
		// initialize store network
		storeNetwork storeNet = new storeNetwork(runStoreNet, stepToAnalyze, netGr, path, "storeNet", nameFile) ;
		storeNet.initStore();
				
		// initialize store simplified network 
		storeNetwork storeSimNet = new storeNetwork(runStoreSimNet, stepToAnalyze, simNetGr, path, "storeSimNet", nameFile);
		storeSimNet.initStore();
		
		// initialize analysis network
		analyzeNetwork analNet = new analyzeNetwork(runAnalysisNet, false ,stepToAnalyze, netGr, path, "analysisNet", nameFile);
		indicator.normalDegreeDistribution.setFrequencyParameters(10, 0, 10);
		indicator.degreeDistribution.setFrequencyParameters(10, 0, 10); 
		
		Map mapNet = new TreeMap<>();
		
		mapNet.put("sizeGrid",  sizeGridX);
		mapNet.put("Da", Da);
		mapNet.put("Db", Db);				
		mapNet.put("f", f);
		mapNet.put("k", k);
		mapNet.put("numStartSeed",  numNodes);
		mapNet.put("stepStore" , stepToStore) ;
		analNet.setupHeader(false, mapNet);
		
		analNet.setIndicators(Arrays.asList(
				indicator.seedCount ,
				indicator.degreeDistribution,
				indicator.normalDegreeDistribution ,
				indicator.edgeCount ,
				indicator.totalEdgeLength 
				));
		analNet.initAnalysis();
		
		// initialize analysis simplify network
		analyzeNetwork analSimNet = new analyzeNetwork(runAnalysisSimNet, false ,stepToAnalyze, simNetGr, path, "analysisSimNet", nameFile);		
		indicator.normalDegreeDistribution.setFrequencyParameters(10, 0, 10);
		indicator.degreeDistribution.setFrequencyParameters(10, 0, 10); 
		indicator.pathLengthDistribution.setFrequencyParameters(10, 0, 5);
		Map mapSimNet = new TreeMap<>();
		
		mapSimNet.put("sizeGrid",  sizeGridX);
		mapSimNet.put("Da", Da);
		mapSimNet.put("Db", Db);				
		mapSimNet.put("f", f);
		mapSimNet.put("k", k);
		mapSimNet.put("numStartSeed",  numNodes);
		mapSimNet.put("stepStore" , stepToStore) ;
	
		analSimNet.setupHeader(false, mapSimNet);
		
		analSimNet.setIndicators(Arrays.asList(
				indicator.pathLengthDistribution ,
				indicator.averageDegree ,
				indicator.degreeDistribution ,
				indicator.edgeCount ,
				indicator.totalEdgeLength 
				));
		analSimNet.initAnalysis();

		// setup viz netGraph
		handleVizStype netViz = new handleVizStype( netGr ,stylesheet.manual , "seed", 1) ;
		netViz.setupIdViz(false , netGr, 20 , "black");
		netViz.setupDefaultParam (netGr, "black", "black", 5 , 0.5 );
		netViz.setupVizBooleanAtr(true, netGr, "black", "red" , false , false ) ;
		netViz.setupFixScaleManual( true , netGr, sizeGridX , 0);

		netGr.display(false);	
		// setup RD viz
		Viz viz = new Viz(lRd);

		int t = 0 ; 
		while ( t <= stepMax && ! lSeed.getListSeeds().isEmpty()  ) {	
			System.out.println("---- step " +t +" --------------");
			// compute layers
			lRd.updateLayer(); 
			lMl.updateLayer();
			lNet.updateLayers(typeVectorField.slopeDistanceRadius , 0 , true , 1 );

			// store network
			storeNet.storeDSGStep(t);
			storeSimNet.storeDSGStep(t);
			storeRd.storeStepRd(t);
			
			// simplify network 
			simNet.compute(t);
			
			// analysis network
			analNet.compute(t);
			analSimNet.compute(t);

			// RD viz
			viz.step();

			t++;
		}

		// close files
		storeNet.closeStore();
		storeSimNet.closeStore();
		storeRd.closeFileWriter();
		
		analNet.closeFileWriter();
		analSimNet.closeFileWriter();
		
		simNetGr.display(false);

		
		// only for viz
		for ( seed s : lSeed.getListSeeds()) 	
			s.getNode().setAttribute("seed", 1);		
		
		
	}		
	public static int getGridSize() { return sizeGridX; }

	
}