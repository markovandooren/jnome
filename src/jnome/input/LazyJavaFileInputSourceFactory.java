package jnome.input;

import java.io.File;

import chameleon.core.namespace.InputSourceNamespace;
import chameleon.util.Util;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.IFileInputSource;
import chameleon.workspace.InputException;
import chameleon.workspace.LazyFileInputSource;

public class LazyJavaFileInputSourceFactory extends FileInputSourceFactory {

	@Override
	public IFileInputSource create(File file, DirectoryLoader loader) throws InputException {
		String declarationName = Util.getAllButLastPart(file.getName());
		LazyFileInputSource javaFileInputSource = new LazyFileInputSource(file, declarationName, (InputSourceNamespace) currentNamespace());
		loader.addInputSource(javaFileInputSource);
		return javaFileInputSource;
	}
	
}
