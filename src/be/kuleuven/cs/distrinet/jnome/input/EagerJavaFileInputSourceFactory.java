package be.kuleuven.cs.distrinet.jnome.input;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.InputSourceNamespace;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectoryLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.FileInputSourceFactory;
import be.kuleuven.cs.distrinet.chameleon.workspace.IFileInputSource;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.LazyFileInputSource;

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
