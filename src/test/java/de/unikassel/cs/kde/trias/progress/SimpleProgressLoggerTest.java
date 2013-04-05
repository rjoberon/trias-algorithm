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

package de.unikassel.cs.kde.trias.progress;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id: SimpleProgressLoggerTest.java,v 1.1 2009-06-15 09:15:53 rja Exp $
 * $Author: rja $
 * 
 */
public class SimpleProgressLoggerTest {

	@Test
	public void testSetMax() {
		
		SimpleProgressLogger pl = new SimpleProgressLogger();
		
		int steps = 10;
		int max = 1500;
		
		pl.setSteps(steps);
		pl.setMax(max);
		log(steps, max);
		Assert.assertEquals(100, pl.getMaxDivSteps());

		max = 9500;
		pl.setSteps(steps);
		pl.setMax(max);
		log(steps, max);
		Assert.assertEquals(100, pl.getMaxDivSteps());
		
		steps = 2;
		max = 1500;
		pl.setSteps(steps);
		pl.setMax(max);
		log(steps, max);
		Assert.assertEquals(512, pl.getMaxDivSteps());
		
		
	}

	private void log(int steps, int max) {
		final int logMinusOne = new Double(Math.log(max) / Math.log(steps)).intValue() - 1;
		final int finalDivSteps = new Double(Math.pow(steps, logMinusOne)).intValue();
		
		System.out.println("steps = " + steps + ", max = " + max + ", logMinusOne = " + logMinusOne + ", final = " + finalDivSteps);
	}
}

