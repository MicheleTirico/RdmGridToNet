package RdmGridToNetMultiLayer;

import java.util.ArrayList;

import RdmGridToNetMultiLayer.layerCell.typeNeighbourhood;

public class vectorField extends framework {
	
	private double sizeX, sizeY;
	private int numVectorX, numVectorY ;
	private vector[][] vectors ;
	private layerCell lCell ;
	private typeVectorField typeVectorField ;
	private typeRadius typeRadius ;
	private int posVal ;
	
	/**
	 * parameters for vectorField
	 */
	private double g , r , alfa ;
	private boolean ceckIntenVector ;
	
	protected enum typeVectorField { slopeDistanceRadius , gravitiy , minVal , maxVal , test  } 
	public enum typeRadius { square , circle}

	
	protected  ArrayList<vector> listVectors = new ArrayList<vector> ();
	
	public vectorField ( ) {
		this(null, 0,0,0 ,0 , null );	
	}

	public vectorField(layerCell lCell , double sizeX, double sizeY , int numVectorX, int numVectorY , typeVectorField typeVectorField) {
		this.lCell = lCell ;
		this.sizeX = sizeX ;
		this.sizeY = sizeY ;
		this.numVectorX = numVectorX ;
		this.numVectorY = numVectorY ;
		this.typeVectorField = typeVectorField ;
		vectors = new vector[numVectorX][numVectorY];
	}
	
	public void setParametersVectorField ( typeVectorField typeVectorField  , typeRadius typeRadius ) {
		this.typeVectorField = typeVectorField ;
		this.typeRadius = typeRadius ;
	}
	
	public void setSlopeParameters (int posVal , double r , double alfa , boolean ceckIntenVector , typeRadius typeRadius ) {
		this.posVal = posVal ; 
		this.r= r ;
		 this.alfa= alfa ;
		 this.ceckIntenVector = ceckIntenVector; 
		 this.typeRadius = typeRadius ;
	}
	
	public void setGravityParameters ( typeRadius typeRadius ) {
		this.typeRadius = typeRadius ;
	}
	public void setMinDirectionParameters (int posVal) {
		this.posVal = posVal ;
	}
	
	protected vector getVector ( seed s ) {
		vector v ; 
		switch (typeVectorField) {
		case slopeDistanceRadius:{
			double [] inten = getVectorSlopeDistanceRadius(s);
			v = new vector(s.getCoords(), inten, null) ;
			}break;

		case minVal :
			v = getVectorMinDelta (s ,true ) ;
			break ;
		default:
			v = null ;
			break;
		}
		return v ;
	}
	
// Summarize vectors --------------------------------------------------------------------------------------------------------------------------------
	public static vector getvectorSum ( vector[] vectors ) {
		double[] intenSum = new double[3] ; 
		for ( vector v : vectors ) {
			double[] inten =v.getInten();
			intenSum[0] = intenSum[0] + inten[0] ;
			intenSum[1] = intenSum[1] + inten[1] ;
		}
 		return new vector( null , intenSum , null ) ;
	}
	
// GET VECTOR ---------------------------------------------------------------------------------------------------------------------------------------
	private vector getVectorMinDelta (seed s, boolean IsDirectionMin ) {
		cell c = lCell.getCell(s);
		
		double max = lCell.getListValNeighbors(typeNeighbourhood.moore, c, posVal).stream().mapToDouble(v -> v).max().getAsDouble();
	
		return new vector( null , new double[] {1,1} , null ) ;

	}
	
	// get vector slope distance radius
	public double[] getVectorSlopeDistanceRadius ( seed s ) {
		double sX = s.getX() , sY = s.getY() , vecX = 0 , vecY = 0 ;
		for ( int x = (int) Math.floor(s.getX() - r ) ; x <= (int) Math.ceil(s.getX() + r ); x++ )
			for ( int y = (int) Math.floor(s.getY() - r ) ; y <= (int) Math.ceil(s.getY() + r ); y++ ) {	
				try {
					cell c = lCell.getCell(x,y);
					if ( typeRadius.equals(typeRadius.circle)) 
						if ( Math.pow(Math.pow(c.getX() - s.getX(), 2) + Math.pow(c.getY() - s.getY(), 2),0.5) > r ) 
							continue ;
				
					double 	distX = Math.pow(1+Math.abs(sY - y), alfa) ,
							distY = Math.pow(1+Math.abs(sX - x), alfa); 
			
					double 	addVecX = ( lCell.getCellVals(x+1, y)[posVal] - lCell.getCellVals(x-1, y)[posVal] ) / distY , 
							addVecY = ( lCell.getCellVals(x, y+1)[posVal] - lCell.getCellVals(x, y-1)[posVal] ) / distX ;
										
					vecX = vecX + addVecX ;
					vecY = vecY + addVecY ;		
					
					if ( Double.isNaN(vecX))			vecX = 0 ;
					if ( Double.isNaN(vecY))			vecY = 0 ;
				} catch (NullPointerException e) {
				}	
			}
		if ( ceckIntenVector ) {	
			vecX = checkValueVector(vecX, .1) ;
			vecY = checkValueVector(vecY, .1) ;
		}
		s.setVec( -vecX, -vecY);
		return new double[] {-vecX ,-vecY} ;
	}
	
	// check max value of vector 
		private double checkValueVector (double vec , double valMax) { 
			double 	vecAbs = Math.abs(vec) , 
					valMaxAbs = Math.abs(valMax);
			
			if ( vecAbs > valMaxAbs )
				if ( vec > 0.0 )
					return valMax;
				else 
					return -valMax ;
			else 
				return vec;
		}
	
}
