package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.analysis.Analyzer;
import org.aikodi.chameleon.analysis.Result;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.AtomicProblem;
import org.aikodi.chameleon.core.validation.Invalid;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;

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
		List<Analysis<?,?>> analyses = new ArrayList<>();
	  analyses.add(new IncomingLeak());
	  analyses.add(new OutgoingLeak());
	  analyses.add(new PublicFieldViolation());
	  analyses.add(new NonDefensiveFieldAssignment());
	  analyses.add(new AssignmentAsExpression());
	  analyze(analyses, writer);
	}
	
	private void analyze(List<Analysis<?,?>> analyses, OutputStreamWriter writer) throws LookupException, InputException, IOException {
		for(Analysis<?,?> analysis: analyses) {
			analyze(analysis, writer);
		}
	}
	
	public void analyze(Analysis<? extends Element,?> analysis, OutputStreamWriter writer) throws InputException, IOException {
		Result<?> result = analysisResult(analysis);
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
