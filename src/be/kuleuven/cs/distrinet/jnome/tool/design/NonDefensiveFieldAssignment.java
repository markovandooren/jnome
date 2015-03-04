package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.List;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.validation.AtomicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.method.Implementation;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.statement.Block;
import org.aikodi.chameleon.oo.statement.Statement;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.oo.variable.MemberVariable;
import org.aikodi.chameleon.support.expression.AssignmentExpression;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UniversalPredicate;
import be.kuleuven.cs.distrinet.rejuse.tree.TreeStructure;

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
	protected <X extends AssignmentExpression> void doPerform(TreeStructure<X> tree) throws Nothing {
	  AssignmentExpression assignment = tree.node();
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
								Statement stat = assignment.farthestAncestor(new UniversalPredicate<Statement,Nothing>(Statement.class) {
									@Override
									public boolean uncheckedEval(Statement s) {
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
