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

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import de.unikassel.cs.kde.trias.io.ModelReaderWriter;
import de.unikassel.cs.kde.trias.io.TriasStringWriter;
import de.unikassel.cs.kde.trias.io.TriasWriter;
import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.Triple;
import de.unikassel.cs.kde.trias.progress.SimpleProgressLogger;
import de.unikassel.cs.kde.trias.util.TriasConfigurator;
import de.unikassel.cs.kde.trias.util.TriasJavaConfigurator;



/**
 * 
 * @author:  rja
 * @version: $Id: TriasTest.java,v 1.6 2011-12-08 15:59:45 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasTest {

	static final int[] minSupp1 = new int[] {1,1,1};
	static final int[] minSupp0 = new int[] {0,0,0};


	@Test
	public void test1() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0}, 
				{1,2,3,0}, 
				{1,3,2,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1}, {1}},
				{{1}, {2}, {3}},
				{{1}, {3}, {2}}
		};
		checkResult(utrList, result, minSupp1);
	}

	@Test
	public void test2() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0}, 
				{1,2,3,0}, 
				{1,3,2,0},
				{1,2,2,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1}, {1}},
				{{1}, {2}, {2,3}},
				{{1}, {2,3}, {2}}
		};
		checkResult(utrList, result, minSupp1);
	}

	@Test
	public void test3() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0}, 
				{1,2,3,0}, 
				{1,3,2,0},
				{1,3,3,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1}, {1}},
				{{1}, {2,3}, {3}},
				{{1}, {3}, {2,3}}
		};
		checkResult(utrList, result, minSupp1);
	}

	@Test
	public void test4() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,2,0},
				{1,2,1,0},
				{2,1,1,0},
				{2,2,1,0},
				{2,1,2,0},
				{1,2,2,0},
				{1,1,3,0},
				{1,3,1,0},
				{3,1,1,0},
				{3,3,1,0},
				{3,1,3,0},
				{1,3,3,0},
				{2,2,3,0},
				{2,3,2,0},
				{3,2,2,0},
				{3,3,2,0},
				{3,2,3,0},
				{2,3,3,0},
				{1,2,3,0},
				{1,3,2,0},
				{2,3,1,0},
				{2,1,3,0},
				{3,2,1,0},
				{3,1,2,0}
		};
		final int[][][] result = new int[][][]{
				{{3}, {1,2}, {1,2,3}},
				{{3}, {1,2,3}, {1,2}},
				{{2}, {1,3}, {1,2,3}},
				{{2}, {1,2,3}, {1,3}},
				{{1}, {2,3}, {1,2,3}},
				{{1}, {1,2,3}, {2,3}},

				{{1,2}, {1,2,3}, {3}},
				{{1,2,3}, {1,2}, {3}},
				{{1,3}, {1,2,3}, {2}},
				{{1,2,3}, {1,3}, {2}},
				{{2,3}, {1,2,3}, {1}},
				{{1,2,3}, {2,3}, {1}},

				{{1,2,3}, {3}, {1,2}},
				{{1,2}, {3}, {1,2,3}},
				{{1,2,3}, {2}, {1,3}},
				{{1,3}, {2}, {1,2,3}},
				{{1,2,3}, {1}, {2,3}},
				{{2,3}, {1}, {1,2,3}},
				
				{{2,3}, {1,2}, {1,3}},
				{{2,3}, {1,3}, {1,2}},
				{{1,3}, {1,2}, {2,3}},
				{{1,3}, {2,3}, {1,2}},
				{{1,2}, {1,3}, {2,3}},
				{{1,2}, {2,3}, {1,3}}
				
		};
		checkResult(utrList, result, minSupp1);
	}



	@Test
	public void test5() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,2,0}, 
				{1,1,3,0}, 
				{1,2,1,0},
				{1,1,1,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1}, {1,2,3}},
				{{1}, {1,2}, {1}}
		};
		checkResult(utrList, result, minSupp1);
	}




	@Test
	public void test6() throws IOException {
		final int[][] utrList = new int[][] {
				{1,2,3,0},
				{1,3,2,0},
				{2,1,3,0},
				{2,3,1,0},
				{3,1,2,0},
				{3,2,1,0}
		};
		final int[][][] result = new int[][][]{
				{{3}, {2}, {1}},
				{{3}, {1}, {2}},
				{{2}, {3}, {1}},
				{{2}, {1}, {3}},
				{{1}, {3}, {2}},
				{{1}, {2}, {3}}
		};
		checkResult(utrList, result, minSupp1);
	}
	
	
	@Test
	public void test7() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1},
				{1,1,2},
				{1,1,3},
				{1,2,1},
				{1,2,2},
				{1,2,3},
				{1,3,1},
				{1,3,2},
				{1,3,3},
				{2,1,1},
				{2,1,2},
				{2,1,3},
				{2,2,1},
				{2,2,2},
				{2,2,3},
				{2,3,1},
				{2,3,2},
				{2,3,3},
				{3,1,1},
				{3,1,2},
				{3,1,3},
				{3,2,1},
				{3,2,2},
				{3,2,3},
				{3,3,1},
				{3,3,2},
				{3,3,3}
		};
		final int[][][] result = new int[][][]{
				{{1,2,3},{1,2,3},{1,2,3}}
		};
		checkResult(utrList, result, minSupp1);
	}

	@Test
	public void test8() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,2,0}, 
				{1,2,1,0}, 
				{2,1,1,0},
				{2,2,1,0},
				{2,1,2,0},
				{1,2,2,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1,2}, {2}},
				{{2}, {1,2}, {1}},
				{{1}, {2}, {1,2}},
				{{2}, {1}, {1,2}},
				{{1,2}, {1}, {2}},
				{{1,2}, {2}, {1}},
		};
		checkResult(utrList, result, minSupp1);
	}
	
	
	@Test
	public void test9() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0},
				{1,1,2,0},
				{1,1,3,0},
				{1,2,1,0},
				{1,2,2,0},
				{1,2,3,0}, 
				{1,3,1,0},
				{1,3,2,0},
				{1,3,3,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1,2,3}, {1,2,3}}
		};
		checkResult(utrList, result, minSupp1);
	}
	
	@Test
	public void test10() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0},
				{1,1,2,0},
				{1,1,3,0},
				{2,1,1,0},
				{2,1,2,0},
				{2,1,3,0}, 
				{3,1,1,0},
				{3,1,2,0},
				{3,1,3,0}
		};
		final int[][][] result = new int[][][]{
				{{1,2,3}, {1}, {1,2,3}}
		};
		checkResult(utrList, result, minSupp1);
	}
	
	@Test
	public void test11() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0},
				{1,2,1,0},
				{1,3,1,0},
				{2,1,1,0},
				{2,2,1,0},
				{2,3,1,0}, 
				{3,1,1,0},
				{3,2,1,0},
				{3,3,1,0}
		};
		final int[][][] result = new int[][][]{
				{{1,2,3}, {1,2,3}, {1}}
		};
		checkResult(utrList, result, minSupp1);
	}


	/** Test failed once with exception because of trOffsetCtr being too big. 
	 * Fixed since version 0.0.3.
	 * 
	 * @throws IOException
	 */
	@Test
	public void test12() throws IOException {
		/*
		 * some data
		 */
		final int[][] utrList = new int[][] {
				{1,2,1,0},
				{1,1,2,0},
				{2,2,1,0},
				{3,1,1,0}
		};
		final int[][][] result = new int[][][]{
				{{1,2}, {2}, {1}},
				{{1}, {1}, {2}},
				{{3}, {1}, {1}}
		};
		checkResult(utrList, result, minSupp1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test13() throws IOException {
		/*
		 * some data
		 */
		final Triple<String>[] relation = new Triple[]{
				new Triple<String>("eins", "zwei", "drei"),
				new Triple<String>("eins", "drei", "vier"),
				new Triple<String>("zwei", "zwei", "drei"),
				new Triple<String>("drei", "drei", "drei")
		}; 

		final Context<String> context = new Context<String>(relation);

		final Trias trias = new Trias();
		final ModelReaderWriter<String> mrw = new ModelReaderWriter<String>(context); 
		/*
		 * configure trias
		 */
		trias.setItemList(mrw.getItemlist());
		trias.setNumberOfItemsPerDimension(mrw.getNumberOfItemsPerDimension());
		trias.setMinSupportPerDimension(new int[]{0,0,0});
		trias.setTriConceptWriter(mrw);
		/*
		 * run trias
		 */
		trias.doWork();
	}
	
	

	/** Smallest non-trivial tri-context
	 * 
	 * @throws IOException
	 */
	@Test
	public void test14() throws IOException {
		/*
		 * some data
		 */
		final int[][] utrList = new int[][] {
				{1,1,1, 0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1}, {1}}
		};
		checkResult(utrList, result, minSupp0);
	}
	
	/** Tetrahedron-Condition
	 * 
	 * @throws IOException
	 */
	@Test
	public void test15() throws IOException {
		/*
		 * some data
		 */
		final int[][] utrList = new int[][] {
				{1,1,1,0},
				{2,2,1,0},
				{2,1,2,0},
				{1,2,2,0}
		};
		final int[][][] result = new int[][][]{
				{{1}, {1}, {1}},
				{{2}, {2}, {1}},
				{{2}, {1}, {2}},
				{{1}, {2}, {2}},
				{{1,2}, {1,2}, {}},
				{{1,2}, {}, {1,2}},
				{{}, {1,2}, {1,2}}
		};
		checkResult(utrList, result, minSupp0);
	}
	
	/** 
	 * partial order X5 - flat context (X5, X5, &le;) embedded into tri-context:
	 * 
	 * <pre>
	 * 4   5
	 *  \ /
	 *   3
	 *  / \
	 * 1   2
	 * </pre>
	 * 
	 * first dimension is flat
	 * 
	 * @throws IOException
	 */
	@Test
	public void test16a() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0},            {1,1,3,0}, {1,1,4,0}, {1,1,5,0},
				           {1,2,2,0}, {1,2,3,0}, {1,2,4,0}, {1,2,5,0},
				                      {1,3,3,0}, {1,3,4,0}, {1,3,5,0},
				                                 {1,4,4,0},
				                                            {1,5,5,0}
		};
		// Dedekind-MacNeille completion
		final int[][][] result = new int[][][]{
				{{1}, {1,2,3,4,5}, {}},	// top
				{{1}, {1,2,3,4}, {4}},	// 4 
				{{1}, {1,2,3,5}, {5}},	// 5
				{{1}, {1,2,3}, {3,4,5}},// 3
				{{1}, {1}, {1,3,4,5}},	// 1
				{{1}, {2}, {2,3,4,5}},	// 2
				{{1}, {}, {1,2,3,4,5}}, // bottom
				{{}, {1,2,3,4,5}, {1,2,3,4,5}}, // caused by embedding into tri-context
		};
		checkResult(utrList, result, minSupp0);
	}
	/** 
	 * partial order X5 - flat context (X5, X5, &le;) embedded into tri-context:
	 * 
	 * <pre>
	 * 4   5
	 *  \ /
	 *   3
	 *  / \
	 * 1   2
	 * </pre>
	 * 
	 * second dimension is flat
	 * 
	 * @throws IOException
	 */
	@Test
	public void test16b() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0}, {1,1,3,0}, {1,1,4,0}, {1,1,5,0},
				{2,1,2,0}, {2,1,3,0}, {2,1,4,0}, {2,1,5,0},
				{3,1,3,0},            {3,1,4,0}, {3,1,5,0},
				{4,1,4,0},
				{5,1,5,0}				
		};
		// Dedekind-MacNeille completion
		final int[][][] result = new int[][][]{
				{{1,2,3,4,5}, {1}, {}},	// top
				{{1,2,3,4}, {1}, {4}},	// 4 
				{{1,2,3,5}, {1}, {5}},	// 5
				{{1,2,3}, {1}, {3,4,5}},// 3
				{{1}, {1}, {1,3,4,5}},	// 1
				{{2}, {1}, {2,3,4,5}},	// 2
				{{}, {1}, {1,2,3,4,5}},  // bottom
				{{1,2,3,4,5}, {}, {1,2,3,4,5}}, // caused by embedding into tri-context
		};
		checkResult(utrList, result, minSupp0);
	}
	
	/** 
	 * partial order X5 - flat context (X5, X5, &le;) embedded into tri-context:
	 * 
	 * <pre>
	 * 4   5
	 *  \ /
	 *   3
	 *  / \
	 * 1   2
	 * </pre>
	 * 
	 * third dimension is flat
	 * 
	 * @throws IOException
	 */
	@Test
	public void test16c() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0}, {1,3,1,0}, {1,4,1,0}, {1,5,1, 0},
				{2,2,1,0}, {2,3,1,0}, {2,4,1,0}, {2,5,1,0},
				{3,3,1,0},            {3,4,1,0}, {3,5,1,0},
				{4,4,1,0},
				{5,5,1,0}				
		};
		// Dedekind-MacNeille completion
		final int[][][] result = new int[][][]{
				{{1,2,3,4,5}, {}, {1}},	// top
				{{1,2,3,4}, {4}, {1}},	// 4 
				{{1,2,3,5}, {5}, {1}},	// 5
				{{1,2,3}, {3,4,5}, {1}},// 3
				{{1}, {1,3,4,5}, {1}},	// 1
				{{2}, {2,3,4,5}, {1}},	// 2
				{{}, {1,2,3,4,5}, {1}},  // bottom
				{{1,2,3,4,5}, {1,2,3,4,5}, {}}, // caused by embedding into tri-context
		};
		checkResult(utrList, result, minSupp0);
	}
	
	/** 
	 * partial order X5 - flat context (X5, X5, &le;) embedded into tri-context:
	 * 
	 * <pre>
	 * 4   5
	 *  \ /
	 *   3
	 *  / \
	 * 1   2
	 * </pre>
	 * 
	 * third dimension is flat
	 * 
	 * @throws IOException
	 */
	@Test
	public void test16d() throws IOException {
		final int[][] utrList = new int[][] {
				{1,1,1,0}, {1,3,1,0}, {1,4,1,0}, {1,5,1, 0},
				{2,2,1,0}, {2,3,1,0}, {2,4,1,0}, {2,5,1,0},
				{3,3,1,0},            {3,4,1,0}, {3,5,1,0},
				{4,4,1,0},
				{5,5,1,0}				
		};
		// Dedekind-MacNeille completion
		final int[][][] result = new int[][][]{
				{{1,2,3,4,5}, {}, {1}},	// top
				{{1,2,3,4}, {4}, {1}},	// 4 
				{{1,2,3,5}, {5}, {1}},	// 5
				{{1,2,3}, {3,4,5}, {1}},// 3
				{{1}, {1,3,4,5}, {1}},	// 1
				{{2}, {2,3,4,5}, {1}},	// 2
				{{}, {1,2,3,4,5}, {1}}  // bottom
				//{{1,2,3,4,5}, {1,2,3,4,5}, {}}, // omitted because of minSupp = 1 for third dimension
		};
		checkResult(utrList, result, new int[]{0, 0, 1});
	}
	
	/** Checks, if Trias computes for the given utrList the given concepts.
	 * 
	 * @param itemList
	 * @param trueConcepts
	 * @throws IOException
	 */
	private void checkResult(final int[][] itemList, final int[][][] trueConcepts, final int[] minSupp) throws IOException {

		/*
		 * put the true concepts into a set
		 */
		final SortedSet<String> trueConceptsSet = new TreeSet<String>();
		final TriasStringWriter writer = new TriasStringWriter(trueConceptsSet);
		writeResult(trueConcepts, writer);

		/*
		 * run trias
		 */
		final SortedSet<String> computedConcepts = new TreeSet<String>();
		final TriasConfigurator config = new TriasJavaConfigurator(itemList, minSupp, computedConcepts);
		final Trias trias = new Trias();
		trias.setProgressLogger(new SimpleProgressLogger("/tmp/trias.log"));
		config.configureTrias(trias);
		trias.doWork();

		/*
		 * compare
		 */
		System.out.println("should: " + trueConceptsSet);
		System.out.println("is    : " + computedConcepts);
		assertEquals(trueConceptsSet, computedConcepts);
	}

	private void writeResult(final int[][][] result, final TriasWriter writer) throws IOException {
		for (int i = 0; i < result.length; i++) {
			writer.write(result[i]);	
		}
	}


}

