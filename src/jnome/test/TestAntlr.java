package jnome.test;

import chameleon.test.provider.BasicNamespaceProvider;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;

/**
 * @author Marko van Dooren
 */
public class TestAntlr extends JavaTest {

	@Override
	public Project makeProject() throws ProjectException {
		Project project = createProject();
		includeBase(project, "testsource"+separator()+"gen"+separator());
		includeCustom(project,"testsource"+separator()+"antlr-2.7.2"+separator()+"antlr"+separator());
		return project;
	}

	@Override
	public BasicNamespaceProvider namespaceProvider() {
		return new BasicNamespaceProvider("antlr");
	}

}
