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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import de.unikassel.cs.kde.trias.Trias;
import de.unikassel.cs.kde.trias.io.TriasStringWriter;

/**
 * 
 * @author:  rja
 * @version: $Id: TriasJavaConfigurator.java,v 1.3 2009-04-23 06:53:13 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasJavaConfigurator implements TriasConfigurator {

	private int[][] utrList;
	private int[] minSupp;
	private SortedSet<String> concepts;
	
	public TriasJavaConfigurator (final int[][] utrList, final int[] minSupp, final SortedSet<String> concepts) {
		this.utrList = utrList;
		this.minSupp = minSupp;
		this.concepts = concepts;
	}

	public void configureTrias(final Trias trias) throws ConfigurationException {

		/*
		 * number of users, tags, resources
		 */
		final int[] numberOfItemsPerDimension = new int[Dimension.noOfDimensions];
		for (int dim = 0; dim < numberOfItemsPerDimension.length; dim++) {
			final Set<Integer> items = new HashSet<Integer>();
			for (int[] row:utrList) {
				items.add(row[dim]);
			}
			numberOfItemsPerDimension[dim] = items.size(); 
		}
		trias.setNumberOfItemsPerDimension(numberOfItemsPerDimension);


		/*
		 * minimal supports
		 */
		trias.setMinSupportPerDimension(minSupp);

		trias.setTriConceptWriter(new TriasStringWriter(concepts));

		trias.setItemList(utrList);
	}


	/** Prints usage information for this configurator.
	 * 
	 * @see de.unikassel.cs.kde.trias.util.TriasConfigurator#usage()
	 */
	public String usage() {
		final StringBuffer buf = new StringBuffer("Does not support lists with holes!");
		
		return buf.toString();
	}
}

