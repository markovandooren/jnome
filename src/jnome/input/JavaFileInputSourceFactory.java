package jnome.input;

import java.io.File;
import java.io.IOException;

import chameleon.core.lookup.LookupException;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputSource;

public class JavaFileInputSourceFactory implements FileInputSourceFactory {

	public JavaFileInputSourceFactory(ModelFactory modelFactory) {
		_modelFactory = modelFactory;
	}
	
	public void pushDirectory(String name) {
	}
	
	public void popDirectory() {
	}
	
	@Override
	public InputSource create(File file) throws IOException, ParseException {
		return new EagerJavaFileInputSource(file, modelFactory());
	}
	
	public ModelFactory modelFactory() {
		return _modelFactory;
	}

	private ModelFactory _modelFactory;
}
