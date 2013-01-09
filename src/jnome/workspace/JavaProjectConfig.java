package jnome.workspace;

import java.io.File;
import java.util.Collections;
import java.util.List;

import jnome.input.BaseJavaProjectLoader;
import chameleon.workspace.ConfigElement;
import chameleon.workspace.ConfigException;
import chameleon.workspace.DocumentLoader;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.ProjectConfig;
import chameleon.workspace.ProjectException;
import chameleon.workspace.View;

public class JavaProjectConfig extends ProjectConfig {

	public JavaProjectConfig(View view, FileInputSourceFactory inputSourceFactory, String projectName, File root, String baseJarPath) throws ConfigException {
		super(view,inputSourceFactory,projectName, root);
		try {
			//Add the base loader.
			view.addBinary(new BaseJavaProjectLoader(baseJarPath));
		} catch (ProjectException e) {
			throw new ConfigException(e);
		}
	}
	
	@Override
	protected void binaryLoaderAdded(DocumentLoader loader) {
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
	@Override
	protected List<String> sourceExtensions() {
		return Collections.singletonList(".java");
	}
	
	public class BinaryPath extends ProjectConfig.BinaryPath {
		public class Jar extends ConfigElement {
			private String _path;
	  	public void setFile(String path) throws ConfigException {
				try {
					_path = path;
					view().addBinary(new JarLoader(_path));
				} catch (ProjectException e) {
					throw new ConfigException(e);
				}
			}
	  	
	  	public String getFile() {
	  		return _path;
	  	}

			@Override
			protected void $update() {
				//FIXME: this transforms a relative path into an absolute path
				_path = ((JarLoader)modelElement()).path();
			}
	  }
	}
	
}
