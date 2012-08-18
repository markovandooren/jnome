package jnome.test;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

public class TestChameleonSupport extends TestChameleon {

	@Override
	public Project makeProject() throws ProjectException {
		Project project = super.makeProject();
		includeCustom(project,"testsource"+separator()+"chameleon-support"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"antlr-3.2"+separator()+"src"+separator());
		return project;
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
