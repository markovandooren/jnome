package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigElement;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.DirectoryLoader;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectConfigurator;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

public class JavaEclipseClasspathConfig extends ConfigElement {

	public final String JUNIT4 = "org.eclipse.jdt.junit.JUNIT_CONTAINER/4";
	
	public JavaEclipseClasspathConfig(Project project, Map<String,String> containerConfiguration) throws ProjectException {
		_project = project;
		_loaders.putAll(containerConfiguration);
		view().addBinary(new BaseJavaProjectLoader(JavaLanguageFactory.javaBaseJar(),java()));
		readFromXML(new File(project.root(),".classpath"));
	}
	
	private Project _project;
	
	private Java java() {
		return (Java) view().language();
	}
	
	private View view() {
		return _project.views().get(0); 
	}
	
	public void addContainer(String configFileID, String path) {
		_loaders.put(configFileID,path);
	}
	
	/**
	 * This method overrides the default behavior and returns "project" 
	 * because this class does not have the same name as
	 * its corresponding XML element.
	 */
	@Override
	public String nodeName() {
		return "classpath";
	}

	
	public class ClasspathEntry extends ConfigElement {
		
		private String _kind;
		
		public void setKind(String kind) {
			_kind = kind;
		}
		
		public String kind() {
			return _kind;
		}
		
		private String _path;
		
		public void setPath(String path) {
			_path = path;
		}
		
		public String path() {
			return _path;
		}
		
		@Override
		protected void $after() throws ConfigException {
			try {
				if(_kind.equals("src")) {
					loadSrc(_path);
				} else if(_kind.equals("lib")) {
					loadBin(_path);
				} else if(_kind.equals("con")) {
					if(_path == null) {
						throw new IllegalStateException("The container has not path attribute.");
					}
					if(! _path.startsWith("org.eclipse.jdt.launching.JRE_CONTAINER")) {
						String path = _loaders.get(_path);
						if(path == null) {
							throw new IllegalStateException("No container with key "+path+" is registered.");
						}
						loadBin(path);
					}
				}
			} catch (ProjectException e) {
				throw new ConfigException(e);
			}
		}
		
		private void loadSrc(String path) throws ProjectException {
			_project.views().get(0).addSource(new DirectoryLoader(path, java().plugin(ProjectConfigurator.class).sourceFileFilter(), new LazyJavaFileInputSourceFactory()));
		}
		
		private void loadBin(String path) throws ProjectException {
			SafePredicate<? super String> binaryFileFilter = java().plugin(ProjectConfigurator.class).binaryFileFilter();
			if(path.endsWith(".jar")) {
				JarFile file;
				try {
					file = new JarFile(_project.absoluteFile(path));
					_project.views().get(0).addBinary(new JarLoader(file, binaryFileFilter));
				} catch (IOException e) {
					throw new ProjectException(e);
				}
			} else {
				_project.views().get(0).addBinary(new DirectoryLoader(".", binaryFileFilter, new LazyJavaFileInputSourceFactory()));
			}
		}
	}
	
	private final Map<String, String> _loaders = new HashMap<String,String>();
}
