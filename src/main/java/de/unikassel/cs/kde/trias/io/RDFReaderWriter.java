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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.TriConcept;
import de.unikassel.cs.kde.trias.model.Triple;

/**
 * 
 * @author:  rja
 * @version: $Id: RDFReaderWriter.java,v 1.2 2009-04-23 06:53:14 rja Exp $
 * $Author: rja $
 * 
 */
public class RDFReaderWriter implements TriasReader, TriasWriter {

	private static final Logger log = Logger.getLogger(RDFReaderWriter.class);

	private final BufferedWriter writer;

	private final ModelReaderWriter<String> mrw; 

	public RDFReaderWriter(final String inputFileName, final String outputFileName) throws FileNotFoundException, UnsupportedEncodingException {
		this(new File(inputFileName), new File(outputFileName));
	}

	public RDFReaderWriter(final File inputFile, final File outputFile) throws FileNotFoundException, UnsupportedEncodingException {
		this(new FileInputStream(inputFile), new FileOutputStream(outputFile));
	}

	public RDFReaderWriter(final InputStream inputStream, final OutputStream outputStream) throws UnsupportedEncodingException {
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		this.mrw = new ModelReaderWriter<String>(generateContext(inputStream));
	}

	/** Reads RDF triples from an inputStream and puts them into an itemList.
	 * 
	 * 
	 * @param inputStream
	 * @return
	 */
	private Context<String> generateContext(final InputStream inputStream) {
		/*
		 * read model from RDF file
		 */
		final Model model = ModelFactory.createDefaultModel();
		model.read(inputStream, "");

		final Triple<String>[] relation = new Triple[(int) model.size()]; 

		/*
		 * read triples
		 */
		final StmtIterator it = model.listStatements();
		int itemId = 0;
		while (it.hasNext()) {
			final Statement statement = it.nextStatement();
			relation[itemId++] = new Triple<String>(
					statement.getSubject().toString().replaceAll("\\n", ""), 
					statement.getPredicate().toString().replaceAll("\\n", ""), 
					statement.getObject().toString().replaceAll("\\n", "")
			);
		}

		return new Context<String>(relation);
	}

	public int[][] getItemlist() throws NumberFormatException, IOException {
		return mrw.getItemlist();
	}

	public void close() throws IOException {
		writer.close();
	}

	public void write(int[][] concept) throws IOException {
		mrw.write(concept);
		mrw.close();
		final TriConcept<String>[] triLattice = mrw.getTriLattice();

		for (int i = 0; i < triLattice.length; i++) {
			final TriConcept<String> triConcept = triLattice[i];
			writer.write(triConcept + "\n");
		}
	}

	public int[] getNumberOfItemsPerDimension() {
		return mrw.getNumberOfItemsPerDimension();
	}
}
