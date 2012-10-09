package jnome.workspace;

import java.io.File;

import jnome.input.LazyJavaFileInputSourceFactory;
import chameleon.core.language.Language;
import chameleon.core.namespace.LazyRootNamespace;
import chameleon.plugin.Plugin;
import chameleon.plugin.PluginImpl;
import chameleon.workspace.ConfigElement;
import chameleon.workspace.ConfigException;
import chameleon.workspace.ConfigLoader;
import chameleon.workspace.View;

public class JavaConfigLoader extends PluginImpl implements ConfigLoader {

	public JavaConfigLoader(String javaBaseJarPath) {
		_basePath = javaBaseJarPath;
	}
	
	private String _basePath;
	
	public String baseJarPath() {
		return _basePath;
	}
	
	@Override
	public ConfigElement createConfigElement(Language language, String projectName, File root) throws ConfigException {
		View view = new View(new LazyRootNamespace(), language);
		return createProjectConfig(projectName, root, view);
	}

	protected JavaProjectConfig createProjectConfig(String projectName, File root, View view) throws ConfigException {
		return new JavaProjectConfig(view, new LazyJavaFileInputSourceFactory(), projectName, root, baseJarPath());
	}

	@Override
	public Plugin clone() {
		return new JavaConfigLoader(_basePath);
	}

}
