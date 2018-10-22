package RdmGridToNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.APSP.APSPInfo;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class staticSympleNetwork extends framework{

	private Graph stSimGr = new SingleGraph ( "StSimNet" ),
					netGr = new SingleGraph ( "net" ) ,
					mulGr = new MultiGraph  ( "multiGr") ,
					grToCreate = null ;
	
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	public enum typeGraph { multiGraph, SingleGraph };
	private typeGraph typeGraph ;
	
	public staticSympleNetwork( Graph  netGr ) {
		this.netGr = netGr ;
	}
	
	public void init(typeGraph typeGraph) {
		this.typeGraph = typeGraph ;
	}
	
// COMPUTE METHODS ----------------------------------------------------------------------------------------------------------------------------------	
	public void compute ( ) {
		if ( typeGraph.equals(typeGraph.multiGraph) )
			grToCreate = mulGr ;
		else 
			grToCreate = stSimGr;
		
		ArrayList < Node > listNodeAdded  = computeNodes(true, netGr, grToCreate);	
		computePaths(listNodeAdded, netGr, grToCreate) ;
		grToCreate.display(false);
	}
		
	private ArrayList<Node> computeNodes ( boolean addNode , Graph grOr ,Graph grToCreate ) {
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> () ;
		for ( Node nNet :grOr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d != 2 ) {
				listNodeToAdd.add(nNet);
				if ( addNode ) {
					Node nSim = createNode(grToCreate, nNet);
					nSim.addAttribute("nNet", nNet);
					nNet.addAttribute("nSim", nSim);
				}	
			}
		}
		return listNodeToAdd ;
	}
	
	private void computePaths ( ArrayList < Node > listNodeAdded , Graph grOr ,Graph grToCreate ) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		dijkstra.init(grOr);		
		for ( Node n0Sim : grToCreate.getEachNode() ) {
			Node n0 = n0Sim.getAttribute("nNet"); 
			dijkstra.setSource(n0);
			dijkstra.compute();
			for ( Node n1Sim : grToCreate.getEachNode() ) {
				if ( !n0Sim.equals(n1Sim)) {
					Node n1 = n1Sim.getAttribute("nNet");		
					Iterator< ? extends Path > it = dijkstra.getAllPathsIterator(n1);
					ArrayList<Path> listPath = new ArrayList<Path> () ;
					int numPath = 0 ;
					while ( it.hasNext() && numPath <= n0.getDegree() ) {
						Path next = it.next();
						listPath.add(next);
						numPath++;
					}
					for ( Path p : listPath ) {
						List<Node> nodePath = p.getNodePath();
						nodePath.remove(n0);
						nodePath.remove(n1);
						boolean goodPath = true ;
						int pos = 0 ;
						while ( goodPath == true && pos < nodePath.size() ) {
							Node n = nodePath.get(pos) ;
							if ( listNodeAdded.contains(n) )
								goodPath = false ;
							pos++ ;
						}
						if ( goodPath ) {
							if ( typeGraph.equals(mulGr))
								choiceGoodPathMultiGr(dijkstra, grToCreate, n0Sim, n1Sim, n1, p);
							else
								choiceGoodPathSingleGr(dijkstra, grToCreate, n0Sim, n1Sim, n1, p);
						}
					}
				}		
			}
		}
	}
	
	private void choiceGoodPathMultiGr ( Dijkstra dijkstra , Graph grToCreate , Node n0Sim , Node n1Sim , Node n1, Path p ) {
		Edge e = createEdge(grToCreate, n0Sim, n1Sim);
		if ( e!=null) {
			e.addAttribute("length", dijkstra.getPathLength(n1) );
			e.addAttribute("path", p);
		}
	}
			
	private void choiceGoodPathSingleGr ( Dijkstra dijkstra , Graph grToCreate , Node n0Sim , Node n1Sim , Node n1, Path p ) {
		Edge ed = null ;
		double len = dijkstra.getPathLength(n1) ;
		Map<Path,Double>  mapPathLen = new HashMap<Path,Double>();
		Map<List<Node>,Double>  mapNodePathLen = new HashMap<List<Node> , Double>();
		ed = createEdge(grToCreate, n0Sim, n1Sim);	
		if ( ed == null ) {
			ed = getEdgeBetweenNodes(n0Sim, n1Sim);
			mapPathLen = ed.getAttribute("pathLen");
			mapNodePathLen = ed.getAttribute("nodePathLen");
			if ( mapPathLen == null ) {
				mapPathLen = new HashMap<Path,Double>();
				mapNodePathLen = new HashMap<List<Node> , Double>(); 
			}
		}
		else {
			mapPathLen = ed.getAttribute("pathLen");
			mapNodePathLen = ed.getAttribute("mapNodePathLen") ;
			if ( mapPathLen == null )
				mapPathLen = new HashMap<Path,Double>();
			if ( mapNodePathLen == null )
				mapNodePathLen = new HashMap<List<Node>,Double>();
		}
		if ( ! mapPathLen.containsValue(len)) {
			mapPathLen.put(p, len);
			ed.addAttribute("pathLen", mapPathLen) ;
		}
	}
	
