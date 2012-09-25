package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.input.ModelFactory;

public class LazyJavaFileInputSource extends JavaFileInputSource {

	
	public LazyJavaFileInputSource(File file, ModelFactory factory, InputSourceNamespace ns) {
		super(file,factory);
		if(file.getAbsolutePath().equals("/Users/marko/git/jnome/testsource/junit3.8.1/src/junit/framework/TestCase.java")) {
			System.out.println("debug");
		}
		ns.addInputSource(this);
	}
	

}
