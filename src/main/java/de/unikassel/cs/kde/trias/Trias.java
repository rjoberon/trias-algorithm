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

package de.unikassel.cs.kde.trias;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.unikassel.cs.kde.trias.io.TriasWriter;
import de.unikassel.cs.kde.trias.progress.DummyProgressLogger;
import de.unikassel.cs.kde.trias.progress.ProgressLogger;
import de.unikassel.cs.kde.trias.progress.ProgressStep;
import de.unikassel.cs.kde.trias.util.Dimension;
import de.unikassel.cs.kde.trias.util.TriasConfigurator;

/**
 * @author rja
 * @version: $Id: Trias.java,v 1.10 2011-12-08 15:59:45 rja Exp $
 * $Author: rja $
 * 
 */
public class Trias {

	/*
	 * all non fixed-size arrays store in their first row their actual size (except multidimensional arrays)
	 */

	private static Logger log = Logger.getLogger(Trias.class);

	private final static int U = Dimension.U.intValue();
	private final static int T = Dimension.T.intValue();
	private final static int R = Dimension.R.intValue();

	/*
	 * variables which must be configured (injected) externally
	 */
	private int[] numberOfItemsPerDimension; // number of items for each dimension
	private int[] minSupportPerDimension;    // minimal support for each dimension
	private TriasWriter triConceptWriter;    // writes the results

	private ProgressLogger pl = new DummyProgressLogger();

	/*
	 * the triples Trias is working on
	 */
	private int[][] itemList; // the triples of the context (i.e. Y)

	/*
	 * data variables
	 */
	private Integer[] uOrder;
	private int[] uOffsets;
	private int[] uRemember;

	private Integer[] trOrder;
	private int[] trOffsets;
	private int[] trRemember;

	/*
	 * comparators
	 */
	private final TriasComparator uComparator  = new OneDimensionComparator(Dimension.U);   // compares U
	private final TriasComparator tComparator  = new OneDimensionComparator(Dimension.T);   // compares T
	private final TriasComparator rComparator  = new OneDimensionComparator(Dimension.R);   // compares R
	private final TriasComparator trComparator = new TwoDimensionComparator(Dimension.T,Dimension.R); // compares TxR


	public Trias() {
		super();
	}

	/** Constructor which calls {@link TriasConfigurator#configureTrias(Trias)}.
	 * @param config
	 */
	public Trias(final TriasConfigurator config) {
		super();
		config.configureTrias(this);
	}

	/**
	 * input file format:
	 * x x x 
	 * x x x
	 * ...
	 * x x x 
	 * 
	 * with
	 *   x ... data
	 */
	public void doWork() throws IOException {
		/* ************************************************************************
		 * Initialize permutations
		 * Those permutations are used to access utrListe in sorted order.
		 */
		initializePermutations();

		/* ************************************************************************
		 * sort by U and TxR
		 */
		sortByUandTxR();

		/* ************************************************************************
		 * build appropriate offset tables
		 */
		buildOffsetTables();

		/* ************************************************************************
		 * run actual computation
		 */
		trias();

		triConceptWriter.close();
	}


