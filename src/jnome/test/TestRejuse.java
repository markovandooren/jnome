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

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;

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
  
  public List<Type> getTestTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();
//    JavaTypeReference ref = new JavaTypeReference("org.jutil.math.matrix.Matrix");
//    ref.setUniParent(_mm);
//    result.add(ref.getType());
	
    result = _mm.getSubNamespace("org").getSubNamespace("jutil").getAllTypes();
    return result;
  }

	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
	
//  public static void main(String[] args) throws Exception, Throwable {
//    new TestSuite(TestRejuse.class).run(new TestResult());
//  }
}
