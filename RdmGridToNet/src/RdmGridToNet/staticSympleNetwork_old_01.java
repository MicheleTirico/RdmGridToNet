package RdmGridToNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

public class staticSympleNetwork_old_01 extends framework{

	private Graph stSimGr = new SingleGraph ( "StSimNet" ),
					netGr = new SingleGraph ( "StSimNet" );
	private int idNodeInt = 0 , idEdgeInt = 0 ;
	private String idNode , idEdge ; 
	
	public staticSympleNetwork_old_01( Graph  netGr ) {
		this.netGr = netGr ;
	}
	
	public void init() {
		
	}
	

	public void compute () {
		ArrayList<Node> listNodesCreated = new ArrayList<Node> ( ) ,
				listeNodeD1 = new ArrayList<Node> ( ) ;
		
		Map <Node,Node> mapEdges = new HashMap<Node,Node> ();
		for ( Node nNet : netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d == 1 ) {
				
				Node nD1 = null , nD2 = null ;
				nD1 = createNode(stSimGr, nNet);
				nNet.addAttribute("nSim", nD1);
				listNodesCreated.add(nNet);
				
					
				Node nearest = getNearestNodeInPath(netGr , nNet);				
				nD2 = createNode(stSimGr, nearest);	
				nearest.addAttribute("nSim", nD2);
				if ( ! 	listNodesCreated.contains(nearest)	)
					listNodesCreated.add(nearest);				
					
				if ( nD1 != null && nD2 != null )
					createEdge(stSimGr, nD1, nD2);
				
				
//				Node nD1 = createNode(stSimGr, nNet);
//				Node nearest = getNearestNodeInPath(netGr , nNet);
//				nNet.addAttribute("nSim", nD1);
//
//				Node nD2 = createNode(stSimGr, nearest);
//				nearest.addAttribute("nSim", nD2);
//				
//				createEdge(stSimGr, nD1, nD2);
//				listeNodeD1.add(nNet);		
//				mapEdges.put(nNet, nearest);
	
			} 
			
		
			
			if ( d == 2 ) {
				ArrayList<Node> listExt = getListExt(nNet);//				System.out.println(listExt);				
				Node n0 = listExt.get(0), n1 = listExt.get(1) ;		
				mapEdges.put(n0, n1);
				Node nSim0 , nSim1 ;
				for ( Node n : listExt ) {
					if ( ! listNodesCreated.contains(n) ) {
						Node nSim = createNode(stSimGr, n) ;
						n.addAttribute("nSim", nSim);
						listNodesCreated.add(n);
					}
				}
				
//				if ( ! listNodesCreated.contains(n0)) {
//					 nSim0 = createNode(stSimGr, n0) ;
//					 n0.addAttribute("nSim", nSim0);
//					 listNodesCreated.add(n0);
//				}
//				else 
//					nSim0 = n0.getAttribute("nSim");
//				
//				if ( ! listNodesCreated.contains(n1)) {
//					 nSim1 = createNode(stSimGr, n1) ;
//					 n1.addAttribute("nSim", nSim1);
//					 listNodesCreated.add(n1);
//				}
//				else 
//					nSim1 = n1.getAttribute("nSim");
//				
//				createEdge(stSimGr, nSim0 , nSim1);	
			}
			else {
//				if ( ! listNodesCreated.contains(nNet) ) {
//					Node n3 = createNode(stSimGr, nNet);
//					Node nSim3 = getNearestNodeInPath(netGr , nNet);
//					nNet.addAttribute("nSim", nSim3);
//				}
			}
		}	
		stSimGr.display(false);	
	}
	
	public void compute6 () {
		ArrayList<Node> listNodesCreated = new ArrayList<Node> ( ) ;
		
		for ( Node nNet : netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d == 1 ) {
			
				Node nD1 = null , nD2 = null ;
				nD1 = createNode(stSimGr, nNet);
				nNet.addAttribute("nSim", nD1);
				listNodesCreated.add(nNet);
				
					
				Node nearest = getNearestNodeInPath(netGr , nNet);				
				nD2 = createNode(stSimGr, nearest);	
				nearest.addAttribute("nSim", nD2);
				listNodesCreated.add(nearest);				
				
				if ( nD1 != null && nD2 != null )
					createEdge(stSimGr, nD1, nD2);
			}
			if ( d > 2 ) {
				if ( ! listNodesCreated.contains(nNet) ) {
					Node nD3 = createNode(stSimGr, nNet);
					nNet.addAttribute("nSim", nD3);
					listNodesCreated.add(nNet);
				}
			}
			
			if ( d == 2 ) {
				ArrayList<Node> listExt = getListExt(nNet); //				System.out.println(listExt);					
				
				Node n0 = listExt.get(0) , n1 = listExt.get(1);
			
				if ( ! listNodesCreated.contains(n0))
					listNodesCreated.add(n0);
				
				if ( ! listNodesCreated.contains(n1))
					listNodesCreated.add(n1);
				
				for ( Node n : listExt ) {
					
						Node nSim = createNode(stSimGr, n) ;
						n.addAttribute("nSim", nSim);
					
					
				}
				Node nSim0 = listExt.get(0).getAttribute("nSim") ,
						nSim1 = listExt.get(1).getAttribute("nSim");
				
		//		createEdge(stSimGr, nSim0, nSim1);
				
				
			}
		}

		stSimGr.display(false);	
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
		ArrayList<Node> listExt = new ArrayList<Node> (),
				listNodeVisited = new ArrayList<Node> ();
		
		Iterator<? extends Node> k = node.getDepthFirstIterator();
		int num = 0 ;
		while ( k.hasNext() && num < 2 ) {
			Node next = k.next();
			int dNext = next.getDegree();
			if ( next.getDegree() == 1) 
				System.out.println(next);
			
			if ( dNext !=2 && ! listExt.contains(next)) {
				listExt.add(next);	
				num++ ;
			}	
		}
		return listExt ;
	}
	
	public ArrayList<Node> getListExt4 (  Node node ) {
		ArrayList<Node> listExt = new ArrayList<Node> (),
				listNodeVisited = new ArrayList<Node> ();
		int nExt = 0 ;
		while ( nExt < 2 ) {
			Node n = null ;
			ArrayList<Node> listNeig = lNet.getListNeighbors(n, false) ;
			Node n0 = listNeig.get(0);
			if ( n0.getDegree() != 2 ) {
				
				listExt.add(n0);
				listNodeVisited.add(n0);
				nExt++;
			} else {	
			}	
		}
		return listExt ;
	}

	public ArrayList<Node> getListExt3 (  Node node ) {
		ArrayList<Node> listExt = new ArrayList<Node>(), 
				listNeig = lNet.getListNeighbors(node, false) ,
				listNodeVisited = new ArrayList<Node> (listNeig),
						listNewNode = new ArrayList<Node> (listNeig);
	
		listNodeVisited.add(node);
		int nExt = 0 ;
		 listNewNode = listNeig  ;
		while ( nExt < 2 ) {
			Node n0 = listNewNode.get(0) , n1 = listNewNode.get(1) ,
					 newN , newN0,  newN1 ;
		
			Node n = n0;
			if ( n.getDegree() != 2 ) {
			
					listExt.add(n);
					nExt++;
				
			}
			else {
				listNodeVisited.add(n);
				int pos = 0 ;
				while ( listNodeVisited.contains(lNet.getListNeighbors(n, false).get(pos))&& 
						! listNodeVisited.contains(lNet.getListNeighbors(n, false).get(pos)) ) {
					newN = lNet.getListNeighbors(n, false).get(pos);

					if ( !listNewNode.contains(newN)) {
						listNewNode.add(newN) ;
						listNodeVisited.add(newN);
						pos++;
				}
			}
			}
			n = n1;
			if ( n.getDegree() != 2 ) {
				if ( ! listExt.contains(n) ) {
					listExt.add(n);
					nExt++;
				}
			}
			else {
				listNodeVisited.add(n);
				int pos = 0 ;
				while ( listNodeVisited.contains(lNet.getListNeighbors(n, false).get(pos)) && 
						! listNodeVisited.contains(lNet.getListNeighbors(n, false).get(pos)) ) {
					newN = lNet.getListNeighbors(n, false).get(pos);
					
					if ( !listNewNode.contains(newN)) {
						listNewNode.add(newN) ;
						listNodeVisited.add(newN);
						pos++;
				}
				}
					
			}	
		}
	

		return listExt ;
		
	}
		
	public ArrayList<Node> getListExt2 (  Node n ) {
		ArrayList<Node> listExt = new ArrayList<Node>();
		Node[] ext = new Node[2];
		int num = 0 ;
		while ( num < 2 ) {
			Node [] neigs = lNet.getNeighbors(n);
			if ( neigs[0].getDegree() != 2 ) {
				listExt.add(neigs[0]);
				num++;
			}
			if ( neigs[1].getDegree() != 2 && num < 2 ) {
				listExt.add(neigs[1]);
				num++;
			}
			
		}
		return listExt ;
	}
	
	public void compute5 () {
		
		ArrayList<Node> listNodesVisited = new ArrayList<Node> ( ) ,
				listNodesToAdd = new ArrayList<Node> ( );
		for ( Node nNet : netGr.getEachNode() ) {
			int d = nNet.getDegree();
			if ( d == 2 )
				listNodesVisited.add(nNet);
			else {
				listNodesToAdd.add(nNet);
				Node nSim = createNode(stSimGr, nNet);
				nNet.addAttribute("nSim", nSim);
				nSim.addAttribute("nNet", nNet);
			}
		}
		
		for ( Node nNet : listNodesToAdd  ) {	
			Node nSim = nNet.getAttribute("nSim");
			int dSim = nSim.getDegree(), dNet =  nNet.getDegree() ;
			if ( dSim != dNet) {
				int d = dNet - dSim ;
				Iterator<? extends Node> k = nNet.getDepthFirstIterator();
				while ( d <= dNet && k.hasNext() ) {
					Node next = k.next();
					if ( next.getDegree() !=2 ) {
						Node n0 = next.getAttribute("nSim") , n1 = nSim ;
						if ( ! n0.equals(n1) ) {
							Edge e = createEdge(stSimGr, n0 , n1);	
							if ( e!= null) 
								d++;
						}
					}
				}
			}
		}
		stSimGr.display(false) ;
	}
	
	
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
	
	private Node createNode ( Graph gr , Node n) {
		
		double[] coord = GraphPosLengthUtils.nodePosition(n);	
		idNode = Integer.toString(idNodeInt); 
		Node newNode = gr.addNode(idNode);
		newNode.addAttribute("xyz", coord[0],coord[1] ,0);	
		idNodeInt++ ;
		return newNode ;
	}
	
	
	
	
	
	public void compute4 ( ) {
		ArrayList<Node> listNodeVisisted = new ArrayList<Node> () ,
				listNodeAdded= new ArrayList<Node> ();
		
		Map<Node,ArrayList<Node>> map = new HashMap() ;
		
		for ( Node n : netGr.getEachNode() ) {
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
	
	
	

	
	public void compute3() {
		
		ArrayList<Node> listNodeVisisted = new ArrayList<Node> () ,
				listNodeAdded = new ArrayList<Node> () 	;
		
		for ( Node n : netGr.getEachNode() ) {
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
		
		Node source = netGr.getNode(4577);
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
            	dijkstra.init(netGr);
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
