package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSource;
import chameleon.workspace.LazyFileInputSource;

public class EagerJavaFileInputSourceFactory extends FileInputSourceFactory {

	@Override
	public InputSource create(File file, DirectoryLoader loader) throws InputException {
		LazyFileInputSource eagerJavaFileInputSource = new LazyFileInputSource(file,(InputSourceNamespace) currentNamespace());
		loader.addInputSource(eagerJavaFileInputSource);
		eagerJavaFileInputSource.load();
		return eagerJavaFileInputSource;
	}
	
}
