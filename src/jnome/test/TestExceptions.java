package jnome.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
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
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectLoader;
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

	@Test
	public void testGenerics() throws LookupException, ProjectException {
		new CustomGenericsTest(project()).testExceptions();
	}

	@Override
	protected File projectFile() {
		return new File("testsource/testexceptions.xml");
	}

	@Override
	public void setLogLevels() {
		Logger.getLogger("chameleon.caching").setLevel(Level.DEBUG);
		Logger.getRootLogger().setLevel(Level.FATAL);
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
