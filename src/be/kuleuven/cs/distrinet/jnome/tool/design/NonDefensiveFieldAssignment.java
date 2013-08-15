package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.analysis.Analysis;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.validation.AtomicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Implementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

/**
 * Check whether a parameter is assigned to a field without being
 * referenced
 * 
 * @author Marko van Dooren
 */
public class NonDefensiveFieldAssignment extends Analysis<AssignmentExpression,Verification> {

	public NonDefensiveFieldAssignment() {
		super(AssignmentExpression.class, Valid.create());
	}

	
	
	private static class NonDefensiveFieldAssignmentResult extends AtomicProblem {

		public NonDefensiveFieldAssignmentResult(FormalParameter parameter, Variable member) {
			super(parameter);
			this._parameter = parameter;
			this._member = member;
		}

		private FormalParameter _parameter;
		private Variable _member; 
		
		@Override
		public String message() {
			Method m = _parameter.nearestAncestor(Method.class);
			Type t = m.nearestAncestor(Type.class);
			return "Warning: encapsulation: unchecked assignment to internal state: parameter "+_parameter.name()+ 
					         " of public method "+m.name()+" in "+t.getFullyQualifiedName()+ 
					         " is assigned to field "+_member.name()+
					         " without being referenced in the statements before the assignment.";
		}
		
	}



	@Override
	protected void doPerform(AssignmentExpression assignment) throws Nothing {
		Verification result = Valid.create();
		try {
			final Method method = assignment.nearestAncestor(Method.class);
			if(method != null && method.isTrue(method.language(Java.class).PUBLIC)) {
				Variable v = assignment.variable();
				if(v instanceof MemberVariable) {
					Expression e = assignment.getValue();
					if(e instanceof CrossReference) {
						final Declaration rhs = ((CrossReference) e).getElement();
						if(rhs instanceof FormalParameter) {
							Type booleanType = assignment.view(JavaView.class).primitiveType("boolean");
							if(! ((FormalParameter) rhs).getType().sameAs(booleanType)) {
								boolean notMentioned = true;
								Statement stat = assignment.farthestAncestor(Statement.class, new SafePredicate<Statement>() {
									@Override
									public boolean eval(Statement s) {
										// If s.parent() == the implementation object, then we have reached the
										// block of the implementation, so we have to stop before that to object
										// the child statement of the block of the implementation
										return (! (s.parent() instanceof Implementation)) && s.nearestAncestor(Method.class) == method;
									}
								});
								Block b = (Block) stat.parent();
								List<Statement> befores = b.statementsBefore(stat);
								for(Statement before : befores) {
									List<CrossReference> crefs = before.descendants(CrossReference.class, new AbstractPredicate<CrossReference,LookupException>(){
										@Override
										public boolean eval(CrossReference cref) throws LookupException {
											return cref.getElement().sameAs(rhs);
										}});
									if(! crefs.isEmpty()) {
										notMentioned = false; 
									}
								}
								if(notMentioned) {
									result = new NonDefensiveFieldAssignmentResult((FormalParameter)rhs,v);
								}
							}
						}
					}
				}
			}
		}
		catch(LookupException exc) {
			// swallow for now.
		}
		setResult(result().and(result));
	}
	
}
