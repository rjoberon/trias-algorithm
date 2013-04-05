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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 
 * @author:  rja
 * @version: $Id: SimpleProgressLogger.java,v 1.2 2009-08-04 14:53:37 rja Exp $
 * $Author: rja $
 * 
 */
public class SimpleProgressLogger implements ProgressLogger {

	private BufferedWriter writer;
	private int lastElement = Integer.MAX_VALUE;
	private int outerCounter = 0;
	private int max = Integer.MAX_VALUE;
	private int steps = 2;
	private int maxDivSteps = max / steps;

	public SimpleProgressLogger() {
		// 
	}

	public SimpleProgressLogger(final BufferedWriter writer) {
		this.writer = writer;
	}

	public SimpleProgressLogger(final OutputStream stream) throws IOException {
		this.writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
	}

	public SimpleProgressLogger(final String fileName) throws IOException {
		this(new FileOutputStream(fileName));
	}

	public void logStep(ProgressStep step) {
		try {
			switch (step) {
			case START:
				writer.write("[" + maxDivSteps);
				break;
			case STOP:
				writer.write("]\n");
				writer.close();
				break;
			case OUTER_SUCCESS:
				writer.write("O");
				writer.flush();
				outerCounter = 0;
				break;
			case OUTER:
				outerCounter++;
				if (outerCounter % maxDivSteps == 0) {
					writer.write(".");
					writer.flush();
				}
				break;
			default:
//				writer.write(step.toString());
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void logExtent(int minElement) {
		try {
			if (minElement < lastElement) {
				lastElement = minElement;
				writer.write("(" + minElement + ")");
				writer.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setMax(int max) {
		this.max = max;
		if (max > steps) {
			this.maxDivSteps = new Double(Math.pow(steps, new Double(Math.log(max) / Math.log(steps)).intValue() - 1)).intValue();
		} else {
			this.maxDivSteps = 1;
		}
	}

	public int getMaxDivSteps() {
		return maxDivSteps;
	}

	public void setMaxDivSteps(int maxDivSteps) {
		this.maxDivSteps = maxDivSteps;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
}

