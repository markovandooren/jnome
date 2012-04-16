package jnome.tool;

import org.rejuse.predicate.SafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.oo.expression.Expression;
import chameleon.oo.type.Type;
import chameleon.oo.variable.MemberVariable;
import chameleon.support.statement.ReturnStatement;

public class OutgoingCollectionViolation extends SafePredicate<ReturnStatement> {

	@Override
	public boolean eval(ReturnStatement statement) {
		boolean result = false;
		try {
		Expression expr = statement.getExpression();
		if(expr instanceof CrossReference) {
			Declaration declaration = ((CrossReference) expr).getElement();
			if(declaration instanceof MemberVariable) {
			Type type = ((MemberVariable) declaration).getType();
				result = new IsCollectionType().eval(type);
			}
		}
		}catch(LookupException exc) {
			// swallow for now
		}
		return result;
	}

}
