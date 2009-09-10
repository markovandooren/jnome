package jnome.test;

import java.util.ArrayList;
import java.util.Collection;

import jnome.input.JavaModelFactory;
import junit.textui.TestRunner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.test.provider.ModelProvider;

/**
 * @author Marko van Dooren
 */
public class TestJnome {

	@Test
	public void test() throws Exception {
		new ExpressionTest(modelProvider(), typeProvider()).testExpressionTypes();
	}
	
	public ElementProvider<Type> typeProvider() {
		return new ElementProvider<Type>() {
			public Collection<Type> elements(Language language) {
				Collection<Type> result = new ArrayList<Type>();
				try {
					result = language.defaultNamespace().getSubNamespace("org").getSubNamespace("jnome").allDeclarations(Type.class);
				} catch (LookupException e) {
					e.printStackTrace();
				}
				return result;
			}
		};
	}
	
	public ModelProvider modelProvider() {
		BasicModelProvider provider = new BasicModelProvider(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jregex"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jnome"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jutil"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"junit3.8.1"+provider.separator()+"src"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"jakarta-log4j-1.2.8"+provider.separator()+"src"+provider.separator()+"java"+provider.separator());
		return provider;
	}

	public void setLogLevels() {
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
}
