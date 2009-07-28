package jnome.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestRejuse.class,TestJnome.class,TestAntlr.class,TestJnomeClone.class,TestGenerics.class})
public class AllTests {


//	public static Test suite() {
//		TestSuite suite = new TestSuite("Test for new jnome structure");
////	  suite.addTestSuite(TestRejuse.class);
////    suite.addTestSuite(TestJnome.class);
////    suite.addTestSuite(TestAntlr.class);
//		return suite;
//	}
}
