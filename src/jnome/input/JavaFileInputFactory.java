package jnome.input;

import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.workspace.FileInputSourceFactory;

public abstract class JavaFileInputFactory implements FileInputSourceFactory {

	public JavaFileInputFactory(Namespace root) {
		_currentNamespace = root;
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

	protected Namespace currentNamespace() {
		return _currentNamespace;
	}

}