	private void trias() throws IOException {

		/* ************************************************************************
		 * outer Next Closure
		 */

		log.debug("-------------- NEXT CLOSURE -------------- " + 
				minSupportPerDimension[U] + " " + 
				minSupportPerDimension[T] + " " + 
				minSupportPerDimension[R]);


		/*
		 * The outer next closure computes concepts (A,I) in (U, TxR, \tilde{Y}).
		 * 
		 * Extent of a triconcept. Both sets are, in general, oversized.
		 */
		int[] extent      = new int[numberOfItemsPerDimension[U] + 1]; // A
		int[] outerIntent = new int[numberOfItemsPerDimension[U] + 1]; // I

		int[] aPlusI; // A + i (stored values are positions in utrListe!)


		extent[0] = 0;          // we start with the empty set
		int i = uOrder[uOffsets[numberOfItemsPerDimension[U]]]; // we start with the largest user (pick one triple position of largest user) 
		pl.setMax(numberOfItemsPerDimension[U]);

		/*
		 *  Compute the hull of the empty set.
		 */
		log.debug("computing the hull of the empty set");
		pl.logStep(ProgressStep.START);
		if (minSupportPerDimension[U] == 0 && trOffsets.length < numberOfItemsPerDimension[T] * numberOfItemsPerDimension[R]) {
			// special case for tri-concept ({}, T, R)
			writeTriples(new int[0][0], extent, new int[]{numberOfItemsPerDimension[T]}, new int[]{numberOfItemsPerDimension[R]});
		}
		outerIntent = prime(itemList, uOrder, uOffsets, trOrder, trOffsets, uRemember, extent, trComparator);      // {}'
		extent      = prime(itemList, trOrder, trOffsets, uOrder, uOffsets, trRemember, outerIntent, uComparator); // {}''

//		System.err.println("A'' = " + getUSetFromArray(itemList, extent) + "   A' = " + getTrSetFromArray(itemList, outerIntent));

		if (extent[0] >= minSupportPerDimension[U]) {
			innerNextClosure(extent, outerIntent);
		}



		/*   
		 * stop, when extent contains all elements (uOffsets[0]) or when i does not change any more
		 */
		log.debug("starting outer next closure loop");
		/*
		 * Used to track changes of i. If i did not change, then i = lastI at the 
		 * beginning of the next loop.
		 */
		int lastI = i + 1; // TODO: workaround to terminate
		while (extent[0] < uOffsets[0] && lastI != i) {
			pl.logStep(ProgressStep.OUTER);

			//log.debug("i = " + uRemember[i] + ", lastI = " + uRemember[lastI]);
			lastI = i;

			/*
			 * for given A and i, calculate A+i
			 */
			// calculate A+i
			aPlusI = aPlusI(uOrder, trOrder, itemList, U, uOffsets, trOffsets, uRemember, trRemember, trComparator, uComparator, extent, i);
			//			pl.logExtent(itemList[extent[1]][U]);
			pl.logExtent(itemList[aPlusI[1]][U]);

			// calculate (A+i)'
			outerIntent = prime(itemList, uOrder, uOffsets, trOrder, trOffsets, uRemember, aPlusI, trComparator);

			/*
			 * check minsupport for product (TODO: do this on projections!)
			 */
			if (outerIntent[0] >= minSupportPerDimension[T] * minSupportPerDimension[R]) {
				// calculate A'' 
				aPlusI = prime(itemList, trOrder, trOffsets, uOrder, uOffsets, trRemember, outerIntent, uComparator);

				// check, if i is smallest NEW element in A+i
				if (aLtI (itemList, U, extent, aPlusI, i)) {

					// yes, i is smallest new element in A+i
					// new hull found!
					extent = aPlusI;

					/*
					 * check minsupport for u
					 */
					if (extent[0] >= minSupportPerDimension[U]) {
						pl.logStep(ProgressStep.OUTER_SUCCESS);

						/*
						 * inner NEXT CLOSURE
						 */
						if (log.isDebugEnabled()) log.debug("starting inner next closure with extent " + toString(extent));
						innerNextClosure(extent, outerIntent);

					} // uminsup if 
					i = uOrder[uOffsets[numberOfItemsPerDimension[U]]]; // re-start with largest element
					log.debug("starting again with largest element: i = " + uRemember[i]);
				} // aLti if
				else {
					if (uRemember[i] > 1) i = uOrder[uOffsets[uRemember[i] - 1]]; // decrement i
				}
			} // tminsup*rminsup if
			else {
				if (uRemember[i] > 1) i = uOrder[uOffsets[uRemember[i] - 1]]; // decrement i
			}
			// decrement i until it is not any longer contained in aSet
			i = getNextI(itemList, uOrder, uOffsets, uRemember, U, extent, i);
		} // outer next closure loop
		pl.logStep(ProgressStep.STOP);
	}

