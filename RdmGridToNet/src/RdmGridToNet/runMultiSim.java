package RdmGridToNet;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Graph;

import RdmGridToNet.framework.morphogen;
import RdmGridToNet.framework.typeVectorField;
import RdmGridToNet.layerMaxLoc.typeComp;
import RdmGridToNet.layerMaxLoc.typeInit;
import RdmGridToNet.layerRd.typeDiffusion;
import RdmGridToNet.symplifyNetwork.typeGraph;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.storeRd.whichMorpToStore;
import viz.handleVizStype;
import viz.handleVizStype.stylesheet;

public class runMultiSim extends framework {
	
// SETUP PARAMETERS ---------------------------------------------------------------------------------------------------------------------------------
	// common parameters
	private static int stepToStore = 10 , 
			stepToAnalyze = 10 ,
			stepToPrint = 100 ,
			stepMax = 5000 ;
	 
	// parameters multi sim 
	private static  double incremKill = 0.005 , 
			incremFeed = 0.005 ,
			minFeed = 0.005 ,
			maxFeed = 0.005 ,
			minKill = 0.01  ,
			maxKill = 0.075 ;
	
	private static  String  path = "D:\\ownCloud\\RdmGrid_exp\\multiSim\\analysisNet\\increm_005" ;
	
	// store and analysis parameters 
	private static boolean  runStoreRd = false ,
			runStoreSimNet = false, 
			runStoreNet = true ,
			runSimNet = false , 
			runAnalysisNet = true ,
			runAnalysisSimNet = false ;
	
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
			minDistSeed = 1 ;
	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;
	
	public static void main(String[] args) throws Exception {	
		
		for ( double f = minFeed ; f <= maxFeed ; f = f + incremFeed ) 
			for ( double k = minKill ; k <= maxKill ; k = k + incremKill ) {
				
	//			System.out.println(f+ " " + k );
				// bucket set
				bks = new bucketSet(1, 1, sizeGridX, sizeGridY );
				bks.initializeBukets();
				
				// layer Rd
				lRd = new layerRd(1, 1, sizeGridX, sizeGridY, typeRadius.circle);		
				lRd.initializeCostVal(initVal0 , initVal1 );	
				lRd.setGsParameters(f, k, Da, Db, tyDif );
				
				// layer max local
				lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.aroundNetGraph, m );
				lMl.initializeLayer();
				
				// layer net
				lNet = new layerNet("net") ;	
				lNet.setLengthEdges("length" , true );
				Graph netGr = lNet.getGraph();
				
				// layer seed
				lSeed = new layerSeed( r , morphogen.b );
		
				// initialize network and seed
				initMultiCircle(perturVal0 , perturVal1 , numNodes , sizeGridX/2 ,sizeGridY/2, radiusRd, radiusNet );		
				
				NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(3);
				
				String nameFile = "f-"+ nf.format(f)+"_k-"+ nf.format(k)+"_";
				System.out.println(nameFile);
				
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
				
				Map map = new TreeMap<>();
				
				map.put("sizeGrid",  sizeGridX);
				map.put("Da", Da);
				map.put("Db", Db);				
				map.put("f", f);
				map.put("k", k);
				map.put("numStartSeed",  numNodes);
				map.put("stepStore" , stepToStore) ;
				analNet.setupHeader(false, map);
				
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
				netViz.setupFixScaleManual( true , netGr, sizeGridX , 0);
		//		netGr.display(false);	
				
				int t = 0 ; 
				while ( t <= stepMax && ! lSeed.getListSeeds().isEmpty()  ) {	
					if ( t / (double) stepToPrint - (int)(t / (double) stepToPrint ) < 0.01) {
//						System.out.println("---- step " +t +" --------------");
//						System.out.println("numberMaxLo " + lMl.getNumMaxLoc());
//						System.out.println("numberNodes "+ netGr.getNodeCount() +"\n"+"numberSeeds "+ lSeed.getListSeeds().size());	
					}
		//			try { 
						// compute layers
						lRd.updateLayer(); 
						lMl.updateLayer();
						lNet.updateLayers(tvf , 0 , true , minDistSeed );

						// store network
						storeNet.storeDSGStep(t);
						storeSimNet.storeDSGStep(t);
						storeRd.storeStepRd(t);
						
						// simplify network 
						simNet.compute(t);

						// analysis network
						analNet.compute(t);
						analSimNet.compute(t);

						t++;
//					}
//					catch (NullPointerException e) {
//						e.printStackTrace();
//						break ;
//					}
				}

				System.out.println("step " + t + " seed " + lSeed.getListSeeds().size() + " node " + netGr.getNodeCount()+ "\n");
				// close files
				storeNet.closeStore();
				storeSimNet.closeStore();
				storeRd.closeFileWriter();
				
				analNet.closeFileWriter();
				analSimNet.closeFileWriter();
				
				
				
			}
	


}
	

}
