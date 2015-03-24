package be.kuleuven.cs.distrinet.jnome.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.test.ModelTest;
import org.aikodi.chameleon.test.provider.BasicNamespaceProvider;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectException;
import org.junit.Test;

/**
 * @author Marko van Dooren
 */
public class TestExceptions extends JavaTest {

	public static class CustomGenericsTest extends ModelTest {

		public CustomGenericsTest(Project provider) throws ProjectException {
			super(provider);
		}

		public void testExceptions() throws LookupException {
				Java7 java = (Java7) view().language();
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
		return new File(TEST_DATA,"testexceptions.xml");
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
