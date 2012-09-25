package jnome.workspace;

import java.io.File;

import jnome.core.language.JavaLanguageFactory;
import jnome.input.LazyJavaFileInputSourceFactory;
import chameleon.core.namespace.LazyRootNamespace;
import chameleon.input.ModelFactory;
import chameleon.workspace.DirectoryLoader;
import chameleon.workspace.ProjectException;

class ProjectConfig extends ConfigElement {
	
	public ProjectConfig(File root) {
		_project = new chameleon.workspace.Project(null, new LazyRootNamespace(), new JavaLanguageFactory().create(), root);
		_root = root;
	}
	
	private File _root;
	
	public File root() {
		return _root;
	}
	
	private chameleon.workspace.Project _project;
	
	public chameleon.workspace.Project project() {
		return _project;
	}
	
	public void setName(String text) {
		_project.setName(text);
	}
	
	public String getName() {
		return _project.name();
	}
	
	public class Language extends ConfigElement {
	}
	
	public class SourcePath extends ConfigElement {
		public class Source extends ProjectConfig.Source {
			protected void $after() throws ConfigException {
				try {
					File root = new File(_path);
					if(!root.isAbsolute()) {
						root = new File(_project.root().getAbsolutePath()+File.separator+_path);
					}
					_project.addSource(new DirectoryLoader(_extension, root, new LazyJavaFileInputSourceFactory(_project.language().plugin(ModelFactory.class))));
				} catch (ProjectException e) {
					throw new ConfigException(e);
				}
			}
		}
	}
	
	public class BinaryPath  extends ConfigElement {
		// Duplicate for now, but that will change when proper support for "binary" modules is added.
		public class Source extends ProjectConfig.Source {
			protected void $after() throws ConfigException {
				try {
					File root = new File(_path);
					if(!root.isAbsolute()) {
						root = new File(_project.root().getAbsolutePath()+File.separator+_path);
					}
					_project.addSource(new DirectoryLoader(_extension, root, new LazyJavaFileInputSourceFactory(_project.language().plugin(ModelFactory.class))));
				} catch (ProjectException e) {
					throw new ConfigException(e);
				}
			}
		}
		
		public class Jar extends ConfigElement {
	  	public void setFile(String path) throws ConfigException {
				try {
//					File file = new File(path);
//					if(! file.isAbsolute()) {
//						file = new File(root().getPath()+File.separator+path);
//					}
//
					_project.addSource(new JarLoader(_project, path));
				} catch (ProjectException e) {
					throw new ConfigException(e);
				}
			}
	  }
	}
	
	public static class Source extends ConfigElement {
		
		protected String _path;
		
		public void setRoot(String path) {
			_path = path;
		}
		
		protected String _extension;
		
		public void setExtension(String text) {
			_extension = text;
		}
		
	}
	
}