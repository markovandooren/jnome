package be.kuleuven.cs.distrinet.jnome.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.test.ModelTest;
import org.aikodi.chameleon.test.provider.BasicDescendantProvider;
import org.aikodi.chameleon.test.provider.BasicNamespaceProvider;
import org.aikodi.chameleon.test.provider.ElementProvider;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.rejuse.predicate.SafePredicate;
import org.junit.Test;

/**
 * @author Marko van Dooren
 */
public class TestCurrent extends JavaTest {

	@Override
	protected File projectFile() {
		return new File(TEST_DATA,"testcurrent.xml");
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
