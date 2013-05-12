package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.io.IOException;
import java.io.OutputStreamWriter;

import be.kuleuven.cs.distrinet.chameleon.core.analysis.Result;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.AtomicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Invalid;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.Analyzer;

// TODO
//   0) public void method only invoked from within current class is suspicious
//   1) Check for use of x/all subclasses of a given application class or interface within a single class or method.
//   2) Check for implementation of equals
//   3) Check for implementation of hashCode when equals is overridden
//   4) Heuristic for checking state pattern 
//      a) list which methods are state dependent and which are not
//      b) check for encapsulation

public class DesignAnalyzer extends Analyzer {

	public DesignAnalyzer(Project project) throws ConfigException {
		super(project);
	}
	
	public void analyze(OutputStreamWriter writer) throws LookupException, InputException, IOException {
	  analyze(new IncomingLeak(),writer);
	  analyze(new OutgoingLeak(),writer);
	  analyze(new PublicFieldViolation(),writer);
	  analyze(new NonDefensiveFieldAssignment(),writer);
	}
	
	public void analyze(Analysis<?,?> analysis, OutputStreamWriter writer) throws InputException, IOException {
		Result result = analysisResult(analysis);
		int index = 1;
		writer.write("\n");
		if(result instanceof Invalid) {
		  for(AtomicProblem problem: ((Invalid)result).problems()) {
		  	writer.write(""+index);
		  	writer.write(" ");
		  	writer.write(problem.message());
		  	writer.write("\n");
		  	writer.flush();
		  	index++;
		  }
		}
	}
	

}
