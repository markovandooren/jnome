/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.language;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.relation.StrictPartialOrder;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.RegularMemberVariable;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

public class JavaHidesRelation extends StrictPartialOrder<Member> {
	@Override
	public boolean contains(Member fst, Member snd) throws LookupException {
		Member first = fst;
		Member second = snd;
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
