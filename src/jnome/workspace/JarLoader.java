package jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jnome.core.language.Java;
import jnome.input.ReflectiveClassParser;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.LazyNamespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.util.Util;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;
import chameleon.workspace.ProjectLoader;

public class JarLoader implements ProjectLoader {

	public JarLoader(Project project, String path) throws ProjectException {
//		this(project, createLoader(path));
		_path = path;
		_project = project;
		_loader = createLoader(path);
		process();
	}
	
	private static ClassLoader createLoader(String path) throws ProjectException {
		try {
			return URLClassLoader.newInstance(new URL[] {new File(path).toURI().toURL()}, null);
		} catch (MalformedURLException e) {
			throw new ProjectException(e);
		}
	}
	
//	private JarLoader(Project project, ClassLoader loader) throws ProjectException {
//		_project = project;
//		_loader = loader;
//		process();
//	}
	
	private ClassLoader _loader;

	private String _path;
	
	private Project _project;
	
	public Project project() {
		return _project;
	}
	
	public URL url() throws MalformedURLException {
		return new File(_path).toURI().toURL();
	}
	
	private void process() throws ProjectException {
  	try {
  	Enumeration<JarEntry> entries = new JarFile(_path).entries();
  	URL url = url();
  	RootNamespace root = project().namespace();
  	ReflectiveClassParser parser = new ReflectiveClassParser((Java)project().language());
		while(entries.hasMoreElements()) {
  		JarEntry entry = entries.nextElement();
  		String name = entry.getName();
  		if(name.endsWith(".class")) {
  			String className = name.substring(0, name.length()-6).replace(File.separatorChar, '.');
  			String shortName = Util.getLastPart(className);
  			boolean validClassName = ! shortName.contains("$");
  			// Check if it is an anonymous inner class by
  			// checking whether the name is a number.
  			try {
  				Integer i = new Integer(shortName);
  			} catch(NumberFormatException e) {
  				validClassName = true;
  			}
  			if(validClassName) {
  				String packageName = Util.getAllButLastPart(className);
  				try {
						LazyNamespace ns = (LazyNamespace) root.getOrCreateNamespace(packageName);
						new LazyReflectiveInputSource(_loader, parser, className, ns);
					} catch (LookupException | MalformedURLException e) {
						throw new ProjectException(e);
					}
  			}
  		}
  	}
  	} catch (IOException e) {
			throw new ProjectException(e);
		}
  }

}
