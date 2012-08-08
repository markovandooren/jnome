package jnome.tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.rejuse.predicate.SafePredicate;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.Config;
import chameleon.core.element.Element;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.support.tool.ModelBuilder;
import chameleon.workspace.Project;

public abstract class Tool {

	public Tool(String[] args, ModelFactory factory, boolean output) throws MalformedURLException, FileNotFoundException, LookupException, ParseException, IOException {
    if(args.length < 2) {
      System.out.println("Usage: java packageName.ToolName "+(output ? "outputDir " : "")+"inputDir* @recursivePackageFQN* #packageFQN* $typeFQN*");
    }
    Config.setCaching(true);
    BasicConfigurator.configure();
//    ((JavaModelFactory)factory).setDebug(true);
    _provider = new ModelBuilder(factory,args,".java",output,true);
    
	  _project = _provider.project();
	}
	
	ModelBuilder _provider;
	
	public Language language() {
		return project().language();
	}
	
	public Project project() {
		return _project;
	}
	
	public Collection<Namespace> namespaces() {
		return _provider.namespaceProvider().elements(project());
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
