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

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

/**
 * The class generates random cost values for each edge in a network.
 * The initialiser access Linkable protocol to define edges.
 * The initialiser generates a public static global matrix for all edges and costs.  
 * PREREQUISITE, must be declared after whirring component in PeerSim.
 * @author M. Ayiad
 * @version 1.0 
 * March 2018
 */
public class CostInitialiser implements peersim.core.Control {

	private int pid;														//linkable protocol ID 
	private int size;														//network size
	private static int[][] initCcost;										//global costs matrix	
	
	private boolean debug;													//for debugging
	private static final int MAX_COST=20; 									//Maximum cost
	
	/**
	 * A constructor
	 * @param prefix a string provided by PeerSim and used to access parameters from the configuration file.
	 */
	public CostInitialiser(String prefix) {
		this.pid  = Configuration.getPid(prefix + ".linkable");
		this.debug = peersim.config.Configuration.contains(prefix+".debug");
	}
		
	/**
	 * Implementation of the common method. This method is called once at simulation start time. 
	 */
	@Override
	public boolean execute() {
		size = Network.size();
		initCcost = new int[size][size];
		for(int i=0; i < size; i++)
			for(int j=0; j < size; j++)
				if(i==j)
					initCcost[i][j]=0;
				else
					initCcost[i][j] = Integer.MAX_VALUE;			
		
		for(int i=0; i < size; i++) {
			Node node = Network.get(i);
			Linkable lnk = (Linkable) node.getProtocol(pid);
			
			for(int j=0; j < lnk.degree(); j++) {
				Node peer = lnk.getNeighbor(j);
				
				if(initCcost[(int)node.getID()][(int)peer.getID()] == Integer.MAX_VALUE) {					
					initCcost[(int)node.getID()][(int)peer.getID()] = CommonState.r.nextInt(MAX_COST)+1;;
					initCcost[(int)peer.getID()][(int)node.getID()] = initCcost[(int)node.getID()][(int)peer.getID()];	
				}
			}
		}
		if(debug) {
			System.out.printf("\n      ");				
			for(int j=0; j < size; j++) 
				System.out.printf("(%3d) ", j);
			for(int i=0; i < size; i++) {
				System.out.printf("\n(%3d) ", i);				
				for(int j=0; j < size; j++) 
					if(initCcost[i][j]==Integer.MAX_VALUE)
						System.out.printf("    X ");
					else
						System.out.printf("%5d ", initCcost[i][j]);
				System.out.println();
			}
		}
		return false;
	}
	/**
	 * Global access service to cost matrix 
	 * @param i node i
	 * @param j node j
	 * @return cost of edge between node i and node j
	 */
	public static int getCost(long i, long j) {
		return initCcost[(int)i][(int)j];
	}
}
