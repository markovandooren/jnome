package org.aikodi.java.input;

import java.io.File;

import org.aikodi.chameleon.core.namespace.DocumentLoaderNamespace;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.DirectoryScanner;
import org.aikodi.chameleon.workspace.FileDocumentLoaderFactory;
import org.aikodi.chameleon.workspace.IFileDocumentLoader;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.LazyFileDocumentLoader;

public class EagerJavaFileInputSourceFactory extends FileDocumentLoaderFactory {

	@Override
	public IFileDocumentLoader create(File file, DirectoryScanner loader) throws InputException {
		String declarationName = Util.getAllButLastPart(file.getName());
		LazyFileDocumentLoader eagerJavaFileInputSource = new LazyFileDocumentLoader(file,declarationName,(DocumentLoaderNamespace) currentNamespace(),loader);
		eagerJavaFileInputSource.load();
		return eagerJavaFileInputSource;
	}
	
}
