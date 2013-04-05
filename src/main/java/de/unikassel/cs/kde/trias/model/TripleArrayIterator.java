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

import java.util.Iterator;

/**
 * 
 * @author:  rja
 * @version: $Id: TripleArrayIterator.java,v 1.2 2009-04-23 06:53:16 rja Exp $
 * $Author: rja $
 * 
 */
public class TripleArrayIterator<T> implements Iterator<Triple<T>> {

	private Triple<T>[] triples;
	private int pos;
	
	public TripleArrayIterator(final Triple<T>[] triples) {
		this.triples = triples;
		this.pos = 0;
	}
	
	public boolean hasNext() {
		return pos < triples.length;
	}

	public Triple<T> next() {
		return triples[pos++];
	}

	public void remove() {
		throw new UnsupportedOperationException("Removing elements from an array of triples is not possible.");
	}
}

