package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.Collection;

import be.kuleuven.cs.distrinet.chameleon.core.analysis.Result;
import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.AtomicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Invalid;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.design.Analysis;
import be.kuleuven.cs.distrinet.jnome.tool.design.IncomingLeak;
import be.kuleuven.cs.distrinet.jnome.tool.design.NonDefensiveFieldAssignment;
import be.kuleuven.cs.distrinet.jnome.tool.design.OutgoingLeak;

// TODO
//   0) public void method only invoked from within current class is suspicious
//   1) Check for use of x/all subclasses of a given application class or interface within a single class or method.
//   2) Check for implementation of equals
//   3) Check for implementation of hashCode when equals is overridden
//   4) Heuristic for checking state pattern 
//      a) list which methods are state dependent and which are not
//      b) check for encapsulation

public class DesignAnalyser {

	public DesignAnalyser(Project project) throws ConfigException {
		_project = project;
	}
	
	private Project _project;

	public void findViolations() throws LookupException, InputException {
	  analyse(new IncomingLeak());
	  analyse(new OutgoingLeak());
	  analyse(new NonDefensiveFieldAssignment());
	}
	
	public void analyse(Analysis<?,?> analysis) throws InputException {
		Result result = analysisResult(analysis);
		int index = 1;
		System.out.println("");
		if(result instanceof Invalid) {
		  for(AtomicProblem problem: ((Invalid)result).problems()) {
		  	System.out.println(index+" "+problem.message());
		  	index++;
		  }
		}
	}
	
	private Result analysisResult(Analysis<?,?> analysis) throws InputException {
		for(Document doc: sourceDocuments()) {
			doc.apply(analysis);
		}
		return analysis.result();
	}

	public Project project() {
		return _project;
	}
	
	public Collection<Document> sourceDocuments() throws InputException {
		return project().sourceDocuments();
	}

}
