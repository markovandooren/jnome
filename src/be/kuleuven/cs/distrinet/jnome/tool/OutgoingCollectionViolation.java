package be.kuleuven.cs.distrinet.jnome.tool;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.MemberVariable;
import be.kuleuven.cs.distrinet.chameleon.support.statement.ReturnStatement;

public class OutgoingCollectionViolation extends SafePredicate<ReturnStatement> {

	@Override
	public boolean eval(ReturnStatement statement) {
		boolean result = false;
		Method nearestAncestor = statement.nearestAncestor(Method.class);
		if(nearestAncestor != null && nearestAncestor.isTrue(statement.language(Java.class).PUBLIC)) {
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
		}
		return result;
	}

}
