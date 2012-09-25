package jnome.test;

import java.io.File;

import chameleon.test.provider.BasicNamespaceProvider;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	@Override
	protected File projectFile() {
		return new File("testsource/testantlr.xml");
	}
	
	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