	/** Inner next closure loop.
	 * 
	 * @param extent
	 * @param relationI
	 * @throws IOException
	 */
	private void innerNextClosure(int[] extent, int[] relationI) throws IOException {
		if (log.isDebugEnabled()) log.debug("inner next closure for concept (" + toString(extent) + ", " + relationToString(relationI) + ")");
		/* *******************************************************************************
		 * pre-processing
		 */
		// map extent to something similar like utrListe TODO: minimize size of arrays!
		final int[][] trListe  = new int[relationI[0]][3];   // stores context as list 
		final Integer[] tOrder = new Integer[relationI[0]];  // sorting by T
		final Integer[] rOrder = new Integer[relationI[0]];  // sorting by R
		final TreeSet<Integer> tSet = new TreeSet<Integer>();
		final TreeSet<Integer> rSet = new TreeSet<Integer>();
		final Map<Integer,Integer> tMap = new HashMap<Integer,Integer>();
		final Map<Integer,Integer> rMap = new HashMap<Integer,Integer>();
		final Map<Tupel, Integer> tupelMap = new HashMap<Tupel, Integer>();

		// put all t and r into set
		for (int j = 1; j <= relationI[0]; j++) {
			tSet.add(itemList[relationI[j]][T]);
			rSet.add(itemList[relationI[j]][R]);
		}

		/*
		 * re-number all t and r
		 */
		// re-number all t
		int tCtr = 1;
		for (int t:tSet) {
			tMap.put(t, tCtr++);
		}
		// re-number all r
		int rCtr = 1;
		for (int r:rSet) {
			rMap.put(r, rCtr++);
		}

		for (int j = 1; j <= relationI[0]; j++) {
			int t = tMap.get(itemList[relationI[j]][T]);
			int r = rMap.get(itemList[relationI[j]][R]);
			tupelMap.put(new Tupel(t, r), relationI[j]); /* to build BxC later, we need to get the position in utrListe
				this could also be done by search first for t and then for r in trListe - because trListe contains position in
				utrListe in first column */
			trListe[j-1][0] = relationI[j]; // remember position in utrListe
			trListe[j-1][T] = t;          // j-1 because trListe starts with zero
			trListe[j-1][R] = r;          
			tOrder[j-1] = j-1;
			rOrder[j-1] = j-1;
		}
		tCtr--; // then tCtr contains number of t's
		rCtr--; // then rCtr contains number of r's

		// sort by T
		final int[] tPermutation = {T,R,0};
		Arrays.sort(tOrder, new IntArrayComparator(trListe, tPermutation));
		// build offset table
		final int[] tOffsets  = new int[tCtr + 2]; // first element to store actual size, last element to store max. offset
		final int[] tRemember = new int[trListe.length];  
		{
			int tOff = 0; // position in tOrder list
			for (int t = 1; t <= tCtr; t++) {
				tOffsets[t] = tOff;
				while (trListe[tOrder[tOff]][T] == t && tOff < trListe.length - 1) {
					// skip equal elements
					tOff++;
				}
			}
			tOffsets [tCtr + 1] = ++tOff; // set pointer after last element
			tOffsets [0] = tCtr; // remember size
			// build tRemember ( = copy of T column)
			for (int j = 0; j < trListe.length; j++) {
				tRemember[j] = trListe[j][T];
			}
		}

		// sort by R 
		int[] rPermutation = {R,T,0};
		Arrays.sort(rOrder, new IntArrayComparator(trListe, rPermutation));
		// build offset table
		int[] rOffsets  = new int[rCtr + 2]; // first element to store actual size, last element to store max. offset
		int[] rRemember = new int[trListe.length];  
		{
			int rOff = 0; // position in tOrder list
			for (int r = 1; r <= rCtr; r++) {
				rOffsets[r] = rOff;
				while (trListe[rOrder[rOff]][R] == r && rOff < trListe.length - 1) {
					// skip equal elements
					rOff++;
				}
			}
			rOffsets [rCtr + 1] = ++rOff; // set pointer after last element
			rOffsets [0] = rCtr; // remember size
			// build rRemember ( = copy of R column)
			for (int j = 0; j < trListe.length; j++) {
				rRemember[j] = trListe[j][R];
			}
		}

		log.debug("inner next closure finished preprocessing, really starts now");
		/* ********************************************************************************
		 * inner next closure starts here
		 */
		int[] intent = new int[numberOfItemsPerDimension[T] + 1];
		int[] aPlusI;
		int[] modus;
		final int off = tOffsets[tCtr];

		/*
		 * handle empty relation and complete extent 
		 * 
		 * TODO: check if this is always correct
		 */
		if (relationI[0] == 0 && extent[0] == numberOfItemsPerDimension[U]) {
			innerNextClosureForEmptyI(extent, tupelMap);
			return;
		}


		intent[0] = 0; // start with empty set

		// hull of the empty set
		modus = prime(trListe, tOrder, tOffsets, rOrder, rOffsets, tRemember, intent, rComparator);   // {}'
		if (modus[0] >= minSupportPerDimension[R]) { // FIXME, always true, because prime returns all elements on empty set!
			intent = prime (trListe, rOrder, rOffsets, tOrder, tOffsets, rRemember, modus, tComparator);  // {}''

			if (intent[0] >= minSupportPerDimension[T] && checkCondition(extent, intent, modus, trListe, tupelMap)) {
				if (log.isDebugEnabled()) log.debug("   empty set hull: " + toString(extent, intent, modus, trListe));
				writeTriples(trListe, extent, intent, modus);
			}
		}

		int j = tOrder[off]; // get largest tag
		int lastJ = j + 1;
		while (intent[0] < tOffsets[0] && lastJ != j) { // stop, if intent contains all elements from T (e.g. B == T)
			pl.logStep(ProgressStep.INNER);
			lastJ = j;
			log.debug("   j = " + tRemember[j] + ", lastJ = " + tRemember[lastJ]);

			// build next hull
			aPlusI = aPlusI (tOrder, rOrder, trListe, T, tOffsets, rOffsets, tRemember, rRemember, rComparator, tComparator, intent, j);
			// B' (= C)
			modus = prime(trListe, tOrder, tOffsets, rOrder, rOffsets, tRemember, aPlusI, rComparator);

			// check minsup for r
			if (modus[0] >= minSupportPerDimension[R]) {
				// B'' (= C')
				aPlusI = prime(trListe, rOrder, rOffsets, tOrder, tOffsets, rRemember, modus, tComparator);

				if (log.isDebugEnabled()) log.debug("   before aPlusI check: " + toString(extent, intent, modus, trListe));
				if (log.isDebugEnabled()) log.debug("   before aPlusI check: " + toString(extent, aPlusI, modus, trListe));


				if (aLtI (trListe, T, intent, aPlusI, j)) {
					// new hull found
					intent = aPlusI;

					// check minsup for t
					if (intent[0] >= minSupportPerDimension[T] && checkCondition(extent, intent, modus, trListe, tupelMap)) {

						pl.logStep(ProgressStep.INNER_SUCCESS);

						if (log.isDebugEnabled()) log.debug("   inner concept: (" + toString(intent, trListe, true) + ", " + toString(modus, trListe, false) + ")");

						if (log.isDebugEnabled()) log.debug("   outer concept: " + toString(extent, intent, modus, trListe));

						writeTriples(trListe, extent, intent, modus);
					}

					j = tOrder[off]; // re-start with largest element
				} else {
					if (tRemember[j] > 1) j = tOrder[tOffsets[tRemember[j] - 1]]; // decrement j

				}
			} else {
				if (tRemember[j] > 1) j = tOrder[tOffsets[tRemember[j] - 1]]; // decrement j

			}

			j = getNextI(trListe, tOrder, tOffsets, tRemember, T, intent, j);

		} // inner next closure loop 
	}

