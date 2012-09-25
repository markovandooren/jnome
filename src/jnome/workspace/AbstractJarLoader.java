package jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.rejuse.association.SingleAssociation;

import chameleon.workspace.Project;
import chameleon.workspace.ProjectLoader;

public abstract class AbstractJarLoader implements ProjectLoader {

	public AbstractJarLoader(Project project, String path) {
		_project = project;
		_path = path;
		_project.addSource(this);
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
		JarFile result;
		String path = path();
		File file = new File(path);
		if(file.isAbsolute()) {
			result = new JarFile(path);
		} else {
			result = new JarFile(project().root().getPath()+File.separator+path);
		}
		return result;
	}

	private SingleAssociation<AbstractJarLoader, Project> _projectLink = new SingleAssociation<AbstractJarLoader, Project>(this);
	
	public SingleAssociation<AbstractJarLoader, Project> projectLink() {
		return _projectLink;
	}


}