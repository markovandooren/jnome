package org.aikodi.java.test;

import org.aikodi.chameleon.core.element.TestElement;
import org.aikodi.chameleon.test.events.TestEvents;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestEvents.class
	,TestElement.class
	,TestRejuse.class
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
