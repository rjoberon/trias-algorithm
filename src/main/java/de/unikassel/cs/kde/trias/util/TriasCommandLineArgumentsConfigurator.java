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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.unikassel.cs.kde.trias.Trias;
import de.unikassel.cs.kde.trias.io.TriasHoleReader;
import de.unikassel.cs.kde.trias.io.TriasHoleWriter;
import de.unikassel.cs.kde.trias.io.TriasReader;
import de.unikassel.cs.kde.trias.io.TriasStandardReader;
import de.unikassel.cs.kde.trias.io.TriasStandardWriter;
import de.unikassel.cs.kde.trias.progress.SimpleProgressLogger;

/**
 * 
 * @author:  rja
 * @version: $Id: TriasCommandLineArgumentsConfigurator.java,v 1.4 2009-06-15 09:15:39 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasCommandLineArgumentsConfigurator implements TriasConfigurator {

	private String[] args;
	private int argctr = 0;
	private final static String delimiter = "\\s"; 

	/** Parses the command line arguments and stores them.
	 * 
	 * Neccessary parameters are: 
	 *   X ... number of triples
	 *   A ... number of items in dim0
	 *	 B ... number of items in dim1
	 *   C ... number of items in dim2
	 *   uminsup, tminsup, rminsup ... minimal support (absolut values!) for dim0, dim1, dim2
	 *   HOLES|NOHOLES ...  HOLES = columns contain holes (i.e., numbers are missing), NOHOLES = opposite
	 *   
	 * @param args
	 */
	public TriasCommandLineArgumentsConfigurator (final String[] args) {
		this.args = args;
	}

	public void configureTrias(final Trias trias) throws ConfigurationException {
		if (args.length < 8) {
			throw new ConfigurationException("Expected 8 arguments, found " + args.length);
		}

		// number of triples
		final int numberOfTriples = nextIntArg();

		/*
		 * number of users, tags, resources
		 */
		final int[] numberOfItemsPerDimension = new int[Dimension.noOfDimensions];
		for (int dim = 0; dim < numberOfItemsPerDimension.length; dim++) {
			numberOfItemsPerDimension[dim] = nextIntArg(); 
		}
		trias.setNumberOfItemsPerDimension(numberOfItemsPerDimension);


		/*
		 * minimal supports
		 */
		final int[] minSupportPerDimension = new int[Dimension.noOfDimensions];
		for (int dim = 0; dim < minSupportPerDimension.length; dim++) {
			minSupportPerDimension[dim] = nextIntArg(); 
		}
		trias.setMinSupportPerDimension(minSupportPerDimension);


		// check, if input file contains holes
		final boolean holes = "HOLES".equals(nextArg());

		/*
		 * set data trias works on, configure output writer
		 */
		//final InputStream inputStream = new FileInputStream("/home/rja/projects/fca-rdf/swrc/fact");
		final InputStream inputStream = System.in;    // TODO: make input configurable
		final OutputStream outputStream = System.out; // TODO: make output configurable
		
		try {
			if (holes) {
				final TriasHoleReader tripleReader = new TriasHoleReader(new BufferedReader (new InputStreamReader(inputStream)), numberOfTriples, delimiter);
				trias.setItemList(tripleReader.getItemlist());
				trias.setTriConceptWriter(new TriasHoleWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), tripleReader.getInverseMapping()));
			} else {
				final TriasReader tripleReader = new TriasStandardReader(new BufferedReader (new InputStreamReader(inputStream)), numberOfTriples, delimiter);
				trias.setItemList(tripleReader.getItemlist());
				trias.setTriConceptWriter(new TriasStandardWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), false));
			}
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
		/*
		 * configure progress logger
		 * TODO: make this configurable
		 */
		try {
			trias.setProgressLogger(new SimpleProgressLogger("trias_progress.log"));
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
		

	}
	
	private int nextIntArg() {
		return Integer.parseInt(args[argctr++]);
	}
	
	private String nextArg() {
		return args[argctr++];
	}


	/** Prints usage information for this configurator.
	 * 
	 * @see de.unikassel.cs.kde.trias.util.TriasConfigurator#usage()
	 */
	public String usage() {
		final StringBuffer buf = new StringBuffer();
		buf.append("TRIAS reads facts from STDIN and writes tri-concepts to STDOUT\n");
		buf.append("usage:\n");
		buf.append("java " + Trias.class.getName() + " X A B C dim0minsup dim1minsup dim2minsup HOLES|NOHOLES\n");
		buf.append(
				" neccessary parameters are:\n" + 
				"   X ... number of triples\n" +
				"   A ... number of items in dim0\n" +
				"   B ... number of items in dim1\n" +
				"   C ... number of items in dim2\n" +
				"   dim0minsup, dim1minsup, dim2minsup ... minimal support (absolut values!) for dim0, dim1, dim2\n" +
		"   HOLES|NOHOLES ...  HOLES = columns contain holes (i.e., numbers are missing), NOHOLES = opposite\n");
		return buf.toString();
	}
}

