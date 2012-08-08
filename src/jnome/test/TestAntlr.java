package jnome.test;

import jnome.input.JavaModelFactory;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.DirectoryProjectBuilder;
import chameleon.workspace.ProjectBuilder;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	@Override
	public ProjectBuilder projectBuilder() {
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
