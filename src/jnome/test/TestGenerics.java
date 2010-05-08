package jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jnome.core.language.Java;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import chameleon.core.lookup.LookupException;
import chameleon.input.ParseException;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.SuperWildcard;
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
		Java language = (Java)language();
		BasicJavaTypeReference tref1 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref1.setUniParent(language.defaultNamespace());
		tref1.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.String")));
		BasicJavaTypeReference tref2 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref2.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.String")));
		tref2.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref3 = (BasicJavaTypeReference) language.createTypeReference("generics.SubList");
		tref3.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.String")));
		tref3.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref4 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref4.addArgument(language.createBasicTypeArgument(language.createTypeReference("java.lang.Object")));
		tref4.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref5 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref5.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.CharSequence")));
		tref5.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref6 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref6.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.String")));
		tref6.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref7 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref7.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
		tref7.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref7duo = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref7duo.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
		tref7duo.setUniParent(language.defaultNamespace());
		BasicJavaTypeReference tref8 = (BasicJavaTypeReference) language.createTypeReference("generics.List");
		tref8.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.CharSequence")));
		tref8.setUniParent(language.defaultNamespace());

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
		if(! type3.subTypeOf(type1)) {
			System.out.println("debug");
		}
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
  public void testGenerics() throws LookupException, ParseException, IOException {
  	new CustomGenericsTest(modelProvider()).testSubtyping();
  }


	@Override
	public void setLogLevels() {
		Logger.getLogger("chameleon.caching").setLevel(Level.DEBUG);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

	@Override
	public ModelProvider modelProvider() {
		BasicModelProvider provider = new BasicModelProvider(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"generics"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("generics");
	}
}
