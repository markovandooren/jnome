package be.kuleuven.cs.distrinet.jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.oo.expression.ExpressionFactory;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.stub.StubExpression;
import org.aikodi.chameleon.test.ModelTest;
import org.aikodi.chameleon.test.provider.BasicNamespaceProvider;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectException;
import org.junit.Test;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;

/**
 * @author Marko van Dooren
 */
public class TestCustomCases extends JavaTest {

	public static class CustomGenericsTest extends ModelTest {

		public CustomGenericsTest(Project provider) throws ProjectException {
			super(provider);
		}

		public void testSubtyping() throws LookupException {
			Java7 language = (Java7)view().language();
			Namespace ns = view().namespace();
			BasicJavaTypeReference listOfString = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfString.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.String")));
			listOfString.setUniParent(ns);
			BasicJavaTypeReference tref2 = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			tref2.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.String")));
			tref2.setUniParent(ns);
			BasicJavaTypeReference tref3 = (BasicJavaTypeReference) language.createTypeReference("test.generics.SubList");
			tref3.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.String")));
			tref3.setUniParent(ns);
			BasicJavaTypeReference tref4 = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			tref4.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.Object")));
			tref4.setUniParent(ns);
			BasicJavaTypeReference listOfExtendsCharSequence = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfExtendsCharSequence.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.CharSequence")));
			listOfExtendsCharSequence.setUniParent(ns);
			BasicJavaTypeReference tref6 = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			tref6.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.String")));
			tref6.setUniParent(ns);
			BasicJavaTypeReference listOfSuperOfStringRef = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfSuperOfStringRef.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
			listOfSuperOfStringRef.setUniParent(ns);
			BasicJavaTypeReference listOfSuperOfStringRefDuo = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfSuperOfStringRefDuo.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
			listOfSuperOfStringRefDuo.setUniParent(ns);
			BasicJavaTypeReference tref8 = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			tref8.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.CharSequence")));
			tref8.setUniParent(ns);

			Type type1 = listOfString.getElement(); // test.generics.List<java.lang.String>
			Type type2 = tref2.getElement();
			Type type3 = tref3.getElement();
			Type type4 = tref4.getElement(); // test.generics.List<java.lang.Object>
			Type type5 = listOfExtendsCharSequence.getElement();
			Type type6 = tref6.getElement();
			JavaType listOfSuperOfString = (JavaType) listOfSuperOfStringRef.getElement();
			JavaType listOfSuperOfStringDuo = (JavaType) listOfSuperOfStringRefDuo.getElement();
			Type listOfSuperOfStringCaptured = listOfSuperOfString.captureConversion();
			Type listOfSuperOfStringDuoCaptured = listOfSuperOfStringDuo.captureConversion();
			Type type8 = tref8.getElement();
			assertTrue(type2.subtypeOf(type1));
			assertTrue(type1.sameAs(type2));
			assertTrue(type1.equals(type2));
			assertTrue(type3.subtypeOf(type1));
			assertFalse(type4.subtypeOf(type1));
			assertFalse(type1.subtypeOf(type4));
			assertTrue(type1.subtypeOf(type5));
			assertFalse(type4.subtypeOf(type5));
			assertFalse(type5.subtypeOf(type1));
			assertTrue(type6.subtypeOf(type5));
			assertTrue(type2.subtypeOf(type6));
			assertFalse(type5.subtypeOf(type6));
			assertTrue(type4.subtypeOf(listOfSuperOfString));
			assertTrue(type4.subtypeOf(type8));
			assertTrue(type8.subtypeOf(listOfSuperOfString));
			assertFalse(type5.subtypeOf(type8));
			assertFalse(type5.subtypeOf(listOfSuperOfString));
			assertFalse(type6.subtypeOf(type8));
			assertFalse(type6.subtypeOf(listOfSuperOfString));
			assertTrue(listOfSuperOfString.subtypeOf(listOfSuperOfStringDuo));
			assertFalse(listOfSuperOfStringCaptured.subtypeOf(listOfSuperOfStringDuoCaptured));
			assertTrue(listOfSuperOfStringCaptured.subtypeOf(listOfSuperOfString));
			assertTrue(listOfSuperOfStringCaptured.subtypeOf(listOfSuperOfStringDuo));
			assertTrue(listOfSuperOfStringDuoCaptured.subtypeOf(listOfSuperOfString));
			assertTrue(listOfSuperOfStringDuoCaptured.subtypeOf(listOfSuperOfStringDuo));
		}

