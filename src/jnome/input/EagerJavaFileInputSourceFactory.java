package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.util.Util;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.IFileInputSource;
import chameleon.workspace.InputException;
import chameleon.workspace.LazyFileInputSource;

public class EagerJavaFileInputSourceFactory extends FileInputSourceFactory {

	@Override
	public IFileInputSource create(File file, DirectoryLoader loader) throws InputException {
		String declarationName = Util.getAllButLastPart(file.getName());
		LazyFileInputSource eagerJavaFileInputSource = new LazyFileInputSource(file,declarationName,(InputSourceNamespace) currentNamespace());
		loader.addInputSource(eagerJavaFileInputSource);
		eagerJavaFileInputSource.load();
		return eagerJavaFileInputSource;
	}
	
}
