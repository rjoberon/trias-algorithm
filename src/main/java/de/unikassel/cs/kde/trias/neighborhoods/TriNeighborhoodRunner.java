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

import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unikassel.cs.kde.trias.Trias;
import de.unikassel.cs.kde.trias.io.ModelReaderWriter;
import de.unikassel.cs.kde.trias.io.TriConceptReader;
import de.unikassel.cs.kde.trias.io.TriasDatabaseSource;
import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.Graph;
import de.unikassel.cs.kde.trias.model.TriConcept;

/**
 * 
 * @author:  rja
 * @version: $Id: TriNeighborhoodRunner.java,v 1.6 2009-08-05 13:39:46 rja Exp $
 * $Author: rja $
 * 
 */
public class TriNeighborhoodRunner {

	private static final String ARRAY_DELIM = ",";

	private static final Logger log = Logger.getLogger(TriNeighborhoodRunner.class);

	private final Properties prop = new Properties();

	@SuppressWarnings("unchecked")
	public static void main(final String[] args) throws Exception {
		final TriNeighborhoodRunner runner;

		final TriConcept<String>[] triConcepts;
		/*
		 * TODO: change here what to choose
		 */
		if (1 != 1 || args.length > 0) {
			// also runs Trias!
			runner = new TriNeighborhoodRunner("database.properties");
			triConcepts = runner.readConceptsFromDatabase();
		} else {
			// reads the pre-computed concepts from file
			runner = new TriNeighborhoodRunner("neighborhood.properties");
			triConcepts = runner.readConceptsFromFile();
		}

		final Graph<TriConcept<String>> graph = runner.computeNeighborhoodGraph(triConcepts);

		// does nothing valuable ...
		runner.computeNeighborhoods(graph);

		// really write the graph
		runner.writeNeighborhoodGraph(graph);

	}
	
	public TriNeighborhoodRunner(final String configFile) throws IOException {
		/*
		 * load properties
		 */
		prop.load(TriNeighborhoodRunner.class.getClassLoader().getResourceAsStream(configFile));
	}


	/**
	 * Reads concepts from a file, given by the property "lattice.file".
	 *  
	 * 
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public TriConcept<String>[] readConceptsFromFile() throws IOException, Exception {
		final TriConceptReader reader = new TriConceptReader();
		/*
		 * map dimensions back
		 */
		reader.setMappingFiles(
				prop.getProperty("lattice.map.file.extent"), 
				prop.getProperty("lattice.map.file.intent"),
				prop.getProperty("lattice.map.file.modus")
		);

		final TriConcept<String>[] triConcepts = asArray(reader.getTriLattice(prop.getProperty("lattice.file")));

		log.info("Found " + triConcepts.length + " tri-concepts");

