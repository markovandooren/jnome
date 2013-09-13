package be.kuleuven.cs.distrinet.jnome.test;

import java.io.File;
import java.util.Collection;

import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicDescendantProvider;
import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicNamespaceProvider;
import be.kuleuven.cs.distrinet.chameleon.test.provider.ElementProvider;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

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
