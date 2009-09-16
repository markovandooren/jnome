package jnome.test;

import org.junit.Before;
import org.junit.Test;

import chameleon.core.Config;
import chameleon.core.reference.CrossReference;
import chameleon.core.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.CloneAndChildTest;
import chameleon.test.CrossReferenceTest;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.test.provider.ModelProvider;

public abstract class JavaTest {
	
	@Before
	public void setCaching() {
    Config.setCaching(true);
	}

	/**
	 * Set the log levels of Log4j. By default, nothing is changed.
	 */
	@Before
	public void setLogLevels() {
		
	}
	
	/**
	 * Test getType expressions in the namespaces provided
	 * by the namespace provider.
	 */
	@Test
	public void testExpressions() throws Exception {
		new ExpressionTest(modelProvider(), typeProvider()).testExpressionTypes();
	}

	/**
	 * Test clone and children for all elements in the namespaces provided
	 * by the namespace provider.
	 */
	@Test
	public void testCloneAndChildren() throws Exception {
		new CloneAndChildTest(modelProvider(), namespaceProvider()).testClone();
	}
	
	/**
	 * Test getElement and getDeclarator for all cross-reference in all namespaces
	 * provided by the namespace provider.
	 */
	@Test
	public void testCrossReferences() throws Exception {
		new CrossReferenceTest(modelProvider(), new BasicDescendantProvider<CrossReference>(namespaceProvider(), CrossReference.class)).testCrossReferences();
	}

	/**
	 * A provider for the model to be tested.
	 */
	public abstract ModelProvider modelProvider();

	/**
	 * A provider for the namespaces to be tested.
	 */
	public abstract BasicNamespaceProvider namespaceProvider();
	
	public ElementProvider<Type> typeProvider() {
		return new BasicDescendantProvider<Type>(namespaceProvider(), Type.class);
	}

}
