package dataAnalysis_old_01;

import java.io.FileWriter;
import java.io.IOException;

public class indicator {

	public enum ind {
		seedCount("sC" , false ),
		gammaIndex("gI", false ) , 
		averageDegree("aD", false ) ,
		normalDegreeDistribution ("nDd", false );
		
		private String id ;
		private boolean isList ;
		
		private ind ( String id , boolean  isList  ) {
			this.id = id ;
			this.isList = isList ; 
		}
	
		public String getId ( ) {
			return id ;
		}
		public void setId ( String id ) {
			this.id = id ;
		}	
		public void setIsList ( boolean isList) {
			this.isList = isList ;
		}
		public boolean getIsList() {
			return isList ;
		}	
	}
	
	public indicator ( ) {
		this(null,null) ;
	}
	
	public indicator(String id, Object object2) {
		// TODO Auto-generated constructor stub
	}
	
	private String path ;
	private FileWriter fw ;

	public FileWriter getFw ( ) {
		return fw ;
	}

	public void setFw ( String path )throws IOException {
		fw = new FileWriter(path,  true) ;
		this.path = path ;
	}
		
	
}
