package jnome.input;

import java.io.File;
import java.io.IOException;

import chameleon.input.ModelFactory;
import chameleon.input.ParseException;

public class EagerJavaFileInputSource extends JavaFileInputSource {

	public EagerJavaFileInputSource(File file, ModelFactory factory) throws IOException, ParseException {
		super(file, factory);
		load();
	}

}
