package jnome.workspace;

import java.io.File;

import jnome.input.LazyJavaFileInputSourceFactory;
import chameleon.core.namespace.LazyRootNamespace;
import chameleon.plugin.LanguagePluginImpl;
import chameleon.workspace.ConfigException;
import chameleon.workspace.ProjectConfigurator;
import chameleon.workspace.ProjectInitialisationListener;
import chameleon.workspace.View;

public class JavaConfigLoader extends LanguagePluginImpl implements ProjectConfigurator {

	public JavaConfigLoader(String javaBaseJarPath) {
		_basePath = javaBaseJarPath;
	}
	
	private String _basePath;
	
	public String baseJarPath() {
		return _basePath;
	}
	
	@Override
	public JavaProjectConfig createConfigElement(String projectName, File root, ProjectInitialisationListener listener) throws ConfigException {
		View view = new JavaView(new LazyRootNamespace(), language());
		if(listener != null) {listener.viewAdded(view);}
		return createProjectConfig(projectName, root, view);
	}

	protected JavaProjectConfig createProjectConfig(String projectName, File root, View view) throws ConfigException {
		return new JavaProjectConfig(view, new LazyJavaFileInputSourceFactory(), projectName, root, baseJarPath());
	}

	@Override
	public JavaConfigLoader clone() {
		return new JavaConfigLoader(_basePath);
	}

}
