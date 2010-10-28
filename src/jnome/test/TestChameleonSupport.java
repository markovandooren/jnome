package jnome.test;

import java.util.List;

import chameleon.core.expression.ActualArgument;
import chameleon.core.expression.ActualArgumentList;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;

public class TestChameleonSupport extends TestChameleon {

	@Override
	public BasicModelProvider modelProvider() {
		BasicModelProvider provider = super.modelProvider();
		provider.includeCustom("testsource"+provider.separator()+"chameleon-support"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-3.2"+provider.separator()+"src"+provider.separator());
		return provider;
	}

	
	
//	@Override
//	public void testExpressions() throws Exception {
//		List l = modelProvider().model().defaultNamespace().descendants(ActualArgument.class);
//		System.out.println("Actual arguments: "+l.size());
//		l = modelProvider().model().defaultNamespace().descendants(ActualArgumentList.class);
//		System.out.println("Actual argument lists: "+l.size());
//	}



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
