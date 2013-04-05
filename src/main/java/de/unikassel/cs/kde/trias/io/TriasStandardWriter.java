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


/**
 * @author rja
 *
 */
public class TriasStandardWriter implements TriasWriter {

	private BufferedWriter writer;
	private boolean writeScores;

	public TriasStandardWriter (final BufferedWriter writer, final boolean writeScores) {
		this.writer = writer;
		this.writeScores = writeScores;
	}

	public void write(final int[][] concept) throws IOException {
		final StringBuffer buf = new StringBuffer();

		/*
		 * write scores
		 */
		if (writeScores) {
			int[] scores = getScores(concept);
			for (int i = 0; i < scores.length; i++) {
				buf.append(scores[i] + " ");
			}
			buf.append("\t");
		}
		/*
		 * write sets
		 */
		buf.append("A = {");
		for (int a = 0; a < concept[0].length; a++) {
			buf.append(concept[0][a] + ", ");
		}

		buf.append("},  B = {");
		for (int a = 0; a < concept[1].length; a++) {
			buf.append(concept[1][a] + ", ");
		}

		buf.append("},  C = {");
		for (int a = 0; a < concept[2].length; a++) {
			buf.append(concept[2][a] + ", ");
		}

		buf.append("}\n");
		writer.write(buf.toString());
	}

	/** Calculates some statistics (like size/volume of concept)
	 * 
	 * @param concept
	 * @return
	 */
	private int[] getScores(final int[][] concept) {
		int[] scores = new int[] {
				concept[0][0],
				concept[1][0],
				concept[2][0],

				concept[0][0]       *  concept[1][0]      *  concept[2][0],

				(concept[0][0] - 1) *  concept[1][0]      *  concept[2][0],
				concept[0][0]       * (concept[1][0] - 1) *  concept[2][0],
				concept[0][0]       *  concept[1][0]      * (concept[2][0] - 1),

				concept[0][0]       * (concept[1][0] - 1) * (concept[2][0] - 1),
				(concept[0][0] - 1) *  concept[1][0]      * (concept[2][0] - 1),
				(concept[0][0] - 1) * (concept[1][0] - 1) *  concept[2][0],

				(concept[0][0] - 1) * (concept[1][0] - 1) * (concept[2][0] - 1)
		};
		return scores;
	}

	public void close() throws IOException {
		writer.close();
	}

}
