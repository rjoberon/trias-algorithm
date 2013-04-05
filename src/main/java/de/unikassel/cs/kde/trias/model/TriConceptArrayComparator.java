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


/**
 * In an array of triconcepts, compares two triconcepts in an array, 
 * given their positions and the dimension in which they should be compared.  
 * 
 * @author rja
 *
 * @param <T>
 */
public class TriConceptArrayComparator<T extends Comparable<T>> implements Comparator<Integer> {

	private TriConcept<T>[] _triconcepts;
	private int _dim;
	private ArrayComparator<T> _comp; 

	public TriConceptArrayComparator(TriConcept<T>[] triconcepts, int dim) { 
		_dim = dim; 
		_triconcepts = triconcepts;
		_comp = new ArrayComparator<T>();
	}

	public int compare(final Integer arg0, final Integer arg1) {
		return _comp.compare(_triconcepts[arg0].getDim(_dim), _triconcepts[arg1].getDim(_dim));
	}		
}
