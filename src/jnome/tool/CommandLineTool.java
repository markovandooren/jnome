package jnome.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jnome.core.language.JavaLanguageFactory;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UnsafePredicate;
import chameleon.core.Config;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.namespace.Namespace;
import chameleon.support.tool.ModelBuilder;
import chameleon.workspace.ConfigException;
import chameleon.workspace.LanguageRepository;
import chameleon.workspace.Project;
import chameleon.workspace.View;
import chameleon.workspace.Workspace;

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

	public <E extends Element,X extends Exception> List<E> find(Class<E> kind, UnsafePredicate<E,X> unsafe) throws X {
		List<E> result = new ArrayList<E>();
		for(Namespace ns: namespaces()) {
			result.addAll(ns.descendants(kind, unsafe));
		}
		return result;
	}
}
