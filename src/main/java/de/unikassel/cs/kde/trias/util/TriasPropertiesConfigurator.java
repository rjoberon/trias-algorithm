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

package de.unikassel.cs.kde.trias.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import de.unikassel.cs.kde.trias.Trias;
import de.unikassel.cs.kde.trias.io.RDFReaderWriter;
import de.unikassel.cs.kde.trias.io.TriasHoleReader;
import de.unikassel.cs.kde.trias.io.TriasHoleWriter;
import de.unikassel.cs.kde.trias.io.TriasReader;
import de.unikassel.cs.kde.trias.io.TriasStandardReader;
import de.unikassel.cs.kde.trias.io.TriasStandardWriter;

/**
 * 
 * @author:  rja
 * @version: $Id: TriasPropertiesConfigurator.java,v 1.3 2009-04-23 06:53:13 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasPropertiesConfigurator implements TriasConfigurator {

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static final String TRIAS_OUTPUT                        = "trias.output";
	private static final String TRIAS_INPUT                         = "trias.input";
	private static final String TRIAS_HOLES                         = "trias.holes";
	private static final String TRIAS_RDF                           = "trias.rdf";
	private static final String TRIAS_MIN_SUPPORT_PER_DIMENSION     = "trias.minSupportPerDimension";
	private static final String TRIAS_NUMBER_OF_ITEMS_PER_DIMENSION = "trias.numberOfItemsPerDimension";
	private static final String TRIAS_NUMBER_OF_TRIPLES             = "trias.numberOfTriples";
	private static final String TRIAS_DELIMITER                     = "trias.delimiter";
	private static final String TRIAS_OUTPUT_SCORES                 = "trias.outputScores";

	private Properties props;

	public TriasPropertiesConfigurator (final Properties props) {
		this.props = props;
	}

	public void configureTrias(final Trias trias) throws ConfigurationException {


		// number of triples
		final int numberOfTriples = Integer.parseInt(props.getProperty(TRIAS_NUMBER_OF_TRIPLES));

		/*
		 * number of users, tags, resources
		 */
		final int[] numberOfItemsPerDimension = new int[Dimension.noOfDimensions];
		for (int dim = 0; dim < numberOfItemsPerDimension.length; dim++) {
			numberOfItemsPerDimension[dim] = Integer.parseInt(props.getProperty(TRIAS_NUMBER_OF_ITEMS_PER_DIMENSION + "." + dim)); 
		}
		trias.setNumberOfItemsPerDimension(numberOfItemsPerDimension);


		/*
		 * minimal supports
		 */
		final int[] minSupportPerDimension = new int[Dimension.noOfDimensions];
		for (int dim = 0; dim < minSupportPerDimension.length; dim++) {
			minSupportPerDimension[dim] = Integer.parseInt(props.getProperty(TRIAS_MIN_SUPPORT_PER_DIMENSION + "." + dim)); 
		}
		trias.setMinSupportPerDimension(minSupportPerDimension);


		// check, if input file contains holes
		final boolean holes = new Boolean(props.getProperty(TRIAS_HOLES, "false"));

		// check, if input is in RDF format
		final boolean rdf = new Boolean(props.getProperty(TRIAS_RDF, "false"));

		/*
		 * configure input
		 */
		InputStream inputStream = System.in;
		if (props.containsKey(TRIAS_INPUT)) {
			try {
				inputStream = new BufferedInputStream(new FileInputStream(props.getProperty(TRIAS_INPUT)));
			} catch (FileNotFoundException e) {
				throw new ConfigurationException(e);
			}
		}
		/*
		 * configure output
		 */
		OutputStream outputStream = System.out;
		if (props.containsKey(TRIAS_OUTPUT)) {
			try {
				outputStream = new BufferedOutputStream(new FileOutputStream(props.getProperty(TRIAS_OUTPUT)));
			} catch (FileNotFoundException e) {
				throw new ConfigurationException(e);
			}
		}

		/*
		 * configure delimiter
		 */
		final String delimiter = props.getProperty(TRIAS_DELIMITER, "\\s");

		/*
		 * configure writing of scores
		 */
		final boolean writeScores = new Boolean(props.getProperty(TRIAS_OUTPUT_SCORES, "false"));

		/*
		 * read data, configure writers
		 */
		try {
			if (rdf) {
				final RDFReaderWriter rdfrw = new RDFReaderWriter(inputStream, outputStream);
				trias.setItemList(rdfrw.getItemlist());
				trias.setTriConceptWriter(rdfrw);
				trias.setNumberOfItemsPerDimension(rdfrw.getNumberOfItemsPerDimension());
			} else {
				if (holes) {
					final TriasHoleReader tripleReader = new TriasHoleReader(new BufferedReader (new InputStreamReader(inputStream, DEFAULT_CHARSET)), numberOfTriples, delimiter);
					trias.setItemList(tripleReader.getItemlist());
					trias.setTriConceptWriter(new TriasHoleWriter(new BufferedWriter(new OutputStreamWriter(outputStream, DEFAULT_CHARSET)), tripleReader.getInverseMapping()));
				} else {
					final TriasReader tripleReader = new TriasStandardReader(new BufferedReader (new InputStreamReader(inputStream, DEFAULT_CHARSET)), numberOfTriples, delimiter);
					trias.setItemList(tripleReader.getItemlist());
					trias.setTriConceptWriter(new TriasStandardWriter(new BufferedWriter(new OutputStreamWriter(outputStream, DEFAULT_CHARSET)), writeScores));
				}
			}
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}

	}


	/** Prints usage information for this configurator.
	 * 
	 * @see de.unikassel.cs.kde.trias.util.TriasConfigurator#usage()
	 */
	public String usage() {
		final StringBuffer buf = new StringBuffer();
		buf.append("The following properties are used for configuration:\n");
		buf.append("\n");
		buf.append(TRIAS_NUMBER_OF_TRIPLES + " ... number of triples\n");
		buf.append(TRIAS_NUMBER_OF_ITEMS_PER_DIMENSION + ".0 ... number of items in dim0\n");
		buf.append(TRIAS_NUMBER_OF_ITEMS_PER_DIMENSION + ".1 ... number of items in dim1\n");
		buf.append(TRIAS_NUMBER_OF_ITEMS_PER_DIMENSION + ".2 ... number of items in dim2\n");
		buf.append(TRIAS_INPUT + " ... path to output file (default: STDIN)\n");
		buf.append(TRIAS_OUTPUT + " ... path to output file (default: STDOUT)\n");
		buf.append(TRIAS_OUTPUT_SCORES + " ... set to 'true', if scores for concepts should be printed (default: false)\n");
		buf.append(TRIAS_HOLES + " ... set to 'true', if the items are not numbered consecutively (default: false)\n");
		buf.append(TRIAS_RDF + " ... set to 'true', if the input file is in RDF format (default: false)\n");
		buf.append(TRIAS_DELIMITER + " ... a Java regular expression depicting, how the items of a triple are separated\n");
		buf.append(TRIAS_MIN_SUPPORT_PER_DIMENSION + ".0 ... minimal number of items of dim0 to be in each tri-concept\n");
		buf.append(TRIAS_MIN_SUPPORT_PER_DIMENSION + ".1 ... minimal number of items of dim1 to be in each tri-concept\n");
		buf.append(TRIAS_MIN_SUPPORT_PER_DIMENSION + ".2 ... minimal number of items of dim2 to be in each tri-concept\n");

		return buf.toString();
	}
}

