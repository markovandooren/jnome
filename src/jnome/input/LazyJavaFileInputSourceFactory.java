package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.input.ModelFactory;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputSource;

public class LazyJavaFileInputSourceFactory extends JavaFileInputFactory implements FileInputSourceFactory {

	public LazyJavaFileInputSourceFactory() {
	}

	@Override
	public InputSource create(File file) {
		LazyJavaFileInputSource lazyJavaFileInputSource = new LazyJavaFileInputSource(file, (InputSourceNamespace) currentNamespace());
		return lazyJavaFileInputSource;
	}
	
	public ModelFactory modelFactory() {
		return _modelFactory;
	}

	private ModelFactory _modelFactory;
}
