package RdmGridToNetMultiLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class layerCell_old_01 {

	private double sizeX, sizeY;
	private int numCellX, numCellY , numVals, numIsTest;
	private cell[][] cells ;
	
	/**
	 * parameters for Rd 
	 */
	protected enum morphogen { a , b }
	private static double f ,  k ,  Da,  Db ;
	public enum typeDiffusion { mooreCost, mooreWeigthed , vonNewmannCost }	
	private typeDiffusion typeDiffusion ;
	
	private double[] rdPArameters ;
	public enum typeNeighbourhood { moore, vonNewmann , m_vn }	
	protected  ArrayList<cell> listCell = new ArrayList<cell> ();
	

	public layerCell_old_01 ( ) {
		this(0,0,0,0 ,0 ,0 );	
	}

	public layerCell_old_01(double sizeX, double sizeY , int numCellX, int numCellY ,  int numVals ,int numIsTest ) {
		this.sizeX = sizeX ;
		this.sizeY = sizeY ;
		this.numCellX = numCellX ;
		this.numCellY = numCellY ;
		this.numVals = numVals ;
		this.numIsTest = numIsTest;
		cells = new cell[numCellX][numCellY];
	}

// INITIALIZATION GRID ------------------------------------------------------------------------------------------------------------------------------
	public void initializeCostVal ( double[] vals ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				cell c = new cell(x,y,new double[3],vals,new boolean[numIsTest]);
				System.out.println(c.getX() + " " + c.getY());
				c.initCoords( sizeX, sizeY );
				cells[x][y] = c ;
				putCellInList(c);			
			}
	}
	
	public void initializeRandomVals ( int[] seedRd, double[] minVal,  double[] maxVal ) {	
		int pos = 0 ;
		while ( pos < seedRd.length ) {
			Random rd = new Random( seedRd[pos] );
			for (int x = 0; x<numCellX; x++)
				for (int y = 0; y<numCellY; y++) {
					double[] vals = new double[seedRd.length] ;
					int i = 0 ; 
					while ( i < seedRd.length ) {
						vals[i] =   minVal[i] + (maxVal[i] - minVal[i]) * rd.nextDouble() ;
						i++;
					}
					cell c = new cell(x,y,new double[3],vals,new boolean[numIsTest]);
					c.initCoords( sizeX, sizeY );
					cells[x][y] = c ;
					putCellInList(c);					
				}
			pos++ ;
		}
	}	
	
	public void initCells ( ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				cell c = new cell(x,y, new double[3],new double[numVals],new boolean[numIsTest]);
				c.initCoords( sizeX, sizeY );
				cells[x][y] = c ;
				putCellInList(c);			
			}
	}
	
// SET RD PARAMETERS --------------------------------------------------------------------------------------------------------------------------------
	// set initial parameters of gray scott model
	public void setGsParameters ( double f , double k , double Da, double Db, typeDiffusion typeDiffusion) {
		this.f = f ;
		this.k = k ;
		this.Da = Da ;
		this.Db = Db ;
		this.typeDiffusion = typeDiffusion;
	}
	