	/**
	 * Handles the case when the extent contains all elements and the outer 
	 * intent is empty. 
	 * 
	 * @param extent
	 * @param tupelMap
	 * @throws IOException
	 */
	private void innerNextClosureForEmptyI(int[] extent, final Map<Tupel, Integer> tupelMap) throws IOException {
		final int[] modus = new int[numberOfItemsPerDimension[R] + 1];
		final int[] intent = new int[numberOfItemsPerDimension[T] + 1];
		final int[][] emptyI = new int[][]{};
		// start with empty set
		intent[0] = 0;
		// hull contains then all elements
		modus[0] = numberOfItemsPerDimension[R];
		for (int r = 1; r <= numberOfItemsPerDimension[R]; r++) {
			modus[r] = r;
		}
		// check and print
		if (intent[0] >= minSupportPerDimension[T] && checkCondition(extent, intent, modus, emptyI, tupelMap)) {
			if (log.isDebugEnabled()) log.debug("   empty set hull: " + toString(extent, intent, modus, emptyI));
			writeTriples(emptyI, extent, intent, modus);
		}
		// now the same with the reverse: modus is empty ...
		modus[0] = 0;
		// and intent contains all elements
		intent[0] = numberOfItemsPerDimension[T];
		for (int t = 1; t <= numberOfItemsPerDimension[T]; t++) {
			intent[t] = t;
		}
		// check and print
		if (modus[0] >= minSupportPerDimension[R] && checkCondition(extent, intent, modus, emptyI, tupelMap)) {
			if (log.isDebugEnabled()) log.debug("   empty set hull: " + toString(extent, intent, modus, emptyI));
			writeTriples(emptyI, extent, intent, modus);
		}
	}

	private void buildOffsetTables() {
		trOffsets  = new int[itemList.length+2]; // TODO: too big
		trRemember = new int[itemList.length]; // stores for each triple its position in trOffsets

		int trOff = 0;
		int trOffsetCtr  = 1;
		Map<Tupel,Integer> map = new HashMap<Tupel,Integer>(itemList.length);
		// iterate over all TAS
		while (trOff < itemList.length) {
			// new element
			int t = itemList[trOrder[trOff]][T];
			int r = itemList[trOrder[trOff]][R];
			map.put(new Tupel (t, r), trOffsetCtr);
			trOffsets[trOffsetCtr] = trOff;
			trOffsetCtr++;		
			while (trOff < itemList.length && 
					t == itemList[trOrder[trOff]][T] && 
					r == itemList[trOrder[trOff]][R]) {
				// skip equal elements
				trOff++;
			}

		}
		if (trOffsetCtr > itemList.length + 1) {
			log.fatal("trOffsetCtr should never be larger than N+1");
			throw new RuntimeException("trOffsetCtr should never be larger than N+1");
		}
		trOffsets[trOffsetCtr] = itemList.length;     // sets offset after last value
		trOffsets[0] = trOffsetCtr - 1; 
		// iterate over triples, store for each triple its position in trRemember
		for (int i = 0; i < itemList.length; i++) {
			trRemember[i] = map.get(new Tupel (itemList[i][T], itemList[i][R]));
		}

	}

	private void sortByUandTxR() {
		/* ************************************************************************
		 * sort by U
		 */
		int[] uPermutation = {U,T,R};
		Arrays.sort(uOrder, new IntArrayComparator(itemList, uPermutation));

		/* ******************************
		 * build appropriate offset table
		 */
		uOffsets  = new int[numberOfItemsPerDimension[0]+2]; // first element to store actual size, last element to store max. offset
		/*
		 * stores for each triple, where its position in uOffsets is 
		 * (since it's always u, this is the U column of the TAS table 
		 * ... TODO: uRemember not neccessary)
		 */
		uRemember = new int[itemList.length]; 

		int uOff = 0; // position in uOrder list
		for (int u = 1; u <= numberOfItemsPerDimension[U]; u++) {
			uOffsets[u] = uOff;
			while (itemList[uOrder[uOff]][U] == u && uOff < itemList.length - 1) { // TODO: a single user at the end might fail!
				// skip equal elements
				uOff++;
			}
		}
		uOffsets [numberOfItemsPerDimension[U] + 1] = ++uOff; // set pointer after last element
		uOffsets [0] = numberOfItemsPerDimension[U]; // remember size
		// build uRemember ( = copy of U column)
		for (int i = 0; i < itemList.length; i++) {
			uRemember[i] = itemList[i][U];
		}


		/* ************************************************************************
		 * sort by TxR 
		 */
		int[] trPermutation = {T,R,U};
		Arrays.sort(trOrder, new IntArrayComparator(itemList, trPermutation));

		log.debug("sorted by U and by TxR");
	}

	private void initializePermutations() {
		uOrder  = new Integer[itemList.length];
		trOrder = new Integer[itemList.length];
		for (int i = 0; i < itemList.length; i++) {
			uOrder[i]  = i; 
			trOrder[i] = i; 
		}
		log.debug("initialized permutations");
	}





	/**
	 * Checks, if the central condition A = (BxC)^{\tilde{Y}} is fulfilled.
	 */
	private boolean checkCondition(final int[] extent, final int[] intent, final int[] modus, final int[][] trListe, Map<Tupel, Integer> tupelMap) throws IOException {
		int[] setBxC      = getBxC(trListe, tupelMap, intent, modus);
		int[] setBxCPrime = prime(itemList, trOrder, trOffsets, uOrder, uOffsets, trRemember, setBxC, uComparator); 

		boolean isAContainedInBxC = isContainedIn(itemList, extent, setBxCPrime, U); // trivially holds
		boolean isBxCContainedInA = isContainedIn(itemList, setBxCPrime, extent, U); // to check

		if (isBxCContainedInA) {
			if (! isAContainedInBxC) { 
				log.fatal("################ GROSSER FEHLER!");
				throw new RuntimeException("################ GROSSER FEHLER!");
			}
			return true;
		}
		return false;
	}

