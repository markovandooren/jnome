package jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.rejuse.association.AssociationListener;
import org.rejuse.association.SingleAssociation;

import chameleon.workspace.Project;
import chameleon.workspace.ProjectLoader;
import chameleon.workspace.ProjectLoaderImpl;

public abstract class AbstractJarLoader extends ProjectLoaderImpl {

	public AbstractJarLoader(File file) {
		_file = file;
	}
	
	private File _file;
	
	public File file() {
		return _file;
	}
	
	protected JarFile createJarFile() throws IOException {
		return new JarFile(_file);
	}

}