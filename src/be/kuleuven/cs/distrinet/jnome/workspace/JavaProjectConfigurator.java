package be.kuleuven.cs.distrinet.jnome.workspace;

import java.io.File;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyRootNamespace;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.BaseLibraryConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.BaseLibraryConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ExtensionPredicate;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectInitialisationListener;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;
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
		processBaseLibraries(view, workspace, baseLibraryConfiguration);
		return createProjectConfig(projectName, root, view, workspace);
	}

	protected JavaProjectConfig createProjectConfig(String projectName, File root, View view, Workspace workspace) throws ConfigException {
		return new JavaProjectConfig(projectName, root, view, workspace, new LazyJavaFileInputSourceFactory());
	}
	
	protected void processBaseLibraries(View view, Workspace workspace, BaseLibraryConfiguration baseLibraryConfiguration) {
		new JavaBaseLibraryConfigurator(language()).process(view, baseLibraryConfiguration);
	}

	public class JavaBaseLibraryConfigurator extends BaseLibraryConfigurator {

		public JavaBaseLibraryConfigurator(Language language) {
			super(language);
		}
		
		@Override
		protected void addBaseLoader(View view) {
			try {
				//Add the base loader.
				view.addBinary(new BaseJavaProjectLoader(baseJarPath(),(Java)language()));
			} catch (ProjectException e) {
				throw new ConfigException(e);
			}
		}
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

	protected Language language(String name, Workspace workspace) {
		return workspace.languageRepository().get(name);
	}


}
