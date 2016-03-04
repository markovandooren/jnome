package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.analysis.Analyzer;
import org.aikodi.chameleon.analysis.Result;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.AtomicProblem;
import org.aikodi.chameleon.core.validation.Invalid;
import org.aikodi.chameleon.workspace.ConfigException;
import org.aikodi.chameleon.workspace.InputException;
import org.aikodi.chameleon.workspace.Project;
import org.aikodi.rejuse.exception.Handler;

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
	
	public void analyze(OutputStreamWriter writer, MessageFormatter formatter) throws LookupException, InputException, IOException {
		List<Analysis<?,?,? extends Exception>> analyses = new ArrayList<>();
	  analyses.add(new IncomingLeak());
	  analyses.add(new OutgoingLeak());
	  analyses.add(new SuspiciousCastAnalysis());
//	  analyses.add(new PublicFieldViolation());
	  analyses.add(new NonPrivateNonFinalField());
	  analyses.add(new EmptyAbstractType());
	  //analyses.add(new NonDefensiveFieldAssignment());
	  analyses.add(new AssignmentAsExpression());
	  analyses.add(new EqualsWithoutHashCode());
	  analyze(analyses, writer,formatter, Handler.<Exception>resume(), Handler.<Exception>resume());
	}
	
	private <E extends Exception, A extends Exception, I extends Exception> void analyze(
	    List<Analysis<?,?,? extends E>> analyses, 
	    OutputStreamWriter writer, 
	    MessageFormatter formatter,
	    Handler<? super E,A> analysisGuard,
	    Handler<Exception,I> handler) throws A, I , IOException {
		for(Analysis<?,?,? extends E> analysis: analyses) {
			analyze(analysis, writer,formatter, analysisGuard, handler);
		}
	}
	
	public <E extends Exception,A extends Exception, I extends Exception> void analyze(Analysis<?,?,E> analysis, 
	    OutputStreamWriter writer, 
	    MessageFormatter formatter,
	    Handler<? super E,A> analysisGuard,
	    Handler<Exception,I> handler) throws A,I, IOException {
		Result<?> result = analysisResult(analysis, analysisGuard, handler);
		if(result instanceof Invalid) {
		  for(AtomicProblem problem: ((Invalid)result).problems()) {
		  	writer.write(formatter.format(problem));
		  	writer.write("\n");
		  	writer.flush();
		  }
		}
	}
	

}
