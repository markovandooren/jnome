package jnome.test;

import java.util.Collection;

import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rejuse.predicate.SafePredicate;

import chameleon.core.language.Language;
import chameleon.oo.type.Type;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.test.provider.ModelProvider;

/**
 * @author Marko van Dooren
 */
public class TestGenericRejuse extends JavaTest {

	@Override
	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}

	@Override
	public ModelProvider modelProvider() {
		BasicModelProvider provider = new BasicModelProvider(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jregex"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"rejuse"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"junit4.7"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.rejuse");
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
//						return object.getFullyQualifiedName().equals("org.rejuse.property.PropertySet");
//					}
//				}.filter(types);
//				return types;
//			}
//		};
//	}
}
