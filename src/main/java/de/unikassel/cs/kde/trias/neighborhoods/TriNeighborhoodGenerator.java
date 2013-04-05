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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unikassel.cs.kde.trias.model.Graph;
import de.unikassel.cs.kde.trias.model.GraphEdge;
import de.unikassel.cs.kde.trias.model.TriConcept;
import de.unikassel.cs.kde.trias.model.TriConceptArrayComparator;

/**
 * Generates the neighborhood graph for a given list of triconcepts.
 *  
 * @author:  rja
 * @version: $Id: TriNeighborhoodGenerator.java,v 1.4 2009-08-05 13:39:46 rja Exp $
 * $Author: rja $
 * 
 */
public class TriNeighborhoodGenerator<T extends Comparable<T>> {

	/**
	 * FIXME: re-use from elsewhere! 
	 */
	private static final int DIMS = 3;


	private static final Logger log = Logger.getLogger(TriNeighborhoodGenerator.class);

	private TriConcept<T>[] concepts;

	/**
	 * This can be used to prune concepts. Only concepts which have at least 
	 * that size, will be regarded.  
	 * 
	 */
	private int thresholds[];

	private T ignoreExtent[];
	private T ignoreIntent[];
	private T ignoreModus[];

	private Integer[][] sortings;




	public TriNeighborhoodGenerator() {
		super();
	}

	public TriNeighborhoodGenerator(final TriConcept<T>[] concepts, final int[] thresholds) {
		super();
		this.concepts = concepts;
		this.thresholds = thresholds;
	}

	/** Computes the neighborhood graph.
	 * 
	 * @return The vertices of the neighborhood graph.
	 */
	public Graph<TriConcept<T>> getNeighborhoodGraph() {
		/*
		 * result graph
		 */
		final Graph<TriConcept<T>> graph = new Graph<TriConcept<T>>();
		/*
		 * maps to each neighborhood-id the set of neighbors (triconcepts) belonging to 
		 * that neighborhood  
		 */
		final Map<Integer, Set<TriConcept<T>>> inverseNeighborMap = new HashMap<Integer, Set<TriConcept<T>>>();
		/*
		 * maps to each triconcept its current neighborhood-ID
		 */
		final Map<TriConcept<T>, Integer> neighborMap = new HashMap<TriConcept<T>, Integer>();
		/*
		 * get sortings for all three dimensions
		 */
		log.info("Sorting " + concepts.length + " concepts");
		sortings = getSortings(concepts);
		/*
		 * merge tri-concepts into neighborhoods, which have the same extent
		 */
		log.info("Merging by extent");
		mergeFirstDim(inverseNeighborMap, neighborMap, graph);
		log.info("Merging by intent");
		mergeDim(inverseNeighborMap, neighborMap, graph, 1);
		log.info("Merging by modus");
		mergeDim(inverseNeighborMap, neighborMap, graph, 2);

		log.info(neighborMap.size() + " elements in neighbor map");
		
		return graph;
	}


	/** Returns for permutations which represent an order over extent, intent,
	 * and modus of the given tri-concepts.
	 * 
	 * @param triconcepts
	 * @return
	 */
	private Integer[][] getSortings(final TriConcept<T>[] triconcepts) {
		final Integer[][] order = new Integer[DIMS][triconcepts.length];
		for (int dim = 0; dim < order.length; dim++) {
			/*
			 * initialize array with identity permutation
			 */
			for (int j = 0; j < triconcepts.length; j++) {
				order[dim][j] = j; 
			}
			/*
			 * get sorted permutation
			 */
			Arrays.sort(order[dim], new TriConceptArrayComparator<T>(triconcepts, dim));
		}
		return order;
	}


	private void mergeFirstDim(final Map<Integer, Set<TriConcept<T>>> inverseNeighborMap, final Map<TriConcept<T>, Integer> neighborMap, final Graph<TriConcept<T>> graph) {
		final int currDim = 0;
		/*
		 * current ID of neighborhood
		 */
		int neighborId = 0;

		TriConcept<T> lastTriConcept = null; 
		/*
		 * iterate over tri-concepts ordered by currDim
		 */
		for (int j=0; j < concepts.length; j++) {
			final TriConcept<T> triConcept = concepts[sortings[currDim][j]];
			/*
			 * some concepts don't fulfill the given thresholds or contain
			 * an item which should be ignored ...
			 */
			if (!isValidConcept(triConcept)) {
				log.info("ignoring concept " + triConcept);
				continue;
			}

			if (checkCondition(currDim, lastTriConcept, triConcept)) {
				/*
				 * first component has changed -> new neighborhood ID
				 */
				neighborId++;
				inverseNeighborMap.put(neighborId, new HashSet<TriConcept<T>>());
				lastTriConcept = triConcept;
				/*
				 * add vertice to graph (otherwise we miss singletons, i.e., 
				 * non-connected vertices)
				 */
				graph.addVertice(triConcept);
			} else {
				/*
				 * firstComponent has not changed -> add edge to graph
				 */
				graph.addEdge(new GraphEdge<TriConcept<T>>(lastTriConcept, triConcept));
				lastTriConcept = triConcept;
			}
			/*
			 * assign neighborID to triconcept
			 */
			neighborMap.put(triConcept, neighborId);
			/*
			 * add triconcept to neighboorhood
			 */
			inverseNeighborMap.get(neighborId).add(triConcept);
		}
	}


