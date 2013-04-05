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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unikassel.cs.kde.trias.model.TriConcept;

/**
 * 
 * @author:  rja
 * @version: $Id: TriConceptReader.java,v 1.3 2009-08-04 14:53:37 rja Exp $
 * $Author: rja $
 * 
 */
public class TriConceptReader {

	private final static Pattern concepts = Pattern.compile("^.*?A = \\{(.*?), \\},  B = \\{(.*?), \\},  C = \\{(.*?), \\}$"); 
	
	private Map<Integer, String> extentMap;	
	private Map<Integer, String> intentMap;
	private Map<Integer, String> modusMap;

	
	public Collection<TriConcept<String>> getTriLattice(final String fileName) throws IOException {
		return getTriLattice(new File(fileName));
	}
	
	public Collection<TriConcept<String>> getTriLattice(final File file) throws IOException {
		return getTriLattice(getBufferedReader(file));
	}

	private BufferedReader getBufferedReader(final File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	}
	
	public Collection<TriConcept<String>> getTriLattice(final BufferedReader reader) throws IOException {
		final Collection<TriConcept<String>> triLattice = new LinkedList<TriConcept<String>>();
		String line;
		while ((line = reader.readLine()) != null) {
			/*
			 * parse line of the form 
			 * 
			 * A = {4552, 4553, },  B = {2, 180, },  C = {332, 7514, 7516, 7630, }
			 * 
			 */
			final Matcher m = concepts.matcher(line);
			if (m.matches()) {
				final TriConcept<String> triConcept = new TriConcept<String>();
				/*
				 * extract the concept
				 */
				triConcept.setExtent(map(extentMap, extractPart(m.group(1))));
				triConcept.setIntent(map(intentMap, extractPart(m.group(2))));
				triConcept.setModus( map(modusMap,  extractPart(m.group(3))));
				triLattice.add(triConcept);
			} else {
				throw new RuntimeException("could not find tri-concepts");
			}
			
		}
		reader.close();
		return triLattice;
	}
	
	private String[] map(final Map<Integer, String> map, final String[] parts) {
		if (map != null) {
			for (int i = 0; i < parts.length; i++) {
				/*
				 * we don't check if an entry exists ... if not: it is set to null
				 */
				parts[i] = map.get(Integer.parseInt(parts[i]));
			}
		}
		return parts;
	}
	
	private String[] extractPart(final String s) {
		return s.split(", ");
	}
	
	/**
	 * If one of the given file names is not equal to <code>null</code>, that
	 * dimension is mapped back using the specified mapping file. Line numbers
	 * in the file correspond to concept ids in the concept file.
	 * 
	 * @param extentMapFileName
	 * @param intentMapFileName
	 * @param modusMapFileName
	 * @throws IOException 
	 */
	public void setMappingFiles(final String extentMapFileName, final String intentMapFileName, final String modusMapFileName) throws IOException {
		extentMap = readMap(extentMapFileName);
		intentMap = readMap(intentMapFileName);
		modusMap  = readMap(modusMapFileName);
	}
	
	private Map<Integer, String> readMap(final String fileName) throws IOException {
		if (fileName != null && !fileName.trim().equals("")) {
			final Map<Integer, String> map = new HashMap<Integer, String>();
			final BufferedReader reader = getBufferedReader(new File(fileName));
			int lineCtr = 1;
			String line;
			while ((line = reader.readLine()) != null) {
				map.put(lineCtr++, line.trim());
			}
			reader.close();
			return map;
		}
		return null;
	}
	
}

