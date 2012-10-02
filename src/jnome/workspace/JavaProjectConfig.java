package jnome.workspace;

import java.io.File;

import jnome.core.language.JavaLanguageFactory;

import chameleon.workspace.ConfigElement;
import chameleon.workspace.ConfigException;
import chameleon.workspace.FileInputSourceFactory;
import chameleon.workspace.ProjectConfig;
import chameleon.workspace.ProjectException;

public class JavaProjectConfig extends ProjectConfig {

	public JavaProjectConfig(File root, FileInputSourceFactory inputSourceFactory) {
		super(root, new JavaLanguageFactory(),inputSourceFactory);
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
