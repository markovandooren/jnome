/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.language;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.relation.StrictPartialOrder;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.variable.RegularMemberVariable;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

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
			          first.sameSignatureAs(second);
		} else if(first instanceof RegularMemberVariable && second instanceof RegularMemberVariable) {
			 result = first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class)) &&
			          first.sameSignatureAs(second);
		} else if(first instanceof Type && second instanceof Type) {
			result = first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class)) &&
      first.sameSignatureAs(second);
		}
		return result;
	}

	@Override
	public boolean equal(Member first, Member second) throws LookupException {
		return first.equals(second);
	}
}
