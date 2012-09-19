package jnome.workspace;

import java.io.IOException;
import java.util.jar.JarFile;

import chameleon.workspace.Project;
import chameleon.workspace.ProjectLoader;

public abstract class AbstractJarLoader implements ProjectLoader {

	public AbstractJarLoader(Project project, String path) {
		_project = project;
		_path = path;
	}
	
	private String _path;
	
	public String path() {
		return _path;
	}
	
	private Project _project;

	public Project project() {
		return _project;
	}

	protected JarFile createJarFile() throws IOException {
		return new JarFile(path());
	}


}