	/* return a String representation of a triadic concept
	 *  
	 * note that this depends entirely on the correct structure of utrListe and trListe
	 */
	private void writeTriples(final int[][] trListe, final int[] extent, final int[] intent, final int[] modus) throws IOException {
		log.debug("found concept " + toString(extent, intent, modus, trListe));

		final int[] mappedExtent = new int[extent[0]]; for (int k=1; k<=extent[0]; k++) mappedExtent[k-1] = itemList[extent[k]][U];
		final int[] mappedIntent = new int[intent[0]]; for (int k=1; k<=intent[0]; k++) mappedIntent[k-1] = trListe.length != 0 ? itemList[trListe[intent[k]][0]][T] : k; 
		final int[] mappedModus  = new int[modus[0]];  for (int k=1; k<=modus[0];  k++) mappedModus[k-1]  = trListe.length != 0 ? itemList[trListe[modus[k]][0]][R] : k;
		triConceptWriter.write(new int[][] {mappedExtent, mappedIntent, mappedModus});
	}

	private String toString (int[] extent, int[] intent, int[] modus, int[][] trListe) {
		final StringBuffer buf = new StringBuffer ("({");

		for (int k=1; k<=extent[0]; k++)  {
			buf.append(itemList[extent[k]][U]);
			if (k < extent[0]) buf.append(", ");
		}
		buf.append("}, {");

		for (int k=1; k<=intent[0]; k++)  {
			buf.append(trListe.length != 0 ? itemList[trListe[intent[k]][0]][T] : k);
			if (k < intent[0]) buf.append(", ");
		}
		buf.append("}, {");

		for (int k=1; k<=modus[0]; k++)  {
			buf.append(trListe.length != 0 ? itemList[trListe[modus[k]][0]][R] : k);
			if (k < modus[0]) buf.append(", ");
		}
		buf.append("})");

		return buf.toString();

	}


	private String relationToString(int[] relationI) {
		final StringBuffer buf = new StringBuffer("{");

		for (int j = 1; j <= relationI[0]; j++) {
			buf.append("(" + itemList[relationI[j]][T] + "," + itemList[relationI[j]][R] + ")");
			if (j < relationI[0]) buf.append(", ");
		}

		buf.append("}");
		return buf.toString();
	}

	/**
	 * @param intentOrModus
	 * @param trListe
	 * @param isIntent - <code>true</code>, if intent given, <code>false</code>
	 * if modus given
	 * @return
	 */
	private String toString(int[] intentOrModus, int[][] trListe, boolean isIntent) {
		final int column = isIntent ? T : R;
		final StringBuffer buf = new StringBuffer ("{");
		for (int k=1; k<=intentOrModus[0]; k++)  {
			buf.append(itemList[trListe[intentOrModus[k]][0]][column]);
			if (k < intentOrModus[0]) buf.append(", ");
		}
		buf.append("}");
		return buf.toString();
	}

	private String toString (int[] extent) {
		final StringBuffer buf = new StringBuffer("{");
		for (int i = 1; i <= extent[0]; i++) {
			buf.append(itemList[extent[i]][U]);
			if (i < extent[0]) {
				buf.append(", ");
			}
		}
		return buf.append("}").toString();
	}

	/* builds the cross product of the two sets b and c 
	 * 
	 * note that this depends entirely on the correct structure of trListe and tupelMap
	 */
	private int[] getBxC(int[][] trListe, Map<Tupel, Integer> tupelMap, int[] bSet, int[] cSet) {
		// B x C bauen (also: bSet x bUmfang)
		int[] setBxC = new int[bSet[0] * cSet[0] + 1];
		setBxC[0]    = bSet[0] * cSet[0]; // set size
		// TODO: this loop could (but not should!) be rewritten using only bTimesCCtr and div/mod for getting tt and rr 
		int bTimesCCtr = 1;
		for (int tt = 1; tt <= bSet[0]; tt++) {
			for (int rr = 1; rr <= cSet[0]; rr++) {
				setBxC[bTimesCCtr++] = tupelMap.get(new Tupel(trListe[bSet[tt]][T], trListe[cSet[rr]][R]));
			}
		}
		return setBxC;
	}

	/* checks if a is contained in b
	 * 
	 * a and b contain rows of utrListe, column says on which column we should do the comparison
	 */
	private boolean isContainedIn(int[][] utrListe, int[] aSet, int[] bSet, int column) {
		// check, if aSet is contained in bSet ... TODO: do this with binary search (or a better method - i.e. merging (are a and b sorted?)
		boolean aContainedInB = true;
		for (int k = 1; k <= aSet[0] && aContainedInB; k++) {
			aContainedInB = false;
			for (int l = 1; l <= bSet[0]; l++) {
				if (utrListe[aSet[k]][column] == utrListe[bSet[l]][column]) {
					aContainedInB = true; 
				}
			}
		}
		return aContainedInB;
	}


	/* returns true, if i is the smallest element (and is contained in b), in which the (ordered) sets a and b differ
	 * 
	 */


	/*
	 * konkret: 
	 * 			if (aLtI (trListe, T, intent, aPlusI, j)) {
	 */
	private boolean aLtI (int[][] utrListe, int column, int[] a, int[] b, int i) {
		int j = 1;
		// skip equal elements (TODO: this works faster with binary search!)
		while (j <= a[0] && j <= b[0] && utrListe[a[j]][column] == utrListe[b[j]][column]) {j++;}

		/*
		 * now the following cases are possible :
		 *   - j > a[0] (thus, end of a reached) and j <= b[0] (thus end of b NOT reached) --> if i = b[j], then TRUE
		 *   - b[j] < a[j] and b[j] = i --> TRUE
		 *   - otherwise: FALSE
		 */
		return ((j <= b[0]) && 
				(utrListe[b[j]][column] == utrListe[i][column]) &&
				(j > a[0] || utrListe[b[j]][column] < utrListe[a[j]][column])); 
	}

