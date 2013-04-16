package be.kuleuven.cs.distrinet.jnome.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;

import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.test.ModelTest;
import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicNamespaceProvider;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;

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
				Java java = (Java) view().language();
				Type exception = java.findType("exception.MyException",view().namespace());
				assertTrue(java.isCheckedException(exception));
		}
	}

	@Test
	public void testGenerics() throws ConfigException, LookupException, ProjectException {
		new CustomGenericsTest(project()).testExceptions();
	}

	@Override
	protected File projectFile() {
		return new File("testsource/testexceptions.xml");
	}

//	@Override
//	public void setLogLevels() {
//		Logger.getLogger("chameleon.caching").setLevel(Level.DEBUG);
//		Logger.getRootLogger().setLevel(Level.FATAL);
//	}

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