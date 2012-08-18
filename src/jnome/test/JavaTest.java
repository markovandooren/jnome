package jnome.test;

import java.io.File;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaFileInputSourceFactory;
import jnome.input.LazyJavaFileInputSourceFactory;

import org.junit.Before;
import org.junit.Test;

import chameleon.core.Config;
import chameleon.core.language.Language;
import chameleon.core.namespace.LazyNamespaceFactory;
import chameleon.core.namespace.NamespaceFactory;
import chameleon.core.namespace.RegularNamespaceFactory;
import chameleon.core.namespace.RootNamespace;
import chameleon.input.ModelFactory;
import chameleon.oo.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.CompositeTest;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

public abstract class JavaTest extends CompositeTest {
	
	/**
	 * Set the log levels of Log4j. By default, nothing is changed.
	 */
	@Before
	public void setLogLevels() {
//		Logger.getRootLogger().setLevel(Level.FATAL);
//		Logger.getLogger("chameleon.test.expression").setLevel(Level.FATAL);
	}
	
	@Before
	public void setMultiThreading() {
		Config.setSingleThreaded(true);
	}
	
//	@Override
//	public void setCaching() {
//	  Config.setCaching(false);
//	}
	
	/**
	 * Test getType expressions in the namespaces provided
	 * by the namespace provider.
	 */
	@Test
	public void testExpressions() throws Exception {
		new ExpressionTest(project(), typeProvider()).testExpressionTypes();
	}

	public ElementProvider<Type> typeProvider() {
		return new BasicDescendantProvider<Type>(namespaceProvider(), Type.class);
	}

	/**
	 * Test the verification by invoking verify() for all namespace parts, and checking if the result is valid.
	 */
	@Test @Override
	public void testVerification() throws Exception {
	}

	protected Project createProject() {
		Java language = new JavaLanguageFactory().create();
		
		NamespaceFactory nsFactory = createNamespaceFactory();

		Project project = new Project("test",new RootNamespace(nsFactory), language);
		
//
//		
//		String fileExtension = ".java";
//		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(project, fileExtension,null, factory);
//		return provider;
		return project;
	}
	
	protected void includeCustom(Project project, String rootDirectory) throws ProjectException {
		FileInputSourceFactory factory = createFactory(project.language());
		File root = new File(rootDirectory);
		DirectoryLoader provider = new DirectoryLoader(project, fileExtension(), root, factory);
	}
	
	public String fileExtension() {
		return ".java";
	}
	
	protected FileInputSourceFactory createFactory(Language language) {
		return new JavaFileInputSourceFactory(language.plugin(ModelFactory.class));
//		return new LazyJavaFileInputSourceFactory(language.plugin(ModelFactory.class));
	}

	private NamespaceFactory createNamespaceFactory() {
	return new RegularNamespaceFactory();
//  return new LazyNamespaceFactory();
}


	protected void includeBase(Project project, String rootDirectory) throws ProjectException {
		includeCustom(project, rootDirectory);
		project.language().plugin(ModelFactory.class).initializePredefinedElements();
	}

//	@Test @Override
//	public void testClone() throws Exception {
//	}
//	@Test @Override
//	public void testChildren() throws Exception {
//	}
//	@Test @Override
//	public void testCrossReferences() throws Exception {
//	}
}
