package be.kuleuven.cs.distrinet.jnome.test;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicNamespaceProvider;

/**
 * @author Marko van Dooren
 */
public class TestJnome extends JavaTest {

	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.jnome");
	}
	
	@Override
	protected File projectFile() {
		return new File(TEST_DATA,"testjnome.xml");
	}
	
	//org.jnome.mm.java.LanguageElementFactory

//	public void setLogLevels() {
//		super.setLogLevels();
//		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
//		Logger.getRootLogger().setLevel(Level.FATAL);
//	}
}
