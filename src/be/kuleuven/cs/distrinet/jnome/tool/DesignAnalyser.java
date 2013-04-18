package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;
import be.kuleuven.cs.distrinet.chameleon.util.action.Nothing;
import be.kuleuven.cs.distrinet.chameleon.workspace.ConfigException;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.jnome.tool.design.Analysis;
import be.kuleuven.cs.distrinet.jnome.tool.design.IncomingCollectionViolation;
import be.kuleuven.cs.distrinet.jnome.tool.design.NonDefensiveFieldAssignment;
import be.kuleuven.cs.distrinet.jnome.tool.design.OutgoingCollectionViolation;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UnsafePredicate;

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

	public void findIncomingViolations() throws LookupException, InputException {
		IncomingCollectionViolation analysis = new IncomingCollectionViolation();
		analyse(analysis);
		System.out.println(analysis.result().message());
	}
	
	public void findUncheckedFieldAssignments() throws LookupException, InputException {
		List<AssignmentExpression> results = find(AssignmentExpression.class,new NonDefensiveFieldAssignment());
		int count = 1;
		for(AssignmentExpression assignment : results) {
			Variable member = assignment.variable();
			Method m = assignment.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			FormalParameter param = (FormalParameter) ((CrossReference)assignment.getValue()).getElement();
			String msg = count++ + ": DEFENSIVE Parameter "+param.name()+ " of public method "+m.name()+" in "+t.getFullyQualifiedName()+ " is assigned to field "+member.name()+" without being referenced before the assignment.";
			System.out.println(msg);
		}
	}
	
	public void findOutgoingViolations() throws LookupException, InputException {
		List<ReturnStatement> results = find(ReturnStatement.class,new OutgoingCollectionViolation());
		int count = 1;
		for(ReturnStatement ret : results) {
			MemberVariable param = (MemberVariable) ((CrossReference)ret.getExpression()).getElement();
			Method m = ret.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			String msg = count++ + ": ENCAPSULATION Public method "+m.name()+ " in "+t.getFullyQualifiedName()+" directly returns the collection stored in field "+param.name();
			System.out.println(msg);
		}
	}
	
	public void findViolations() throws LookupException, InputException {
	  findIncomingViolations();
	  findOutgoingViolations();
	  findUncheckedFieldAssignments();
	}
	
//	public static void main(String[] args) throws Exception {
//		DesignChecker checker = new DesignChecker(args);
//		checker.findIncomingViolations();
//		checker.findOutgoingViolations();
//		checker.findNonDefensiveFieldAssignments();
//  }
	
	public void analyse(Analysis<?,?> analysis) throws Nothing, InputException {
		for(Document doc: sourceDocuments()) {
			doc.apply(analysis);
		}
	}

	//TODO Extract to class Tool or so?
	public <E extends Element> List<E> find(Class<E> kind, SafePredicate<E> safe) throws InputException {
		List<E> result = new ArrayList<E>();
		for(Document doc: sourceDocuments()) {
			//System.out.println("Analyzing "+type.getFullyQualifiedName());
			result.addAll(doc.descendants(kind, safe));
		}
		return result;
	}
	
	public Project project() {
		return _project;
	}
	
	public Collection<Document> sourceDocuments() throws InputException {
		return project().sourceDocuments();
	}

	public <E extends Element,X extends Exception> List<E> find(Class<E> kind, UnsafePredicate<E,X> unsafe) throws X, InputException {
		List<E> result = new ArrayList<E>();
		for(Document doc: sourceDocuments()) {
			result.addAll(doc.descendants(kind, unsafe));
		}
		return result;
	}

}
