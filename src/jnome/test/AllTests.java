package jnome.test;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Marko van Dooren
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for new jnome structure");
//	  suite.addTestSuite(TestRejuse.class);
//    suite.addTestSuite(TestJnome.class);
//    suite.addTestSuite(TestAntlr.class);
		return suite;
	}
}
