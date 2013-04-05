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

package de.unikassel.cs.kde.trias.neighborhoods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.unikassel.cs.kde.trias.model.GraphEdge;
import de.unikassel.cs.kde.trias.model.TriConcept;

/**
 * 
 * @author:  rja
 * @version: $Id: TriNeighborHoodFinder.java,v 1.1 2009-04-23 06:53:15 rja Exp $
 * $Author: rja $
 * 
 */
public class TriNeighborHoodFinder<T extends Comparable<T>> {


	public HashSet<HashSet<TriConcept<T>>> findNeighborhoods(final Set<GraphEdge<TriConcept<T>>> graph) {
		HashSet<HashSet<TriConcept<T>>> neighborhoods = new HashSet<HashSet<TriConcept<T>>>();

		for (final GraphEdge<TriConcept<T>> edge : graph) {
			final TriConcept<T> startVertice = edge.getStartVertice();
			final TriConcept<T> endVertice = edge.getEndVertice();


			boolean neighborhoodFound = false;

			for (final Collection<TriConcept<T>> neighborhood : neighborhoods) {
				if (neighborhood.contains(startVertice)) {
					neighborhoodFound = true;
					neighborhood.add(endVertice);
				} else if (neighborhood.contains(endVertice)) {
					neighborhoodFound = true;
					neighborhood.add(startVertice);
				}
			}
			if (!neighborhoodFound) {
				/*
				 * no neighborhood found -> create new
				 */
				final HashSet<TriConcept<T>> neighborhood = new HashSet<TriConcept<T>>();
				neighborhood.add(startVertice);
				neighborhood.add(endVertice);
				neighborhoods.add(neighborhood);
			}
		}

		/*
		 * now we might need several merge steps
		 */
		boolean changed = true;
		while (changed) {
			changed = false;
			final HashSet<HashSet<TriConcept<T>>> merged = merge(neighborhoods);
			System.out.println("|merged| = " + merged.size() + ", |neighborhoods| = " + neighborhoods.size());

			if (merged.size() < neighborhoods.size()) {
				neighborhoods = merged;
				changed = true;
			}
		}

		return neighborhoods;
	}

	/**
	 * @param neighborhoods
	 * @return
	 */
	private HashSet<HashSet<TriConcept<T>>> merge(final HashSet<HashSet<TriConcept<T>>> neighborhoods) {
		final Iterator<HashSet<TriConcept<T>>> iterator = neighborhoods.iterator();
		HashSet<TriConcept<T>> set = null;
		HashSet<TriConcept<T>> set2 = null;

		boolean merge = false;

		a: while (iterator.hasNext()) {
			set = iterator.next();
			System.out.println(set);

			final Iterator<HashSet<TriConcept<T>>> iterator2 = neighborhoods.iterator();
			while (iterator2.hasNext()) {
				set2 = iterator2.next();
				System.out.println("  " + set2);

				if (set.equals(set2)) continue;
				if (disjoint(set, set2)) continue;
				/*
				 * merge
				 */
				System.out.println("    merging " + neighborhoods.size() + " / " + set.size() + " / " + set2.size());
				merge = true;
				break a;
			}
		}
		if (merge) {
			final HashSet<HashSet<TriConcept<T>>> result = new HashSet<HashSet<TriConcept<T>>>();
			/*
			 * copy neighborhoods into result
			 * (since removing them from neighborhoods did not work ...)
			 */
			for (final HashSet<TriConcept<T>> neighborhood : neighborhoods) {
				if (neighborhood.equals(set) || neighborhood.equals(set2)) continue;
				result.add(neighborhood);
			}
			result.add(set);
			return result;
		} 
		return neighborhoods;
	}

	private boolean remove(final HashSet<HashSet<TriConcept<T>>> neighborhoods, final HashSet<TriConcept<T>> set) {
		final Iterator<HashSet<TriConcept<T>>> iterator = neighborhoods.iterator();
		while (iterator.hasNext()) {
			final Set<TriConcept<T>> set2 = iterator.next();
			if (set.equals(set2)) {
				System.out.println("pre:  " + neighborhoods.size());
				iterator.remove();
				System.out.println("post:  " + neighborhoods.size());
				return true;
			}
		}
		return false;
	}

	private boolean disjoint(final Set<TriConcept<T>> a, final Set<TriConcept<T>> b) {
		for (final TriConcept<T> triConcept : b) {
			if (a.contains(triConcept)) return false;
		}
		return true;
	}

}

