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

import java.io.File;
import java.util.Date;
import java.util.Formatter;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.reports.GraphObserver;

/**
 * The control class extends the functionality of peersim.reports.GraphObserver to LinkStateProtoocl protocol.
 * The class prints nodes and edge costs into out file using DOT language.
 * PREREQUISITE, must be used for LinkStateProtoocl protocol in PeerSim.
 * @author M. Ayiad
 * @version 1.0 
 * March 2018
 */
public class GraphPrinter extends GraphObserver {
	
	private static final String PAR_PROT = "protocol";	
    private static final String PAR_FILEPATH = "outf";

	
	private final int pid;
    private String outf;
	/**
	 * A constructor
	 * @param prefix a string provided by PeerSim and used to access parameters from the configuration file.
	 */
	public GraphPrinter(String prefix) {
		super(prefix);
		pid  = Configuration.getPid(prefix + "." + PAR_PROT);
		outf = Configuration.getString(prefix + "." + PAR_FILEPATH);
	}
	
	/**
	 * Implementation of the common method. This method is called in each cycle. 
	 */
	@Override
	public boolean execute() {
		final int n = Network.size();
        int l=0, m = 0;
        System.out.println( "[" + CommonState.getTime() + "] drawing ... ");
        try {
            int[] drawn = new int[n];
            Date date = new Date(System.currentTimeMillis());
            String filename = String.format("%s%03d.graph", outf, CommonState.getTime() );
            Formatter file = new Formatter(new File(filename));
            if(undir)
                //file.format("// M. Ayiad, %s \ngraph random { size=\"11.69,16.53!\"; ratio=\"fill\"; margin=0; dpi=300; \n", date.toString());
            	file.format("// M. Ayiad, %s \ngraph random { ratio=\"fill\"; margin=0; \n", date.toString());
            else
                //file.format("// M. Ayiad, %s \ndigraph random { size=\"11.69,16.53!\"; ratio=\"fill\"; margin=0; dpi=300; \n", date.toString());
            	file.format("// M. Ayiad, %s \ngraph random { ratio=\"fill\"; margin=0; \n", date.toString());

            for (int i=0; i < n ; i++) {

                DistanceVectorProtocol protocol = (DistanceVectorProtocol)Network.get(i).getProtocol(pid);
            	Linkable link = (Linkable) Network.get(i).getProtocol(FastConfig.getLinkable(pid));
            	                                
                for (int j = 0; j < link.degree(); j++) {
                    long peerId = link.getNeighbor(j).getID();
                    int cost = -1;
                    if(protocol.getPaths()!=null && protocol.getPaths().containsKey(peerId))
                    	cost = protocol.getPaths().get(peerId).cost; 
                                    	
                	if(undir) {
                        m = 0;
                        while (m < l && peerId != drawn[m]) m++;
                        if (m < l) continue;
                        file.format("   %d -- %d [dir=both, label=%d];\n", i, peerId, cost);
                    }
                    else{
                        file.format("   %d -> %d;\n", i, peerId);
                    }
                }
                drawn[l++] = (int)Network.get(i).getID();
            }
            file.format(" }");
            file.close();

        } catch (Exception ex) { ex.printStackTrace(); }
        System.out.println("done.");
		
		return false;
	}

}
