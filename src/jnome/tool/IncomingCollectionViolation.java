package jnome.tool;

import jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.oo.expression.Expression;
import chameleon.oo.method.Method;
import chameleon.oo.type.Type;
import chameleon.oo.variable.FormalParameter;
import chameleon.oo.variable.MemberVariable;
import chameleon.oo.variable.Variable;
import chameleon.support.expression.AssignmentExpression;

public class IncomingCollectionViolation extends SafePredicate<AssignmentExpression> {

	@Override
	public boolean eval(AssignmentExpression assignment) {
		boolean result = false;
		try {
			Method method = assignment.nearestAncestor(Method.class);
			if(method != null && method.isTrue(method.language(Java.class).PUBLIC)) {
				Variable v = assignment.variable();
				if(v instanceof MemberVariable) {
					Expression e = assignment.getValue();
					if(e instanceof CrossReference) {
						Declaration rhs = ((CrossReference) e).getElement();
						if(rhs instanceof FormalParameter) {
							Type type_of_value = ((FormalParameter)rhs).getType();
							result = new IsCollectionType().eval(type_of_value);
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