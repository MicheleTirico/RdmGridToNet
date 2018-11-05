package RdmGridToNet;

import java.io.IOException;
import java.util.Arrays;

import org.graphstream.graph.Graph;
import RdmGridToNet.layerMaxLoc.typeComp;
import RdmGridToNet.layerMaxLoc.typeInit;
import RdmGridToNet.layerRd.typeDiffusion;
import RdmGridToNet.symplifyNetwork.typeGraph;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.storeRd.whichMorpToStore;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;
import viz.*;

public class runAndAnalyze extends framework {
 
	private static int sizeGrid = 200 , stepToAnalyze = 10 ;
	private static double Da = 0.2 , Db = 0.1 ;	
	private static double g = 1, alfa = 2 , Ds = .1	, r = 2 ;
	private static String  path = "C:\\Users\\frenz\\ownCloud\\RdmGrid_exp\\test" ;
	private static boolean  runStoreRd = true ,
							runStoreSimNet = false, 
							runStoreNet = false ,
							runSimNet = false , 
							runAnalysisNet = false,
							runAnalysisSimNet = false;

	public static int getGridSize() { return sizeGrid; }

	public static void main(String[] args) throws IOException {	
		// bucket set
		bks = new bucketSet(1, 1, sizeGrid, sizeGrid);
		bks.initializeBukets();

		// layer Rd
		lRd = new layerRd(1, 1, sizeGrid, sizeGrid, typeRadius.circle);		
		lRd.initializeCostVal(1,0);	
		
		// set Rd classical pattern
		setRdType ( RdmType.solitions) ;	
		lRd.setGsParameters(f, k, Da, Db, typeDiffusion.mooreCost );
		
		lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.wholeGrid, morphogen.b);
		lMl.initializeLayer();
		
		lNet = new layerNet("net") ;	
		Graph netGr = lNet.getGraph();
		Graph locGr = lMl.getGraph() ;
		
		lSeed = new layerSeed( r , morphogen.b );
		lSeed.setupGravityLayer(g, alfa, Ds);
	
		initMultiCircle(1, 1, 50 , sizeGrid/2 ,sizeGrid/2, 2 , 4 );		
		
		lNet.setLengthEdges("length" , true );
		
		String nameFile = "f-"+f+"_k-"+k+"_";
		
		// initialize rd store csv
		storeRd storeRd = new storeRd(runStoreRd, whichMorpToStore.a, 10 , path, "storeRd", nameFile + "Rd") ;
		storeRd.initStore();
		
		// Initialize simplify network
		symplifyNetwork simNet = new symplifyNetwork(runSimNet, netGr);
		simNet.init(typeGraph.singleGraph, true, true, stepToAnalyze);
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
		analNet.setIndicators(Arrays.asList(
				indicator.seedCount ,
				indicator.degreeDistribution,
				indicator.normalDegreeDistribution 
				));
		analNet.initAnalysis();
		
		// initialize analysis simplify network
		analyzeNetwork analSimNet = new analyzeNetwork(runAnalysisSimNet, true , stepToAnalyze, simNetGr, path, "analysisSimNet", nameFile) ;
		indicator.pathLengthDistribution.setFrequencyParameters(100, 0, 10);
		analSimNet.setIndicators(Arrays.asList(
				indicator.averageDegree , 
				indicator.gammaIndex ,
				indicator.pathLengthDistribution));
		analSimNet.initAnalysis();
		
		// setup viz netGraph
		handleVizStype netViz = new handleVizStype( netGr ,stylesheet.manual , "seed", 1) ;
		netViz.setupIdViz(false , netGr, 20 , "black");
		netViz.setupDefaultParam (netGr, "black", "black", 5 , 0.5 );
		netViz.setupVizBooleanAtr(true, netGr, "black", "red" , false , false ) ;
		netViz.setupFixScaleManual( false , netGr, sizeGrid , 0);
	
		netGr.display(false);	
		
		// setup RD viz
		Viz viz = new Viz(lRd);

		int t = 0 ; 
		while ( t <= 1000 && ! lSeed.getListSeeds().isEmpty()  ) {	
			System.out.println("---- step " +t +" --------------");
			try { 
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
			catch (NullPointerException e) {
				break ;
			}
		}

		// close files
		storeNet.closeStore();
		storeSimNet.closeStore();
		storeRd.closeFileWriter();
		
		analNet.closeFileWriter();
		analSimNet.closeFileWriter();
		
		// only for viz
		for ( seed s : lSeed.getListSeeds()) 	
			s.getNode().setAttribute("seed", 1);		
		
		
	}		
}