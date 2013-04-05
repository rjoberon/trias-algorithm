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



/**
 * 
 * @author:  rja
 * @version: $Id: Triple.java,v 1.2 2009-04-23 06:53:16 rja Exp $
 * $Author: rja $
 * 
 */
public class Triple<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128032626283222174L;
	private T object;
	private T attribute;
	private T condition;
	
	public Triple() {
		//
	}
	
	public Triple(T object, T attribute, T condition) {
		super();
		this.object = object;
		this.attribute = attribute;
		this.condition = condition;
	}
	
	public T getDimension(final int dimension) {
		if (dimension == 0) {
			return object;
		} else if (dimension == 1) {
			return attribute;
		} else if (dimension == 2) {
			return condition;
		}
		throw new IndexOutOfBoundsException("Input dimension must be 0, 1, or 2 but was " + dimension);
	}
	
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}
	public T getAttribute() {
		return attribute;
	}
	public void setAttribute(T attribute) {
		this.attribute = attribute;
	}
	public T getCondition() {
		return condition;
	}
	public void setCondition(T condition) {
		this.condition = condition;
	}
	
	public String toString() {
		return "(" + object + ", " + attribute + ", " + condition + ")";
	}
	
}