		public void testWildcards() throws LookupException {
			Java7 java = (Java7)view().language();
			Namespace ns = view().namespace();
			BasicJavaTypeReference listStringRef = java.createTypeReference("test.generics.List");
			listStringRef.addArgument(java.createEqualityTypeArgument(java.createTypeReference("java.lang.String")));
			listStringRef.setUniParent(ns);

			BasicJavaTypeReference listSuperStringRef = java.createTypeReference("test.generics.List");
			listSuperStringRef.addArgument(java.createSuperWildcard(java.createTypeReference("java.lang.String")));
			listSuperStringRef.setUniParent(ns);

			BasicJavaTypeReference listSuperCharSequenceRef = java.createTypeReference("test.generics.List");
			listSuperCharSequenceRef.addArgument(java.createSuperWildcard(java.createTypeReference("java.lang.CharSequence")));
			listSuperCharSequenceRef.setUniParent(ns);

			BasicJavaTypeReference listExtendsCharSequenceRef = java.createTypeReference("test.generics.List");
			listExtendsCharSequenceRef.addArgument(java.createExtendsWildcard(java.createTypeReference("java.lang.CharSequence")));
			listExtendsCharSequenceRef.setUniParent(ns);


			Type string = ns.find("java.lang.String", Type.class);
			Type listString = listStringRef.getElement();
			Type listSuperString = listSuperStringRef.getElement();
			Type listSuperCharSequence = listSuperCharSequenceRef.getElement();
			Type listExtendsCharSequence = listExtendsCharSequenceRef.getElement();

			assertTrue(listString.subtypeOf(listSuperString));
			assertFalse(listString.subtypeOf(listSuperCharSequence));

			{
				MethodInvocation<?> invocation = java.plugin(ExpressionFactory.class).createInvocation("add", new StubExpression(listSuperString));
				invocation.addArgument(new StubExpression(string));
				invocation.setUniParent(ns);
				try {
					invocation.getElement();
				} catch(LookupException exc) {
					assertTrue(false);
				}
			}

			{
				MethodInvocation invocationSuper = java.plugin(ExpressionFactory.class).createInvocation("add", new StubExpression(listSuperCharSequence));
				invocationSuper.addArgument(new StubExpression(string));
				invocationSuper.setUniParent(ns);
				try {
					invocationSuper.getElement();
				} catch(LookupException exc) {
					assertTrue(false);
				}
			}

			{
				MethodInvocation invocationExtends = java.plugin(ExpressionFactory.class).createInvocation("add", new StubExpression(listExtendsCharSequence));
				invocationExtends.addArgument(new StubExpression(string));
				invocationExtends.setUniParent(ns);
				try {
					invocationExtends.getElement();
					assertTrue(false);
				} catch(LookupException exc) {
				}
			}
		}

	}

	@Test
	public void testGenerics() throws LookupException, ProjectException, ConfigException {
		CustomGenericsTest customGenericsTest = new CustomGenericsTest(project());
		customGenericsTest.testSubtyping();
		customGenericsTest.testWildcards();
	}


	@Override
	protected File projectFile() {
		return new File(TEST_DATA,"testcustom.xml");
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
		return new BasicNamespaceProvider("test");
	}

	//	public ElementProvider<Type> typeProvider() {
	//		return new ElementProvider<Type>() {
	//
	//			public Collection<Type> elements(View language) {
	//				Collection<Type> types = new BasicDescendantProvider<Type>(namespaceProvider(), Type.class).elements(language);
	//				new SafePredicate<Type>() {
	//
	//					@Override
	//					public boolean eval(Type object) {
	//						return object.getFullyQualifiedName().equals("test.generics.RawInvocationGenericMethod");
	//					}
	//				}.filter(types);
	//				return types;
	//			}
	//		};
	//	}
}
