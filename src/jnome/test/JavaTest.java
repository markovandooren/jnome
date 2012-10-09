package jnome.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import jnome.core.language.Java;
import jnome.core.language.JavaLanguageFactory;
import jnome.input.BaseJavaProjectLoader;
import jnome.input.EagerJavaFileInputSourceFactory;
import jnome.input.LazyJavaFileInputSourceFactory;
import jnome.workspace.JarLoader;
import jnome.workspace.JavaConfigLoader;

import org.junit.Before;
import org.junit.Test;

import chameleon.core.Config;
import chameleon.core.language.Language;
import chameleon.core.namespace.LazyNamespaceFactory;
import chameleon.core.namespace.LazyRootNamespace;
import chameleon.core.namespace.NamespaceFactory;
import chameleon.core.namespace.RegularNamespaceFactory;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.reference.CrossReference;
import chameleon.oo.type.Type;
import chameleon.support.test.ExpressionTest;
import chameleon.test.CompositeTest;
import chameleon.test.CrossReferenceTest;
import chameleon.test.provider.BasicDescendantProvider;
import chameleon.test.provider.ElementProvider;
import chameleon.workspace.BootstrapProjectConfig;
import chameleon.workspace.ConfigException;
import chameleon.workspace.ConfigLoader;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.LanguageRepository;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;
import chameleon.workspace.View;

public abstract class JavaTest extends CompositeTest {
	
	@Before
	public void loadProperties() {
		try {
		Properties properties = new Properties();
			properties.load(new BufferedInputStream(new FileInputStream(new File("test.properties"))));
			_javaBaseJarPath = properties.getProperty("api");
		} catch (IOException e) {
			throw new RuntimeException("need file test.properties with property 'api' set to the location of the jar with the Java base library.");
		}
	}
	
	protected abstract File projectFile();	
	
	private String _javaBaseJarPath;
	
	public String javaBaseJarPath() {
		return _javaBaseJarPath;
	}
	
	/**
	 * Set the log levels of Log4j. By default, nothing is changed.
	 */
	@Before
	public void setLogLevels() {
//		Logger.getRootLogger().setLevel(Level.FATAL);
//		Logger.getLogger("chameleon.test.expression").setLevel(Level.FATAL);
	}
	
	@Before
	public void setMultiThreading() {
		Config.setSingleThreaded(true);
	}
	
//	@Override
//	public void setCaching() {
//	  Config.setCaching(false);
//	}
	
	@Override
	@Test
	public void testCrossReferences() throws Exception {
		Project project = project();
		ElementProvider<Type> typeProvider = typeProvider();
		new ExpressionTest(project, typeProvider).testExpressionTypes();
		new CrossReferenceTest(project, new BasicDescendantProvider<CrossReference>(namespaceProvider(), CrossReference.class)).testCrossReferences();
	}

	public ElementProvider<Type> typeProvider() {
		return new BasicDescendantProvider<Type>(namespaceProvider(), Type.class);
	}

	/**
	 * Test the verification by invoking verify() for all namespace parts, and checking if the result is valid.
	 */
	@Test @Override
	public void testVerification() throws Exception {
	}

	protected final Project makeProject() throws ConfigException {
		Project project;
		LanguageRepository repo = new LanguageRepository();
		Java java = new JavaLanguageFactory().create();
		repo.add(java);
		java.setPlugin(ConfigLoader.class, new JavaConfigLoader(javaBaseJarPath()));
		BootstrapProjectConfig config = new BootstrapProjectConfig(projectFile().getParentFile(), repo);
		config.readFromXML(projectFile());
		project = config.project();
//		View view = project.views().get(0);
		return project;
	}
	
}
