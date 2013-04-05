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

package de.unikassel.cs.kde.trias;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.unikassel.cs.kde.trias.util.ConfigurationException;
import de.unikassel.cs.kde.trias.util.TriasCommandLineArgumentsConfigurator;
import de.unikassel.cs.kde.trias.util.TriasConfigurator;
import de.unikassel.cs.kde.trias.util.TriasPropertiesConfigurator;

/**
 * 
 * @author:  rja
 * @version: $Id: TriasRunner.java,v 1.3 2009-04-23 06:53:17 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasRunner {

	private static final String PROPERTIES_FILE_NAME = "trias.properties";

	public static void main(String[] args) throws IOException {
		
		final Trias trias = new Trias();
		TriasConfigurator config;

		if (args.length > 0) {
			/*
			 * use command line arguments for configuration
			 */
			config = new TriasCommandLineArgumentsConfigurator(args);
		} else {
			final InputStream resourceAsStream = TriasRunner.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
			if (resourceAsStream == null) {
				System.err.println("Could not find file '" + PROPERTIES_FILE_NAME + "' for configuration.");
				System.exit(1);
			}

			final Properties prop = new Properties();
			prop.load(resourceAsStream);
			config = new TriasPropertiesConfigurator(prop);
			
		}
		System.err.println(config.usage());
		

		/*
		 * configure trias
		 */
		try {
			config.configureTrias(trias);
		} catch (final ConfigurationException e) {
			System.err.println("Could not configure Trias: " + e);
			System.err.println(config.usage());
			System.exit(1);
		}

		trias.doWork();

	}
}

