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
import org.aikodi.chameleon.workspace.XMLProjectLoader;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.LanguageRepository;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.chameleon.workspace.Workspace;
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
//		Timer.INFIX_OPERATOR_INVOCATION.reset();
//		Timer.PREFIX_OPERATOR_INVOCATION.reset();
//		Timer.POSTFIX_OPERATOR_INVOCATION.reset();
//		Lists.LIST_CREATION.reset();
		Project project = project();
//		ElementProvider<Type> typeProvider = typeProvider();
		new ExpressionTest(project, namespaceProvider(),threadPool).testExpressionTypes();
		new CrossReferenceTest(project, namespaceProvider(),threadPool).testCrossReferences();
//		new CrossReferenceTest(project, new BasicDescendantProvider<CrossReference>(typeProvider(), CrossReference.class),threadPool).testCrossReferences();
//		System.out.println("Created "+SimpleNameSignature.COUNT+" SimpleNameSignature objects.");
//		System.out.println("Created "+LexicalLookupContext.CREATED+" lexical lookup contexts.");
//		System.out.println("Created "+LocalLookupContext.CREATED+" local lookup contexts.");
//		System.out.println("Block linear context: "+Block.LINEAR.elapsedMillis()+"ms");
//		System.out.println("Local variable declarator linear context: "+LocalVariableDeclarator.LINEAR.elapsedMillis()+"ms");
//		System.out.println("Local context allocations per class:");
//		for(Map.Entry<Class, Integer> entry: LocalLookupContext.ALLOCATORS.entrySet()) {
//			System.out.println(entry.getKey().getName()+" : "+entry.getValue());
//		}
//		System.out.println("infix operator invocations: "+Timer.INFIX_OPERATOR_INVOCATION.elapsedMillis()+"ms");
//		System.out.println("prefix operator invocations: "+Timer.PREFIX_OPERATOR_INVOCATION.elapsedMillis()+"ms");
//		System.out.println("postfix operator invocations: "+Timer.POSTFIX_OPERATOR_INVOCATION.elapsedMillis()+"ms");
//		System.out.println("list creation: "+Lists.LIST_CREATION.elapsedMillis()+"ms");
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
		Java7 java = new Java7LanguageFactory().create();
		repo.add(java);
		java.setPlugin(ProjectConfigurator.class, new JavaProjectConfigurator(Java7LanguageFactory.javaBaseJar()));
		XMLProjectLoader config = new XMLProjectLoader(workspace);
		project = config.project(projectFile(),null);
		return project;
	}
	
}
