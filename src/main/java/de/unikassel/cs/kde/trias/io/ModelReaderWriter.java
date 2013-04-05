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

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.TriConcept;
import de.unikassel.cs.kde.trias.model.Triple;
import de.unikassel.cs.kde.trias.util.Dimension;

/**
 * 
 * @author:  rja
 * @version: $Id: ModelReaderWriter.java,v 1.3 2011-12-02 13:20:37 rja Exp $
 * $Author: rja $
 * 
 */
public class ModelReaderWriter<T extends Comparable<T>> implements TriasReader, TriasWriter {
	
	/**
	 * The context this class is working on.
	 */
	private int[][] itemList;
	/**
	 * The tri lattice of the context, as computed by trias.
	 */
	private final Set<TriConcept<T>> triLattice;
	private TriConcept<T>[] triLatticeArray = null;
	/**
	 * For each dimension a map which maps the original strings to the integers
	 * used in Trias. 
	 */
	private final Map<T, Integer>[] stringToInt;
	/**
	 * The inverse map to {@link #stringToInt}.
	 */
	private final List<T>[] intToString;
	
	private final int[] numberOfItemsPerDimension;

	
	
	
	@SuppressWarnings("unchecked")
	public ModelReaderWriter(final Context context) {
		/*
		 * initialize mappings
		 */
		stringToInt = new TreeMap[Dimension.noOfDimensions];
		intToString = new LinkedList[Dimension.noOfDimensions];
		for (int dim = 0; dim < stringToInt.length; dim++) {
			stringToInt[dim] = new TreeMap<T, Integer>();
			intToString[dim] = new LinkedList<T>();
		}
		/*
		 * for numbering the items of each dimension
		 */
		numberOfItemsPerDimension = new int[Dimension.noOfDimensions];
		Arrays.fill(numberOfItemsPerDimension, 0);
		/*
		 * generate the item list
		 */
		this.itemList = generateItemList(context);
		/*
		 * stores the resulting triLattice
		 */
		triLattice = new LinkedHashSet<TriConcept<T>>();
	}
	
	public Map<T, Integer>[] getMap() {
		return stringToInt;
	}
	
	public int[][] getItemlist() {
		return itemList;
	}

	private int[][] generateItemList(final Context<T> context) {
		/*
		 * resulting itemlist
		 */
		itemList = new int[context.getRelation().length][Dimension.noOfDimensions + 1];
		/*
		 * numbering triples
		 */
		int tripleCtr = 0;
		for (final Triple<T> triple:context) {
			for (int dim = 0; dim < numberOfItemsPerDimension.length; dim++) {
				/*
				 * create new key, if neccessary
				 */
				if (!stringToInt[dim].containsKey(triple.getDimension(dim))) {
					intToString[dim].add(numberOfItemsPerDimension[dim], triple.getDimension(dim));
					stringToInt[dim].put(triple.getDimension(dim), numberOfItemsPerDimension[dim] + 1); // Trias expects numbering from 1
					numberOfItemsPerDimension[dim]++;
				}
				/*
				 * add triple to list
				 */
				itemList[tripleCtr][dim] = stringToInt[dim].get(triple.getDimension(dim));
			}
			tripleCtr++;
		}
		return itemList;
	}

	public int[] getNumberOfItemsPerDimension() {
		return numberOfItemsPerDimension;
	}
	
	/** Must be called after writing finished.
	 * 
	 * @see de.unikassel.cs.kde.trias.io.TriasWriter#close()
	 */
	@SuppressWarnings("unchecked")
	public void close() throws IOException {
		/*
		 * copy triLattice list into array
		 */
		triLatticeArray = new TriConcept[triLattice.size()];
		int i = 0;
		for (final TriConcept<T> triConcept: triLattice) {
			triLatticeArray[i++] = triConcept;
		}
		triLattice.clear();
	}

	public TriConcept<T>[] getTriLattice() {
		return triLatticeArray;
	}
	
	/** 
	 * Adds the tri concept to the tri lattice, which can be obtained 
	 * via {@link #getTriLattice()} after {@link #close()} has been
	 * called. 
	 * 
	 * @see de.unikassel.cs.kde.trias.io.TriasWriter#write(int[][])
	 */
	@SuppressWarnings("unchecked")
	public void write(int[][] concept) throws IOException {
		final TriConcept triConcept = new TriConcept();

		final T[][] triConceptSets = (T[][]) new Comparable[Dimension.noOfDimensions][];
		
		for (int dim = 0; dim < triConceptSets.length; dim++) {
			triConceptSets[dim] = (T[]) new Comparable[concept[dim].length];
			for (int i = 0; i < concept[dim].length; i++) {
				triConceptSets[dim][i] = intToString[dim].get(concept[dim][i] - 1);
			}
		}
		
		triConcept.setExtent(triConceptSets[0]);
		triConcept.setIntent(triConceptSets[1]);
		triConcept.setModus(triConceptSets[2]);
		
		triLattice.add(triConcept);
	}
}

