package dataAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import RdmGridToNet.framework;
import RdmGsaNet_gsAlgo.gsAlgo.reactionType;

public class analyze extends framework {

	private static FileWriter fw1, fw2 ,fw3 ;
	
	public enum indicator {
		seedCount("sC", false,false ),
		gammaIndex("gI", false,false) , 
		averageDegree("aD", false,false) ,
		normalDegreeDistribution ("nDd", true,false),
		gammaIndexWoD2("gI", false,true) , 
		averageDegreeWoD2("aD", false,true) ,
		normalDegreeDistributionWoD2 ("nDd", true,true);
		
		private String id;
		private String path ;
		private FileWriter fw ;
		private boolean isList  , woD2 ;
		
		private indicator ( String id , boolean  isList , boolean woD2 ) {
			this.id = id ;
			this.isList = isList ;
			this.woD2 = woD2 ;
		}
		public FileWriter getFw ( ) {
			return fw ;
		}
		public String getId ( ) {
			return id ;
		}
		public void setId ( String id ) {
			this.id = id ;
		}
		public void setFw ( String path )throws IOException {
			fw = new FileWriter(path,  true) ;
			this.path = path ;
		}
		
		public void setIswoD2 ( boolean woD2) {
			this.woD2 = woD2 ;
		}
		public boolean getwoD2() {
			return woD2 ;
		}
		
		public void setIsList ( boolean isList) {
			this.isList = isList ;
		}
		public boolean getIsList() {
			return isList ;
		}
	}
		
	protected ArrayList<indicator> listIndicators = new ArrayList<indicator>();
	private indicator indicator ;

}
