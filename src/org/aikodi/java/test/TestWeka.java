package org.aikodi.java.test;

import java.io.File;
import java.util.Collection;

import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.test.provider.BasicDescendantProvider;
import org.aikodi.chameleon.test.provider.BasicNamespaceProvider;
import org.aikodi.chameleon.test.provider.ElementProvider;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.rejuse.predicate.SafePredicate;

/**
 * @author Marko van Dooren
 */
public class TestWeka extends JavaTest {

	@Override
	protected File projectFile() {
		return new File(TEST_DATA,"testweka.xml");
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("weka");
	}

//	public ElementProvider<Type> typeProvider() {
//		return new ElementProvider<Type>() {
//
//			public Collection<Type> elements(View view) {
//				Collection<Type> types = new BasicDescendantProvider<Type>(namespaceProvider(), Type.class).elements(view);
//				new SafePredicate<Type>() {
//
//					@Override
//					public boolean eval(Type object) {
//						return object.getFullyQualifiedName().equals("weka.gui.experiment.AlgorithmListPanel");
//					}
//				}.filter(types);
//				return types;
//			}
//		};
//	}

}
