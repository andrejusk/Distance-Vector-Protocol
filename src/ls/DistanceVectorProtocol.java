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

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * The class implements a network layer Distance-Vector protocol.
 * It is based on the original Link-State protocol class.
 *
 * The protocol broadcast its local graph to all nodes in the network.
 * Each instance of the protocol then computes the shortest path tree using the Bellman-Ford algorithm.
 *
 * @author A. Kostarevas, M. Ayiad
 * @version 1.0
 * April 2018
 */
public class DistanceVectorProtocol implements CDProtocol {

    /* Enumerated states */
    private enum State {
        INITIALISE, BROADCAST, COMPUTE
    }

    /* Network graph as a set of edges (Unvisited nodes) */
    private ArrayList<Edge> graph;
    /* shortest path tree (visited nodes) */
    private TreeMap<Long, Path> paths;
    /* current phase of the protocol */
    private State phase;
    /* limits computation cycles to one  */
    private boolean done;

    /**
     * A constructor.
     *
     * @param prefix required by PeerSim to access protocol's alias in the configuration file.
     */
    public DistanceVectorProtocol(@SuppressWarnings("unused") String prefix) {
        /* Start in INIT phase */
        this.phase = State.INITIALISE;
        /* Enable computation cycle */
        this.done = false;
    }

    /**
     * PeerSim cyclic service. To be execute every cycle.
     *
     * @param host Reference to host node.
     * @param pid Global protocol's ID in this simulation.
     */
    @Override
    public void nextCycle(Node host, int pid) {

        /* Reference local Linkable protocol */
        Linkable lnk = (Linkable) host.getProtocol(FastConfig.getLinkable(pid));

        /* Reference host's node ID */
        long nodeId = host.getID();

        /* Current phase */
        switch (phase) {
            case INITIALISE:
                init(lnk, nodeId);
                /* Transit to next phase */
                phase = State.BROADCAST;
                break;
            case BROADCAST:
                /* Broadcast the local graph */
                broadcast(pid);
                /* Transit to next phase */
                phase = State.COMPUTE;
                break;
            case COMPUTE:
                /* Compute shortest paths */
                compute(nodeId);
                break;
        }
    }

    /**
     * Initialises local graph.
     *
     * @param lnk Reference local Linkable protocol.
     * @param nodeId Host Node ID.
     */
    private void init(Linkable lnk, long nodeId) {
        long neighborId;
        /* Create information containers */
        this.graph = new ArrayList<>();
        this.paths = new TreeMap<>();
        /* Add neighbours - access neighbours in the Linkable */
        for (int i = 0; i < lnk.degree(); i++) {
            /* Get neighbour's i ID */
            neighborId = lnk.getNeighbor(i).getID();
            /* Get cost of the link between this node and neighbour i */
            int cost = CostInitialiser.getCost(nodeId, neighborId);
            /* Add edge to local graph */
            graph.add(new Edge(nodeId, neighborId, cost));
        }
    }

    /**
     * Broadcasts local graph to peers.
     * @param pid Global protocol's ID in this simulation.
     */
    private void broadcast(int pid) {
        /* Get network size */
        int size = Network.size();
        for (int i = 0; i < size; i++) {
            /* Access node i */
            Node tempNode = Network.get(i);
            /* Access LS protocol in node i */
            DistanceVectorProtocol tempProtocol = (DistanceVectorProtocol) tempNode.getProtocol(pid);
            /* Copy local graph */
            ArrayList<Edge> tempGraph = new ArrayList<>(graph);
            /* Send the copy to node i */
            tempProtocol.receive(tempGraph);
        }
    }

    /**
     * Compute shortest path using Bellman-Ford algorithm.
     * @param nodeId Host Node ID.
     */
    private void compute(long nodeId) {
        /* Do it only once */
        if (done) {
            return;
        }
        System.out.println("TODO: implement " + nodeId);
        done = true;
    }

    /**
     * Receives a graph form a neighbour and updates local graph
     * removes duplicate edges if any
     *
     * @param neighborGraph a copy of neighbour's local graph
     */
    private void receive(ArrayList<Edge> neighborGraph) {
        /* For each edge in the neighbour's graph */
        for (Edge aNeighborGraph : neighborGraph) {
            /* Ignore duplicate edges */
            boolean duplicate = false;
            for (Edge aGraph : graph) {
                if (aNeighborGraph.equals(aGraph)) {
                    duplicate = true;
                    break;
                }
            }
            /* Add new edge */
            if (!duplicate) {
                graph.add(aNeighborGraph);
            }
        }
    }

    /**
     * Access to local path tree. Used bye the observer.
     *
     * @return the local path tree
     */
    public TreeMap<Long, Path> getPaths() {
        return paths;
    }

    /**
     * used by PeerSim to clone this protocol at the start of the simulation
     */
    @Override
    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (Exception ignored) { }
        return o;
    }
}
