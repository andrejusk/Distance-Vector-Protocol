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
package dv;

/**
 * The class represent a shortest path from source node(host node) to destination
 * @author M. Ayiad
 * @version 1.0 
 * March 2018
 */
public class Path {
	public long destination;			//destination node ID
	public long predecessor;			//predecessor node ID from source node
	public int cost;					//total path cost to destination

	/**
	 * A constructor.
	 * @param destination
	 * @param predecessor
	 * @param cost
	 */
	public Path(long destination, long predecessor, int cost) {
		this.destination = destination;
		this.predecessor = predecessor;
		this.cost = cost;
	}
	
	/**
	 * copy path information into new Path instance.
	 * @return the new path copy
	 */
	public Path copy() { return new Path(destination, predecessor, cost); }
	
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
		Path p = (Path) o;			    return p.destination == this.destination;
	}
	
	@Override
	public String toString() {
		return "[" + destination + "->" + predecessor + ", " + cost + "]";
	}
}
