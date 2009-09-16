package jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jnome.core.type.JavaTypeReference;
import jnome.input.JavaModelFactory;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Test;

import chameleon.core.Config;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.input.ParseException;
import chameleon.test.ModelTest;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ModelProvider;

/**
 * @author Marko van Dooren
 */
public class TestGenerics extends JavaTest {

	public static class CustomGenericsTest extends ModelTest {

	public CustomGenericsTest(ModelProvider provider) throws ParseException, IOException {
		super(provider);
	}

	@Test
	public void testSubtyping() throws LookupException {
		JavaTypeReference tref1 = new JavaTypeReference("test.List");
		tref1.setUniParent(language().defaultNamespace());
		tref1.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.String")));
		JavaTypeReference tref2 = new JavaTypeReference("test.List");
		tref2.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.String")));
		tref2.setUniParent(language().defaultNamespace());
		JavaTypeReference tref3 = new JavaTypeReference("test.SubList");
		tref3.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.String")));
		tref3.setUniParent(language().defaultNamespace());
		JavaTypeReference tref4 = new JavaTypeReference("test.List");
		tref4.addArgument(new BasicTypeArgument(new JavaTypeReference("java.lang.Object")));
		tref4.setUniParent(language().defaultNamespace());
		JavaTypeReference tref5 = new JavaTypeReference("test.List");
		tref5.addArgument(new ExtendsWildCard(new JavaTypeReference("java.lang.CharSequence")));
		tref5.setUniParent(language().defaultNamespace());
		JavaTypeReference tref6 = new JavaTypeReference("test.List");
		tref6.addArgument(new ExtendsWildCard(new JavaTypeReference("java.lang.String")));
		tref6.setUniParent(language().defaultNamespace());
		JavaTypeReference tref7 = new JavaTypeReference("test.List");
		tref7.addArgument(new SuperWildCard(new JavaTypeReference("java.lang.String")));
		tref7.setUniParent(language().defaultNamespace());
		JavaTypeReference tref8 = new JavaTypeReference("test.List");
		tref8.addArgument(new SuperWildCard(new JavaTypeReference("java.lang.CharSequence")));
		tref8.setUniParent(language().defaultNamespace());

		Type type1 = tref1.getType();
		Type type2 = tref2.getType();
		Type type3 = tref3.getType();
		Type type4 = tref4.getType();
		Type type5 = tref5.getType();
		Type type6 = tref6.getType();
		Type type7 = tref7.getType();
		Type type8 = tref8.getType();
		assertTrue(type2.subTypeOf(type1));
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
	
	@Override
	public void setCaching() {
    Config.setCaching(false);
	}
	
	@Test
  public void testGenerics() throws LookupException, ParseException, IOException {
  	new CustomGenericsTest(modelProvider()).testSubtyping();
  }


//	@Override
//	public void setLogLevels() {
//		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
//		Logger.getLogger("lookup.subtyping").setLevel(Level.DEBUG);
//		Logger.getRootLogger().setLevel(Level.FATAL);
//	}

	@Override
	public ModelProvider modelProvider() {
		BasicModelProvider provider = new BasicModelProvider(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"generics"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("test");
	}
}
