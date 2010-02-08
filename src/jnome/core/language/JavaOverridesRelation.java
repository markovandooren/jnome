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

public class JavaOverridesRelation extends StrictPartialOrder<Member> {
		@Override
		public boolean contains(Member first, Member second) throws LookupException {
		  boolean result;
		  if((first instanceof Method) && (second instanceof Method)) {
		    assert first != null;
		    assert second != null;
		    Method<?,?,?,?> method1 = (Method<?,?,?,?>) first;
		    Method<?,?,?,?> method2 = (Method<?,?,?,?>) second;
		    Ternary temp = method2.is(method2.language(Java.class).OVERRIDABLE);
		    boolean overridable;
		    if(temp == Ternary.TRUE) {
		      overridable = true;
		    } else if (temp == Ternary.FALSE) {
		      overridable = false;
		    } else {
		      throw new LookupException("The overridability of the other method could not be determined.");
		    }
		    result = overridable && 
		             method1.signature().sameAs(method2.signature()) && 
		             method1.nearestAncestor(Type.class).subTypeOf(method2.nearestAncestor(Type.class)) && 
		             method1.sameKind(method2);
		  } 
		  else {
		    result = false;
		  }
		  return result; 
		}

		@Override
		public boolean equal(Member first, Member second) {
		  return first.equals(second);
		}
	}