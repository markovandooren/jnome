package be.kuleuven.cs.distrinet.jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.test.ModelTest;
import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicNamespaceProvider;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;

/**
 * @author Marko van Dooren
 */
public class TestGenerics extends JavaTest {

	public static class CustomGenericsTest extends ModelTest {

	public CustomGenericsTest(Project provider) throws ProjectException {
		super(provider);
	}

	@Test
	public void testSubtyping() throws LookupException {
		Java language = (Java)view().language();
		Namespace ns = view().namespace();
		BasicJavaTypeReference tref1 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref1.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.String")));
		tref1.setUniParent(ns);
		BasicJavaTypeReference tref2 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref2.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.String")));
		tref2.setUniParent(ns);
		BasicJavaTypeReference tref3 = (BasicJavaTypeReference) language.createTypeReference("generics.SubList");
		tref3.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.String")));
		tref3.setUniParent(ns);
		BasicJavaTypeReference tref4 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref4.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.Object")));
		tref4.setUniParent(ns);
		BasicJavaTypeReference tref5 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref5.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.CharSequence")));
		tref5.setUniParent(ns);
		BasicJavaTypeReference tref6 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref6.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.String")));
		tref6.setUniParent(ns);
		BasicJavaTypeReference tref7 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref7.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
		tref7.setUniParent(ns);
		BasicJavaTypeReference tref7duo = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref7duo.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
		tref7duo.setUniParent(ns);
		BasicJavaTypeReference tref8 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref8.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.CharSequence")));
		tref8.setUniParent(ns);

		Type type1 = tref1.getType();
		Type type2 = tref2.getType();
		Type type3 = tref3.getType();
		Type type4 = tref4.getType();
		Type type5 = tref5.getType();
		Type type6 = tref6.getType();
		Type type7 = tref7.getType();
		Type type7duo = tref7duo.getType();
		Type type8 = tref8.getType();
		assertTrue(type2.subTypeOf(type1));
		assertTrue(type1.sameAs(type2));
		assertTrue(type1.equals(type2));
		assertTrue(type3.subTypeOf(type1));
		assertFalse(type4.subTypeOf(type1));
		assertFalse(type1.subTypeOf(type4));
		assertTrue(type1.subTypeOf(type5));
		assertFalse(type4.subTypeOf(type5));
		assertFalse(type5.subTypeOf(type1));
		assertTrue(type6.subTypeOf(type5));
		assertTrue(type2.subTypeOf(type6));
		assertFalse(type5.subTypeOf(type6));
		assertTrue(type4.subTypeOf(type7));
		assertTrue(type4.subTypeOf(type8));
		assertTrue(type8.subTypeOf(type7));
		assertFalse(type5.subTypeOf(type8));
		assertFalse(type5.subTypeOf(type7));
		assertFalse(type6.subTypeOf(type8));
		assertFalse(type6.subTypeOf(type7));
	}
	}
	
//	@Override
//	public void setCaching() {
//    Config.setCaching(true);
//	}
	
	@Test
  public void testGenerics() throws LookupException, ProjectException, ConfigException {
  	new CustomGenericsTest(project()).testSubtyping();
  }


//	@Override
//	public void setLogLevels() {
//		Logger.getLogger("chameleon.caching").setLevel(Level.DEBUG);
//		Logger.getRootLogger().setLevel(Level.FATAL);
//	}

	@Override
	protected File projectFile() {
		return new File("testsource/testgenerics.xml");
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
		return new BasicNamespaceProvider("generics");
	}
}
