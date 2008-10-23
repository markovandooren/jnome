/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 *
 * This file is part of Jnome.
 *
 * Jnome is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Jnome is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Jnome; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.jnome.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import chameleon.core.MetamodelException;
import chameleon.core.type.Type;

/**
 * @author marko
 */
public class TestAntlr extends ExpressionTest {

	/**
	 * @param arg
	 * @throws TokenStreamException
	 * @throws RecognitionException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public TestAntlr(String arg)
		throws
			TokenStreamException,
			RecognitionException,
			MalformedURLException,
			FileNotFoundException,
			IOException,
			Exception {
		super(arg);
	}

	/**
	 *
	 */

	public void addTestFiles() {
		include("testsource"+getSeparator()+"gen"+getSeparator());
		include("testsource"+getSeparator()+"antlr-2.7.2"+getSeparator()+"antlr"+getSeparator());
	}

	/**
	 *
	 */

	public Set getTestTypes() throws MetamodelException {
		return _mm.getSubNamespace("antlr").getAllTypes();
	}

}
