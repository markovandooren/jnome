package jnome.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

/**
 * @author Marko van Dooren
 */
public class TestChameleon extends JavaTest {

	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

	@Override
	public Project makeProject() throws ProjectException {
		Project project = createProject();
		includeCustom(project,"testsource"+separator()+"jregex"+separator());
		includeCustom(project,"testsource"+separator()+"rejuse"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"hamcrest-1.2"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"junit4.7"+separator());
		includeCustom(project,"testsource"+separator()+"chameleon"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"apache-log4j-1.2.15"+separator()+"src"+separator()+"main"+separator()+"java"+separator());
		return project;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("chameleon");
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
//						return object.getFullyQualifiedName().equals("chameleon.core.property.Defined");
//					}
//				}.filter(types);
//				return types;
//			}
//		};
//	}
}
