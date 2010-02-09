/**
 * 
 */
package jnome.core.language;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.core.type.Type;
import chameleon.oo.language.ObjectOrientedLanguage;

public class JavaImplementsRelation extends StrictPartialOrder<Member> {

	@Override
	public boolean contains(Member first, Member second) throws LookupException {
	  boolean result;
	  
	  if((first != second) && (first instanceof Method) && (second instanceof Method)) {
	    Method<?,?,?,?> method1 = (Method<?,?,?,?>) first;
	    Method<?,?,?,?> method2 = (Method<?,?,?,?>) second;
	    boolean defined1 = checkDefined(method1);
	    if(defined1) {
	    boolean defined2 = checkDefined(method2);
	    result = (!defined2) && 
	             method1.signature().sameAs(method2.signature()) &&
	             (! method2.nearestAncestor(Type.class).subTypeOf(method1.nearestAncestor(Type.class))) &&
	             method1.sameKind(method2);
	    } else {
	    	result = false;
	    }
	  }
	  else {
	    result = false;
	  }
	  return result; 
	}

	public boolean checkDefined(Member<?,?,?,?> member) throws LookupException {
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