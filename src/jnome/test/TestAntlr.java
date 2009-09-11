package jnome.test;

import jnome.input.JavaModelFactory;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ModelProvider;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	public ModelProvider modelProvider() {
		BasicModelProvider provider = new BasicModelProvider(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
