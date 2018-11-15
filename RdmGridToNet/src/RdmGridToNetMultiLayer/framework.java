package RdmGridToNetMultiLayer;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;


public abstract class framework {
	
	protected static bucketSet bks = new bucketSet() ;
	protected static layerCell lRd = new layerCell() ,
			lBumps = new layerCell();
	protected static layerMaxLoc lMl = new layerMaxLoc();
	protected static layerSeed lSeed = new layerSeed() ;
	protected static vectorField vfRd = new vectorField() ,
			vfBumps = new vectorField( ) ;
	protected static layerNet lNet = new layerNet() ;
	
	protected static int idNodeInt , idEdgeInt ; 
	protected static String idNode, idEdge  ;
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------	
	// get spatial distance from 2 nodes 
	public static double getDistGeom ( Node n1 , Node n2 ) {	
		
		double [] 	coordN1 = GraphPosLengthUtils.nodePosition(n1) , 
					coordN2 = GraphPosLengthUtils.nodePosition(n2); 
		
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}
	
	public static double getDistGeom ( double [] coordN1 , double [] coordN2 ) {			
		return  Math.pow(Math.pow( coordN1[0] - coordN2[0] , 2 ) + Math.pow( coordN1[1] - coordN2[1] , 2 ), 0.5 )  ;
	}
	
	public bucketSet getBks () {
		return bks;
	}
	public layerCell getLRd ( ) {
		return lRd ;
	}
	public layerCell getLBumbs ( ) {
		return lBumps ;
	}

	
	// initialize world with more than 1 circle and corresponding Rdm pulse 
	public static void initCircle ( double valA , double valB , int numNodes , int centreX ,int centreY , int radiusRd , int radiusNet ) {	
	//	lRd.setValueOfCellAround(valA, valB, centreX, centreY, radiusRd);		
	
	//	lSeed.initSeedCircle(numNodes, radiusNet, centreX,centreY );
	}
}
