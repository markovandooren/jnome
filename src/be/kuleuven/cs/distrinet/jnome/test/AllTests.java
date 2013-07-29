package be.kuleuven.cs.distrinet.jnome.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestRejuse.class,
	TestJnome.class,
	TestAntlr.class,
	TestCustomCases.class,
	TestGenericRejuse.class,
	TestChameleon.class,
	TestChameleonSupport.class,
	TestProjectConfig.class
	})
public class AllTests {}
