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

public class staticSympleNetwork_old_02 extends framework{

	private Graph stSimGr = new SingleGraph ( "StSimNet" ),
					netGr = new SingleGraph ( "net" ) ,
					mulGr = new MultiGraph  ( "multiGr");
	
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	
	public staticSympleNetwork_old_02( Graph  netGr ) {
		this.netGr = netGr ;
	}
	
	public void init(typeGraph typeGraph) {
		this.typeGraph = typeGraph ;
	}
	
	public enum typeGraph { multiGraph, SingleGraph };
	private typeGraph typeGraph ;
	
	public void compute ( ) {
		Graph grToCreate = null ;
		System.out.println(typeGraph);
		if ( typeGraph.equals(typeGraph.multiGraph) )
			grToCreate = mulGr ;
		else 
			grToCreate = stSimGr;
		
		ArrayList < Node > listNodeAdded  = computeNodes(true, netGr, grToCreate);
		
		computePaths(listNodeAdded, netGr, grToCreate) ;
		grToCreate.display(false);
	}
		
	private Graph createSingelGr ( Graph grMul ) {	
		Graph grSin = new SingleGraph ( "test" );
		for ( Node nMul : grMul.getEachNode()) {
			Node nSin = createNode(grSin, nMul);
			nSin.addAttribute("nMul", nMul);
			nMul.addAttribute("nSin", nSin);
		}	
		for ( Edge e : grMul.getEachEdge() ) 
			try {
				createEdge(grSin, e.getNode0().getAttribute("nSin" ), e.getNode1().getAttribute("nSin" )) ;
			} catch (EdgeRejectedException ex) {
				// TODO: handle exception
			}
		return grSin ;
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
	private void computePaths_02 ( ArrayList < Node > listNodeAdded , Graph grOr ,Graph grToCreate ) {
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
							Edge e = createEdge(grToCreate, n0Sim, n1Sim);
							if ( e!=null) {
								e.addAttribute("length", dijkstra.getPathLength(n1) );
								e.addAttribute("path", p);
							}
						}
					}
				}		
			}
		}
	}
	

	private  void removeDoubleEdge (Graph gr) {

		ArrayList<Edge> listEdToRemove = new ArrayList<Edge> ();		
		for ( Edge e : gr.getEachEdge() ) {
			ArrayList<Edge> allEdges = getAllEdgeBetweenNodes(e.getNode0(), e.getNode1());
			ArrayList<List<Node>> listNodePath = new ArrayList<List<Node>>() ,
					listNOdePathVisited =  new ArrayList<List<Node>>();
			for ( Edge ed : allEdges) {
				Path p = ed.getAttribute("path");
				List<Node> nodePath = p.getNodePath() ,
						reverseNodePath = nodePath;
				Collections.reverse(reverseNodePath);
				if ( listNodePath.contains(reverseNodePath) && listNOdePathVisited.contains(reverseNodePath) ) {
					listEdToRemove.add(ed);
					listNOdePathVisited.add(nodePath);
				}
			}
		}
		listEdToRemove.stream().forEach(e-> gr.removeEdge(e));
	}
	
	private  void removeDoubleEdge_08 (Graph gr) {
		Map<Edge, List<Node>> mapEdgePath = new HashMap<Edge, List<Node>>();
		Map< List<Node> , Edge> mapPathEdge = new HashMap< List<Node> , Edge >();
		ArrayList<Edge> listEdToRemove = new ArrayList<Edge> ();
		for ( Edge e : gr.getEachEdge()) {
			Path p =  e.getAttribute("path") ;
			mapEdgePath.put(e, p.getNodePath());
			mapPathEdge.put( p.getNodePath(), e);
		}
		ArrayList<List<Node>> list = new ArrayList<List<Node>>() ;
		for ( List<Node> l : mapEdgePath.values() ) {
			list.add(l);
		}
		ArrayList<List<Node>> listVisited =  new ArrayList<List<Node>>() ;
		for ( List nodePath : list ) {
			List<Node> reverse  = nodePath ;
			Collections.reverse(nodePath);
			if( list.contains(reverse)  ) {
				listVisited.add(nodePath);
				Edge e = mapPathEdge.get(nodePath) ;
				if (! listEdToRemove.contains(e))
					listEdToRemove.add(e);
			}
		}	
		listEdToRemove.stream().forEach(e-> gr.removeEdge(e));
	}
	private  void removeDoubleEdge_07 (Graph gr) {
		ArrayList<Edge> listEdToRemove = new ArrayList<Edge> ();
		ArrayList<Path> listPath =  getListPath(gr);
		ArrayList<Path> listPathCecked = new ArrayList<Path> () ;
		Map<Path,Edge> mapEdgePath = getMapEdgePath(gr);
		
		for ( Path p0 : listPath ) {
			List<Node> nodePath = p0.getNodePath() ;
			List<Node> InverseNodePath =  nodePath; 
			Collections.reverse(InverseNodePath);
			for ( Path p1 : listPath ) {
				List<Node> nodePath1 = p1.getNodePath() ;
				if ( nodePath1.equals(InverseNodePath) && listPathCecked.contains(p0) ) {
					Edge e = mapEdgePath.get(p1);
					if ( !listEdToRemove.contains(e)) {
						listEdToRemove.add(e);
						listPathCecked.add(p0);
					}
				}		
			}
		}
		listEdToRemove.stream().forEach(e-> gr.removeEdge(e));
	}
	
	
	private void removeDoubleEdge_06 (Graph gr) {
		ArrayList<Edge> listEdToRemove = new ArrayList<Edge> ();
		for ( Edge e : gr.getEachEdge() ) {
			Node n0 = e.getNode0() , 
					n1 = e.getNode1() ;
			
			ArrayList<Edge> listEdge = getAllEdgeBetweenNodes(n0, n1);
			Map<Edge, Double> mapEdgeLen = new HashMap<Edge, Double> () ;
			for ( Edge ed : listEdge) 
				mapEdgeLen.put(ed, ed.getAttribute("length") ) ;
			
			ArrayList<Edge> list = new ArrayList<Edge> ();
			for ( Edge ed : mapEdgeLen.keySet()) {
				double len = mapEdgeLen.get(ed);
				for ( Edge edd : list) {
					double test = edd.getAttribute("length") ; 
					if ( test - len < 0.01 ) 
						listEdToRemove.add(edd);
					else
						list.add(edd);	
				}	
			}	
		}
		listEdToRemove.stream().forEach(e-> gr.removeEdge(e));
	}
	
	private void removeDoubleEdge_05 (Graph gr) {
		for ( Node n : gr.getEachNode() ) {
			
			List<Edge> listEdge = (List<Edge> ) n.getEdgeSet();
			ArrayList<Edge>	list = new ArrayList<Edge> () ;
			Edge e0 = listEdge.get(0);
			listEdge.remove(0);
			double lenTest = e0.getAttribute("length") ;
			for ( Edge e : listEdge) {
				double len = e.getAttribute("length");
				if ( lenTest - len < 0.001 ) {
					list.add(e);
					
				}
			}
		}
	}
	
	private void removeDoubleEdge_04 (Graph gr) {
		ArrayList<Edge> edgeToRemove = new ArrayList<Edge>();
		ArrayList<Node> NodeCecked = new ArrayList<Node>();
		for ( Edge e  : gr.getEachEdge()) {
			Node n0 = e.getNode0() , 
					n1 = e.getNode1() ;
			if ( NodeCecked.contains(n0))
				continue;
			if ( NodeCecked.contains(n1))
				continue;
			
			NodeCecked.add(n0);
			NodeCecked.add(n1);
			
			ArrayList<Edge> listEdge = getAllEdgeBetweenNodes(n0, n1);
			double lenTest = e.getAttribute("length") ;
			for ( Edge ed : listEdge )  {
				double len = ed.getAttribute("length") ;//				System.out.println(len);
				if ( Math.abs(len - lenTest ) < 0.0001 && ! edgeToRemove.contains(ed) ) 
					edgeToRemove.add(ed) ;
			}
		}
		edgeToRemove.stream().forEach(e-> gr.removeEdge(e));
	}
	
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
	
	private ArrayList<Path> getListceckedPath ( Graph gr) {
		ArrayList<Path> list = new ArrayList<Path>  (getListPath(gr)) ;
		ArrayList<Path> listToReturn = new ArrayList<Path>  ( ) ;
		Map<Path, List<Node>> mapPathNodePAth = new HashMap<Path, List<Node>> () ;
		
		for ( Path p : list ) {
			List<Node> nodePath = p.getNodePath();
			mapPathNodePAth.put(p, nodePath);
		}
		
		
		return listToReturn; 
	}
	private void removeDoubleEdge_03 (Graph gr) {
		for ( Node n : gr.getEachNode()) {
			ArrayList<Node> neigs = getListNeighbors(n);
			for ( Edge e : n.getEdgeSet() ) {
				Node opp = e.getOpposite(n);
				 
				Path path = e.getAttribute("path");
				List<Node> nodePathOpp  = path.getNodePath() ;
				
			}
		}
	}
	
	
	private void removeDoubleEdge_02 (Graph gr) {
		ArrayList<Edge> listEdgeToRemove = new ArrayList<>() ;
		for (Node n : gr ) {
			ArrayList<Node> listNeig = getListNeighbors(n) ; 
			for ( Node neig : listNeig )  
				for ( Edge e : getAllEdgeBetweenNodes(n, neig)) {
					
					Path p = e.getAttribute("path") ;
					List<Node> nodePath = p.getNodePath();
					if ( ! nodePath.isEmpty()) {
						Node first = nodePath.get(0) ;
						
						Node last = nodePath.get(nodePath.size() - 1 );
						
						if ( listNeig.contains(last) || listNeig.contains(first))
							listEdgeToRemove.add(e);
					}
				}	
			}
		listEdgeToRemove.stream().forEach(e-> gr.removeEdge(e));
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
	
	public void compute_good() {
		mulGr = stSimGr ;
		Iterator<? extends Edge> itEdge = netGr.getEdgeIterator();
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> () ;
		while (itEdge.hasNext()) {
			Edge e = itEdge.next();
			Node n0 = e.getNode0() , n1 = e.getNode1();
			Node n = n0 ;
			if ( n.getDegree() != 2 && ! listNodeToAdd.contains(n) ) {
				Node nSim = createNode(mulGr, n);
				nSim.addAttribute("nNet", n);
				n.addAttribute("nSim", nSim);
				listNodeToAdd.add(n);
			}
			n = n1 ;
			if ( n.getDegree() != 2 && ! listNodeToAdd.contains(n) ) {
				Node nSim = createNode(mulGr, n);
				nSim.addAttribute("nNet", n);
				n.addAttribute("nSim", nSim);
				listNodeToAdd.add(n);
			}
		}
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		dijkstra.init(netGr);
		ArrayList<Path> paths = new ArrayList<Path> ();
		for ( Node n0Sim : mulGr.getEachNode() ) {
			Node n0 = n0Sim.getAttribute("nNet"); 
			dijkstra.setSource(n0);
			dijkstra.compute();
			for ( Node n1Sim : mulGr.getEachNode() ) {
				if ( !n0Sim.equals(n1Sim)) {
					Node n1 = n1Sim.getAttribute("nNet");
					Path p = dijkstra.getPath(n1);					
					double length = dijkstra.getPathLength(n1);
					List<Node> nodePath = p.getNodePath();
					nodePath.remove(n0);
					nodePath.remove(n1);	
					boolean goodPath = true ;
					int pos = 0 ;
					while ( goodPath == true && pos < nodePath.size() ) {
						Node n = nodePath.get(pos) ;
						if ( listNodeToAdd.contains(n) )
							goodPath = false ;
						pos++ ;
					}
					if ( goodPath ) {
						Edge e = createEdge(mulGr, n0Sim, n1Sim);
						if ( e!=null)
							e.addAttribute("length", length);
					}	
				}	
			}
		}
		mulGr.display(false) ;
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
	
	// get length edge 
	private double getLength ( Edge e ) {
		return e.getAttribute("length") ;
	}
	
	// get Graph
	public Graph getGraph ( ) {
		return stSimGr;
	}
	
// OLD COMPUTE --------------------------------------------------------------------------------------------------------------------------------------
	public void compute_02 () {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> ();
		ArrayList<Path> paths = new ArrayList<Path>() ;
		dijkstra.init(netGr);
		
		for ( Node nNet :netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d != 2) {
				dijkstra.setSource(nNet);
				dijkstra.compute();
				if ( ! listNodeToAdd.contains(nNet)) {
//					Node nSim = createNode(mulGr, nNet);
//					nSim.addAttribute("nNet", nNet);
//					nNet.addAttribute("nSim", nSim);
					listNodeToAdd.add(nNet);
				}
				ArrayList <Node> listNearInPath = getListExt(nNet) ;
				for ( Node near : listNearInPath ) {
					Iterable<Path> p = dijkstra.getAllPaths(near);
					Iterator<Path> pIt = p.iterator();
					while ( pIt.hasNext() ){
						Path path = pIt.next();
						paths.add(path);
					}
					
//					System.out.println(p);
				//	p.forEach(n-> paths.add(n));
					if ( ! listNodeToAdd.contains(near)) {
//						Node nSim = createNode(mulGr, near);
//						nSim.addAttribute("nNet", near);
//						nNet.addAttribute("nSim", nSim);
						listNodeToAdd.add(near);
					}
				}
			}	
		}
		ArrayList<Node> listNodeVisited = new ArrayList<Node> ();
		for ( Path p : paths ) { 
			Node n0 = p.getNodePath().get(0) ,
					n1 = p.getNodePath().get(p.getNodeCount()-1);
			
			Node n0Sim , n1Sim ;
			
			if ( listNodeVisited.contains(n0)) {
				n0Sim = n0.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n0 ) ;
				n0Sim = createNode(mulGr, n0);
				n0Sim.addAttribute("nNet", n0);
				n0.addAttribute("nSim", n0Sim);
			}
			
			if ( listNodeVisited.contains(n1)) {
				n1Sim = n1.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n1 ) ;
				n1Sim = createNode(mulGr, n1);
				n1Sim.addAttribute("nNet", n1);
				n1.addAttribute("nSim", n1Sim);
			}
		
			System.out.println(n0 + " " + n1 + p );
			if  ( !n0Sim.equals(n1Sim))
				createEdge(mulGr, n0Sim, n1Sim);	
		}
		mulGr.display(false) ; 
	}

	public void compute_03 () {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> ();
		ArrayList<Path> paths = new ArrayList<Path>() ;
		dijkstra.init(netGr);
		
		for ( Node nNet :netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d != 2) {
				dijkstra.setSource(nNet);
				dijkstra.compute();
				if ( ! listNodeToAdd.contains(nNet)) {
					listNodeToAdd.add(nNet);
				}
				ArrayList <Node> listNearInPath = getListExt(nNet) ;
				System.out.println(listNearInPath);
				for ( Node near : listNearInPath ) {

					paths.add(dijkstra.getPath(near));
					if ( ! listNodeToAdd.contains(near)) {
						listNodeToAdd.add(near);
					}
				}
			}	
		}
		ArrayList<Node> listNodeVisited = new ArrayList<Node> ();
		ArrayList<Node[]> listEdges= new ArrayList<Node[]> ();
		
		
		for ( Path p : paths ) { 
			Node[] edge = new Node[2];
			Node n0 = p.getNodePath().get(0) ,
					n1 = p.getNodePath().get(p.getNodeCount()-1);
			
			Node n0Sim , n1Sim ;
			
			if ( listNodeVisited.contains(n0)) {
				n0Sim = n0.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n0 ) ;
				n0Sim = createNode(mulGr, n0);
				n0Sim.addAttribute("nNet", n0);
				n0.addAttribute("nSim", n0Sim);
			}
			
			if ( listNodeVisited.contains(n1)) {
				n1Sim = n1.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n1 ) ;
				n1Sim = createNode(mulGr, n1);
				n1Sim.addAttribute("nNet", n1);
				n1.addAttribute("nSim", n1Sim);
			}
			edge[0] = n0Sim;
			edge[1] = n1Sim;
			listEdges.add(edge);
		}
		
	//		System.out.println(n0 + " " + n1 + p );
		for( Node[] edge : listEdges) {
			Node n0Sim = edge[0] , n1Sim = edge[1];
			if  ( !n0Sim.equals(n1Sim))
				createEdge(mulGr, n0Sim, n1Sim);	
		}
		
	
		mulGr.display(false) ; 
	}

	public void compute_04 () {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> ();
		ArrayList<Path> paths = new ArrayList<Path>() ;
		dijkstra.init(netGr);
			
 		for ( Node nNet :netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d != 2) {
				dijkstra.setSource(nNet);
				dijkstra.compute();	
				if ( ! listNodeToAdd.contains(nNet)) 
					listNodeToAdd.add(nNet);
				
				ArrayList <Node> listNearInPath = getListExt(nNet) ;
				for ( Node near : listNearInPath ) {			
					paths.add(dijkstra.getPath(near));
					if ( ! listNodeToAdd.contains(near)) 
						listNodeToAdd.add(near);
					
				}
			}	
		}
		ArrayList<Node> listNodeVisited = new ArrayList<Node> ();
		ArrayList<Node[]> listEdges= new ArrayList<Node[]> ();
			
		for ( Path p : paths ) { 
			Node[] edge = new Node[2];
			Node n0 = p.getNodePath().get(0) ,
					n1 = p.getNodePath().get(p.getNodeCount()-1);
			
			Node n0Sim , n1Sim ;
			
			if ( listNodeVisited.contains(n0)) {
				n0Sim = n0.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n0 ) ;
				n0Sim = createNode(mulGr, n0);
				n0Sim.addAttribute("nNet", n0);
				n0.addAttribute("nSim", n0Sim);
			}
			
			if ( listNodeVisited.contains(n1)) {
				n1Sim = n1.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n1 ) ;
				n1Sim = createNode(mulGr, n1);
				n1Sim.addAttribute("nNet", n1);
				n1.addAttribute("nSim", n1Sim);
			}
			edge[0] = n0Sim;
			edge[1] = n1Sim;
			listEdges.add(edge);
		}
		
	//		System.out.println(n0 + " " + n1 + p );
		for( Node[] edge : listEdges) {
			Node n0Sim = edge[0] , n1Sim = edge[1];
			if  ( !n0Sim.equals(n1Sim))
				createEdge(mulGr, n0Sim, n1Sim);	
		}
		
	
		mulGr.display(false) ; 
	}
	
	public void compute_06 ( ) {
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> () ,
				listNodeD2 = new ArrayList<Node> () , 
				listNodeVisited = new ArrayList<Node>();
		
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		dijkstra.init(netGr);
		
		for ( Node nNet :netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d == 2 ) 
				listNodeD2.add(nNet);
			else {
				listNodeToAdd.add(nNet);
				Node nSim = createNode(mulGr, nNet);
				nSim.addAttribute("nNet", nNet);
				nNet.addAttribute("nSim", nSim);
			}
		}
		ArrayList<Path> paths = new ArrayList<Path> ();
		
		for ( Node n2 : listNodeD2 ) {
			ArrayList<Node> neigs = new ArrayList<Node>() ;
			Iterator<? extends Node> k = n2.getDepthFirstIterator();
			int d = 0 , pos = 0;	
			while ( k.hasNext()  && pos < 2 ) {
				Node n =  k.next();
				d = n.getDegree();	
				if ( d != 2 ) {
					neigs.add(n) ;	
					pos++;
				}
			}
			if ( neigs.size() == 2) {
				dijkstra.setSource(neigs.get(0));
				dijkstra.compute();
				Path p = dijkstra.getPath(neigs.get(1));
				if ( ! paths.contains(p))
					paths.add(p);
				}
		}
		for ( Path p : paths) {
			List<Node> nodePath = p.getNodePath();
			Node n0 = p.getRoot() ;
				Node n1 =  nodePath.get(nodePath.size() -1 ) ;
			Node n0Sim = n0.getAttribute("nSim"),
					n1Sim = n1.getAttribute("nSim");
			createEdge(mulGr, n1Sim, n0Sim);
				
			
		}
		mulGr.display(false);	
	}
		
	public void compute_05 () {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		ArrayList<Node> listNodeToAdd = new ArrayList<Node> ();
		ArrayList<Path> paths = new ArrayList<Path>() ;
		dijkstra.init(netGr);
		ArrayList<Node[]> listEdges= new ArrayList<Node[]> ();
		
			
 		for ( Node nNet :netGr.getEachNode() ) {
			int d = nNet.getDegree();
			dijkstra.setSource(nNet);
			dijkstra.compute();
			
			if ( d == 1 ) {
				listNodeToAdd.add(nNet);
				Node near = getNearestNodeInPath(netGr, nNet);
				if ( !listNodeToAdd.contains(near))
					listNodeToAdd.add(near);
				
				Path p = dijkstra.getPath(near);
				if ( ! paths.contains(p))
					paths.add(p);
		//		listEdges.add(new Node[] {nNet, near});
				
			}
			
			if ( d == 2 ) {
				ArrayList <Node> listNearInPath = getListExt(nNet) ;
				if ( listNearInPath.size() == 2) {
					Node n0 = listNearInPath.get(0) , n1 = listNearInPath.get(1); 
					
					if ( ! listNodeToAdd.contains(n0))
						listNodeToAdd.add(n0);
					if ( ! listNodeToAdd.contains(n1))
						listNodeToAdd.add(n1);
					
					dijkstra.setSource(n0);
					dijkstra.compute();
					Path p = dijkstra.getPath(n1);
					if ( !paths.contains(p))
						paths.add(p);
				}
			}
			
 		}
 		ArrayList<Node> listNodeVisited = new ArrayList<Node> ();

		for ( Path p : paths ) { 
			Node[] edge = new Node[2];
			Node n0 = p.getNodePath().get(0) ,
					n1 = p.getNodePath().get(p.getNodeCount()-1);
			
			Node n0Sim , n1Sim ;
			
			if ( listNodeVisited.contains(n0)) {
				n0Sim = n0.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n0 ) ;
				n0Sim = createNode(mulGr, n0);
				n0Sim.addAttribute("nNet", n0);
				n0.addAttribute("nSim", n0Sim);
			}
			
			if ( listNodeVisited.contains(n1)) {
				n1Sim = n1.getAttribute("nSim");
			}
			else {
				listNodeVisited.add( n1 ) ;
				n1Sim = createNode(mulGr, n1);
				n1Sim.addAttribute("nNet", n1);
				n1.addAttribute("nSim", n1Sim);
			}
			edge[0] = n0Sim;
			edge[1] = n1Sim;
			listEdges.add(edge);
		}
		
		
		
		for( Node[] edge : listEdges) {
			Node n0Sim = edge[0] , n1Sim = edge[1];
			if  ( !n0Sim.equals(n1Sim))
				createEdge(mulGr, n0Sim, n1Sim);				
		}	
		mulGr.display(false) ; 
		
	}
		
}
