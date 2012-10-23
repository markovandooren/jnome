package jnome.workspace;

import java.io.File;

import jnome.input.BaseJavaProjectLoader;
import chameleon.workspace.ConfigElement;
import chameleon.workspace.ConfigException;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.ProjectConfig;
import chameleon.workspace.ProjectException;
import chameleon.workspace.View;

public class JavaProjectConfig extends ProjectConfig {

	public JavaProjectConfig(View view, FileInputSourceFactory inputSourceFactory, String projectName, File root, String baseJarPath) throws ConfigException {
		super(view,inputSourceFactory,projectName, root);
		try {
			view.addBinary(new BaseJavaProjectLoader(new File(baseJarPath)));
		} catch (ProjectException e) {
			throw new ConfigException(e);
		}
	}
	
	public class BinaryPath extends ProjectConfig.BinaryPath {
		public class Jar extends ConfigElement {
	  	public void setFile(String path) throws ConfigException {
				try {
					view().addBinary(new JarLoader(file(path)));
				} catch (ProjectException e) {
					throw new ConfigException(e);
				}
			}
	  }
	}
	
}
