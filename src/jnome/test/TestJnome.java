package jnome.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

/**
 * @author Marko van Dooren
 */
public class TestJnome extends JavaTest {

	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("org.jnome");
	}
	
	@Override
	public Project makeProject() throws ProjectException {
		Project project = createProject();
		includeBase(project,"testsource"+separator()+"gen"+separator());
		includeCustom(project,"testsource"+separator()+"jregex"+separator());
		includeCustom(project,"testsource"+separator()+"antlr-2.7.2"+separator()+"antlr"+separator());
		includeCustom(project,"testsource"+separator()+"jnome"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"jutil"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"junit3.8.1"+separator()+"src"+separator());
		includeCustom(project,"testsource"+separator()+"jakarta-log4j-1.2.8"+separator()+"src"+separator()+"java"+separator());
		return project;
	}

	public void setLogLevels() {
		super.setLogLevels();
		Logger.getLogger("chameleon.test").setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.FATAL);
	}
}
