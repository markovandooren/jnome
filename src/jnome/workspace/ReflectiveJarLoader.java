package jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;

import jnome.core.language.Java;
import jnome.input.ReflectiveClassParser;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.LazyNamespace;
import chameleon.core.namespace.RootNamespace;
import chameleon.util.Util;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;
import chameleon.workspace.ProjectLoader;

public class ReflectiveJarLoader extends AbstractJarLoader implements ProjectLoader {

	public ReflectiveJarLoader(Project project, String path) throws ProjectException {
		super(project, path);
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
	
	private ClassLoader _loader;

	private void process() throws ProjectException {
  	try {
  	Enumeration<JarEntry> entries = createJarFile().entries();
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
