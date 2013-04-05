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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.unikassel.cs.kde.trias.Trias;
import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.TriConcept;
import de.unikassel.cs.kde.trias.model.Triple;

/**
 * 
 * @author:  rja
 * @version: $Id: ModelReaderWriterTest.java,v 1.3 2009-04-23 06:53:16 rja Exp $
 * $Author: rja $
 * 
 */
public class ModelReaderWriterTest {

	private TriConcept<String>[] triLattice;
	private ModelReaderWriter<String> modelReaderWriter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		/*
		 * set up context
		 */
		final Triple[] relation = new Triple[]{
				new Triple("eins", "eins", "eins"),
				new Triple("eins", "zwei", "drei"),
				new Triple("eins", "drei", "zwei")
		};

		modelReaderWriter = new ModelReaderWriter<String>(new Context<String>(relation));

		/*
		 * set up corresponding triLattice
		 */
		triLattice = new TriConcept[] {
				new TriConcept(
						new String[]{"eins"}, 
						new String[]{"eins"}, 
						new String[]{"eins"}
				),
				new TriConcept( 
						new String[]{"eins"},
						new String[]{"zwei"},
						new String[]{"drei"}
				),
				new TriConcept( 
						new String[]{"eins"},
						new String[]{"drei"},
						new String[]{"zwei"}
				)
		};

	}

	@Test
	public void testTrias() throws NumberFormatException, IOException {
		/*
		 * set trias up
		 */
		final Trias trias = new Trias();
		trias.setMinSupportPerDimension(new int[]{1,1,1});
		trias.setItemList(modelReaderWriter.getItemlist());
		trias.setTriConceptWriter(modelReaderWriter);
		
		final int[] numberOfItemsPerDimensionShould = new int[]{1, 3, 3};
		final int[] numberOfItemsPerDimensionIs = modelReaderWriter.getNumberOfItemsPerDimension();
		assertTrue(Arrays.equals(numberOfItemsPerDimensionShould, numberOfItemsPerDimensionIs));
		trias.setNumberOfItemsPerDimension(numberOfItemsPerDimensionIs);
		/*
		 * run trias
		 */
		trias.doWork();
		/*
		 * extract tri lattice
		 */
		final TriConcept<String>[] triLatticeTrias = modelReaderWriter.getTriLattice();

		System.out.println("Should: " + Arrays.toString(triLattice));
		System.out.println("Is:     " + Arrays.toString(triLatticeTrias));
		
		assertEquals(triLattice.length, triLatticeTrias.length);
		
		final boolean deepEquals = equals(triLattice, triLatticeTrias);
		
		System.out.println("Equal:  " + deepEquals);
		
		assertTrue(deepEquals);

	}
	
	private boolean equals(final TriConcept<String>[] aArray, final TriConcept<String>[] bArray) {
		final Set<TriConcept<String>> aSet = new HashSet<TriConcept<String>>();
		for (final TriConcept<String> a:aArray) {
			aSet.add(a);
		}

		final Set<TriConcept<String>> bSet = new HashSet<TriConcept<String>>();
		for (final TriConcept<String> b:bArray) {
			bSet.add(b);
		}

		return aSet.equals(bSet);
	}
	

}