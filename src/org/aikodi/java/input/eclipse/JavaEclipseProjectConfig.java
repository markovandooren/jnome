package org.aikodi.java.input.eclipse;

import java.util.HashMap;
import java.util.Map;

import java.io.File;

import org.aikodi.chameleon.core.namespace.LazyRootNamespace;
import org.aikodi.chameleon.workspace.ConfigElement;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectException;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.language.Java7LanguageFactory;
import org.aikodi.java.workspace.JavaView;

/**
 * A class for parsing the eclipse .project and .class path files and creating a corresponding
 * Chameleon project.
 * 
 * @author Marko van Dooren
 */
public class JavaEclipseProjectConfig extends ConfigElement {

	public JavaEclipseProjectConfig(File root, Map<String,String> containerConfiguration, Map<String, String> environment) {
		if(root == null) {
			throw new IllegalArgumentException("The root directory for the Eclipse project must be given.");
		}
		_root = root;
		_containerConfiguration = new HashMap<>(containerConfiguration);
		_environment = new HashMap<>(environment);
		readFromXML(new File(_root,".project"));
	}
	
	private Map<String,String> _containerConfiguration;
  private Map<String, String> _environment;
	
	@Override
	public String nodeName() {
		return "projectDescription";
	}
	
	public class Name extends ConfigElement {
		@Override
		public void $setText(String text) throws ConfigException {
			_name = text;
		}
	}
	
	private String _name;
	
	@Override
	protected void $after() throws ConfigException {
		Java7 java = new Java7LanguageFactory().create();
		_project = new Project(_name, _root, new JavaView(new LazyRootNamespace(), java));
		try {
			new JavaEclipseClasspathConfig(_project,_containerConfiguration,_environment);
		} catch (ProjectException e) {
			throw new ConfigException(e);
		}
	}
	
	private Project _project;
	
	public Project project() {
		return _project;
	}
	
	private File _root;
}
