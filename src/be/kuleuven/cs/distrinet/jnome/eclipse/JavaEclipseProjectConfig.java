package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.util.Map;

import org.aikodi.chameleon.core.namespace.LazyRootNamespace;
import org.aikodi.chameleon.workspace.ConfigElement;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.ProjectException;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7LanguageFactory;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;

public class JavaEclipseProjectConfig extends ConfigElement {

	public JavaEclipseProjectConfig(File root, Map<String,String> containerConfiguration) {
		_root = root;
		_containerConfiguration = containerConfiguration;
		readFromXML(new File(_root,".project"));
	}
	
	private Map<String,String> _containerConfiguration;
	
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
			new JavaEclipseClasspathConfig(_project,_containerConfiguration);
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
