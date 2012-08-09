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
public class TestGenericRejuse extends JavaTest {

	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

	@Override
	public ProjectBuilder projectBuilder() {
		Java lang = new JavaLanguageFactory().create();
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new Project("test", new RootNamespace(), lang), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jregex"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"rejuse"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"junit4.7"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.rejuse");
	}
	
//	public ElementProvider<Type> typeProvider() {
//		return new ElementProvider<Type>() {
//
//			public Collection<Type> elements(Language language) {
//				Collection<Type> types = new BasicDescendantProvider<Type>(namespaceProvider(), Type.class).elements(language);
//				new SafePredicate<Type>() {
//
//					@Override
//					public boolean eval(Type object) {
//						return object.getFullyQualifiedName().equals("org.rejuse.property.PropertySet");
//					}
//				}.filter(types);
//				return types;
//			}
//		};
//	}
}
