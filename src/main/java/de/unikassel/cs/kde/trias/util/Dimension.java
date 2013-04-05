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

package de.unikassel.cs.kde.trias.util;



/**
 * 
 * @author:  rja
 * @version: $Id: Dimension.java,v 1.2 2009-04-23 06:53:13 rja Exp $
 * $Author: rja $
 * 
 */
public enum Dimension {
	U(0),
	T(1),
	R(2);
	private final static Dimension[] dimArray = new Dimension[]{U, T, R};
	public final static int noOfDimensions = dimArray.length;
	
	private int dim;
	
	private Dimension(final int dim) {
		this.dim = dim;
	}
	
	/**
	 * @return The integer corresponding to this dimension.
	 */
	public int intValue() {
		return dim;
	}
	
	/** For given integer, returns the corresponding dimension.
	 * 
	 * @param value
	 * @return
	 */
	public static Dimension byValue(final int value) {
		return dimArray[value];
	}
	
}

