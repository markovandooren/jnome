/**
 * 
 */
package org.aikodi.java.core.language;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.relation.StrictPartialOrder;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguageImpl;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.RegularMemberVariable;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.rejuse.logic.ternary.Ternary;

public class JavaHidesRelation implements StrictPartialOrder<Declaration> {
	@Override
	public boolean contains(Declaration fst, Declaration snd) throws LookupException {
		Declaration first = fst;
		Declaration second = snd;
		boolean result = false;
		if((first instanceof NormalMethod) && (second instanceof NormalMethod)) {
			result = first.lexical().nearestAncestor(Type.class).subtypeOf(second.lexical().nearestAncestor(Type.class)) &&
			         (first.isTrue(first.language(ObjectOrientedLanguage.class).CLASS())) &&
			          first.sameSignatureAs(second);
		} else if(first instanceof RegularMemberVariable && second instanceof RegularMemberVariable) {
			 result = first.lexical().nearestAncestor(Type.class).subtypeOf(second.lexical().nearestAncestor(Type.class)) &&
			          first.sameSignatureAs(second);
		} else if(first instanceof Type && second instanceof Type) {
			result = first.lexical().nearestAncestor(Type.class).subtypeOf(second.lexical().nearestAncestor(Type.class)) &&
      first.sameSignatureAs(second);
		}
		return result;
	}

	@Override
	public boolean equal(Declaration first, Declaration second) throws LookupException {
		return first.equals(second);
	}
}