// COMPUTE VALS -------------------------------------------------------------------------------------------------------------------------------------	
	public void computeBumbs ( double incremX , double incremY , double incremZ ) {
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			double val = incremZ * Math.sin(coord[0]*incremX) * Math.cos( coord[1]*incremY ) ;
			c.setVal(0, val);
		}
	}
	
	/**
	 * return a grid of double with value z coords 
	 * z is computed from position of cell 
	 * @param incremX
	 * @param incremY
	 * @param incremZ
	 * @return
	 */
	public double[][] getBumbsFromPosition ( double incremX , double incremY , double incremZ  ) {
		double[][] grid = new double [numCellX][numCellY];
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			int[] pos = c.getPos();		
			grid[pos[0]][pos[1]] = incremZ * Math.sin(pos[0]*incremX) * Math.cos( pos[1]*incremY) ;
		}
		return grid ;
	}
	
	/**
	 * return a grid of double with value z coords 
	 * z is computed from coords of cell 
	 * @param incremX
	 * @param incremY
	 * @param incremZ
	 * @return
	 */
	public double[][] getBumbsFromCoords ( double incremX , double incremY , double incremZ  ) {
		double[][] grid = new double [numCellX][numCellY];
		for ( cell c : listCell ) {
			double[] coord = c.getCoords() ;
			int[] pos = c.getPos();		
			grid[pos[0]][pos[1]] = incremZ * Math.sin(coord[0]*incremX) * Math.cos( coord[1]*incremY) ;
		}
		return grid ;
	}
	
	public void setGridInValsLayer ( double[][] grid , int posVal ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				this.getCell(x,y).setVal(posVal, grid[x][y]);
			}
	}
	
	public void setGridInCoordsLayer ( double[][] grid ) {
		for (int x = 0; x<numCellX; x++)
			for (int y = 0; y<numCellY; y++) {
				this.getCell(x,y).setCoord(2 , grid[x][y]);
			}
	}
	
	// set perturbation 
	public void setValueOfCell ( double[] vals , int cellX, int cellY ) {
		cells[cellX][cellY].setVals(vals);		
	}
	
	// set perturbation in radius
	public void setValueOfCellAround  ( double[] vals , int cellX, int cellY, int radius ) {
		for ( int x = (int) Math.floor(cellX - radius) ; x <= (int) Math.ceil(cellX + radius ) ; x++  )
			for ( int y = (int) Math.floor(cellY - radius ) ; y <= (int) Math.ceil(cellY + radius ) ; y++  ) {
				cells[x][y].setVals(vals);							
			}		
	}

	// get value of morphogen 
		protected double getValMorp ( cell c, morphogen m , boolean checkVal ) {		
			double val = 0 ;
			if ( m.equals(morphogen.a))
				val = c.getVals()[0];
			else
				val =  c.getVals()[1];
		
			if ( checkVal )
				if (val>1 )
					return 1;
				else if (val < 0)
					return 0 ;
			return val;
		}
		
	// update cells 
		public void updateLayer (  ) {
			
			for ( cell c : listCell ) {
				double 	valA = getValMorp(c, morphogen.a, false),
						valB = getValMorp(c, morphogen.b, false);
				
				
				morphogen a = morphogen.a ;
				morphogen b = morphogen.b ;
					
				double coefDiffA = Da ,
					coefDiffB = Db;
				
				
				double 	diffA = coefDiffA * getDiffusion(typeDiffusion,c ,a ) ,
						diffB = coefDiffB * getDiffusion(typeDiffusion, c, b) ,
		
						
						react = valA * valB * valB ,
				
						extA = f * ( 1 - valA ) ,
						extB = ( f + k ) * valB ;
		
			//	System.out.println(diffA + " " + diffB);
				double	newValA =  valA + diffA - react + extA,
						newValB =  valB + diffB + react - extB;
				c.setVals(new double[] {newValA, newValB} );
			}
		}
			
		// get Fick's diffusion 
		private double getDiffusion ( typeDiffusion typeDiffusion, cell c , morphogen m ) {
			double 	diff = 0 , 
					val = getValMorp(c, m, false) ,
					valNeig = 0,
					valNeigS = 0 ,	// sum of values of side neighbors 
					valNeigC = 0;	// sum of values of corner neighbors	
			ArrayList<cell> listNeig = new ArrayList<cell>();
			
			switch (typeDiffusion) {
				case mooreCost: {
					listNeig = getListNeighbors(typeNeighbourhood.moore, c.getX(),c.getY()) ;
					
					for ( cell neig : listNeig) 
						 valNeig = valNeig + getValMorp(neig, m, false);

					diff = -  val + valNeig / listNeig.size()  ;
				}	break;
				
				case vonNewmannCost: {
					listNeig = getListNeighbors(typeNeighbourhood.vonNewmann, c.getX(),c.getY()) ;
					
					for ( cell neig : listNeig) 
						 valNeig = valNeig + getValMorp(neig, m, false);

					diff = -  val + valNeig / listNeig.size()  ;
				}	break;
				
				case mooreWeigthed : {
					ArrayList<cell> listNeigS = new ArrayList<cell>(getListNeighbors(typeNeighbourhood.vonNewmann, c.getX(), c.getY()));
					ArrayList<cell> listNeigC = new ArrayList<cell>(getListNeighbors(typeNeighbourhood.m_vn, c.getX(), c.getY()));
					
					for ( cell neigS : listNeigS ) 
						valNeigS = valNeigS + getValMorp(neigS, m, false);

					for ( cell neigC : listNeigC ) 
						 valNeigC = valNeigC + getValMorp(neigC, m, false);
				
					diff = - val + 0.2 * valNeigS + 0.05 * valNeigC ; 				
				}	break ;
			}
			return diff ; 
		}

	// get value of morphogen 
	protected double getValCeck ( cell c, int pos , boolean checkVal ) {		
		double  val =  c.getVals()[pos];
	
		if ( checkVal )
			if (val>1 )
				return 1;
			else if (val < 0)
				return 0 ;
		return val;
	}
		
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	// get center of the world
	protected double[] getCenter () {		
		return new double[] { numCellX * sizeX / 2 , numCellY * sizeY / 2} ;
	}
	
