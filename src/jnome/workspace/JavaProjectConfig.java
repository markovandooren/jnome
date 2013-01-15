package jnome.workspace;

import java.io.File;

import jnome.core.language.Java;
import jnome.input.BaseJavaProjectLoader;
import chameleon.workspace.BootstrapProjectConfig.BaseLibraryConfiguration;
import chameleon.workspace.ConfigException;
import chameleon.workspace.DocumentLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.ProjectConfiguration;
import chameleon.workspace.ProjectConfigurator;
import chameleon.workspace.ProjectException;
import chameleon.workspace.View;
import chameleon.workspace.Workspace;

public class JavaProjectConfig extends ProjectConfiguration {

	public JavaProjectConfig(String projectName, File root, View view, Workspace workspace, FileInputSourceFactory inputSourceFactory, String baseJarPath, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException {
		super(projectName,root,view, workspace, inputSourceFactory);
		if(baseLibraryConfiguration.mustLoad("Java")) {
			try {
				//Add the base loader.
				view.addBinary(new BaseJavaProjectLoader(baseJarPath,(Java)language("java")));
			} catch (ProjectException e) {
				throw new ConfigException(e);
			}
		}
	}
	
	@Override
	protected void binaryLoaderAdded(DocumentLoader loader) throws ConfigException {
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
	  			view().addBinary(new JarLoader(_path, language("java").plugin(ProjectConfigurator.class).binaryFileFilter()));
	  		} catch (ProjectException e) {
	  			throw new ConfigException(e);
	  		}
	  	}
	  	
	  }
	}
	
}
