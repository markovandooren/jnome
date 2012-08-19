package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.input.ModelFactory;

public class LazyJavaFileInputSource extends JavaFileInputSource {

	
	public LazyJavaFileInputSource(File file, ModelFactory factory, InputSourceNamespace ns) {
		super(file,factory);
		ns.addInputSource(this);
	}
	

}
