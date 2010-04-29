/**
 * 
 */
package jnome.core.language;

import jnome.core.type.JavaTypeReference;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.NamedTarget;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.method.MethodSignature;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.ElementReferenceWithTarget;
import chameleon.core.relation.StrictPartialOrder;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.SimpleNameMethodSignature;

public class JavaOverridesRelation extends StrictPartialOrder<Member> {
		@Override
		public boolean contains(Member first, Member second) throws LookupException {
		  boolean result = false;
		  if((first instanceof Method) && (second instanceof Method)) {
		    assert first != null;
		    assert second != null;
		    Method<?,?,?,?> method1 = (Method<?,?,?,?>) first;
		    Method<?,?,?,?> method2 = (Method<?,?,?,?>) second;
		    Ternary temp = method2.is(method2.language(Java.class).OVERRIDABLE);
		    if(temp == Ternary.TRUE) {
		      result = true;
		    } else if (temp == Ternary.FALSE) {
		      result = false;
		    } else {
		      throw new LookupException("The overridability of the other method could not be determined.");
		    }
		    if(result) {
		    	result =  method1.sameKind(method2) && method1.nearestAncestor(Type.class).subTypeOf(method2.nearestAncestor(Type.class));
		    	if(result) {
		    	MethodSignature signature1 = method1.signature();
		    	MethodSignature<?,?> signature2 = method2.signature();
		    	result = signature1.sameParameterBoundsAs(signature2);
		    	if(result) {
		    	MethodSignature erasure2 = signature2.language(Java.class).erasure((SimpleNameMethodSignature) signature2);
		    	 result = signature1.sameParameterBoundsAs(erasure2);
		    	}
		    	}
		    }
		  } 
		  return result; 
		}
		
		
		
		@Override
		public boolean equal(Member first, Member second) {
		  return first.equals(second);
		}
	}