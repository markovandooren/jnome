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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author marko
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for new jnome structure");
		//$JUnit-BEGIN$
		suite.addTestSuite(Test1.class); //OK
		suite.addTestSuite(TestMethod.class);//OK
		suite.addTestSuite(TestVar.class);//OK
		suite.addTestSuite(TestInt.class); //OK
   
	  suite.addTestSuite(TestRejuse.class);
    //suite.addTestSuite(TestJnomeTNG.class);//ERROR
	//source van rejuse nodig
    suite.addTestSuite(TestJnome.class);
    suite.addTestSuite(TestAntlr.class);//OK
		//$JUnit-END$
		return suite;
	}
}
