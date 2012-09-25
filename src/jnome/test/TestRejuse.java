package jnome.test;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectLoader;
import chameleon.workspace.ProjectException;

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
	protected File projectFile() {
		return new File("testsource/testrejuse.xml");
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
