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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

import de.unikassel.cs.kde.trias.model.TriConcept;

/**
 * 
 * @author:  rja
 * @version: $Id: TriConceptReaderTest.java,v 1.2 2009-04-23 06:53:16 rja Exp $
 * $Author: rja $
 * 
 */
public class TriConceptReaderTest {

	@Test
	public void testGetTriLatticeBufferedReader() {
		final InputStream resourceAsStream = TriConceptReaderTest.class.getClassLoader().getResourceAsStream("concepts");
		
		final TriConceptReader reader = new TriConceptReader();
		
		try {
			final Collection<TriConcept<String>> triLattice = reader.getTriLattice(new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8")));

			Assert.assertEquals(410, triLattice.size());
			
			final String[] firstExtent = triLattice.iterator().next().getExtent();
			Assert.assertTrue(Arrays.equals(new String[]{"4552", "4553"}, firstExtent));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

