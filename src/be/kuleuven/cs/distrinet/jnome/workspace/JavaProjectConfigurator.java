package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyRootNamespace;
import be.kuleuven.cs.distrinet.chameleon.workspace.BaseLibraryConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.BaseLibraryConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ExtensionPredicate;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfiguration;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfiguratorImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

/**
 * A class for configuring Java projects.
 * 
 * @author Marko van Dooren
 */
public class JavaProjectConfigurator extends ProjectConfiguratorImpl implements ProjectConfigurator {

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
	protected void addBaseLibraries(View view, BaseLibraryConfiguration baseLibraryConfiguration) {
		new JavaBaseLibraryConfigurator(java()).process(view, baseLibraryConfiguration);
	}
	
	protected Java java() {
		return (Java) language();
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
	public Predicate<? super String,Nothing> binaryFileFilter() {
		return new ExtensionPredicate("class");
	}

	protected Language language(String name, Workspace workspace) {
		return workspace.languageRepository().get(name);
	}

	@Override
	protected ProjectConfiguration createProjectConfig(View view) throws ConfigException {
		return new JavaProjectConfiguration(view, new LazyJavaFileInputSourceFactory());
	}

	@Override
	protected View createView() {
		return new JavaView(new LazyRootNamespace(), language());
	}

}
