package jnome.tool;

import java.util.List;

import jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UnsafePredicate;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.oo.expression.Expression;
import chameleon.oo.method.Implementation;
import chameleon.oo.method.Method;
import chameleon.oo.statement.Block;
import chameleon.oo.statement.Statement;
import chameleon.oo.variable.FormalParameter;
import chameleon.oo.variable.MemberVariable;
import chameleon.oo.variable.Variable;
import chameleon.support.expression.AssignmentExpression;

public class NonDefensiveFieldAssignment extends SafePredicate<AssignmentExpression> {

	@Override
	public boolean eval(AssignmentExpression assignment) {
		boolean result = false;
		try {
			final Method method = assignment.nearestAncestor(Method.class);
			if(method != null && method.isTrue(method.language(Java.class).PUBLIC)) {
				Variable v = assignment.variable();
				if(v instanceof MemberVariable) {
					Expression e = assignment.getValue();
					if(e instanceof CrossReference) {
						final Declaration rhs = ((CrossReference) e).getElement();
						if(rhs instanceof FormalParameter) {
							result = true;
							Statement stat = assignment.farthestAncestor(Statement.class, new SafePredicate<Statement>() {
								@Override
								public boolean eval(Statement s) {
									return (! (s.parent() instanceof Implementation)) && s.nearestAncestor(Method.class) == method;
								}
							});
							Block b = (Block) stat.parent();
							List<Statement> befores = b.statementsBefore(stat);
							for(Statement before : befores) {
								List<CrossReference> crefs = before.descendants(CrossReference.class, new UnsafePredicate<CrossReference,LookupException>(){
									@Override
									public boolean eval(CrossReference cref) throws LookupException {
										return cref.getElement().sameAs(rhs);
									}});
								if(! crefs.isEmpty()) {
									result = false; 
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
		return result;
	}
}