	private void mergeDim(final Map<Integer, Set<TriConcept<T>>> inverseNeighborMap, final Map<TriConcept<T>, Integer> neighborMap, final Graph<TriConcept<T>> graph, int currDim) {

		int neighborId = 0;
		int currNeighborId = 0;
		TriConcept<T> lastTriConcept = null;

		/*
		 * iterate over tri-concepts ordered by currDim
		 */			
		for (int j=0; j < concepts.length; j++) {
			final TriConcept<T> triConcept = concepts[sortings[currDim][j]];
			/*
			 * some concepts don't fulfill the given thresholds or contain
			 * an item which should be ignored ...
			 */
			if (!isValidConcept(triConcept)) continue;


			if (checkCondition(currDim, lastTriConcept, triConcept)) {
				/*
				 * second component has changed -> get neighborhood ID
				 */
				neighborId = neighborMap.get(triConcept);
				lastTriConcept = triConcept;
				/*
				 * add vertice to graph (otherwise we miss singletons, i.e., 
				 * non-connected vertices)
				 */
				graph.addVertice(triConcept);
			} else {					
				/*
				 * firstComponent has not changed -> add edge to graph
				 */
				graph.addEdge(new GraphEdge<TriConcept<T>>(lastTriConcept,triConcept));
				lastTriConcept = triConcept;
			}

			currNeighborId = neighborMap.get(triConcept);
			if (currNeighborId != neighborId) {
				/*
				 * join neighborhoods currNeighborId and neighborId
				 */
				// get members of second neighborhood
				final Set<TriConcept<T>> sndNeighborhood = inverseNeighborMap.get(currNeighborId);
				// update their neighborhood to neighborId
				for (final TriConcept<T> neighbor:sndNeighborhood) {
					neighborMap.put(neighbor, neighborId);
				}
				// change inverse mapping by adding set to neighborId-Neighboorhood
				inverseNeighborMap.remove(currNeighborId);
				inverseNeighborMap.get(neighborId).addAll(sndNeighborhood);
			}
		}
	}

	/**
	 * Check whether we have found a new concept which we can use for the graph
	 * 
	 * @param dim
	 * @param lastTriConcept
	 * @param triConcept
	 * @param thresholds
	 * @return
	 */
	private boolean checkCondition(final int dim, final TriConcept<T> lastTriConcept, final TriConcept<T> triConcept) {
		/*
		 * last concept == null 
		 * 
		 * or
		 * 
		 * last concept[dim] != current concept[dim]
		 * 
		 */
		return lastTriConcept == null || !Arrays.equals(triConcept.getDim(dim),lastTriConcept.getDim(dim));
	}

	/**
	 * 
	 * 
	 * @param triConcept
	 * @return
	 */
	private boolean isValidConcept(final TriConcept<T> triConcept) {
		/*
		 * 
		 */
		return 	
		isIntersectionEmpty(triConcept.getExtent(), ignoreExtent) &&
		isIntersectionEmpty(triConcept.getIntent(), ignoreIntent) && 
		isIntersectionEmpty(triConcept.getModus(),  ignoreModus) &&
		triConcept.getDim(0).length >= thresholds[0] &&
		triConcept.getDim(1).length >= thresholds[1] &&
		triConcept.getDim(2).length >= thresholds[2];
	}

	/**
	 * Checks, if a and b have at least one element in common.
	 * TODO: stupid implementation using two loops ... 
	 * 
	 * @param aSet
	 * @param bSet
	 * @return
	 */
	private boolean isIntersectionEmpty (final T[] aSet, final T[] bSet) {
		if (aSet == null || bSet == null) return true;
		for (final T a: aSet) {
			for (final T b: bSet) {
				if (a.equals(b)) return false;
			}
		}
		return true;
	}


	public TriConcept<T>[] getConcepts() {
		return concepts;
	}

	public void setConcepts(TriConcept<T>[] concepts) {
		this.concepts = concepts;
	}

	public int[] getThresholds() {
		return thresholds;
	}

	/**
	 * Only concepts which are larger or equal to threshold are regarded for the
	 * graph. 
	 * 
	 * @param thresholds
	 */
	public void setThresholds(int[] thresholds) {
		this.thresholds = thresholds;
	}


	public void setIgnoreItems(final T ignoreExtent[], final T ignoreIntent[], final T ignoreModus[]) {
		this.ignoreExtent = ignoreExtent;
		this.ignoreIntent = ignoreIntent;
		this.ignoreModus = ignoreModus;
		log.info("ignore sizes: " + ignoreExtent.length + ", " + ignoreIntent.length + ", " + ignoreModus.length);
		log.info("ignore.extent: " + Arrays.toString(ignoreExtent));
		log.info("ignore.intent: " + Arrays.toString(ignoreIntent));
		log.info("ignore.modus: " + Arrays.toString(ignoreModus));
	}

}