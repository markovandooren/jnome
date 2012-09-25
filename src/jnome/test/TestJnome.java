package jnome.test;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

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

	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
}
