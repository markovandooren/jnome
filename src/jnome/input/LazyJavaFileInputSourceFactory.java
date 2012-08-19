package jnome.input;

import java.io.File;
import java.io.IOException;

import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.InputSource;

public class LazyJavaFileInputSourceFactory implements FileInputSourceFactory {

	public LazyJavaFileInputSourceFactory(ModelFactory modelFactory) {
		_modelFactory = modelFactory;
		_currentNamespace = modelFactory.language().defaultNamespace();
	}

	private Namespace _currentNamespace;
	
	public void pushDirectory(String name) {
		// FIXME make this lazy to avoid creation of namespaces for directories without source files
		//       take intermediate levels into account though.
		try {
			_currentNamespace = _currentNamespace.getOrCreateNamespace(name);
		} catch (LookupException e) {
			throw new ChameleonProgrammerException(e);
		}
	}
	
	public void popDirectory() {
		_currentNamespace = (Namespace) _currentNamespace.parent();
	}
	
	private Namespace currentNamespace() {
		return _currentNamespace;
	}
	
	@Override
	public InputSource create(File file) throws IOException, ParseException {
		return new LazyJavaFileInputSource(file, modelFactory(), (InputSourceNamespace) currentNamespace());
	}
	
	public ModelFactory modelFactory() {
		return _modelFactory;
	}

	private ModelFactory _modelFactory;
}
