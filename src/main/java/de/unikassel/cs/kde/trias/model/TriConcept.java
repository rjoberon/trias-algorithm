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
 * @version: $Id: TriConcept.java,v 1.4 2009-08-05 13:39:46 rja Exp $
 * $Author: rja $
 * 
 */
public class TriConcept<T extends Comparable<T>> implements Serializable, Iterable<T[]>, Comparable<TriConcept<T>> {

	private static int EXTENT = 0;
	private static int INTENT = 1;
	private static int MODUS  = 2;
	public  static int DIMS   = 3;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1463615489346679350L;
	private T[][] triConcept;

	@SuppressWarnings("unchecked")
	public TriConcept() {
		super();
		this.triConcept = (T[][]) new Comparable[DIMS][];
	}

	public TriConcept(T[] extent, T[] intent, T[] modus) {
		this();
		this.triConcept[EXTENT] = extent;
		this.triConcept[INTENT] = intent;
		this.triConcept[MODUS] = modus;
	}
	public T[] getExtent() {
		return triConcept[EXTENT];
	}
	public void setExtent(T[] extent) {
		this.triConcept[EXTENT] = extent;
	}
	public T[] getIntent() {
		return triConcept[INTENT];
	}
	public void setIntent(T[] intent) {
		this.triConcept[INTENT] = intent;
	}
	public T[] getModus() {
		return triConcept[MODUS];
	}
	public void setModus(T[] modus) {
		this.triConcept[MODUS] = modus;
	}

	public String toString() {
		return "(" + Arrays.toString(triConcept[EXTENT]) + ", " + Arrays.toString(triConcept[INTENT]) + ", " + Arrays.toString(triConcept[MODUS]) + ")";
		//return triConcept[EXTENT].length + "," + triConcept[INTENT].length + "," + triConcept[MODUS].length;
	}


	/** Two tri concepts are equal, if their extent, intent and modus are equal.
	 *  
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof TriConcept) {
			TriConcept<T> oo;
			try {
				oo = (TriConcept<T>) o;	
			} catch (Exception e) {
				return false;
			}
			return 
			Arrays.equals(triConcept[EXTENT], oo.triConcept[EXTENT]) &&
			Arrays.equals(triConcept[INTENT], oo.triConcept[INTENT]) && 
			Arrays.equals(triConcept[MODUS],  oo.triConcept[MODUS]);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 
		Arrays.hashCode(triConcept[EXTENT]) * 
		Arrays.hashCode(triConcept[INTENT]) * 
		Arrays.hashCode(triConcept[MODUS]);
	}
	
	/** Returns the part of the tri-concept in the corresponding dimension.
	 * <ol>
	 * <li>extent</li>
	 * <li>intent</li>
	 * <li>modus</li>
	 * </ol> 
	 * 
	 * @param dimension
	 * @return
	 */
	public T[] getDim(final int dimension) {
		return triConcept[dimension];
	}

	/**
	 * Returns an iterator, which iterates over all parts of a TriConcept (extent, intent, modus). 
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T[]> iterator() {
		return new TriConceptIterator(this.triConcept);
	}

	private class TriConceptIterator implements Iterator<T[]> {

		private T[][] _triConcept;

		private int pos = 0;

		public boolean hasNext() {
			return pos < _triConcept.length;
		}

		public TriConceptIterator(T[][] triConcept) {
			this._triConcept = triConcept;
		}

		public T[] next() {
			return _triConcept[pos++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public int compareTo(final TriConcept<T> o) {
		int compareTo = compareTo(this.getExtent(), o.getExtent());
		if (compareTo == 0) {
			compareTo = compareTo(this.getIntent(), o.getIntent());
			if (compareTo == 0) {
				return compareTo(this.getModus(), o.getModus());
			} else {
				return compareTo;
			}
		} else {
			return compareTo;
		}
	}
	
	private int compareTo(final T[] a, final T[] b) {
		for (int i = 0; i < a.length && i < b.length; i++) {
			final int compareTo = a[i].compareTo(b[i]);
			if (compareTo == 0) {
				// skip
			} else {
				return compareTo;
			}
		}
		return 0;
	}

}

