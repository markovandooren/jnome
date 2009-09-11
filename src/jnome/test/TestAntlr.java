package jnome.test;

import java.util.ArrayList;
import java.util.List;

import jnome.input.JavaModelFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.BasicModelProvider;
import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.test.provider.ModelProvider;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	public ModelProvider modelProvider() {
		BasicModelProvider provider = new BasicModelProvider(new JavaModelFactory(), ".java");
		provider.includeBase("testsource"+provider.separator()+"gen"+provider.separator());
		provider.includeCustom("testsource"+provider.separator()+"antlr-2.7.2"+provider.separator()+"antlr"+provider.separator());
		return provider;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
