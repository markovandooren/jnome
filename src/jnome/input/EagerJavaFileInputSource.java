package jnome.input;

import java.io.File;

import chameleon.input.ModelFactory;
import chameleon.workspace.InputException;

public class EagerJavaFileInputSource extends JavaFileInputSource {

	public EagerJavaFileInputSource(File file, ModelFactory factory) throws InputException {
		super(file, factory);
		load();
	}

}