// GET NEIGHBORS ------------------------------------------------------------------------------------------------------------------------------------	 
	protected ArrayList<cell> getListNeighbors ( typeNeighbourhood typeNeighbourhood , int cellX , int cellY ) {
		ArrayList<cell> list = new  ArrayList<cell> ();		
		switch (typeNeighbourhood) {
			case moore: {
				list.addAll(Arrays.asList(
		    			
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)],
				
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)],
				
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)]
								));		
			} break ;
		    	 
			case vonNewmann : {
				list.addAll(Arrays.asList(
						cells[checkCell(cellX,numCellX)][checkCell(cellY+1,numCellY)],
						cells[checkCell(cellX,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY,numCellY)],
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY,numCellY)]
								)); 
			} break ;	
			case m_vn : {
				list.addAll(Arrays.asList(		    			
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY-1,numCellY)],				
						cells[checkCell(cellX+1,numCellX)][checkCell(cellY+1,numCellY)],							
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY-1,numCellY)],
						cells[checkCell(cellX-1,numCellX)][checkCell(cellY+1,numCellY)]
								));		
			} break ;
		}
		return list; 
	 }	
	
	//check boundary condition
	private int checkCell ( int cell , int maxCell) {		
		if ( cell > maxCell - 1 ) 
			return 0  ;
		if ( cell  <= 0 ) 
			return maxCell-1;
		else return cell;
	}
	
// LIST CELL ACTIVE ---------------------------------------------------------------------------------------------------------------------------------	
	private void putCellInList (cell c) {
		if ( !listCell.contains(c))
			listCell.add(c);
	}
 
	public ArrayList<cell> getListCell () {
		return listCell;		
	}
	
	public cell getCell (int X , int Y) {
		try {
			return  cells[X][Y]; 
		} catch (ArrayIndexOutOfBoundsException e) {
			return null ;
		}
	} 
	
	public cell getCell (int[] pos ) {
		try {
			return  cells[pos[0]][pos[1]]; 
		} catch (ArrayIndexOutOfBoundsException e) {
			return null ;
		}
	} 
	
	public cell getCell ( double[] coords) {
		try {
			return cells[(int) Math.floor(coords[0])][(int) Math.floor(coords[1])] ;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public cell getCell ( Node n ) {
		double[] coords = GraphPosLengthUtils.nodePosition(n);	
		return cells[(int) Math.floor(coords[0])][(int) Math.floor(coords[1])] ;
	}

	public int[] getSizeGrid () {
		return new int[] {numCellX, numCellY} ;
	}
	
	public cell[][] getCells () {
		return cells;
	}
}

