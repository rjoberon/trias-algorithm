/**
 *  
 *  Trias Algorithm - Trias is an algorithm for computing triadic concepts which
 * 		fulfill minimal support constraints.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.unikassel.cs.kde.trias.model;


/**
 * Represents an edge between two vertices.
 * 
 * @author:  rja
 * @version: $Id: GraphEdge.java,v 1.2 2009-04-23 06:53:16 rja Exp $
 * $Author: rja $
 * 
 */
public class GraphEdge<T> {

	private T startVertice;
	private T endVertice;
	
	public GraphEdge(T startVertice, T endVertice) {
		super();
		this.startVertice = startVertice;
		this.endVertice = endVertice;
	}
	public T getStartVertice() {
		return startVertice;
	}
	public void setStartVertice(T startVertice) {
		this.startVertice = startVertice;
	}
	public T getEndVertice() {
		return endVertice;
	}
	public void setEndVertice(T endVertice) {
		this.endVertice = endVertice;
	}
	
	@Override
	public String toString() {
		return startVertice + " -> " + endVertice;
	}
	
	
		
}

