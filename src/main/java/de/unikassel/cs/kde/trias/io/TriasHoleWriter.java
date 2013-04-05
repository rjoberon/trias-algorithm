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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;


/**
 * @author rja
 *
 */
public class TriasHoleWriter implements TriasWriter {
	
	private BufferedWriter writer;
	private HashMap<Integer,Integer>[] imagToReal;
	
	public TriasHoleWriter(final BufferedWriter writer, final HashMap<Integer,Integer>[] imagToReal) {
		this.writer = writer;
		this.imagToReal = imagToReal;
	}
	
	public void write(final int[][] sets) throws IOException {
	StringBuffer buf = new StringBuffer();
		
		/*
		 * write sets
		 */
		buf.append("A = {");
		for (int a = 0; a < sets[0].length; a++) {
			buf.append(imagToReal(sets[0][a], 0) + ", ");
		}
		
		buf.append("},  B = {");
		for (int a = 0; a < sets[1].length; a++) {
			buf.append(imagToReal(sets[1][a], 1) + ", ");
		}
		
		buf.append("},  C = {");
		for (int a = 0; a < sets[2].length; a++) {
			buf.append(imagToReal(sets[2][a], 2) + ", ");
		}
		
		buf.append("}\n");
		writer.write(buf.toString());
	}

	private int imagToReal (int imagValue, int pos) {
		return imagToReal[pos].get(imagValue);
	}
	
	public void close() throws IOException {
		writer.close();
	}

}
