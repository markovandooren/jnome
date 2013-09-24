package be.kuleuven.cs.distrinet.jnome.test;

import java.io.File;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.core.Config;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LexicalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LocalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContextFactory;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.test.ExpressionTest;
import be.kuleuven.cs.distrinet.chameleon.support.variable.LocalVariableDeclarator;
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
		Config.setSingleThreaded(true);
	}
	
	@Override
	@Test
	public void testCrossReferences() throws Exception {
//		ElementImpl.elementsCreated = 0;
//		ElementImpl.elementsOnWhichParentInvoked = 0;
//		LexicalLookupContext.CREATED = 0;
//		LocalLookupContext.CREATED = 0;
//		Block.LINEAR.reset();
//		LocalVariableDeclarator.LINEAR.reset();
//		LookupContextFactory.LEXICAL_ALLOCATORS.clear();
//		LookupContextFactory.LEXICAL_DONE.clear();
//		LocalLookupContext.ALLOCATORS.clear();
//		LocalLookupContext.DONE.clear();
		Project project = project();
		ElementProvider<Type> typeProvider = typeProvider();
		new ExpressionTest(project, typeProvider).testExpressionTypes();
		new CrossReferenceTest(project, new BasicDescendantProvider<CrossReference>(typeProvider(), CrossReference.class)).testCrossReferences();
//		System.out.println("Created "+LexicalLookupContext.CREATED+" lexical lookup contexts.");
//		System.out.println("Created "+LocalLookupContext.CREATED+" local lookup contexts.");
//		System.out.println("Block linear context: "+Block.LINEAR.elapsedMillis()+"ms");
//		System.out.println("Local variable declarator linear context: "+LocalVariableDeclarator.LINEAR.elapsedMillis()+"ms");
//		System.out.println("Local context allocations per class:");
//		for(Map.Entry<Class, Integer> entry: LocalLookupContext.ALLOCATORS.entrySet()) {
//			System.out.println(entry.getKey().getName()+" : "+entry.getValue());
//		}
//		System.out.println("Elements created: "+ElementImpl.elementsCreated);
//		System.out.println("Elements on which parent was invoked: "+ElementImpl.elementsOnWhichParentInvoked);
//		System.out.println("Elements ratio: "+(double)ElementImpl.elementsOnWhichParentInvoked/(double)ElementImpl.elementsCreated);
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
		BootstrapProjectConfig config = new BootstrapProjectConfig(workspace);
		project = config.project(projectFile(),null);
		return project;
	}
	
}
