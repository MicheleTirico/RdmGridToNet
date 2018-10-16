package RdmGridToNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class staticSympleNetwork {

	private Graph stSimGr = new SingleGraph ( "StSimNet" ),
					net = new SingleGraph ( "StSimNet" );
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	

	public staticSympleNetwork( Graph  net ) {
		this.net = net ;
	}
	
	public void init() {
		
	}
	
	public void compute ( ) {
		ArrayList<Node> listNodeVisisted = new ArrayList<Node> () ,
				listNodeAdded= new ArrayList<Node> ();
		
		Map<Node,ArrayList<Node>> map = new HashMap() ;
		
		for ( Node n : net.getEachNode() ) {
			ArrayList<Node>  listNodeToConnect = new ArrayList<Node> () 	;
			if ( n.getDegree() != 2) {		
				Node nSource = createNode(stSimGr, n);
				if ( ! listNodeAdded.contains(nSource) )
					listNodeAdded.add(nSource);
				int d = 0;
				Iterator<? extends Node> k = n.getBreadthFirstIterator();
				while ( d <= n.getDegree() && k.hasNext() ) {
					try {
						Node next = k.next();
						listNodeVisisted.add(next);
						if ( next.getDegree() != 2 ) {
							listNodeToConnect.add(next);
							map.put(n,listNodeToConnect) ;
							d++;
						}
						
						
					} catch (ArrayIndexOutOfBoundsException e) {
						continue ;
					}
				}
				
				for ( Node no : map.keySet()) {
					Node nextNode = createNode(stSimGr, no);
				
				}
				
				
//				for ( Node nNext : listNodeToConnect ) {
//					listNodeToConnect.remove(nSource);
//					
//					if ( !listNodeAdded.contains(nNext)) {
//		 				Node nextNode = createNode(stSimGr, nNext);
//						listNodeAdded.add(nNext);
//						
//						idEdge = Integer.toString(idEdgeInt) ;                   	
////						Edge e = stSimGr.addEdge(idEdge, sourceNode, nextNode);
//						idEdgeInt++;
//						
//					}
//				}
			}
		}
		stSimGr.display(false);
	}
	
	
	private Node createNode ( Graph gr , Node n) {
		
		double[] coord = GraphPosLengthUtils.nodePosition(n);	
		idNode = Integer.toString(idNodeInt); 
		Node newNode = gr.addNode(idNode);
		newNode.addAttribute("xyz", coord[0],coord[1] ,0);	
		idNodeInt++ ;
		return newNode ;
	}
	public void compute3() {
		
		ArrayList<Node> listNodeVisisted = new ArrayList<Node> () ,
				listNodeAdded = new ArrayList<Node> () 	;
		
		for ( Node n : net.getEachNode() ) {
			ArrayList<Node> listNodeExt = new ArrayList<Node> () ;
			
			if ( n.getDegree() == 2 && ! listNodeVisisted.contains(n)) {
			
				Iterator<? extends Node> k = n.getBreadthFirstIterator();
				while ( k.hasNext() && listNodeExt.size() < 2 ) {
					Node next = k.next();
					listNodeVisisted.add(next);
					if ( next.getDegree() != 2 
							//&& !listNodeAdded.contains(next) 
							&& !listNodeExt.contains(next) ) {
						listNodeExt.add(next);
						listNodeAdded.add(next);
					}
				}
			
		
			if ( listNodeExt.size() != 0 ) {
				System.out.println(listNodeExt);
				Node n0 = listNodeExt.get(0), n1 = listNodeExt.get(1);
//				listNodeAdded.remove(n0);
//				listNodeAdded.remove(n1);
				
				double[] coordSource0 = GraphPosLengthUtils.nodePosition(n0);		
				idNode = Integer.toString(idNodeInt++);
	        	Node newNode0 = stSimGr.addNode(idNode);
	        	newNode0.addAttribute("xyz", coordSource0[0],coordSource0[1] ,0);	
	        	idNodeInt++ ;
	        	
	        	double[] coordSource1 = GraphPosLengthUtils.nodePosition(n1);		
				idNode = Integer.toString(idNodeInt++);
				Node newNode1 = stSimGr.addNode(idNode);
	        	newNode1.addAttribute("xyz", coordSource1[0],coordSource1[1] ,0);	
	        	idNodeInt++ ;
	        	
	        	double dist = layerNet.getDistGeom(coordSource0, coordSource1) ;
	        
	        	idEdge = Integer.toString(idEdgeInt++) ;                   	
            	Edge e = stSimGr.addEdge(idEdge, newNode0, newNode1);
			}
		}
		}
//		for (Node nTest : listNodeAdded ) {
//			double[] coordSource0 = GraphPosLengthUtils.nodePosition(nTest);		
//			idNode = Integer.toString(idNodeInt++);
//        	Node newNode = stSimGr.addNode(idNode);
//        	newNode.addAttribute("xyz", coordSource0[0],coordSource0[1] ,0);	
//        	idNodeInt++ ;
//		}
		stSimGr.display(false);
	}


	public void compute2() {
		
		Node source = net.getNode(4577);
		double[] coordSource = GraphPosLengthUtils.nodePosition(source);
//		Node n0 = stSimGr.addNode(idNode);
//		n0.addAttribute("xyz", coordSource[0],coordSource[1] , 0);
//		n0.addAttribute("nNet", source);
		
		ArrayList<Node> listNoeAlreadyVisited = new ArrayList<Node>();
//		source.addAttribute("nSim", n0);

		Iterator<? extends Node> k = source.getEdgeIterator();
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
	
		Node old = source ;
	    while (k.hasNext()) {

	    	Node next = k.next(); 		    	

	    	int degree = next.getDegree(); 
	    	System.out.println(next +" " + degree);

	    	if ( degree != 2 && ! listNoeAlreadyVisited.contains(next) ) {
            	listNoeAlreadyVisited.add(next) ;
            	idNode = Integer.toString(idNodeInt++);
            	Node n = stSimGr.addNode(idNode);
            	double[] coordNext = GraphPosLengthUtils.nodePosition(next);
            	
            	n.addAttribute("xyz", coordNext[0],coordNext[1] ,0);	
            	n.addAttribute("nNet", next);
            	next.addAttribute("nSim", n);
                     	
            	dijkstra.setSource(next);	
            	dijkstra.init(net);
            	dijkstra.compute();
//            	double length = dijkstra.getPathLength(next);
            	
          //  	System.out.println(length);
            	
            	idEdge = Integer.toString(idEdgeInt) ;         
//            	
            	Edge e = stSimGr.addEdge(idEdge, n, old.getAttribute("nSim"));
            	old = next ;
            	idEdgeInt++;
            }
            
        }
	    stSimGr.display(false);
	}

	
	public Graph getGraph ( ) {
		return stSimGr;
	}
	
	
}
