/**
 * 
 */
package jnome.core.language;

import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import chameleon.core.lookup.LookupException;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.member.DeclarationWithParametersSignature;
import chameleon.oo.member.Member;
import chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import chameleon.oo.method.Method;
import chameleon.oo.type.Type;

public class JavaImplementsRelation extends StrictPartialOrder<Member> {

	@Override
	public boolean contains(Member first, Member second) throws LookupException {
	  boolean result = false;
	  if((!first.sameAs(second)) && (first instanceof Method) && (second instanceof Method)) {
	    Method method1 = (Method) first;
	    Method method2 = (Method) second;
//	    result = checkDefined(method1);
//	    if(result) {
	    	result = !checkDefined(method2);
	    	result = result && first.name().equals(second.name());
	    	if(result) {
	    		DeclarationWithParametersSignature signature1 = method1.signature();
	    		DeclarationWithParametersSignature signature2 = method2.signature();
	    		result = signature1.sameParameterBoundsAs(signature2);
					if(!result) {
						DeclarationWithParametersSignature erasure2 = signature2.language(Java.class).erasure((SimpleNameDeclarationWithParametersSignature) signature2);
						result = signature1.sameParameterBoundsAs(erasure2);
					}
	    		result = result &&
	    		(! method2.nearestAncestor(Type.class).subTypeOf(method1.nearestAncestor(Type.class))) &&
	    		method1.sameKind(method2);
	    	} 
//	    } 
	  }
	  return result; 
	}

	public boolean checkDefined(Member member) throws LookupException {
		Ternary temp1 = member.is(member.language(ObjectOrientedLanguage.class).DEFINED);
		boolean defined1;
		if(temp1 == Ternary.TRUE) {
		  defined1 = true;
		} else if (temp1 == Ternary.FALSE) {
		  defined1 = false;
		} else {
			temp1 = member.is(member.language(ObjectOrientedLanguage.class).DEFINED);
		  throw new LookupException("The definedness of the first element could not be determined.");
		}
		return defined1;
	}

	@Override
	public boolean equal(Member first, Member second) {
		return first == second;
	}
	
}