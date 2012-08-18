package jnome.input;

import java.io.File;

import chameleon.core.namespace.LazyNamespace;
import chameleon.input.ModelFactory;

public class LazyJavaFileInputSource extends JavaFileInputSource {

	
	public LazyJavaFileInputSource(File file, ModelFactory factory, LazyNamespace ns) {
		super(file,factory);
		ns.addInputSource(this);
	}
	

}
