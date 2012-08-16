package jnome.input;

import java.io.File;

import chameleon.input.ModelFactory;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputSource;

public class JavaFileInputSourceFactory implements FileInputSourceFactory {

	public JavaFileInputSourceFactory(ModelFactory modelFactory) {
		_modelFactory = modelFactory;
	}

	@Override
	public InputSource create(File file) {
		return new LazyJavaFileInputSource(file, modelFactory());
	}
	
	public ModelFactory modelFactory() {
		return _modelFactory;
	}

	private ModelFactory _modelFactory;
}
