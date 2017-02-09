package org.aikodi.java.input;

import java.io.IOException;
import java.util.jar.JarFile;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.workspace.JarScanner;
import org.aikodi.java.workspace.JavaView;

public class BaseJavaProjectLoader extends JarScanner {

	public BaseJavaProjectLoader(JarFile path, Java7 java) {
		super(path, java.plugin(ProjectConfigurator.class).binaryFileFilter(),true);
	}
	
	@Override
	protected void createDocumentLoaders() throws IOException, LookupException, InputException {
		// First create input sources for the base classes in rt.jar
		super.createDocumentLoaders();
		// The add predefined elements.
		new PredefinedElementsFactory((JavaView) view(), this).initializePredefinedElements();
	}
	



}
