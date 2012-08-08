package jnome.test;

import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.DirectoryProjectBuilder;
import chameleon.workspace.ProjectBuilder;

/**
 * @author Marko van Dooren
 */
public class TestRejuse extends JavaTest {

	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

	@Override
	public ProjectBuilder projectBuilder() {
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jregex"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jutil"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"junit3.8.1"+provider.separator()+"src"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.jutil");
	}
	
//public ElementProvider<Type> typeProvider() {
//return new ElementProvider<Type>() {
//
//	public Collection<Type> elements(Language language) {
//		Collection<Type> types = new BasicDescendantProvider<Type>(namespaceProvider(), Type.class).elements(language);
//		new SafePredicate<Type>() {
//
//			@Override
//			public boolean eval(Type object) {
//				return object.getFullyQualifiedName().equals("org.jutil.Test");
//			}
//		}.filter(types);
//		return types;
//	}
//};
//}

}
