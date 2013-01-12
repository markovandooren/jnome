package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;
import chameleon.workspace.LazyFileInputSource;

public class LazyJavaFileInputSourceFactory extends FileInputSourceFactory {

	@Override
	public InputSource create(File file, DirectoryLoader loader) throws InputException {
		LazyFileInputSource javaFileInputSource = new LazyFileInputSource(file, (InputSourceNamespace) currentNamespace());
		loader.addInputSource(javaFileInputSource);
		return javaFileInputSource;
	}
	
}
