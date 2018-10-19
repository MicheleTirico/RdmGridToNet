package RdmGridToNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
					mulGr = new MultiGraph  ( "multiGr");
	
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	
	public staticSympleNetwork( Graph  netGr ) {
		this.netGr = netGr ;
	}
	
	public void init() {
		
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
	
	public void compute () {
		ArrayList < Node > listNodeAdded  = computeNodes(true, netGr, stSimGr);
		computePaths_02(listNodeAdded, netGr, stSimGr);
		stSimGr.display(false);
		
		
	}
	private void computePaths_02 ( ArrayList < Node > listNodeAdded , Graph grOr ,Graph grToCreate) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		
		dijkstra.init(grOr);		
		for ( Node n0Sim : grToCreate.getEachNode() ) {
			Node n0 = n0Sim.getAttribute("nNet"); 
			dijkstra.setSource(n0);
			dijkstra.compute();
			for ( Node n1Sim : grToCreate.getEachNode() ) {
				if ( !n0Sim.equals(n1Sim)) {
					Node n1 = n1Sim.getAttribute("nNet");
					ArrayList<Path> listPath = getAllPaths(n0, n1);
					 System.out.println(listPath.size());
					for ( Path p : listPath) {
						if ( listPath.size() > 1 ) {
							createNode(grToCreate, p.getNodePath().get(1));
		//					Path p = dijkstra.getPath(n1);
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
								if ( e!=null)
									e.addAttribute("length", dijkstra.getPathLength(n1) );
							}
						}
					}
				}
			}
		}
	}
	
	private ArrayList<Path> getAllPaths ( Node n0 , Node n1 ) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, "length", "length") ; 
		dijkstra.init(netGr);
		dijkstra.setSource(n0);
		dijkstra.compute();
		ArrayList<Path> list = new ArrayList<Path> () ;
		
	//	System.out.println(dijkstra.getAllPaths(n1) );
		Iterator it = dijkstra.getAllPathsIterator(n1);
		int x = 0 ;
		while ( it.hasNext() ) {
			Path p = (Path) it.next() ;
			if  ( ! list.contains(p)) {
				list.add(p);
				x++ ;
			}
			if ( x >= n0.getDegree() )
				return list;		
		}

		return list;
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
					Path p = dijkstra.getPath(n1);
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
						if ( e!=null)
							e.addAttribute("length", dijkstra.getPathLength(n1) );
					}
				}
			}
		}
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
