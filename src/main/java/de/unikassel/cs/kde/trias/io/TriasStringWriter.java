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
import java.util.SortedSet;

/**
 * 
 * @author:  rja
 * @version: $Id: TriasStringWriter.java,v 1.2 2009-04-23 06:53:14 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasStringWriter implements TriasWriter {

	private SortedSet<String> concepts;
	
	public TriasStringWriter(final SortedSet<String> concepts) {
		this.concepts = concepts;
	}
	
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}

	public void write(int[][] concept) {
		concepts.add(toString(concept));
	}

	private String toString(int[][] concept) {
		final StringBuffer buf = new StringBuffer();
		buf.append("(");
		writeSet(buf, concept[0]);
		buf.append(", ");
		writeSet(buf, concept[1]);
		buf.append(", ");
		writeSet(buf, concept[2]);
		buf.append(")");
		
		final String string = buf.toString();
		return string;
	}
	
	private void writeSet(final StringBuffer buf, final int[] set) {
		buf.append("{");
		for (int i = 0; i < set.length; i++) {
			buf.append(set[i]);
			if (i != set.length - 1) {
				buf.append(",");
			}
		}
		buf.append("}");
	}
}

