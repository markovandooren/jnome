package jnome.test;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import chameleon.core.namespace.RootNamespace;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.DirectoryProjectBuilder;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectBuilder;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	@Override
	public ProjectBuilder projectBuilder() {
		Java language = new JavaLanguageFactory().create();
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new Project("test",new RootNamespace(), language), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
