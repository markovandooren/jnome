package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.Config;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.support.tool.ModelBuilder;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.LanguageRepository;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.chameleon.workspace.Workspace;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

public abstract class CommandLineTool {

	public CommandLineTool(String[] args) throws ConfigException {
    if(args.length < 2) {
      System.out.println("Usage: java packageName.ToolName "+"xmlConfigFile @recursivePackageFQN* #packageFQN* $typeFQN*");
    }
    Config.setCaching(true);
    LanguageRepository repo = new LanguageRepository();
		Workspace workspace = new Workspace(repo);
    repo.add(new JavaLanguageFactory().create());
		_provider = new ModelBuilder(args, workspace);
    
	  _project = _provider.project();
	}
	
	ModelBuilder _provider;
	
	public Language language() {
		return view().language();
	}
	
	public Project project() {
		return _project;
	}
	
	public View view() {
		return project().views().get(0);
	}
	
	public Collection<Namespace> namespaces() {
		return _provider.namespaceProvider().elements(view());
	}
	
	private Project _project;
	
	public <E extends Element> List<E> find(Class<E> kind, SafePredicate<E> safe) {
		List<E> result = new ArrayList<E>();
		for(Namespace ns: namespaces()) {
			//System.out.println("Analyzing "+type.getFullyQualifiedName());
			result.addAll(ns.descendants(kind, safe));
		}
		return result;
	}

	public <E extends Element,X extends Exception> List<E> find(Class<E> kind, Predicate<E,X> unsafe) throws X {
		List<E> result = new ArrayList<E>();
		for(Namespace ns: namespaces()) {
			result.addAll(ns.descendants(kind, unsafe));
		}
		return result;
	}
}
