package be.kuleuven.cs.distrinet.jnome.eclipse;

import java.io.File;
import java.util.Map;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.LazyRootNamespace;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigElement;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
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
		Java java = new JavaLanguageFactory().create();
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
