package be.kuleuven.cs.distrinet.jnome.input.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.aikodi.chameleon.workspace.ConfigElement;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.DirectoryScanner;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectConfigurator;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;
import be.kuleuven.cs.distrinet.jnome.input.BaseJavaProjectLoader;
import be.kuleuven.cs.distrinet.jnome.input.LazyJavaFileInputSourceFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JarLoader;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.string.Strings;

public class JavaEclipseClasspathConfig extends ConfigElement {

	public final String JUNIT4 = "org.eclipse.jdt.junit.JUNIT_CONTAINER/4";
	
	public JavaEclipseClasspathConfig(Project project, Map<String,String> containerConfiguration, Map<String, String> environment) throws ProjectException {
		_project = project;
		_environment = new HashMap<>(environment);
		_loaders.putAll(containerConfiguration);
		view().addBinary(new BaseJavaProjectLoader(Java7LanguageFactory.javaBaseJar(),java()));
		readFromXML(new File(project.root(),".classpath"));
	}
	
	private Project _project;
	
	private Java7 java() {
		return (Java7) view().language();
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
		
		private String finalPath() {
			String[] box = new String[1];
			box[0] = _path;
			_environment.forEach((k,v) ->{
				if(box[0] != null) {
				  box[0] = box[0].replace(k, v);
				}
			});
			return box[0];
		}
		
		
		public String path() {
			return _path;
		}
		
		public void setExclude(String excluded) {
			_exludeGlobs = Strings.splitUnescapedPipe(excluded);
			
			//FIXME finish this.
		}
		
		private List<String> _exludeGlobs = new ArrayList<>();
		
		@Override
		protected void $after() throws ConfigException {
			try {
				if(_kind.equals("src")) {
					loadSrc(finalPath());
				} else if(_kind.equals("lib") || _kind.equals("var")) {
					loadBin(finalPath());
				} else if(_kind.equals("con")) {
					if(finalPath() == null) {
						throw new IllegalStateException("The container has not path attribute.");
					}
					if(! finalPath().startsWith("org.eclipse.jdt.launching.JRE_CONTAINER")) {
						String path = _loaders.get(finalPath());
//						if(path == null) {
//							throw new IllegalStateException("No container with key "+_path+" is registered.");
//						}
						if(path != null) {
						  loadBin(path);
						}
					}
				} else if(_kind.equals("output")) {
					//FIXME Do we need to support the output option? It is tool dependeny, but since
					// this configuration is for Eclipse, we should set the corresponding (currently
					// non-existing) option for the (non-existing) compiler.
				} else {
					throw new IllegalStateException("Unknown classpath entry kind: "+_kind);
				}
			} catch (ProjectException e) {
				throw new ConfigException(e);
			}
		}
		
		private void loadSrc(String path) throws ProjectException {
			_project.views().get(0).addSource(new DirectoryScanner(path, java().plugin(ProjectConfigurator.class).sourceFileFilter(), new LazyJavaFileInputSourceFactory()));
		}
		
		private void loadBin(String path) throws ProjectException {
			Predicate<? super String,Nothing> binaryFileFilter = java().plugin(ProjectConfigurator.class).binaryFileFilter();
			if(path.endsWith(".jar")) {
				JarFile file;
				try {
					file = new JarFile(_project.absoluteFile(path));
					_project.views().get(0).addBinary(new JarLoader(file, binaryFileFilter));
				} catch (IOException e) {
					throw new ProjectException(e);
				}
			} else {
				_project.views().get(0).addBinary(new DirectoryScanner(".", binaryFileFilter, new LazyJavaFileInputSourceFactory()));
			}
		}
	}
	
	private final Map<String, String> _loaders = new HashMap<String,String>();
	private final Map<String, String> _environment;
}
