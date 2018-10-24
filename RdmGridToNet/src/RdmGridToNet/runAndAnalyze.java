package RdmGridToNet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import org.graphstream.ui.view.Viewer;

import RdmGridToNet.framework.RdmType;
import RdmGridToNet.framework.morphogen;
import RdmGridToNet.framework.typeVectorField;
import RdmGridToNet.layerMaxLoc.typeComp;
import RdmGridToNet.layerMaxLoc.typeInit;
import RdmGridToNet.layerNet.typeSetupLayer;
import RdmGridToNet.layerRd.typeComputeMaxLocal;
import RdmGridToNet.layerRd.typeDiffusion;
import RdmGridToNet.layerRd.typeInitializationMaxLocal;
import RdmGridToNet.symplifyNetwork.typeGraph;
import dataAnalysis.analyzeNetwork;
import netViz.handleVizStype;
import netViz.handleVizStype.stylesheet;

public class runAndAnalyze extends framework {
 
	private static int sizeGrid = 200 ;
	private static double Da = 0.2 , Db = 0.1 ;	
	private static double g = 1, alfa = 2 , Ds = .1	, r = 2 ;
	private static String  path = "C:\\Users\\frenz\\ownCloud\\RdmGrid_exp\\test" ;

	public static void main(String[] args) throws IOException {	
		
		bks = new bucketSet(1, 1, sizeGrid, sizeGrid);
		bks.initializeBukets();

		lRd = new layerRd(1, 1, sizeGrid, sizeGrid, typeRadius.circle);		
		lRd.initializeCostVal(1,0);	
			
		setRdType ( RdmType.solitions) ;	
		lRd.setGsParameters(f, k, Da, Db, typeDiffusion.mooreCost );
		
		lMl = new layerMaxLoc(true,true, typeInit.test, typeComp.wholeGrid, morphogen.b);
		lMl.initializeLayer();
		
		lNet = new layerNet("net") ;	
		Graph netGr = lNet.getGraph();
		Graph locGr = lMl.getGraph() ;
		
		symplifyNetwork simSingN = new symplifyNetwork(true , netGr);
	
		analyzeNetwork aNent = new analyzeNetwork(true , 10 , netGr, path, "analyzeNet", idPattern) ;		
		aNent.setIndicators(new ArrayList<indicator> ( Arrays.asList(	
				indicator.averageDegree,
				indicator.gammaIndex, 
				indicator.seedCount 
//				indicator.normalDegreeDistribution 
				)));
		aNent.initAnalysis();
		
		storeNetwork sN = new storeNetwork(true,  10 , netGr, path, "dsg", idPattern +"_net_") ;
		sN.initStore();
		
		lSeed = new layerSeed(g, alfa, Ds, r , morphogen.b );

		initMultiCircle(1, 1, 50 , sizeGrid/2 ,sizeGrid/2, 2 , 4 );		
		
		lNet.setLengthEdges("length" , true );
	
		// setup viz netGraph
		handleVizStype netViz = new handleVizStype( netGr ,stylesheet.manual , "seed", 1) ;
		netViz.setupIdViz(false , netGr, 20 , "black");
		netViz.setupDefaultParam (netGr, "black", "black", 5 , 0.5 );
		netViz.setupVizBooleanAtr(true, netGr, "black", "red" , false , false ) ;
		netViz.setupFixScaleManual( false , netGr, sizeGrid , 0);
		netGr.display(false);	
				
		simSingN.init(typeGraph.singleGraph, true, true , 10 );
		Graph simSingGr = simSingN.getGraph();

		// analyze simplify network
		String nameFileSim = "f_" + f + "_k_" + k ;
		analyzeNetwork aNsimSingNet = new analyzeNetwork(true, 10 , simSingGr, path, "analyzeSimSingNet", nameFileSim );
		aNsimSingNet.setIndicators(new ArrayList<indicator> ( Arrays.asList(	
				indicator.averageDegree,
				indicator.gammaIndex 
//				indicator.normalDegreeDistribution 
				)));
		aNsimSingNet.initAnalysis();
		
		int t = 0 ; 
		while ( t <= 30 && ! lSeed.getListSeeds().isEmpty()  ) {	
			System.out.println("---- step " +t +" --------------");
			try { 
				lRd.updateLayer();
				lMl.updateLayer();
				lNet.updateLayers(typeVectorField.slopeDistanceRadius , 0 , true , 1 );

				aNent.computeIndicators(t);
				sN.storeDSGStep(t);
				
				simSingN.compute(t);
				aNsimSingNet.computeIndicators(t);
				t++;
			}
			catch (NullPointerException e) {
				break ;
			}
		}
		
		aNent.closeFileWriter();
		aNsimSingNet.closeFileWriter();
		sN.closeStore();
		
	
		
//		for ( Node n : sSn.getGraph().getEachNode() ) {
//			ArrayList<Node> list = n.getAttribute("listNeig" ) ;
//			ArrayList<Path> listPath = n.getAttribute("listPath" ) ;
//			System.out.println(n + " " + n.getAttribute("dNet") + " " + list + " " + listPath.size());	
//			System.out.println(n + " " + n.getAttribute("mapNeigLen"));
//		}
		
		// only for viz
		for ( seed s : lSeed.getListSeeds()) 	
			s.getNode().setAttribute("seed", 1);		
	}

	
		
		
}