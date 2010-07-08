package jnome.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestRejuse.class,TestJnome.class,TestAntlr.class,TestGenerics.class,TestGenericRejuse.class,
	                   TestChameleon.class,TestChameleonSupport.class})
public class AllTests {

}
