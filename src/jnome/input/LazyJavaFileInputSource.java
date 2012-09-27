package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.workspace.InputSourceImpl;

public class LazyJavaFileInputSource extends JavaFileInputSource {

	
	public LazyJavaFileInputSource(File file, InputSourceNamespace ns) {
		this(file);
		setNamespace(ns);
	}
	
	public LazyJavaFileInputSource(File file) {
		super(file);
	}

	@Override
	public InputSourceImpl clone() {
		return new LazyJavaFileInputSource(file());
	}
	

}
