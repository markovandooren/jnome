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

public class EncapsulationChecker extends Tool {

	public EncapsulationChecker(String[] args, ModelFactory factory) throws MalformedURLException, FileNotFoundException, LookupException,
			ParseException, IOException {
		super(args, factory,false);
	}

	public void findIncomingViolations() throws LookupException {
		List<AssignmentExpression> results = find(AssignmentExpression.class,new IncomingCollectionViolation());
		for(AssignmentExpression assignment : results) {
			Variable member = assignment.variable();
			Method m = assignment.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			FormalParameter param = (FormalParameter) ((CrossReference)assignment.getValue()).getElement();
			String msg = "Parameter "+param.name()+ " of method "+m.name()+" in "+t.getFullyQualifiedName()+ " is directly assigned to "+member.name();
			System.out.println(msg);
		}
	}
	
	
	public void findOutgoingViolations() throws LookupException {
		List<ReturnStatement> results = find(ReturnStatement.class,new OutgoingCollectionViolation());
		for(ReturnStatement ret : results) {
			MemberVariable param = (MemberVariable) ((CrossReference)ret.getExpression()).getElement();
			Method m = ret.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			String msg = "Method "+m.name()+ " in "+t.getFullyQualifiedName()+" directly returns the collection stored in "+param.name();
			System.out.println(msg);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
  	JavaModelFactory factory = new JavaModelFactory();
		EncapsulationChecker checker = new EncapsulationChecker(args, factory);
		checker.findIncomingViolations();
		checker.findOutgoingViolations();
  }

}
