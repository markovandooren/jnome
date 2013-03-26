package be.kuleuven.cs.distrinet.jnome.tool;

import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UnsafePredicate;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Implementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;

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
