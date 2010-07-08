package jnome.test;

import java.util.Collection;

import org.rejuse.predicate.SafePredicate;

import chameleon.core.language.Language;
import chameleon.oo.type.Type;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ElementProvider;

public class TestChameleonSupport extends TestChameleon {

	@Override
	public BasicModelProvider modelProvider() {
		BasicModelProvider provider = super.modelProvider();
		provider.includeCustom("testsource"+provider.separator()+"chameleon-support"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-3.2"+provider.separator()+"src"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("chameleon.support");
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
