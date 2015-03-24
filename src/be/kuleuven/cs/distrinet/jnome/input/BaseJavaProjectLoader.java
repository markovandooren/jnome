package be.kuleuven.cs.distrinet.jnome.input;

import java.io.IOException;
import java.util.jar.JarFile;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.ProjectConfigurator;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

public class BaseJavaProjectLoader extends JarLoader {

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
