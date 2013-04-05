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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.unikassel.cs.kde.trias.model.Graph;
import de.unikassel.cs.kde.trias.model.GraphEdge;
import de.unikassel.cs.kde.trias.model.TriConcept;

/**
 * Writes a graph to graphviz format.
 * 
 * @author rja
 *
 * @param <T> - the Class of the graph vertices.
 */
public class PajekGraphWriter<T extends Comparable<T>> implements GraphWriter<T> {

	//private String[] dimensionColors = new String[] {"Red", "Green", "Blue"};
	/*
	 * as used in xfig figure 
	 */
	private String[] dimensionColors = new String[] {"Black", "Gray65", "Gray30"};
	private String[] dimensionLabels = new String[] {"0", "1", "2"};

	/**
	 * Pajek uses windows line breaks (\r)
	 */
	private static final String LINE_BREAK = "\r";
	private static final String FILE_EXTENSION = ".net";

	private BufferedWriter writer;

	public PajekGraphWriter(final String graphFilename) throws IOException {
		this(new BufferedWriter(new FileWriter(new File(graphFilename + FILE_EXTENSION))));
	}

	public PajekGraphWriter(final File graphFile) throws IOException {
		this(new BufferedWriter(new FileWriter(graphFile)));
	}

	public PajekGraphWriter(final OutputStream outputStream) throws IOException  {
		this(new BufferedWriter(new OutputStreamWriter(outputStream, "ISO-8859-1")));
	}

	public PajekGraphWriter(final BufferedWriter writer) {
		this.writer = writer;
	}

	/* (non-Javadoc)
	 * @see de.unikassel.cs.kde.trias.neighborhoods.GraphWriter#writeGraph(java.util.Set)
	 */
	public void writeGraph(final Graph<TriConcept<T>> graph) throws IOException {
		/*
		 * maps labels to ids of vertices
		 */
		final Map<TriConcept<T>, Integer> map = new HashMap<TriConcept<T>, Integer>();
		/*
		 * map and write the vertices
		 */
		writeln("*Vertices " + graph.getVertices().size());
		for (final TriConcept<T> vertice: graph.getVertices()) {
			if (!map.containsKey(vertice)) {
				map.put(vertice, map.size() + 1);
			}
			writeln("  " + map.get(vertice) + " \"" + toString(vertice) + "\" fs 6");
		}
		/*
		 * write edges
		 */
		writeln("*Edges");
		for (final GraphEdge<TriConcept<T>> edge: graph.getEdges()) {
			final HashMap<String,String> attributes = new HashMap<String,String>();
			/*
			 *  get matching dimension and size
			 */
			int matchingDim  = getEqualDim(edge.getStartVertice(), edge.getEndVertice());
			int matchingSize = edge.getStartVertice().getDim(matchingDim).length;
			/*
			 * set edge attributes
			 */
			attributes.put("color", dimensionColors[matchingDim]);
			attributes.put("weight", Integer.toString(matchingSize));
			attributes.put("style", "setlinewidth(" + matchingSize + ")");
			//attributes.put("label", "\"" + dimensionLabels[matchingDim] + "\"");
			/*
			 * draw edge
			 */
			writeln("  " + 
					map.get(edge.getStartVertice()) + " " + 
					map.get(edge.getEndVertice()) + " " +
					matchingSize + " " +
					"c " + dimensionColors[matchingDim]);
			//writer.write("   \"" + map.get(edge.getStartVertice()) + "\" -> \"" + map.get(edge.getEndVertice()) + "\" [" + getEdgeAttributes(attributes) + "];\n");

		}
		/*
		 * finish graph
		 */
		writer.close();
	}
	
	private void writeln(String s) throws IOException {
		writer.write(s + LINE_BREAK);
	}

	/**
	 * Writes a simple vertice (without HTML). 
	 * 
	 * @param triConcept
	 * @return
	 */
	private String toString(final TriConcept<T> triConcept) {
		final StringBuffer buf = new StringBuffer();
		for (int dim = 0; dim < TriConcept.DIMS; dim++) {
			
			final T[] part = triConcept.getDim(dim);

			buf.append("{");
			for (int i = 0; i < part.length; i++) {
				buf.append(part[i]);
				if (i < part.length - 1) buf.append(", ");
			}
			buf.append("}");

			if (dim < TriConcept.DIMS - 1) buf.append(", "); 
		}
		return buf.toString();
	}

	/** Returns the dimension, in which the given tri concepts are equal.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int getEqualDim (TriConcept<T> a, TriConcept<T> b) {
		for (int dim = 0; dim < TriConcept.DIMS; dim++) {
			if (Arrays.equals(a.getDim(dim), b.getDim(dim))) return dim;
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see de.unikassel.cs.kde.trias.neighborhoods.GraphWriter#setDimensionColors(java.lang.String[])
	 */
	public void setDimensionColors(String[] dimensionColors) {
		this.dimensionColors = dimensionColors;
	}
	/* (non-Javadoc)
	 * @see de.unikassel.cs.kde.trias.neighborhoods.GraphWriter#setDimensionLabels(java.lang.String[])
	 */
	public void setDimensionLabels(String[] dimensionLabels) {
		this.dimensionLabels = dimensionLabels;
	}

}
