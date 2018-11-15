package RdmGridToNetMultiLayer;

import RdmGridToNetMultiLayer.layerCell.typeDiffusion;
import layerViz.vizLayerCell;

public class test {

public static void main(String[] args) {
		
		int size = 100 ;
		layerCell lSc = new layerCell(1, 1, size, size ,3,3) ;
		lSc.initCells();		
		lSc.setGridInCoordsLayer(lSc.getBumbsFromPosition (1 , 1 , 1));
		lSc.setGridInValsLayer(lSc.getBumbsFromPosition (1 , 1 , 1) , 0);		
		vizLayerCell vLc = new vizLayerCell(lSc, 0);	
		vLc.step();
		
		layerCell lRd = new layerCell(1, 1, size, size ,2,3) ;
		lRd.initializeCostVal(new double[] {1,0});
		
		lRd.setValueOfCellAround(new double[] {1, 1}, size/2,size/2 ,3 );
		lRd.setGsParameters(0.030 , 0.062, 0.2, 0.1, typeDiffusion.mooreCost);
		vizLayerCell vLRd = new vizLayerCell(lRd, 1);	
		
		// bucket set
		bucketSet bks = new bucketSet(1, 1, size, size );
		bks.initializeBukets();
		
		for ( cell c : lRd.getListCell() ) {
		//	if ( c.getVals()[0] !=0 )
		//	System.out.println( c.getX() + " "+ c.getY() + " " + c.getVals()[0] + " " + c.getVals()[1]);
		}
		
		int t = 0 ;
		
		while ( t< 1000 ) {
			lRd.updateLayer();
			vLRd.step() ;
			System.out.println(t);
			t++ ;
		}
		
		
		
		
		
	
	
		
		
	}


}
