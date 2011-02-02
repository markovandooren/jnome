/**
 * 
 */
package jnome.core.language;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaHidesRelation extends StrictPartialOrder<Member> {
	@Override
	public boolean contains(Member fst, Member snd) throws LookupException {
		Member<?,?,?> first = fst;
		Member<?,?,?> second = snd;
		boolean result = false;
		if((first instanceof NormalMethod) && (second instanceof NormalMethod)) {
			result = first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class)) &&
			         (first.is(first.language(ObjectOrientedLanguage.class).CLASS) == Ternary.TRUE) && 
			          first.signature().sameAs(second.signature());
		} else if(first instanceof RegularMemberVariable && second instanceof RegularMemberVariable) {
			 result = first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class)) &&
			          first.signature().sameAs(second.signature());
		} else if(first instanceof Type && second instanceof Type) {
			result = first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class)) &&
      first.signature().sameAs(second.signature());
		}
		return result;
	}

	@Override
	public boolean equal(Member first, Member second) throws LookupException {
		return first.equals(second);
	}
}