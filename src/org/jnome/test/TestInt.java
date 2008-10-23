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

import java.util.ArrayList;

import org.jnome.input.JavaMetaModelFactory;

import chameleon.core.type.Type;

/**
 * @author marko
 */
public class TestInt extends Test {

	/**
	 * @param arg0
	 * @throws Exception
	 */
	public TestInt(String arg0) throws Exception {
		super(arg0);
		String path1 = "c:\\chameleon\\test\\TestInt.java";
		//String path2 = "testsource\\gen\\";
		String path2 = "c:\\chameleon\\testsource\\gen\\java\\lang\\Object.java";
	    ArrayList al = new ArrayList();
	    al.add(path1);
	    al.add(path2);
		_files = JavaMetaModelFactory.loadFiles(al, ".java", true);
//    _files.add(new File("src/org/seastar/test/TestInt.seastar"));
    _mm = new JavaMetaModelFactory().getMetaModel(new DummyLinkage(), _files);
  }
  
  public void testInt() throws Exception {
     Type testInt = _mm.findType("TestInt");
  }

}