	/*
	 * searches for i in the set (which is sorted by utrListe)
	 */
	private int getNextI(int[][] utrListe, Integer[] uOrder, int[] uOffsets, int[] uRemember, int column, int[] set, int i) {
		/*
		 * we might have to decrement i further, if neccessary
		 */
		while (uRemember[i] > 1 &&                           // as long as i is larger than 1, make i smaller
				set[0] < uOffsets[0] &&                       // as long as set contains not all possible elements, make i smaller
				isContainedIn(i, set, utrListe, column)) {    // as long as i is contained in set, make i smaller

			// if set is full, i is always contained ... stop, otherwise infinite loop!
			i = uOrder[uOffsets[uRemember[i] - 1]]; // decrement i
		}
		return i;
	}

	/* returns true, if i is in the set  
	 * set[0] = |set|
	 * we only look on column in set
	 */
	private boolean isContainedIn(int i, int[] set, int[][] liste, int column ) {
		// do binary search in menge
		int l = 1;
		int u = set[0];
		int m = 0;      // mid
		while (u >= l) {
			m = (l + u) / 2; // Java rounds to zero, i.e. downwards, if l and u are positive
			if (liste[i][column] < liste[set[m]][column]) {
				// search lower half; 
				u = m - 1;
			} else if(liste[i][column] > liste[set[m]][column]) {
				// search upper half
				l = m + 1;
			} else {
				// found!
				return true;
			}
		}
		// not found
		return false;
	}


	// computes A+i according to definition of NEXT CLOSURE (WITHOUT doing the hull!)
	private int[] aPlusI(Integer[] uOrder, Integer[] trOrder, int[][] liste, int column, int[] uOffsets, int[] trOffsets, int[] uRemember, int[] trRemember, TriasComparator trComparator, TriasComparator uComparator, int[] aSet, int i) {
		int[] menge = new int[aSet[0] + 2];
		menge[0]    = 0; // initialize element-count

		// intersect with {1,2,...,i-1}
		for (int j=1; j<=aSet[0]; j++) { // TODO: this can be further restricted!?
			// copy those elements of A, which are smaller than i
			if (liste[aSet[j]][column] < liste[i][column]) {
				// copy
				menge[j] = aSet[j]; 
				menge[0] = j; // remember number of elements
			} else {
				break;
			}
		}
		menge[0]++;          // increase number of elements
		menge[menge[0]] = i; // add i

		return menge;

		/*
		 // die Hülle berechnen TODO: aussen brauchen wir temp ... wird dort derzeit nochmals berechnet
		  int [] temp = einStrich(liste, uOrder, uOffsets, trOrder, trOffsets, uRemember, menge, trComparator);
		  temp = einStrich (liste, trOrder, trOffsets, uOrder, uOffsets, trRemember, temp, uComparator);	
		  return temp; */
	}

