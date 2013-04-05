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

package de.unikassel.cs.kde.trias.io;

import java.io.BufferedReader;
import java.io.IOException;

import de.unikassel.cs.kde.trias.util.Dimension;

/**
 * @author rja
 *
 */
public class TriasStandardReader implements TriasReader {

	private String delimiter = "\\s";
	private int numberOfItems;
	private BufferedReader reader;
	
	public TriasStandardReader(final BufferedReader reader, int numberOfTriples, final String delimiter) {
		super();
		this.numberOfItems = numberOfTriples;
		this.delimiter = delimiter;
		this.reader = reader;
	}

	public int[][] getItemlist() throws NumberFormatException, IOException {
		int[][] utrListe = new int[numberOfItems][Dimension.noOfDimensions + 1];
		int ctr = 0;
		while (reader.ready()) {
			String[] parts = reader.readLine().split(delimiter);
			for (int dim = 0; dim < Dimension.noOfDimensions; dim++) {
				utrListe[ctr][dim] = Integer.parseInt(parts[dim]);	
			}
			ctr++;
		}
		reader.close();
		return utrListe;
	}

}
