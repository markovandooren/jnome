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
public class TestJnomeTNG extends ExpressionTest {
	/**
	 * @param arg
	 * @throws TokenStreamException
	 * @throws RecognitionException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public TestJnomeTNG(String arg) throws TokenStreamException,
			RecognitionException, MalformedURLException, FileNotFoundException,
			IOException, Exception {
		super(arg);
	}
	/**
	 *
	 */
	public void addTestFiles() {
//     include("/home/marko/testsource/JnomeTNG/src/", "**/*.java");
//     include("/home/marko/testsource/unit/src/", "**/*.java");
//     include("/home/marko/testsource/junit3.8.1/src", "**/*.java");
//     include("/home/marko/testsource/jregex", "**/*.java");
//     include("/home/marko/testsource/antlr-2.7.2/antlr", "**/*.java");
		
	  include("C:\\Chameleon\\src");
	  include("testsource\\gen\\");
  	  //include("testsource\\unit\\src\\");
	  //include("testsource\\jregex\\");
	  //include("testsource\\junit3.8.1\\src\\");
	  //include("C:\\Chameleon\\testsource\\antlr-2.7.2\\antlr");
	  
	  
  }
  
	/**
	 *
	 */
	public Set getTestTypes() throws MetamodelException {
		Set result = _mm.getSubNamespace("org").getSubNamespace("jnome").getAllTypes();
		result.addAll(_mm.getSubNamespace("chameleon").getAllTypes());
		return result;
	}
  
  public void testExpressionTypes() throws Exception {
    Type temp = _mm.findType("org.jnome.test.TestRejuse");
    processType(temp);
  }
  
}
