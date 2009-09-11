package jnome.test;

import org.junit.Test;

import chameleon.core.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.CloneAndChildTester;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.test.provider.ModelProvider;

public abstract class JavaTest {

	@Test
	public void testExpressions() throws Exception {
		new ExpressionTest(modelProvider(), typeProvider()).testExpressionTypes();
	}

	@Test
	public void testCloneAndChildren() throws Exception {
		new CloneAndChildTester(modelProvider(), namespaceProvider()).testClone();
	}

	public abstract ModelProvider modelProvider();

	public abstract BasicNamespaceProvider namespaceProvider();
	
	public ElementProvider<Type> typeProvider() {
		return new BasicDescendantProvider<Type>(namespaceProvider(), Type.class);
	}

}
