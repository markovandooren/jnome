package jnome.tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import jnome.input.JavaModelFactory;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.input.ModelFactory;
import chameleon.input.ParseException;
import chameleon.oo.method.Method;
import chameleon.oo.type.Type;
import chameleon.oo.variable.FormalParameter;
import chameleon.oo.variable.MemberVariable;
import chameleon.oo.variable.Variable;
import chameleon.support.expression.AssignmentExpression;
import chameleon.support.statement.ReturnStatement;
import chameleon.workspace.ConfigException;
import chameleon.workspace.ProjectException;

// TODO
//   1) Check for use of x/all subclasses of a given application class or interface within a single class or method.
//   2) Check for implementation of equals
//   3) Check for implementation of hashCode when equals is overridden
//   4) Heuristic for checking state pattern 
//      a) list which methods are state dependent and which are not
//      b) check for encapsulation

public class DesignChecker extends CommandLineTool {

	public DesignChecker(String[] args) throws ConfigException {
		super(args);
	}

	public void findIncomingViolations() throws LookupException {
		List<AssignmentExpression> results = find(AssignmentExpression.class,new IncomingCollectionViolation());
		int count = 1;
		for(AssignmentExpression assignment : results) {
			Variable member = assignment.variable();
			Method m = assignment.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			FormalParameter param = (FormalParameter) ((CrossReference)assignment.getValue()).getElement();
			String msg = count++ + "ENCAPSULATION Collection parameter "+param.name()+ " of public method "+m.name()+" in "+t.getFullyQualifiedName()+ " is directly assigned to field "+member.name();
			System.out.println(msg);
		}
	}
	
	public void findNonDefensiveFieldAssignments() throws LookupException {
		List<AssignmentExpression> results = find(AssignmentExpression.class,new NonDefensiveFieldAssignment());
		int count = 1;
		for(AssignmentExpression assignment : results) {
			Variable member = assignment.variable();
			Method m = assignment.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			FormalParameter param = (FormalParameter) ((CrossReference)assignment.getValue()).getElement();
			String msg = count++ + "DEFENSIVE Parameter "+param.name()+ " of public method "+m.name()+" in "+t.getFullyQualifiedName()+ " is assigned to field "+member.name()+" without being referenced before the assignment.";
			System.out.println(msg);
		}
	}
	
	public void findOutgoingViolations() throws LookupException {
		List<ReturnStatement> results = find(ReturnStatement.class,new OutgoingCollectionViolation());
		int count = 1;
		for(ReturnStatement ret : results) {
			MemberVariable param = (MemberVariable) ((CrossReference)ret.getExpression()).getElement();
			Method m = ret.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			String msg = count++ + "ENCAPSULATION Public method "+m.name()+ " in "+t.getFullyQualifiedName()+" directly returns the collection stored in field "+param.name();
			System.out.println(msg);
		}
	}
	
	public static void main(String[] args) throws Exception {
		DesignChecker checker = new DesignChecker(args);
		checker.findIncomingViolations();
		checker.findOutgoingViolations();
		checker.findNonDefensiveFieldAssignments();
  }

}