	// puts for every x \in menge the value of toBeSorted[x][U] in a set and returns this  
	private TreeSet<Integer> getUSetFromArray (int[][] toBeSorted, int[] menge) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		for (int i = 1; i <= menge[0]; i++) {
			result.add(toBeSorted[menge[i]][U]);
		}
		return result;
	}
	private TreeSet<Integer> getTSetFromArray (int[][] toBeSorted, int[] menge) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		for (int i = 1; i <= menge[0]; i++) {
			result.add(toBeSorted[menge[i]][T]);
		}
		return result;
	}
	private TreeSet<Integer> getRSetFromArray (int[][] toBeSorted, int[] menge) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		for (int i = 1; i <= menge[0]; i++) {
			result.add(toBeSorted[menge[i]][R]);
		}
		return result;
	}
	// puts for every x \in menge the value of (toBeSorted[x][T],toBeSorted[x][R]) in a set returns this
	private TreeSet<Tupel> getTrSetFromArray(int[][] toBeSorted, int[] menge) {
		TreeSet <Tupel> result = new TreeSet<Tupel>();
		for (int i = 1; i <= menge[0]; i++) {
			Tupel t = new Tupel (toBeSorted[menge[i]][T], toBeSorted[menge[i]][R]);
			result.add(t);
		}
		return result;
	}


	// interface for comparison of certain columns in a utrListe
	private interface TriasComparator {
		public int compare (int[] a, int[] b);
	}

	private class OneDimensionComparator implements TriasComparator {
		private final int dim;
		public OneDimensionComparator (final Dimension dim) {
			this.dim = dim.intValue();
		}
		public int compare (int[] a, int[] b) {
			return a[dim] - b[dim];
		}
	}


	/**
	 * Compares columns T and R  of the arrays a and b
	 * 
	 * @author rja
	 *
	 */
	private class TwoDimensionComparator implements TriasComparator {
		private final int dim1;
		private final int dim2;

		public TwoDimensionComparator (final Dimension dim1, final Dimension dim2) {
			this.dim1 = dim1.intValue();
			this.dim2 = dim2.intValue();
		}

		public int compare (int[] a, int[] b) {
			if (a[dim1] < b[dim1]) {
				return -1;
			} else if (a[dim1] > b[dim1]) {
				return +1;
			}
			return a[dim2] - b[dim2];
		}
	}


	/* 
	 * computes "first derivative" (' operator); for all A \subseteq U holds: (prime(prime(A)), prime(A)) is a concept
	 * 
	 * TODO: * Vorsortierung der Elemente von menge nach Größe (kleinste zu erst) um Schnitt zu beschleunigen
	 *       * leere Menge korrekt verarbeiten (gesamte Menge zurückgeben) oder Exception werfen?
	 */
	private int[] prime (final int[][] values, final Integer[] order, final int[] offsets, final Integer[] xOrder, final int[] xOffsets, final int[] remember, final int[] menge, final TriasComparator comparator) {


		if (menge[0] == 0) { // empty set --> return all elements
			int[] result = new int[xOffsets.length]; // hat die Größe von xOffsets
			for (int i = 1; i <= xOffsets[0]; i++) {
				result[i] = xOrder[xOffsets[i]]; 
			}
			result[0] = xOffsets[0];
			return result;
		} else {

			// TODO: preprocessing: order values in menge by offsets[i + 1] - offsets[i] (i.e. by size, smallest first, to speed up intersection)

			// order menge by size
			Integer[] mengeOrdered = orderBySize(menge, offsets, remember);
			
			// copy first column to result
			int u0 = menge[mengeOrdered[0]]; // smallest set
			int[] result = new int[offsets[remember[u0] + 1] - offsets[remember[u0]] + 1]; // mehr Elemente können es nicht werden
			result[0] = offsets[remember[u0] + 1] - offsets[remember[u0]]; // how many objects? // TODO: = result.length - 1
			for (int i = 0; i < result[0]; i++) {
				int l = order[offsets[remember[u0]] + i];
				result[i + 1] =  l; 
			}
			/*
			 * intersection of result with all other sets (offsets[])
			 */ 
			for (int i = 1; i < mengeOrdered.length; i++) {
				/*
				 * gute Schnittfunktion auswählen
				 */
				//final int rate = 2;
				//if (result[0] / (offsets[remember[menge[i]] + 1] - offsets[remember[menge[i]]]) < rate && 
				//	result[0] / (offsets[remember[menge[i]] + 1] - offsets[remember[menge[i]]]) > 1/rate) {
				// ungefähr gleich groß (0.5 < |A| / |B| < 2)
				s2intersection (result, values, order, offsets[remember[menge[mengeOrdered[i]]]], offsets[remember[menge[mengeOrdered[i]]] + 1] - 1, comparator);
				//} else {
				// unterschiedlich große Mengen: binäre Suche effektiver
				//	intersection (result, values, order, offsets[remember[menge[i]]], offsets[remember[menge[i]] + 1] - 1, comparator);
				//}
			}
			return result;
		}

	}



	private Integer[] orderBySize (int[] menge, int[] offsets, int[] remember) {
		// initialize order array to: 1..menge[0] 
		Integer[] order = new Integer[menge[0]];
		for (int i = 0; i < order.length; i++) {
			order[i] = i + 1;
		}
		Arrays.sort(order,new IntArraySizeComparator(menge, offsets, remember));
		return order;
	}

	/*
	 *  stores a tupel (a,b); contains methods equals, compareTo and hashCode
	 */
	private class Tupel implements Comparable<Tupel>{
		int a;
		int b;

		public Tupel (int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode () {
			return a*b;

		}

		@Override
		public boolean equals (Object o) {
			if (! (o instanceof Tupel)) {
				return false;
			}
			return equals((Tupel)o);
		}

		public boolean equals (Tupel other) {
			return this.a == other.a && this.b == other.b;
		}

		@Override
		public String toString () {
			return "(" + a + "," + b + ")";
		}

		public int compareTo (Tupel o) {
			if (o == null) { throw new NullPointerException(); }
			if (this.a < o.a) {
				return -1;
			} else if (this.a > o.a) {
				return +1;
			} 
			return this.b - o.b;
		}
	}

	/* fast set intersection (theoretically fast but complicated ... thus not used)
	 * 
	 */ 
	private void intersection (int[] result, int[][] values, Integer[] order, int startQ, int endQ, int startD, int endD) {
		/* Strategie:
		 * 
		 * die Mitte des kleineren Feldes nehmen und dort ein Element suchen, welches im anderen enthalten ist
		 * dieses dann festhalten und an dieser Stelle halbieren und verzweigen
		 * 
		 */
		if (endQ < startQ || endD < startD) {
			return;
		}
		// Felder tauschen, falls D kleiner als Q 
		// dadurch enthält das Endergebnis Indizes verschiedener User!
		if (endQ-startQ > endD-startD) {
			// endQ <-> endD
			int t = endQ;
			endQ = endD;
			endD = t;
			// startQ <-> startD
			t = startQ;
			startQ = startD;
			startD = t;
		}
		// Mitte von Q nehmen und in D suchen
		int midPosQ = startQ + (endQ - startQ)/2;

		//		System.out.println("searching for " + toString(values[order[midPosQ]]) + " in D");
		//		System.out.println("startD = " + startD + ", endD = " + endD);

		// do binary search
		int l = startD; // lower bound
		int u = endD;   // upper bound
		int i = 0;      // mid
		while (u >= l) {
			i = (l + u) / 2; // Java rounds to zero, i.e. downwards, if l and u are positive
			int comp = myComparator(values[order[midPosQ]], values[order[i]]); 
			if (comp < 0) {
				// search lower half; 
				u = i - 1;
			} else if(comp > 0) {
				// search upper half
				l = i + 1;
			} else {
				// gefunden!
				result[result[0]] = i;
				result[0] ++;
				break;
			}
		}
		if (u < l) {
			// if not found, K[u] < X < K[l] holds			
			//			System.out.println("not found, i = " + i);
			intersection(result, values, order, startQ, midPosQ - 1, startD, u);
			intersection(result, values, order, midPosQ + 1, endQ, l, endD);	
		} else {
			// found on pos i
			intersection(result, values, order, startQ, midPosQ - 1, startD, i - 1);
			intersection(result, values, order, midPosQ + 1, endQ, i + 1, endD);	
		}
	}
	// vergleicht die Spalten 1 und 2 der Felder a und b
	private int myComparator (int[] a, int[] b) {
		if (a[1] < b[1]) {
			return -1;
		} else if (a[1] > b[1]) {
			return +1;
		}
		return a[2] - b[2];
	}	


	/* Diese Routine berechnet den Schnitt zwischen der Menge result und der Menge, die von order[startB] bis order[endB] steht 
	 * Eingabe:
	 *   result: ein Feld, welches die Menge A enthält, diese ist nach values() (für entsprechendes c) sortiert, 
	 *           d.h. es gilt stets: comp(values[result[i]], values[result[i+1]]) == -1 (gleiches gilt für die Menge B 
	 *           result[0] enthält stets die ANZAHL der Elemente der Menge! (d.h. von 1 bis result[0] iterieren)
	 */
	private void s2intersection (int[] result, int[][] values, Integer[] order, int startB, int endB, TriasComparator comparator) {
		int i = 1;             // Start der Menge A
		int i_max = result[0]; // Ende  der Menge A 
		result[0] = 0;
		int j = startB;        // Start der Menge B
		int comp;              // speichert Vergleichsergebnis
		while (i <= i_max && j <= endB) { // iterieren, bis Ende einer Menge erreicht
			comp = comparator.compare(values[result[i]], values[order[j]]);
			if (comp < 0) {
				// kleiner
				i++;
			} else if (comp > 0) {
				// größer
				j++;
			} else {
				// gleich --> Wert merken!
				result[0] ++;                 // ein Element mehr in der Ergebnis-Menge
				result[result[0]] = order[j]; // Position in order-Tabelle merken
				i++;
				j++;
			}
		}
	}

	/* neccessary to sort utrListe by different column permutations
	 * benötigt zum Sortieren der utrListe nach verschiedenen Spaltenpermutationen
	 * 
	 */
	private class IntArrayComparator implements Comparator<Integer> {

		private final int[][] _ints;
		private final int[] _permutation;

		public IntArrayComparator(int[][] ints, int[] permutation) { 
			_ints = ints; 
			_permutation = permutation;
		}

		public int compare(Integer arg0, Integer arg1) {
			for(int j = 0; j<_permutation.length; j++) {
				if (_ints[arg0][_permutation[j]] < _ints[arg1][_permutation[j]]){
					return -1;
				}
				if (_ints[arg0][_permutation[j]] > _ints[arg1][_permutation[j]]){
					return 1;
				}
			}
			return arg0 - arg1;
		}		
	}

	/* neccessary to sort menge by size of the element extends (or intents)
	 * benötigt zum Sortieren der menge nach Größe der Elementumfänge (bzw. -inhalte)
	 * 
	 */
	private class IntArraySizeComparator implements Comparator<Integer> {

		private final int[] _menge;
		private final int[] _offsets;
		private final int[] _remember;	

		public IntArraySizeComparator(int[] menge, int[] offsets, int[] remember) { 
			_menge    = menge; 
			_offsets  = offsets;
			_remember = remember; 
		}

		public int compare(Integer arg0, Integer arg1) {
			// erste Menge enthält weniger Elemente
			if (_offsets[_remember[_menge[arg0]] + 1] - _offsets[_remember[_menge[arg0]]] < _offsets[_remember[_menge[arg1]] + 1] - _offsets[_remember[_menge[arg1]]]){
				return -1;
			}
			// erste Menge enthält mehr Elemente
			if (_offsets[_remember[_menge[arg0]] + 1] - _offsets[_remember[_menge[arg0]]] > _offsets[_remember[_menge[arg1]] + 1] - _offsets[_remember[_menge[arg1]]]){
				return 1;
			}	
			return 0;
		}		
	}

	/** Sets the number of items for each dimension.
	 * 
	 * @param numberOfItemsPerDimension
	 */
	public void setNumberOfItemsPerDimension(int[] numberOfItemsPerDimension) {
		this.numberOfItemsPerDimension = numberOfItemsPerDimension;
	}

	/** Sets the minimal support values for each dimension.
	 * 
	 * @param minSupportPerDimension
	 */
	public void setMinSupportPerDimension(int[] minSupportPerDimension) {
		this.minSupportPerDimension = minSupportPerDimension;
	}

	/** Sets the input data, an array of size {@link #numberOfTriples} times 4.
	 * 
	 * @param itemList
	 */
	public void setItemList(int[][] itemList) {
		this.itemList = itemList;
	}


	/** Sets the writer which writes the computed tri concepts.
	 * 
	 * @param triConceptWriter
	 */
	public void setTriConceptWriter(TriasWriter triConceptWriter) {
		this.triConceptWriter = triConceptWriter;
	}

	public ProgressLogger getPl() {
		return pl;
	}

	public void setProgressLogger(ProgressLogger pl) {
		this.pl = pl;
	}
}
