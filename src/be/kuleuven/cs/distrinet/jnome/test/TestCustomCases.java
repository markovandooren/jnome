package be.kuleuven.cs.distrinet.jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.aikodi.chameleon.core.document.Document;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.oo.expression.ExpressionFactory;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.method.RegularImplementation;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.statement.Block;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.stub.StubExpression;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.statement.StatementExpression;
import org.aikodi.chameleon.support.variable.LocalVariableDeclarator;
import org.aikodi.chameleon.test.ModelTest;
import org.aikodi.chameleon.test.provider.BasicNamespaceProvider;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.DirectDocumentLoader;
import org.aikodi.chameleon.workspace.DirectScanner;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectException;
import org.junit.Test;

import be.kuleuven.cs.distrinet.jnome.core.expression.JavaNameExpression;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.JavaMethodInvocation;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.namespacedeclaration.JavaNamespaceDeclaration;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaType;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;
import be.kuleuven.cs.distrinet.jnome.core.variable.JavaVariableDeclaration;

/**
 * @author Marko van Dooren
 */
public class TestCustomCases extends JavaTest {

	public static class GenericMethodInvocationTest extends ModelTest {

		public GenericMethodInvocationTest(Project project) throws ProjectException {
			super(project);
		}
		
		public void testMethodCalls() throws LookupException, ProjectException {
			Java7 language = (Java7)view().language();
			Namespace ns = view().namespace();
			DirectScanner scanner = new DirectScanner();
			view().addSource(scanner);
			Document document = new Document();
      NamespaceDeclaration nsd = new JavaNamespaceDeclaration("test");
      document.add(nsd);
			RegularJavaType type = new RegularJavaType("Test");
			nsd.add(type);
			DirectDocumentLoader loader = new DirectDocumentLoader(scanner, document);
			Type test = ns.find("test.Test", Type.class);
			NormalMethod caller = new NormalMethod(new SimpleNameMethodHeader("m", new BasicJavaTypeReference("void")));
			Block body = new Block();
			caller.setImplementation(new RegularImplementation(body));
			
			BasicJavaTypeReference listRef = new BasicJavaTypeReference("java.util.List");
			listRef.addArgument(new PureWildcard());
			LocalVariableDeclarator declarator = new LocalVariableDeclarator(listRef);
			declarator.add(new JavaVariableDeclaration("first"));
			declarator.add(new JavaVariableDeclaration("second"));
			body.addStatement(declarator);
			JavaMethodInvocation call = new JavaMethodInvocation("two", null);
			call.addArgument(new JavaNameExpression("first"));
			call.addArgument(new JavaNameExpression("second"));
			body.addStatement(new StatementExpression(call));
			type.add(caller);
			
			SimpleNameMethodHeader calleeHeader = new SimpleNameMethodHeader("two", new BasicJavaTypeReference("void"));
			calleeHeader.addTypeParameter(new FormalTypeParameter("T"));
			BasicJavaTypeReference listTRef = new BasicJavaTypeReference("java.util.List");
			listTRef.addArgument(new EqualityTypeArgument(new BasicJavaTypeReference("T")));
			calleeHeader.addFormalParameter(new FormalParameter("first", listTRef.clone(listTRef)));
			calleeHeader.addFormalParameter(new FormalParameter("second", listTRef.clone(listTRef)));
			NormalMethod callee = new NormalMethod(calleeHeader);
			callee.setImplementation(new RegularImplementation(new Block()));
			type.add(callee);
			try {
			  call.getElement();
				assertTrue(false);
			} catch(LookupException exc) {
			}
		}
		
	}
	
	public static class GenericTypeReferenceTest extends ModelTest {

		public GenericTypeReferenceTest(Project provider) throws ProjectException {
			super(provider);
		}

