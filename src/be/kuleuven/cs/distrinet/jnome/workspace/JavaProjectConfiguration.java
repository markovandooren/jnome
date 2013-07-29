package be.kuleuven.cs.distrinet.jnome.workspace;

import java.io.IOException;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.FileInputSourceFactory;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;

public class JavaProjectConfiguration extends ProjectConfiguration {

	public JavaProjectConfiguration(View view, FileInputSourceFactory inputSourceFactory) throws ConfigException {
		super(view, inputSourceFactory);
	}
	
	@Override
	protected void binaryNonBaseLoaderAdded(DocumentLoader loader) throws ConfigException {
		if(loader instanceof BaseJavaProjectLoader) {
			return;
		}
		if(loader instanceof JarLoader) {
			BinaryPath p = createOrGetChild(BinaryPath.class);
			p.createOrUpdateChild(BinaryPath.Jar.class,loader);
		} else {
			super.binaryLoaderAdded(loader);
		}
	}
	
	public class BinaryPath extends ProjectConfiguration.BinaryPath {
		
		public class Jar extends Archive {
	  	
	  	protected void pathChanged() throws ConfigException {
	  		try {
	  			JarFile path = new JarFile(project().absoluteFile(_path));
					view().addBinary(new JarLoader(path, language(Java.NAME).plugin(ProjectConfigurator.class).binaryFileFilter()));
	  		} catch (ProjectException | IOException e) {
	  			throw new ConfigException(e);
	  		}
	  	}
	  	
	  }
	}
	
}
