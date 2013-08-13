package be.kuleuven.cs.distrinet.jnome.input;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.InputSourceNamespace;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectoryLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.FileInputSourceFactory;
import be.kuleuven.cs.distrinet.chameleon.workspace.IFileInputSource;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.LazyFileInputSource;

public class LazyJavaFileInputSourceFactory extends FileInputSourceFactory {

	@Override
	public IFileInputSource create(File file, DirectoryLoader loader) throws InputException {
		InputSourceNamespace currentNamespace = (InputSourceNamespace) currentNamespace();
//		System.out.println("Adding file: "+file.getAbsolutePath()+ " to namespace "+currentNamespace.getFullyQualifiedName());
		String declarationName = Util.getAllButLastPart(file.getName());
		LazyFileInputSource javaFileInputSource = new LazyFileInputSource(file, declarationName, currentNamespace,loader);
		return javaFileInputSource;
	}
	
}
