package jnome.input;

import java.io.File;

import chameleon.core.namespace.Namespace;
import chameleon.workspace.FileInputSource;

public class EagerJavaFileInputSource extends JavaFileInputSource {

	public EagerJavaFileInputSource(File file, Namespace ns) {
		super(file);
		setUniNamespace(ns);
	}
	
	@Override
	public FileInputSource clone() {
		EagerJavaFileInputSource eagerJavaFileInputSource = new EagerJavaFileInputSource(file(),null);
		return eagerJavaFileInputSource;
	}

}
