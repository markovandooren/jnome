package jnome.test;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Marko van Dooren
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for new jnome structure");
		//$JUnit-BEGIN$
   
	  suite.addTestSuite(TestRejuse.class);
    //suite.addTestSuite(TestJnomeTNG.class);//ERROR
	//source van rejuse nodig
    suite.addTestSuite(TestJnome.class);
    suite.addTestSuite(TestJRegex.class);
    suite.addTestSuite(TestAntlr.class);//OK
		//$JUnit-END$
		return suite;
	}
}
