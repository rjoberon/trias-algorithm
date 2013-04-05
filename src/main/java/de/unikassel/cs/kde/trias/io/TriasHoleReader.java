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
import java.util.HashMap;

import de.unikassel.cs.kde.trias.util.Dimension;

/**
 * @author rja
 *
 */
public class TriasHoleReader implements TriasReader {

	private String delimiter = "\\s";


	/**
	 * number of TAS
	 */
	private int numberOfItems;
	
	
	private HashMap<Integer,Integer>[] mapping;
	private HashMap<Integer,Integer>[] inverseMapping;
	private int mappingValues[];
	private BufferedReader reader;
	

	@SuppressWarnings("unchecked")
	public TriasHoleReader(final BufferedReader reader, int numberOfItems, final String delimiter) {
		super();
		this.numberOfItems = numberOfItems;
		this.delimiter = delimiter;
		this.reader = reader;
		mappingValues = new int[Dimension.noOfDimensions];
		
		mapping = new HashMap[Dimension.noOfDimensions];
		inverseMapping = new HashMap[Dimension.noOfDimensions];
		
		for (int dim = 0; dim < mapping.length; dim++) {
			mappingValues[dim] = 1;
			mapping[dim] = new HashMap<Integer, Integer>();
			inverseMapping[dim] = new HashMap<Integer, Integer>();
		}
	}
	
	public int[][] getItemlist() throws NumberFormatException, IOException {
		int[][] itemList = new int[numberOfItems][Dimension.noOfDimensions + 1];
		int ctr = 0;
		while (reader.ready()) {
			String[] parts = reader.readLine().split(delimiter);
			for (int dim = 0; dim < Dimension.noOfDimensions; dim++) {
				itemList[ctr][dim] = getMapping(parts, dim);	
			}
			ctr++;
		}
		reader.close();
		return itemList;
	}
	
	
	/** returns the mapped value for parts[POS]
	 * 
	 * @param parts array with values
	 * @param pos position in parts
	 * @return
	 */
	private int getMapping (String[] parts, int pos) {
		int key = Integer.parseInt(parts[pos]);
		if (!mapping[pos].containsKey(key)) {
			/*
			 * get next imag value
			 */
			int value = mappingValues[pos]++;
			/*
			 * save it
			 */
			mapping[pos].put(key, value);
			inverseMapping[pos].put(value, key);
		}
		return mapping[pos].get(key);
	}

	public HashMap<Integer, Integer>[] getInverseMapping() {
		return inverseMapping;
	}

}
