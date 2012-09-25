package jnome.test;

import java.io.File;

import chameleon.test.provider.BasicNamespaceProvider;

public class TestChameleonSupport extends TestChameleon {

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("chameleon.support");
	}

	@Override
	protected File projectFile() {
		return new File("testsource/testchameleonsupport.xml");
	}

//	public ElementProvider<Type> typeProvider() {
//		return new ElementProvider<Type>() {
//
//			public Collection<Type> elements(Language language) {
//				Collection<Type> types = new BasicDescendantProvider<Type>(namespaceProvider(), Type.class).elements(language);
//				new SafePredicate<Type>() {
//
//					@Override
//					public boolean eval(Type object) {
//						return object.getFullyQualifiedName().equals("chameleon.core.property.StaticChameleonProperty");
//					}
//				}.filter(types);
//				return types;
//			}
//		};
//	}
}
