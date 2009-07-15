/*
 * Created on Dec 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
package jnome.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;

/**
 * @author marko
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestJRegex extends ExpressionTest {

	/**
	 * @param arg
	 * @throws TokenStreamException
	 * @throws RecognitionException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public TestJRegex(String arg)
		throws Exception {
		super(arg);
	}

	public void addTestFiles() {
			include("testsource"+getSeparator()+"gen"+getSeparator());
			include("testsource"+getSeparator()+"jregex"+getSeparator());
	}

	/**
	 *
	 */

	public List<Type> getTestTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
		assertNotNull(_mm);
	JavaTypeReference ref = new JavaTypeReference("jregex.util.io.AnyPath");
	ref.setUniParent(_mm);
	result.add(ref.getType());
		
//		assertNotNull(_mm.getSubNamespace("jregex"));
//    result = _mm.getSubNamespace("jregex").getAllTypes();
		return result;
	}

  public static void main(String[] args) throws Exception, Throwable {
    new TestSuite(TestJRegex.class).run(new TestResult());
  }
	@Override
	public void setLogLevels() {
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

}
