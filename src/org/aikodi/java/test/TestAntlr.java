package org.aikodi.java.test;

import java.io.File;

import org.aikodi.chameleon.test.provider.BasicNamespaceProvider;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	@Override
	protected File projectFile() {
		return new File(TEST_DATA,"testantlr.xml");
	}
	
	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
