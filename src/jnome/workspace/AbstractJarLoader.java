package jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.rejuse.association.SingleAssociation;

import chameleon.workspace.Project;
import chameleon.workspace.ProjectLoader;

public abstract class AbstractJarLoader implements ProjectLoader {

	public AbstractJarLoader(Project project, File file) {
		_project = project;
		_file = file;
		_project.addSource(this);
	}
	
	private File _file;
	
	public File file() {
		return _file;
	}
	
	private Project _project;

	public Project project() {
		return _project;
	}

	protected JarFile createJarFile() throws IOException {
		return new JarFile(_file);
	}

	private SingleAssociation<AbstractJarLoader, Project> _projectLink = new SingleAssociation<AbstractJarLoader, Project>(this);
	
	public SingleAssociation<AbstractJarLoader, Project> projectLink() {
		return _projectLink;
	}


}