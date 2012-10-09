package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;

public class LazyJavaFileInputSourceFactory extends FileInputSourceFactory {

	@Override
	public InputSource create(File file, DirectoryLoader loader) throws InputException {
		JavaFileInputSource javaFileInputSource = new JavaFileInputSource(file, (InputSourceNamespace) currentNamespace());
		loader.addInputSource(javaFileInputSource);
		return javaFileInputSource;
	}
	
}
