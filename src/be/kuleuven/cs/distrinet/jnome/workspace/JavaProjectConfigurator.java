package be.kuleuven.cs.distrinet.jnome.workspace;

import java.io.File;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyRootNamespace;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig.BaseLibraryConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ExtensionPredicate;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectInitialisationListener;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

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
	public JavaProjectConfigurator(JarFile javaBaseJarPath) {
		_basePath = javaBaseJarPath;
	}
	
	private JarFile _basePath;
	
	public JarFile baseJarPath() {
		return _basePath;
	}
	
	@Override
	public JavaProjectConfig createConfigElement(String projectName, File root, Workspace workspace, ProjectInitialisationListener listener, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException {
		View view = new JavaView(new LazyRootNamespace(), language());
		if(listener != null) {listener.viewAdded(view);}
		return createProjectConfig(projectName, root, view, workspace, baseLibraryConfiguration);
	}

	protected JavaProjectConfig createProjectConfig(String projectName, File root, View view, Workspace workspace, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException {
		return new JavaProjectConfig(projectName, root, view, workspace, new LazyJavaFileInputSourceFactory(), baseJarPath(), baseLibraryConfiguration);
	}

	@Override
	public JavaProjectConfigurator clone() {
		return new JavaProjectConfigurator(_basePath);
	}

	@Override
	public SafePredicate<? super String> sourceFileFilter() {
		return new ExtensionPredicate("java");
	}

	@Override
	public SafePredicate<? super String> binaryFileFilter() {
		return new ExtensionPredicate("class");
	}

}
