package be.kuleuven.cs.distrinet.jnome.workspace;

import java.io.IOException;
import java.util.jar.JarFile;

import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.FileDocumentLoaderFactory;
import org.aikodi.chameleon.workspace.ProjectConfiguration;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;

public class JavaProjectConfiguration extends ProjectConfiguration {

	public JavaProjectConfiguration(View view, FileDocumentLoaderFactory inputSourceFactory) throws ConfigException {
		super(view, inputSourceFactory);
	}
	
	@Override
	protected void binaryNonBaseScannerAdded(DocumentScanner loader) throws ConfigException {
		if(loader instanceof BaseJavaProjectLoader) {
			return;
		}
		if(loader instanceof JarScanner) {
			BinaryPath p = createOrGetChild(BinaryPath.class);
			p.createOrUpdateChild(BinaryPath.Jar.class,loader);
		} else {
			super.binaryScannerAdded(loader);
		}
	}
	
	public class BinaryPath extends ProjectConfiguration.BinaryPath {
		
		public class Jar extends Archive {
	  	
	  	protected void pathChanged() throws ConfigException {
	  		try {
	  			JarFile path = new JarFile(project().absoluteFile(path()));
					view().addBinary(new JarScanner(path, language(Java7.NAME).plugin(ProjectConfigurator.class).binaryFileFilter()));
	  		} catch (ProjectException | IOException e) {
	  			throw new ConfigException(e);
	  		}
	  	}
	  	
	  }
	}
	
}
