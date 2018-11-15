package RdmGridToNetMultiLayer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import RdmGridToNetMultiLayer.layerCell.typeDiffusion;
import RdmGridToNetMultiLayer.layerCell_old_01.morphogen;
import RdmGridToNetMultiLayer.layerMaxLoc.typeComp;
import RdmGridToNetMultiLayer.layerMaxLoc.typeInit;
import RdmGridToNetMultiLayer.layerSeed.handleLimitBehaviur;
import RdmGridToNetMultiLayer.vectorField.typeRadius;
import RdmGridToNetMultiLayer.vectorField.typeVectorField;
import dataAnalysis.indicatorSet.indicator;
import dataAnalysis.analyzeNetwork;
import dataAnalysis.storeNetwork;
import dataAnalysis.storeRd;
import dataAnalysis.storeRd.whichMorpToStore;

import layerViz.vizLayerCell;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;
import viz.*;

public class runAndAnalyze extends framework  {
	
//	protected static layerSeed lSeed = new layerSeed();
//	protected static layerRd lRd = new layerRd();
//	protected static layerNet lNet = new layerNet() ;
//	protected static layerMaxLoc lMl = new layerMaxLoc();
	
	// common parameters
	private static int stepToStore = 10 , 
				stepToAnalyze = 10 ,
				stepToPrint = 100 ,
				stepMax = 1000 ;
		
	private static  String  path = "D:\\ownCloud\\RdmGrid_exp\\test" ;
		
	// store and analysis parameters 
	private static boolean  runStoreRd = false ,
			runStoreSimNet = false , 
			runStoreNet = false ,
			runSimNet = false , 
			runAnalysisNet =false,
			runAnalysisSimNet = false;
	
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
//	private static morphogen m = morphogen.b;
	private static double r = 2 ,
			minDistSeed = 1 , 
			alfa = 2 ;
//	private static typeVectorField tvf = typeVectorField.slopeDistanceRadius;
	
	// initialize circle seeds
	private static int numNodes = 50, 
			radiusRd = 2 , 
			radiusNet = 4 ;

	public static void main(String[] args) throws Exception {	
		
		// bucket set
		bks = new bucketSet(1, 1, sizeGridX, sizeGridY );
		bks.initializeBukets();

		// layer Rd
		lRd = new layerCell(1, 1, sizeGridX, sizeGridY ,2,5) ;
		lRd.initializeCostVal(new double[] {1,0});
		lRd.setValueOfCellAround(new double[] {1, 1}, sizeGridX/2,sizeGridY/2 ,3 );
		lRd.setGsParameters(0.030 , 0.062, 0.2, 0.1, typeDiffusion.mooreCost);
		
		for ( cell c : lRd.getListCell() ) {
	//		System.out.println(c + " " + c.getArrayIsTest().length);
		}
		// layer bumps
		lBumps = new layerCell(1, 1, sizeGridX, sizeGridY ,3,3) ;
		lBumps.initCells();		
		lBumps.setGridInCoordsLayer(lBumps.getBumbsFromPosition (1 , 1 , 1));
		lBumps.setGridInValsLayer(lBumps.getBumbsFromPosition (1 , 1 , 1) , 0);		
		
		// layer max local
		lMl = new layerMaxLoc(true,lRd,true, typeInit.test, typeComp.wholeGrid ,1);
		lMl.initializeLayer();
		
		// vector field Rd
		vfRd = new vectorField(lRd, 1, 1 , sizeGridX, sizeGridY, typeVectorField.slopeDistanceRadius) ;
		vfRd.setSlopeParameters( 1 , r, alfa, true, typeRadius.circle);
		
		vfBumps = new vectorField(lBumps,  1, 1 , sizeGridX, sizeGridY, typeVectorField.minVal);
		vfBumps.setMinDirectionParameters(0);
		
		lNet = new layerNet("net") ;
		
		// layer Seed
		lSeed = new layerSeed(handleLimitBehaviur.stopSimWhenReachLimit, new vectorField[] { vfRd
																						// , vfBumps 
				} );
		lSeed.initSeedCircle(20, 3, sizeGridX/2, sizeGridY/2);
			
		initCircle(perturVal0,perturVal1,numNodes , sizeGridX/2 ,sizeGridY/2, 2 , radiusNet );		
		
		lNet.getGraph().display(false) ;
		
		lNet.setLengthEdges("length" , true );
		
		// viz 
		vizLayerCell vLRd = new vizLayerCell(lRd, 1);	
	//	vizLayerCell vLc = new vizLayerCell(lBumps, 0);	
	//	vLc.step();

		int t = 0 ; 
		while ( t <= stepMax // && ! lSeed.getListSeeds().isEmpty() && lNet.seedHasReachLimit == false 
				) {	
			System.out.println( "-------------------" + t +"-------------------");
			
			// update layers
			lRd.updateLayer();
			lMl.updateLayer();
			lNet.updateLayers( 0 ,true,1);
			
			 System.out.println("maxLoc " + lMl.getNumMaxLoc()	) ;
			
			// viz
			vLRd.step();
			
			t++ ;
		}
			

	
		
	}		
	
	
}