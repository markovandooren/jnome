package be.kuleuven.cs.distrinet.jnome.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.core.Config;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.test.ExpressionTest;
import be.kuleuven.cs.distrinet.chameleon.test.CompositeTest;
import be.kuleuven.cs.distrinet.chameleon.test.CrossReferenceTest;
import be.kuleuven.cs.distrinet.chameleon.test.provider.BasicDescendantProvider;
import be.kuleuven.cs.distrinet.chameleon.test.provider.ElementProvider;
import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.LanguageRepository;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaProjectConfigurator;

public abstract class JavaTest extends CompositeTest {
	
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
		Config.setSingleThreaded(true);
	}
	
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
		Workspace workspace = new Workspace(repo);
		Java java = new JavaLanguageFactory().create();
		repo.add(java);
		java.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(JavaLanguageFactory.javaBaseJar()));
		BootstrapProjectConfig config = new BootstrapProjectConfig(projectFile().getParentFile(), workspace);
//		config.readFromXML();
		project = config.project(projectFile(),null);
//		View view = project.views().get(0);
		return project;
	}
	
}
