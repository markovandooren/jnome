package jnome.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jnome.core.language.Java;
import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rejuse.predicate.SafePredicate;

import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.Type;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.test.provider.ModelProvider;

/**
 * @author Marko van Dooren
 */
public class TestChameleon extends JavaTest {

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
		provider.includeCustom("testsource"+provider.separator()+"hamcrest-1.2"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"junit4.7"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"chameleon"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"apache-log4j-1.2.15"+provider.separator()+"src"+provider.separator()+"main"+provider.separator()+"java"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("chameleon");
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
//						return object.getFullyQualifiedName().equals("chameleon.core.property.Defined");
//					}
//				}.filter(types);
//				return types;
//			}
//		};
//	}
}
