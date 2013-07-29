package be.kuleuven.cs.distrinet.jnome.test;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicNamespaceProvider;

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
