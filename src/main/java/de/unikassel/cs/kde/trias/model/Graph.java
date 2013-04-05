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

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author:  rja
 * @version: $Id: Graph.java,v 1.1 2009-08-05 13:39:46 rja Exp $
 * $Author: rja $
 * 
 */
public class Graph<T extends Comparable<T>> {

	final Set<GraphEdge<T>> edges = new HashSet<GraphEdge<T>>();
	
	final Set<T> vertices = new HashSet<T>();

	public Set<GraphEdge<T>> getEdges() {
		return edges;
	}

	public Set<T> getVertices() {
		return vertices;
	}
	
	public void addEdge(final GraphEdge<T> edge) {
		edges.add(edge);
		addVertice(edge.getStartVertice());
		addVertice(edge.getEndVertice());
	}
	
	public void addVertice(final T vertice) {
		vertices.add(vertice);
	}
}

