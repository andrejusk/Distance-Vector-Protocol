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

/**
 * The class represent an edge E in a graph<V,E>.
 * @author M. Ayiad
 * @version 1.0 
 * March 2018
 */
public class Edge implements Comparable<Edge>{
	public long source;					//source node ID
	public long destination;			//destination node ID
	public int cost;					//edge cost/weight
	
	/**
	 * A constructor.
	 * @param source
	 * @param destination
	 * @param cost
	 */
	public Edge(long source, long destination, int cost) {
		this.source = source;
		this.destination = destination;
		this.cost = cost;
	}
	
	/**
	 * copy edge information into new Edge instance.
	 * @return the new edge copy
	 */
	public Edge copy() { return new Edge(source, destination, cost); }
	
	/**
	 * compare two edges by the cost, used for sorting edges
	 */
	@Override
	public int compareTo(Edge o) {
		return this.cost - o.cost;
	}
	
	@Override
	public int hashCode() { return (int)destination; }
	
	@Override
	public boolean equals(Object o) {
	    // self check
	    if (this == o)					return true;
	    // null check
	    if (o == null)					return false;
	    // type check and cast
	    if (getClass() != o.getClass())	return this.hashCode()==o.hashCode();
	    // field comparison
	    Edge e = (Edge) o;
	    return e.source == this.source && e.destination == this.destination;
	}
	
	@Override
	public String toString() {
		return "[" + source + ", " + destination + ", " + cost + "]";
	}
}