		public void testSubtyping() throws LookupException {
			Java7 language = (Java7)view().language();
			Namespace ns = view().namespace();
			BasicJavaTypeReference listOfStringRef = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfStringRef.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.String")));
			listOfStringRef.setUniParent(ns);
			BasicJavaTypeReference tref2 = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			tref2.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.String")));
			tref2.setUniParent(ns);
			BasicJavaTypeReference tref3 = (BasicJavaTypeReference) language.createTypeReference("test.generics.SubList");
			tref3.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.String")));
			tref3.setUniParent(ns);
			BasicJavaTypeReference listOfObjectRef = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfObjectRef.addArgument(language.createEqualityTypeArgument(language.createTypeReference("java.lang.Object")));
			listOfObjectRef.setUniParent(ns);
			BasicJavaTypeReference listOfExtendsCharSequenceRef = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfExtendsCharSequenceRef.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.CharSequence")));
			listOfExtendsCharSequenceRef.setUniParent(ns);
			BasicJavaTypeReference tref6 = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			tref6.addArgument(language.createExtendsWildcard(language.createTypeReference("java.lang.String")));
			tref6.setUniParent(ns);
			BasicJavaTypeReference listOfSuperOfStringRef = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfSuperOfStringRef.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
			listOfSuperOfStringRef.setUniParent(ns);
			BasicJavaTypeReference listOfSuperOfStringRefDuo = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listOfSuperOfStringRefDuo.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.String")));
			listOfSuperOfStringRefDuo.setUniParent(ns);
			BasicJavaTypeReference listSuperOfCharSequenceRef = (BasicJavaTypeReference) language.createTypeReference("test.generics.List");
			listSuperOfCharSequenceRef.addArgument(language.createSuperWildcard(language.createTypeReference("java.lang.CharSequence")));
			listSuperOfCharSequenceRef.setUniParent(ns);

			Type listOfString = listOfStringRef.getElement(); // test.generics.List<java.lang.String>
			Type type2 = tref2.getElement();
			Type type3 = tref3.getElement();
			Type listOfObject = listOfObjectRef.getElement(); // test.generics.List<java.lang.Object>
			Type listOfExtendsCharSequence = listOfExtendsCharSequenceRef.getElement(); // test.generics.List<? extends CharSequence>
			Type type6 = tref6.getElement();
			JavaType listOfSuperOfString = (JavaType) listOfSuperOfStringRef.getElement();
			JavaType listOfSuperOfStringDuo = (JavaType) listOfSuperOfStringRefDuo.getElement();
			Type listOfSuperOfStringCaptured = listOfSuperOfString.captureConversion();
			Type listOfSuperOfStringDuoCaptured = listOfSuperOfStringDuo.captureConversion();
			Type listSuperOfCharSequence = listSuperOfCharSequenceRef.getElement();
			assertTrue(type2.subtypeOf(listOfString));
			assertTrue(listOfString.sameAs(type2));
			assertTrue(listOfString.equals(type2));
			assertTrue(type3.subtypeOf(listOfString));
			assertFalse(listOfObject.subtypeOf(listOfString));
			assertFalse(listOfString.subtypeOf(listOfObject));
			assertTrue(listOfString.subtypeOf(listOfExtendsCharSequence));
			assertFalse(listOfObject.subtypeOf(listOfExtendsCharSequence));
			assertFalse(listOfExtendsCharSequence.subtypeOf(listOfString));
			assertTrue(type6.subtypeOf(listOfExtendsCharSequence));
			assertTrue(type2.subtypeOf(type6));
			assertFalse(listOfExtendsCharSequence.subtypeOf(type6));
			assertTrue(listOfObject.subtypeOf(listOfSuperOfString));
			assertTrue(listOfObject.subtypeOf(listSuperOfCharSequence));
//			assertTrue(listSuperOfCharSequence.subtypeOf(listOfSuperOfString));
			assertFalse(listOfExtendsCharSequence.subtypeOf(listSuperOfCharSequence));
			assertFalse(listOfExtendsCharSequence.subtypeOf(listOfSuperOfString));
			assertFalse(type6.subtypeOf(listSuperOfCharSequence));
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
	public void testTypeReferences() throws LookupException, ProjectException, ConfigException {
		GenericTypeReferenceTest customGenericsTest = new GenericTypeReferenceTest(project());
		customGenericsTest.testSubtyping();
		customGenericsTest.testWildcards();
	}

	@Test
	public void testMethodCalls() throws LookupException, ProjectException {
		GenericMethodInvocationTest test = new GenericMethodInvocationTest(project());
		test.testMethodCalls();
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
