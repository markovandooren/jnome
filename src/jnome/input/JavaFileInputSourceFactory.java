package jnome.input;

import java.io.File;

import chameleon.core.namespace.Namespace;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;

public class JavaFileInputSourceFactory extends JavaFileInputFactory {

	public JavaFileInputSourceFactory() {
		super();
	}
	
	public void pushDirectory(String name) {
	}
	
	public void popDirectory() {
	}
	
	@Override
	public InputSource create(File file) throws InputException {
		EagerJavaFileInputSource eagerJavaFileInputSource = new EagerJavaFileInputSource(file,currentNamespace());
		eagerJavaFileInputSource.load();
		return eagerJavaFileInputSource;
	}
	
}
