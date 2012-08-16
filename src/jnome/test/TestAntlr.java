package jnome.test;

import chameleon.input.ModelFactory;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.DirectoryProjectBuilder;
import chameleon.workspace.ProjectBuilder;
import chameleon.workspace.ProjectException;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	@Override
	public ProjectBuilder projectBuilder() throws ProjectException {
		DirectoryProjectBuilder provider = createBuilder();
		includeBase(provider, "testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
