package jnome.test;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaFileInputSourceFactory;

import org.junit.Before;
import org.junit.Test;

import chameleon.core.Config;
import chameleon.core.namespace.RootNamespace;
import chameleon.input.ModelFactory;
import chameleon.oo.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.CompositeTest;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.workspace.DirectoryProjectBuilder;
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

	protected DirectoryProjectBuilder createBuilder() {
		Java language = new JavaLanguageFactory().create();
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new Project("test",new RootNamespace(), language), ".java",null, new JavaFileInputSourceFactory(language.plugin(ModelFactory.class)));
		return provider;
	}

	protected void includeBase(DirectoryProjectBuilder provider, String dirName) throws ProjectException {
		provider.includeCustom(dirName);
		provider.project().language().plugin(ModelFactory.class).initializePredefinedElements();
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
