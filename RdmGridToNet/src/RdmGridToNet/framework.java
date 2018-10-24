package RdmGridToNet;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public abstract class framework  {
	
	protected static boolean isFeedBackModel;
	protected enum typeFeedbackModel { booleanSingleImpact , booleanCombinedImpact }
	protected static typeFeedbackModel  typeFeedbackModel  ;
	
	protected static layerSeed lSeed = new layerSeed();
	protected static layerRd lRd = new layerRd();
	protected static layerNet lNet = new layerNet() ;
	protected static bucketSet bks = new bucketSet() ;
	protected static layerMaxLoc lMl = new layerMaxLoc();
	
	protected static String idPattern ;
	
	protected static int idNodeInt , idEdgeInt , idMaxLocInt;
	protected static String idNode, idEdge , idMaxLoc ;
	protected static double  f , k  ;
	
	public enum typeRadius { square , circle}
	protected static typeRadius typeRadius;
	protected static ArrayList<cell> listCell = new ArrayList<cell> ();
	protected static ArrayList<bucket> listBucket = new ArrayList<bucket>();
	
	protected enum morphogen { a , b }		
	protected enum typeVectorField { gravity , slope , slopeDistance , slopeRadius , slopeDistanceRadius } 
	public enum RdmType { holes , solitions , movingSpots , pulsatingSolitions , mazes , U_SkateWorld , f055_k062 , chaos , spotsAndLoops , worms , waves }
	public enum typeNeighbourhood { moore, vonNewmann , m_vn }	

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

	// initialize world with more than 1 circle and corresponding Rdm pulse 
	public static void initMultiCircle ( double valA , double valB , int numNodes , int centreX ,int centreY , int radiusRd , int radiusNet ) {	
		lRd.setValueOfCellAround(valA, valB, centreX, centreY, radiusRd);		
		if ( isFeedBackModel)
			lSeed.initializationSeedCircleFeedBack (numNodes, radiusNet, centreX,centreY );
		else 
			lSeed.initializationSeedCircle(numNodes, radiusNet, centreX,centreY );
	}

// COMMON METHODS -----------------------------------------------------------------------------------------------------------------------------------
	// set RD start values to use in similtion ( gsAlgo )
	protected static void setRdType ( RdmType pattern ) {
		
		switch ( pattern ) {
			case holes: 				{ f = 0.039 ; k = 0.058 ; } 
										break ;
			case solitions :			{ f = 0.030 ; k = 0.062 ; } 
										break ; 
			case mazes : 				{ f = 0.029 ; k = 0.057 ; } 
										break ;
			case movingSpots :			{ f = 0.014 ; k = 0.054 ; } 
										break ;
			case pulsatingSolitions :	{ f = 0.025 ; k = 0.060 ; } 
										break ;
			case U_SkateWorld :			{ f = 0.062 ; k = 0.061 ; } 
										break ;
			case f055_k062 :			{ f = 0.055 ; k = 0.062 ; } 
										break ;
			case chaos :				{ f = 0.026 ; k = 0.051 ; } 
										break ;
			case spotsAndLoops :		{ f = 0.018 ; k = 0.051 ; } 
										break ;
			case worms :				{ f = 0.078 ; k = 0.061 ; } 
										break ;
			case waves :				{ f = 0.014 ; k = 0.045 ; } 
										break ;		
		}	
		idPattern = pattern.toString();	
	}
	
	public static void isFeedBackModel ( boolean isFbM , typeFeedbackModel  type ) {
		isFeedBackModel = isFbM ;
		typeFeedbackModel  = type  ; 
	}	
}
