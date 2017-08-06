package org.aikodi.java.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.support.tool.ModelBuilder;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.LanguageRepository;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.chameleon.workspace.Workspace;
import org.aikodi.java.core.language.Java7LanguageFactory;
import org.aikodi.rejuse.predicate.Predicate;
import org.aikodi.rejuse.predicate.SafePredicate;

public abstract class CommandLineTool {

	public CommandLineTool(String[] args) throws ConfigException {
    if(args.length < 2) {
      System.out.println("Usage: java packageName.ToolName "+"xmlConfigFile @recursivePackageFQN* #packageFQN* $typeFQN*");
    }
    LanguageRepository repo = new LanguageRepository();
		Workspace workspace = new Workspace(repo);
    repo.add(new Java7LanguageFactory().create());
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
			result.addAll(ns.lexical().descendants(kind, safe));
		}
		return result;
	}

	public <E extends Element,X extends Exception> List<E> find(Class<E> kind, Predicate<E,X> unsafe) throws X {
		List<E> result = new ArrayList<E>();
		for(Namespace ns: namespaces()) {
			result.addAll(ns.lexical().descendants(kind, unsafe));
		}
		return result;
	}
}
