package jnome.input;

import java.io.File;
import java.util.Collections;
import java.util.List;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.core.namespace.Namespace;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.util.Util;
import chameleon.workspace.FileInputSource;
import chameleon.workspace.InputException;
import chameleon.workspace.InputSourceImpl;

public class JavaFileInputSource extends FileInputSource {

	public JavaFileInputSource(File file, InputSourceNamespace ns) throws InputException {
		super(file, ns);
	}
	
	@Override
	public List<String> targetDeclarationNames(Namespace ns) {
		return Collections.singletonList(Util.getAllButLastPart(file().getName()));
	}

	@Override
	public InputSourceImpl clone() {
		try {
			return new JavaFileInputSource(file(),null);
		} catch (InputException e) {
			throw new ChameleonProgrammerException(e);
		}
	}
	

}