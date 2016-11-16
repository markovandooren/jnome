package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.jar.JarFile;

import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.core.namespace.LazyRootNamespace;
import org.aikodi.chameleon.workspace.BaseLibraryConfiguration;
import org.aikodi.chameleon.workspace.BaseLibraryConfigurator;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.ExtensionPredicate;
import org.aikodi.chameleon.workspace.ProjectConfiguration;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.chameleon.workspace.ProjectConfiguratorImpl;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.chameleon.workspace.Workspace;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.predicate.Predicate;
import org.aikodi.rejuse.predicate.SafePredicate;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;

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
	
	protected Java7 java() {
		return (Java7) language();
	}
	
	public class JavaBaseLibraryConfigurator extends BaseLibraryConfigurator {

		public JavaBaseLibraryConfigurator(Language language) {
			super(language);
		}
		
		@Override
		protected void addBaseScanner(View view) {
			try {
				//Add the base loader.
				// FIXME The loader waits until it is added and then creates the predefined elements
				// This does not work with the IBM VM, which splits the base jar in two parts.
				// Object and Integer are in separate jar files, so Integer isn't loaded right now.
				view.addBinary(new BaseJavaProjectLoader(baseJarPath(),(Java7)language()));
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
