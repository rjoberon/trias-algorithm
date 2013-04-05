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

import java.util.Comparator;

public class ArrayComparator<T extends Comparable<T>> implements Comparator<T[]> {

	public int compare(T[] o1, T[] o2) {
		int pos = 0;
		// skip equal elements
		while (pos < o1.length && pos < o2.length && o1[pos].equals(o2[pos])) pos++;
		
		// end of o1 reached, o2 longer
		if (o1.length == pos && o2.length > pos) return -1;
		
		// end of o2 reached, o1 longer
		if (o2.length == pos && o1.length > pos) return 1;
		
		// end of both sets reached: equal
		if (o1.length == pos && o2.length == pos) return 0;
		
		// first position found, where sets differ
		return o1[pos].compareTo(o2[pos]);
	}

	
}
