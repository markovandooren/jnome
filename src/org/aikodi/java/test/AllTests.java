package org.aikodi.java.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestRejuse.class
	,TestJnome.class
	,TestAntlr.class
	,TestCustomCases.class
	,TestGenericRejuse.class
	,TestChameleon.class
	,TestChameleonSupport.class
	,TestProjectConfig.class
//	,TestWeka.class
	})
public class AllTests {}
