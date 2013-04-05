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

import de.unikassel.cs.kde.trias.Trias;


/**
 * 
 * @author:  rja
 * @version: $Id: TriasConfigurator.java,v 1.2 2009-04-23 06:53:13 rja Exp $
 * $Author: rja $
 * 
 */
public interface TriasConfigurator {

	/** Configures the Trias algorithm according to the capabilities 
	 * of the configurator. 
	 *  
	 * @param trias - an instance of the Trias algorithm which should
	 * be configured.
	 */
	public void configureTrias(final Trias trias) throws ConfigurationException;
	
	/** Returns a string describing how to use this configurator.
	 * @return
	 */
	public String usage();
	
}

