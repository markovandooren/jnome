package jnome.test;

import java.io.File;

import chameleon.test.provider.BasicNamespaceProvider;

/**
 * @author Marko van Dooren
 */
public class TestJnome extends JavaTest {

	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.jnome");
	}
	
	@Override
	protected File projectFile() {
		return new File("testsource/testjnome.xml");
	}

//	public void setLogLevels() {
//		super.setLogLevels();
//		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
//		Logger.getRootLogger().setLevel(Level.FATAL);
//	}
}
