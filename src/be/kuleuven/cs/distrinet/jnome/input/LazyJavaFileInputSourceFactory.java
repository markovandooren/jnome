package be.kuleuven.cs.distrinet.jnome.input;

import java.io.File;

import org.aikodi.chameleon.core.namespace.DocumentLoaderNamespace;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.DirectoryScanner;
import org.aikodi.chameleon.workspace.FileDocumentLoaderFactory;
import org.aikodi.chameleon.workspace.IFileDocumentLoader;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.LazyFileDocumentLoader;

public class LazyJavaFileInputSourceFactory extends FileDocumentLoaderFactory {

	@Override
	public IFileDocumentLoader create(File file, DirectoryScanner loader) throws InputException {
		DocumentLoaderNamespace currentNamespace = (DocumentLoaderNamespace) currentNamespace();
//		System.out.println("Adding file: "+file.getAbsolutePath()+ " to namespace "+currentNamespace.getFullyQualifiedName());
		String declarationName = Util.getAllButLastPart(file.getName());
		LazyFileDocumentLoader javaFileInputSource = new LazyFileDocumentLoader(file, declarationName, currentNamespace,loader);
		return javaFileInputSource;
	}
	
}
