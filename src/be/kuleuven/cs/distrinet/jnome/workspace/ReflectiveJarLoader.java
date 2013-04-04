package be.kuleuven.cs.distrinet.jnome.workspace;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.input.ReflectiveClassParser;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyNamespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoaderImpl;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;

/**
 * A class for loading Java classes from jar files via reflection.
 *
 * This class is not part of the {@link ZipLoader} hierarchy because there are too few similarities
 * and it does not fit in well. Currently, the regular {@link JarLoader} class is recommended, so
 * I'm not going to put in the effort to adapt this class to fit in the {@link ZipLoader} hierarchy.
 * Therefore, it is marked as deprecated, but it does function propertly.
 * 
 * @author Marko van Dooren
 * @deprecated
 */
public class ReflectiveJarLoader extends DocumentLoaderImpl {

	public ReflectiveJarLoader(String path) throws ProjectException, InputException {
		this(path,false);
	}
	public ReflectiveJarLoader(String path, boolean isBaseLoader) throws ProjectException, InputException {
		_path = path;
	}
	
	private String _path;
	
	@Override
	protected void notifyViewAdded(View view) throws ProjectException {
		_loader = createLoader(view.project().absoluteFile(_path));
		try {
			createInputSources();
		} catch (InputException e) {
			throw new ProjectException(e);
		}

	}
	
	private static ClassLoader createLoader(File file) throws ProjectException {
		try {
			return URLClassLoader.newInstance(new URL[] {file.toURI().toURL()}, null);
		} catch (MalformedURLException e) {
			throw new ProjectException(e);
		}
	}
	
	private ClassLoader _loader;

	private void createInputSources() throws ProjectException, InputException {
  	try {
  	Enumeration<JarEntry> entries = new JarFile(project().absolutePath(_path)).entries();
  	RootNamespace root = view().namespace();
  	ReflectiveClassParser parser = new ReflectiveClassParser((Java)view().language());
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
						LazyNamespace ns = (LazyNamespace) root.getOrCreateNamespace(packageName);
						new LazyReflectiveInputSource(_loader, parser, className, ns,this);
  			}
  		}
  	}
  	} catch (IOException e) {
			throw new ProjectException(e);
		}
  }

}
