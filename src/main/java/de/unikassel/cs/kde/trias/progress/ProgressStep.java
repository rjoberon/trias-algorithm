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

package de.unikassel.cs.kde.trias.progress;


/**
 * 
 * @author:  rja
 * @version: $Id: ProgressStep.java,v 1.2 2009-08-04 14:53:37 rja Exp $
 * $Author: rja $
 * 
 */
public enum ProgressStep {
	OUTER("o"),
	OUTER_SUCCESS("O"),
	INNER("i"),
	INNER_SUCCESS("I"),
	START("s"),
	STOP("S");
	
	private String step;
	
	ProgressStep(final String step) {
		this.step = step;
	}
	
	@Override
	public String toString() {
		return step; 
	}
	
}

