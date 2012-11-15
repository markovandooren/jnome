package jnome.workspace;

import java.io.IOException;
import java.util.jar.JarFile;

import chameleon.workspace.DocumentLoaderImpl;

public abstract class AbstractJarLoader extends DocumentLoaderImpl {

	public AbstractJarLoader(String path) {
		_path = path;
	}
	
	private String _path;
	
	public String path() {
		return _path;
	}
	
	protected JarFile createJarFile() throws IOException {
		return new JarFile(file(_path));
	}

}