		return triConcepts;
	}

	/**
	 * Writes the neighborhood graph of the given tri-concepts.
	 * 
	 * 
	 * @param triConcepts
	 * @throws IOException
	 */
	public Graph<TriConcept<String>> computeNeighborhoodGraph(final TriConcept<String>[] triConcepts) {
		/*
		 * configure neighborhood generator
		 */
		final TriNeighborhoodGenerator<String> generator = new TriNeighborhoodGenerator<String>();
		generator.setConcepts(triConcepts);
		generator.setThresholds(getIntArrayFromString(prop.getProperty("neighborhood.thresholds"), ARRAY_DELIM));
		if ("true".equals(prop.getProperty("neighborhood.ignore"))) {
			generator.setIgnoreItems(
					prop.getProperty("neighborhood.ignore.extent").split(ARRAY_DELIM),
					prop.getProperty("neighborhood.ignore.intent").split(ARRAY_DELIM),
					prop.getProperty("neighborhood.ignore.modus").split(ARRAY_DELIM)
			);
		}

		log.info("Computing tri-neighborhoods");
		final Graph<TriConcept<String>> graph = generator.getNeighborhoodGraph();
		log.info("Neighborhood graph has " + graph.getEdges().size() + " edges and " + graph.getVertices()+ " vertices");

		return graph;
	}

	public void writeNeighborhoodGraph(final Graph<TriConcept<String>> graph) throws IOException {
		/*
		 * write graphviz graph
		 */
		log.info("Writing graph to disk");
		final GraphWriter<String> writer = new GraphvizGraphWriter<String>(prop.getProperty("lattice.file"));
		final GraphWriter<String> pWriter = new PajekGraphWriter<String>(prop.getProperty("lattice.file"));

		final String[] dimensionLabels = prop.getProperty("graph.labels").split(" ");
		writer.setDimensionLabels(dimensionLabels);
		pWriter.setDimensionLabels(dimensionLabels);

		writer.writeGraph(graph);
		pWriter.writeGraph(graph);

	}

	/**
	 * Computes the neighborhoods (i.e., connected components) of the given 
	 * neighborhood graph.
	 * 
	 * @param graph
	 * @return
	 */
	public HashSet<HashSet<TriConcept<String>>> computeNeighborhoods(final Graph<TriConcept<String>> graph) {
		final TriNeighborhoodFinder<String> finder = new TriNeighborhoodFinder<String>();
		final HashSet<HashSet<TriConcept<String>>> neighborhoods = finder.findNeighborhoods(graph);
		log.info("Found " + neighborhoods.size() + " neighborhoods");

		int sum = 0;
		for (final Collection<TriConcept<String>> neighborhood : neighborhoods) {
			final int size = neighborhood.size();
			log.info(size + " " + neighborhood);
			sum += size;
		}
		log.info(sum + " ( = sum of edges)");
		return neighborhoods;
	}


	/** Copies the collection into an array.
	 * 
	 * @param triLattice
	 * @return
	 */
	private static TriConcept<String>[] asArray(final Collection<TriConcept<String>> triLattice) {
		final TriConcept<String>[] array = new TriConcept[triLattice.size()];
		int i = 0;
		for (final TriConcept<String> triConcept : triLattice) {
			array[i++] = triConcept;
		}
		return array;
	}

	/**
	 * Reads the TAS from database and computes the tri-concepts using Trias.
	 * 
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private TriConcept<String>[] readConceptsFromDatabase() throws IOException, Exception {
		/*
		 * load properties
		 */
		final Properties prop = new Properties();
		prop.load(TriNeighborhoodRunner.class.getClassLoader().getResourceAsStream("database.properties"));
		/*
		 * get some data from database
		 */
		final Connection conn = TriasDatabaseSource.getConnection(
				prop.getProperty("db.url"),
				prop.getProperty("db.user"),
				prop.getProperty("db.pass")
		);

		final TriasDatabaseSource dataSource = new TriasDatabaseSource(
				conn, 
				prop.getProperty("db.query"),
				new Object[]{}
		);

		final Context<String> itemlist = dataSource.getItemlist();
		log.info("Got " + itemlist.getRelation().length + " triples from database");

		final ModelReaderWriter<String> mrw = new ModelReaderWriter<String>(itemlist);


		final Trias trias = new Trias();

		/*
		 * configure trias
		 */
		trias.setItemList(mrw.getItemlist());
		trias.setNumberOfItemsPerDimension(mrw.getNumberOfItemsPerDimension());
		trias.setMinSupportPerDimension(getIntArrayFromString(prop.getProperty("trias.minSupport"), ARRAY_DELIM));
		trias.setTriConceptWriter(mrw);
		/*
		 * run trias
		 */
		log.info("Starting to compute tri-concepts");
		trias.doWork();


		final TriConcept<String>[] triConcepts = mrw.getTriLattice();

		log.info("Found " + triConcepts.length + " tri-concepts");

		return triConcepts;
	}

	/**
	 * Splits an input String each time delim is found and converts the 
	 * parts to integer.
	 *  
	 * @param input
	 * @param delim
	 * @return
	 */
	private static int[] getIntArrayFromString(final String input, final String delim) {
		final String[] parts = input.split(delim);
		final int[] result = new int[parts.length];
		for (int i=0; i < parts.length; i++) {
			result[i] = Integer.parseInt(parts[i]);
		}
		return result;
	}
}

