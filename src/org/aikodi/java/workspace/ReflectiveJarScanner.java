package org.aikodi.java.workspace;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.aikodi.chameleon.core.namespace.LazyNamespace;
import org.aikodi.chameleon.core.namespace.RootNamespace;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.AbstractZipScanner;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.chameleon.workspace.DocumentScannerContainer;
import org.aikodi.chameleon.workspace.DocumentScannerImpl;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.input.ReflectiveClassParser;

/**
 * A class for scanning Java classes from jar files via reflection.
 *
 * This class is not part of the {@link AbstractZipScanner} hierarchy because there are too few similarities
 * and it does not fit in well. Currently, the regular {@link JarScanner} class is recommended, so
 * I'm not going to put in the effort to adapt this class to fit in the {@link AbstractZipScanner} hierarchy.
 * Therefore, it is marked as deprecated, but it does function properly.
 * 
 * @author Marko van Dooren
 */
public class ReflectiveJarScanner extends DocumentScannerImpl {

	public ReflectiveJarScanner(String path) throws ProjectException, InputException {
		this(path,false);
	}
	public ReflectiveJarScanner(String path, boolean isBaseLoader) throws ProjectException, InputException {
		_path = path;
	}
	
	private String _path;
	
	@Override
	public void notifyContainerConnected(DocumentScannerContainer container) throws ProjectException {
		View view = view();
		if(view != null) {
			_loader = createLoader(view.project().absoluteFile(_path));
		}
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
  	_jarFile = new JarFile(project().absolutePath(_path));
		Enumeration<JarEntry> entries = _jarFile.entries();
  	RootNamespace root = view().namespace();
  	ReflectiveClassParser parser = new ReflectiveClassParser((Java7)view().language());
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
						new LazyReflectiveDocumentLoader(_loader, parser, className, ns,this);
  			}
  		}
  	}
  	} catch (IOException e) {
			throw new ProjectException(e);
		}
  }

	private JarFile _jarFile;
	
	@Override
	protected void finalize() throws Throwable {
		_jarFile.close();
	}
	@Override
	public String label() {
		return _path;
	}
	
	@Override
	public boolean scansSameAs(DocumentScanner loader) {
		if(loader == this) {
			return true;
		} else if(loader instanceof ReflectiveJarScanner) {
			return ((ReflectiveJarScanner) loader)._jarFile.equals(_jarFile);
		}
		return false;
	}
}
