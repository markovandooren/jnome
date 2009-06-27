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

import java.util.Set;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import chameleon.core.MetamodelException;

/**
 * @author Marko van Dooren
 */
public class TestRejuse extends ExpressionTest {

  /**
   * Constructor for Test.
   * @param arg0
   */
  public TestRejuse(String arg0) throws Exception {
    super(arg0);
  }
  
  public void addTestFiles() {
		include("testsource"+getSeparator()+"gen"+getSeparator());
		include("testsource"+getSeparator()+"jregex"+getSeparator());
		include("testsource"+getSeparator()+"jutil"+getSeparator()+"src"+getSeparator());
	  include("testsource"+getSeparator()+"junit3.8.1"+getSeparator()+"src"+getSeparator());
  	
  }
  
  public Set getTestTypes() throws MetamodelException {
    return _mm.getSubNamespace("org").getSubNamespace("jutil").getAllTypes();
  }
  
  public static void main(String[] args) throws Exception, Throwable {
    new TestSuite(TestRejuse.class).run(new TestResult());
  }
}
