package be.kuleuven.cs.distrinet.jnome.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.File;

import org.aikodi.chameleon.core.Config;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.test.ExpressionTest;
import org.aikodi.chameleon.test.CompositeTest;
import org.aikodi.chameleon.test.CrossReferenceTest;
import org.aikodi.chameleon.test.provider.BasicDescendantProvider;
import org.aikodi.chameleon.test.provider.ElementProvider;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.LanguageRepository;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.chameleon.workspace.Workspace;
import org.aikodi.chameleon.workspace.XMLProjectLoader;
import org.junit.Before;
import org.junit.Test;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;

public abstract class JavaTest extends CompositeTest {
	
	public final static File TEST_DATA = new File("testdata");
	
	protected abstract File projectFile();	
	
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
		Config.setSingleThreaded(false);
	}
	
  private static ExecutorService threadPool = Executors.newCachedThreadPool();

  @Override
  protected ExecutorService threadPool() {
  	return threadPool;
  }
  
	@Override
	@Test
	public void testCrossReferences() throws Exception {
		Project project = project();
		new ExpressionTest(project, namespaceProvider(),threadPool).testExpressionTypes();
		new CrossReferenceTest(project, namespaceProvider(),threadPool).testCrossReferences();
		Logger.trace("Total loading time {} ms", () -> 
		(double)project.views().stream()
		  .flatMap(v -> v.scanners(DocumentScanner.class).stream())
		  .flatMap(s -> s.documentLoaders().stream())
		  .mapToLong(l -> l.loadTime()).sum()/1000000); 
		
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
		Workspace workspace = new Workspace(repo);
		Java7 java = new Java7LanguageFactory().create();
		repo.add(java);
		java.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(Java7LanguageFactory.javaBaseJar()));
		XMLProjectLoader config = new XMLProjectLoader(workspace);
		project = config.project(projectFile(),null);
		return project;
	}
	
}
