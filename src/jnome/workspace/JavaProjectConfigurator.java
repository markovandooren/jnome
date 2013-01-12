package jnome.workspace;

import java.io.File;

import jnome.input.LazyJavaFileInputSourceFactory;
import chameleon.core.namespace.LazyRootNamespace;
import chameleon.plugin.LanguagePluginImpl;
import chameleon.workspace.ConfigException;
import chameleon.workspace.ProjectConfigurator;
import chameleon.workspace.ProjectInitialisationListener;
import chameleon.workspace.View;
import chameleon.workspace.BootstrapProjectConfig.BaseLibraryConfiguration;

/**
 * A class for configuring Java projects.
 * 
 * @author Marko van Dooren
 */
public class JavaProjectConfigurator extends LanguagePluginImpl implements ProjectConfigurator {

	/**
	 * Initialize a new Java project configurator with the give path of the jar that contains
	 * the base library (typically named rt.jar).
	 * 
	 * @param javaBaseJarPath The path of the jar file that contains the base library of Java.
	 */
 /*@
   @ public
   @
   @ pre javaBaseJarPath != null;
   @
   @ post baseJarPath() == javaBaseJarPath;
   @*/
	public JavaProjectConfigurator(String javaBaseJarPath) {
		_basePath = javaBaseJarPath;
	}
	
	private String _basePath;
	
	public String baseJarPath() {
		return _basePath;
	}
	
	@Override
	public JavaProjectConfig createConfigElement(String projectName, File root, ProjectInitialisationListener listener, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException {
		View view = new JavaView(new LazyRootNamespace(), language());
		if(listener != null) {listener.viewAdded(view);}
		return createProjectConfig(projectName, root, view, baseLibraryConfiguration);
	}

	protected JavaProjectConfig createProjectConfig(String projectName, File root, View view, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException {
		return new JavaProjectConfig(view, new LazyJavaFileInputSourceFactory(), projectName, root, baseJarPath(), baseLibraryConfiguration);
	}

	@Override
	public JavaProjectConfigurator clone() {
		return new JavaProjectConfigurator(_basePath);
	}

}