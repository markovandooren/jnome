package jnome.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.RootNamespace;
import chameleon.input.ParseException;
import chameleon.oo.type.Type;
import chameleon.test.ModelTest;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.DirectoryProjectBuilder;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectBuilder;
import chameleon.workspace.ProjectException;

/**
 * @author Marko van Dooren
 */
public class TestExceptions extends JavaTest {

	public static class CustomGenericsTest extends ModelTest {

		public CustomGenericsTest(Project provider) throws ProjectException {
			super(provider);
		}

		@Test
		public void testExceptions() throws LookupException {
				Java java = (Java) project().language();
				Type exception = java.findType("exception.MyException");
				assertTrue(java.isCheckedException(exception));
		}
	}

	//	@Override
	//	public void setCaching() {
	//    Config.setCaching(true);
	//	}

	@Test
	public void testGenerics() throws LookupException, ProjectException {
		new CustomGenericsTest(project()).testExceptions();
	}


	@Override
	public void setLogLevels() {
		Logger.getLogger("chameleon.caching").setLevel(Level.DEBUG);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

	@Override
	public ProjectBuilder projectBuilder() throws ProjectException {
		Java lang = new JavaLanguageFactory().create();
		DirectoryProjectBuilder provider = new DirectoryProjectBuilder(new Project("test", new RootNamespace(), lang), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"exceptions"+provider.separator());
		return provider;
	}

	//	@Override @Test
	//	public void testExpressions() throws Exception {
	//		ExpressionTest expressionTest = new ExpressionTest(modelProvider(), typeProvider());
	//		List<RegularType> elements = expressionTest.language().defaultNamespace().descendants(RegularType.class);
	//		System.out.println("Starting to lock down the regular types in the model.");
	//		for(RegularType element:elements) {
	//			element.parentLink().lock();
	//		}
	//		System.out.println("Locked down the model.");
	//		expressionTest.testExpressionTypes();
	//	}


	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("exception");
	}
}
