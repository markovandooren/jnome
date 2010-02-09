/**
 * 
 */
package jnome.core.language;

import org.rejuse.logic.ternary.Ternary;

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
	    assert first != null;
	    assert second != null;
	    Method<?,?,?,?> method1 = (Method<?,?,?,?>) first;
	    Method<?,?,?,?> method2 = (Method<?,?,?,?>) second;
	    ObjectOrientedLanguage lang = method1.language(ObjectOrientedLanguage.class);
	    Ternary temp1 = method1.is(lang.DEFINED);
	    boolean defined1;
	    if(temp1 == Ternary.TRUE) {
	      defined1 = true;
	    } else if (temp1 == Ternary.FALSE) {
	      defined1 = false;
	    } else {
	    	temp1 = method1.is(lang.DEFINED);
	      throw new LookupException("The definedness of the first method could not be determined.");
	    }
	    if(defined1) {
	    Ternary temp2 = method2.is(lang.DEFINED);
	    boolean defined2;
	    if(temp2 == Ternary.TRUE) {
	      defined2 = true;
	    } else if (temp2 == Ternary.FALSE) {
	      defined2 = false;
	    } else {
	      throw new LookupException("The definedness of the second method could not be determined.");
	    }
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

	@Override
	public boolean equal(Member first, Member second) {
		return first == second;
	}
	
}