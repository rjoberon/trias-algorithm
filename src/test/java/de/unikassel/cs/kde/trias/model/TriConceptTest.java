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

package de.unikassel.cs.kde.trias.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id: TriConceptTest.java,v 1.2 2009-04-23 06:53:17 rja Exp $
 * $Author: rja $
 * 
 */
public class TriConceptTest {

	@Test
	public void testHashCode() {
		//fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		final TriConcept<String> c1 = new TriConcept<String>();
		c1.setExtent(new String[]{"a", "b", "c"});
		c1.setIntent(new String[]{"d", "e", "f"});
		c1.setModus(new String[]{"g", "h", "i"});

		final TriConcept<String> c2 = new TriConcept<String>();
		c2.setExtent(new String[]{"a", "b", "c"});
		c2.setIntent(new String[]{"d", "e", "f"});
		c2.setModus(new String[]{"g", "h", "i"});

		assertTrue(c1.equals(c2));
		
		final TriConcept<String> c3 = new TriConcept<String>();
		c3.setExtent(new String[]{"a", "b", "g"});
		c3.setIntent(new String[]{"d", "e", "f"});
		c3.setModus(new String[]{"g", "h", "i"});
		
		assertFalse(c1.equals(c3));
		assertFalse(c2.equals(c3));

	}
	
	
	/**
	 * Checks, if two arrays of tri concepts are equals with {@link Arrays.deepEquals()}.
	 * Note however, that order of the elements in the arrays matters!
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEqualsObjectArray() {
		final TriConcept<String>[] triLattice1 = new TriConcept[] {
				new TriConcept<String>(
						new String[]{"eins"}, 
						new String[]{"eins"}, 
						new String[]{"eins"}
				),
				new TriConcept<String>( 
						new String[]{"eins"},
						new String[]{"zwei"},
						new String[]{"drei"}
				),
				new TriConcept<String>( 
						new String[]{"eins"},
						new String[]{"drei"},
						new String[]{"zwei"}
				)
		};
		
		final TriConcept<String>[] triLattice2 = new TriConcept[] {
				new TriConcept<String>(
						new String[]{"eins"}, 
						new String[]{"eins"}, 
						new String[]{"eins"}
				),
				new TriConcept<String>( 
						new String[]{"eins"},
						new String[]{"zwei"},
						new String[]{"drei"}
				),
				new TriConcept<String>( 
						new String[]{"eins"},
						new String[]{"drei"},
						new String[]{"zwei"}
				)
		};
		
		assertTrue(Arrays.deepEquals(triLattice1, triLattice2));
		
		assertTrue(equals(triLattice1, triLattice1));
		
		assertTrue(equals(triLattice1, triLattice2));
		
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

