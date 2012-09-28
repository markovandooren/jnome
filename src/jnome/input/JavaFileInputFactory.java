package jnome.input;

import chameleon.core.namespace.Namespace;
import chameleon.workspace.FileInputSourceFactory;

public abstract class JavaFileInputFactory implements FileInputSourceFactory {

	public JavaFileInputFactory(Namespace root) {
		_currentNamespace = root;
	}
	
	private Namespace _currentNamespace;

	public void pushDirectory(String name) {
		// FIXME make this lazy to avoid creation of namespaces for directories without source files
		//       take intermediate levels into account though.
			_currentNamespace = _currentNamespace.getOrCreateNamespace(name);
	}

	public void popDirectory() {
		_currentNamespace = (Namespace) _currentNamespace.parent();
	}

	protected Namespace currentNamespace() {
		return _currentNamespace;
	}

}