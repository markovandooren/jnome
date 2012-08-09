package jnome.test;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.core.namespace.RootNamespace;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.DirectoryProjectBuilder;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectBuilder;

/**
 * @author Marko van Dooren
 */
public class TestJnome extends JavaTest {

	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.jnome");
	}
	
	public ProjectBuilder projectBuilder() {
		Java lang = new JavaLanguageFactory().create();
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new Project("test", new RootNamespace(), lang), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jregex"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jnome"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jutil"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"junit3.8.1"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jakarta-log4j-1.2.8"+provider.separator()+"src"+provider.separator()+"java"+provider.separator());
		return provider;
	}

	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
}