// GET METHODS --------------------------------------------------------------------------------------------------------------------------------------
	private ArrayList<Path> getListPath ( Graph gr ) {
		ArrayList<Path> list = new ArrayList<Path> () ;
		for ( Edge e : gr.getEachEdge()) {
			Path path = e.getAttribute("path") ;
			if ( ! list.contains(path))
				list.add(path);
		}
		return list;
	}
	
	private Map<Path,Edge> getMapEdgePath ( Graph gr ) {	
		Map<Path,Edge> map = new  HashMap<Path,Edge> () ;
		ArrayList<Path> list = new ArrayList<Path> () ;
		for ( Edge e : gr.getEachEdge()) {
			Path path = e.getAttribute("path") ;
			if ( ! list.contains(path)) {
				list.add(path);
				map.put( path , e );
			}
		}
		return map;
	}
	
	private Edge getEdgeBetweenNodes ( Node n0 , Node n1 ) {
		ArrayList<Edge> list = new ArrayList<Edge> ( ) ;
		for (Edge e : n0.getEdgeSet() ) 
			if ( e.getNode0().equals(n1) || e.getNode1().equals(n1) )
				return e ;
		return null ;
	}
	
	private ArrayList<Edge> getAllEdgeBetweenNodes ( Node n0 , Node n1 ) {
		ArrayList<Edge> list = new ArrayList<Edge> ( ) ;
		for (Edge e : n0.getEdgeSet() ) 
			if ( e.getNode0().equals(n1) || e.getNode1().equals(n1) )
				if ( ! list.contains(e) )
					list.add(e);	
		return list ;
	}
	
	private ArrayList<Node> getListNeighbors ( Node node ) { 
		ArrayList<Node> listNeig = new ArrayList<Node>();
		Iterator<Node> iter = node.getNeighborNodeIterator() ;	
		while (iter.hasNext()) {		 
			Node neig = iter.next() ;		//		System.out.println(neig.getId() + neig.getAttributeKeySet());
			if ( !listNeig.contains(neig) )
				listNeig.add(neig);
		} 
		listNeig.remove(node) ; 
		return listNeig ;
	}

	public Node getNearestNodeInPath (Graph gr , Node source ) {
		Iterator<? extends Node> k = source.getDepthFirstIterator();
		while ( k.hasNext() ) {	
			Node n =  k.next();
			int degree = n.getDegree();	
			if ( degree > 2) 
				return n; 		
		}
		return null ;
	}
	
	public ArrayList<Node> getListExt ( Node node ) {
		ArrayList<Node> listExt = new ArrayList<Node> () ;	
		Iterator<? extends Node> k = node.getDepthFirstIterator();
		int num = 0 ;
		while ( k.hasNext() && num < 2 ) {
			Node next = k.next();
			int dNext = next.getDegree();			
			if ( dNext !=2  && ! listExt.contains(next) ) {
				listExt.add(next);	
				num++ ;
			}	
		}
		return listExt ;
	}
	
	// get length edge 
	private double getLength ( Edge e ) {
		return e.getAttribute("length") ;
	}
	
	// get Graph
	public Graph getGraph ( ) {
		return grToCreate;
	}
	
// CREATE METHODS -----------------------------------------------------------------------------------------------------------------------------------
	// create edge
	private Edge createEdge ( Graph gr ,Node n0 , Node n1 ) {		
		try {
			idEdge = Integer.toString(idEdgeInt);
			Edge e = gr.addEdge(idEdge, n0, n1);
			idEdgeInt++;
			return e ;
		} catch (EdgeRejectedException e) {
			return null ;
		}
	}
	
	// create Node
	private Node createNode ( Graph gr , Node n) {		
		double[] coord = GraphPosLengthUtils.nodePosition(n);	
		idNode = Integer.toString(idNodeInt); 
		Node newNode = gr.addNode(idNode);
		newNode.addAttribute("xyz", coord[0],coord[1] ,0);	
		idNodeInt++ ;
		return newNode ;
	}

}
