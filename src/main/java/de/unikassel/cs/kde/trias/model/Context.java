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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 
 * @author:  rja
 * @version: $Id: Context.java,v 1.3 2011-12-02 13:20:37 rja Exp $
 * $Author: rja $
 * 
 */
public class Context<T> implements Iterable<Triple<T>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6137971180325104187L;
	private Triple<T>[] relation;

	public Context() {
		super();
	}
	
	public Context(Triple<T>[] relation) {
		this();
		this.relation = relation;
	}

	public Triple<T>[] getRelation() {
		return relation;
	}

	public void setRelation(final Triple<T>... relation) {
		this.relation = relation;
	}

	public Iterator<Triple<T>> iterator() {
		return new TripleArrayIterator<T>(relation);
	} 
	
	@Override
	public String toString() {
		return Arrays.toString(relation);
	}
	
}

