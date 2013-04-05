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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.unikassel.cs.kde.trias.model.Context;
import de.unikassel.cs.kde.trias.model.Triple;

/**
 * 
 * @author:  rja
 * @version: $Id: TriasDatabaseSource.java,v 1.2 2009-04-23 06:53:14 rja Exp $
 * $Author: rja $
 * 
 */
public class TriasDatabaseSource {

	private static final Logger log = Logger.getLogger(TriasDatabaseSource.class);

	private String query;
	private Object[] arguments;
	private Connection conn;

	public static Connection getConnection (final String dbURL, final String dbUser, final String dbPass) throws Exception {
		Connection conn = null;
		/*
		 * connect to DB
		 */
		Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		conn = DriverManager.getConnection (dbURL, dbUser, dbPass);
		log.info("Database connection established");

		if (conn != null) {
			return conn;
		}
		throw new RuntimeException("Could not get connection.");
	}

	@SuppressWarnings("unchecked")
	public TriasDatabaseSource(final Connection conn, final String query, final Object[] arguments) {
		this.conn = conn;
		this.query = query;
		this.arguments = arguments;
	}

	@SuppressWarnings("unchecked")
	public Context<String> getItemlist() throws IOException {
		PreparedStatement stmt = null;
		try {
			/*
			 * prepare and execute query
			 */
			stmt = conn.prepareStatement(query);
			for (int i=0; i < arguments.length; i++) {
				stmt.setObject(i+1, arguments[i]);
			}
			final ResultSet rst = stmt.executeQuery();
			/*
			 * prepare datastructures for results
			 */
			final List<Triple<String>> list = new LinkedList<Triple<String>>();
			/*
			 * loop over results
			 */
			while (rst.next()) {
				final Triple<String> triple = new Triple<String>(
						rst.getString(1), 
						rst.getString(2),
						rst.getString(3)
						);
				list.add(triple);
			}
			/*
			 * copy results into array of triples
			 */
			final Triple<String>[] itemList = new Triple[list.size()];
			int itemCtr = 0;
			for (final Triple<String> triple: list) {
				itemList[itemCtr++] = triple;
			}
			list.clear();
			return new Context<String>(itemList);
		} catch (final SQLException e) {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (final SQLException e1) {
					throw new IOException(e1);
				}
			}
			throw new IOException(e);
		}

	}

}

