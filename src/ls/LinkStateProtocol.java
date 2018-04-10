/*
 * Copyright (c) 2018, 
 *     University of Reading, Computer science Department
 *     CS2CA17 module - lab sessions
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU,
 * Lesser General Public License version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 */
package ls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;
import peersim.graph.Graph;
import sun.awt.image.ImageWatched;

/**
 * The class implements a network layer Link-State protocol.
 * The protocol broadcast its local graph to all nodes in the network. 
 * Each instance of the protocol then computes the shortest path tree using Dijkstra algorithm. 
 * @author M. Ayiad
 * @version 1.0 
 * March 2018
 */
public class LinkStateProtocol implements CDProtocol {
	
	private static final int INIT=1;
	private static final int BCST=2;
	private static final int CMPT=3;
	
	private ArrayList<Edge> graph; 		//network graph as a set of edges (Unvisited nodes)
	private TreeMap<Long, Path> paths;	//shortest path tree (visited nodes)
	private int phase;					//current phase of the protocol
	private boolean done;				//limits computation cycles to one 
	
	/**
	 * A contractor.
	 * @param prefix required by PeerSim to access protocol's alias in the configuration file.
	 */
	public LinkStateProtocol(String prefix) {
		this.phase = INIT;				//start in INIT phase
		this.done = false;				//enable computation cycle
	}
	
	@Override
	/**
	 * PeerSim cyclic service. To be execute every cycle.
	 * @param host reference to host node (peersim.core.GeralNode)
	 * @param pid  global protocol's ID in this simulation
	 */
	public void nextCycle(Node host, int pid) {
		
		Linkable lnk = (Linkable) host.getProtocol(FastConfig.getLinkable(pid));		//reference local Linkable protocol 
		
		long nodeId=host.getID(), neighborId;											//reference host's node ID 	
		switch(phase) {																	//current phase
		case INIT:																		 
																						// create information containers
			this.graph = new ArrayList<Edge>();											
			this.paths = new TreeMap<Long, Path>();
																						// add neighbours
			for(int i=0; i < lnk.degree(); i++) {										//access neighbours in the Linkable
				neighborId = lnk.getNeighbor(i).getID();								//get neighbour's i ID
				int cost = CostInitialiser.getCost(nodeId, neighborId);					//get cost of the link between this node and neighbour i 				
				graph.add(new Edge(nodeId, neighborId, cost));      					//add edge to local graph
			}
																						// transit to next phase
			phase = BCST;
			break;
		case BCST:																		//broadcast the local graph
			//get network size
			int size = Network.size();
			for (int i = 0; i < size; i++) {
				//access node i
				Node tempNode = Network.get(i);

				//access LS protocol in node i
				LinkStateProtocol tempProtocol = (LinkStateProtocol) tempNode.getProtocol(pid);

				//copy local graph
				ArrayList<Edge> tempGraph = new ArrayList<>(graph);

				//send the copy to node i
				tempProtocol.recieve(tempGraph);
			}
																						//Transit to next phase
			phase = CMPT;
			break;
		case CMPT:																		//compute shortest path using Dijkstra algorithm		
			if(!done) {																	//do it only once
				paths.put(nodeId, new Path(nodeId, nodeId, 0));							//assume host node as source node
				Collections.sort(graph);												//sort edges in ascending order by cost value
				boolean updated = true;													
				while(updated) {														//continue iteration until no more updates
			    	updated = false;	    	
			    	
			    	TreeMap<Long, Path> temp = new TreeMap<Long, Path>();				//copy current path tree
			    	for(Path p : paths.values())
			    		temp.put(p.destination, p.copy());
			    	
			    	for(Path p : temp.values()) {										//for each path, find a new edges if any, compute shortest path
			    		Iterator<Edge> itr = graph.iterator();
					    while(itr.hasNext()) {
					    	Edge e = itr.next(); 

					    	if(p.destination == e.source) {								//a new edge
					    		if(!paths.containsKey(e.destination)) { 		 	    //no path to edge destination, add new path to destination
					    			if(e.source == nodeId)
					    				paths.put(e.destination,  new Path(e.destination, e.destination, e.cost));
					    			else
					    				paths.put(e.destination,  new Path(e.destination, p.predecessor, p.cost + e.cost));
					    		}
					    		for(Path x : paths.values()) {	 						//update all current paths
					    			int src2_x_ = x.cost;
					    			int src2new = paths.get(e.destination).cost;
					    			int _x_2new = CostInitialiser.getCost(x.destination, e.destination);

					    			if( _x_2new < Integer.MAX_VALUE && (src2_x_ + _x_2new) < src2new) {
					    				paths.get(e.destination).cost = src2_x_ + _x_2new;
					    				paths.get(e.destination).predecessor = x.predecessor;
					    			}
					    		}					    			
					    		updated=true;
					    		itr.remove();											//remove visited edge 
					    	}//if
					    }//while
			    	}//for
			    }//while
			    done = true;															//the job done, don't run next cycle.
			}//done
			break;
		}//switch
	}//nextCycle
	
	/**
	 * Receives a graph form a neighbour and updates local graph
	 * removes duplicate edges if any
	 * @param neighborGraph a copy of neighbour's local graph 
	 */
	public void recieve(ArrayList<Edge> neighborGraph) {
		//for each edge in the neighbour's graph
		for (Edge aNeighborGraph : neighborGraph) {
			//ignore duplicate edges
			boolean duplicate = false;
			for (Edge aGraph : graph) {
				if (aNeighborGraph.equals(aGraph)) {
					duplicate = true;
					break;
				}
			}
			//add new edge
			if (!duplicate) {
				graph.add(aNeighborGraph);
			}
		}
	}

	/**
	 * Access to local path tree. Used bye the observer.
	 * @return the local path tree
	 */
	public TreeMap<Long, Path> getPaths() { return paths; }								// return current path tree
	
	/**
	 * used by PeerSim to clone this protocol at the start of the simulation
	 */
	@Override   
	public Object clone() {
      Object o = null;
       try {
    	   o = super.clone(); 
       } catch(CloneNotSupportedException cnse) {/*do nothing*/};
       return o;
    }//clone
}
