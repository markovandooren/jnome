/**
 * 
 */
package jnome.core.language;

import jnome.core.type.JavaTypeReference;

import org.rejuse.logic.ternary.Ternary;

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
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.support.member.simplename.SimpleNameMethodSignature;

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
		    MethodSignature signature1 = method1.signature();
		    MethodSignature erasure2 = erasure((SimpleNameMethodSignature) method2.signature());
		    MethodSignature signature2 = method2.signature();
				result = overridable && 
		             (signature1.sameAs(signature2)  || signature1.sameAs(erasure2))&& // ) && // 
		             method1.nearestAncestor(Type.class).subTypeOf(method2.nearestAncestor(Type.class)) && 
		             method1.sameKind(method2);
		  } 
		  else {
		    result = false;
		  }
		  return result; 
		}
		
		public SimpleNameMethodSignature erasure(SimpleNameMethodSignature signature) {
			SimpleNameMethodSignature result = new SimpleNameMethodSignature(signature.name());
			result.setUniParent(signature.parent());
			for(TypeReference tref : signature.typeReferences()) {
				JavaTypeReference jref = (JavaTypeReference) tref;
				result.add(erasure(jref));
			}
			return result;
		}
		
		public TypeReference erasure(JavaTypeReference jref) {
			JavaTypeReference result = new JavaTypeReference(erasure(jref.getTarget()), jref.getName());
			result.setArrayDimension(jref.arrayDimension());
			return result;
		}
		
		public <T extends CrossReference<?,?,? extends TargetDeclaration>> CrossReference<?,?,? extends TargetDeclaration> erasure(T ref) {
			if(ref instanceof JavaTypeReference) {
				return erasure((JavaTypeReference)ref);
			} else if( ref != null){
				CrossReference<?,?,? extends TargetDeclaration> result = ref.clone();
				// replace target with erasure.
				if(ref instanceof NamedTarget) {
					NamedTarget namedTarget = (NamedTarget)result;
					InvocationTarget<?, ?> target = namedTarget.getTarget();
					if(target instanceof CrossReference) {
					  namedTarget.setTarget((InvocationTarget)erasure((T)target));
					}
				} else if(ref instanceof ElementReferenceWithTarget) {
					ElementReferenceWithTarget eref = (ElementReferenceWithTarget) result;
					eref.setTarget(erasure(eref.getTarget()));
				}
				return result;
			} else {
				return null;
			}
		}
		
		@Override
		public boolean equal(Member first, Member second) {
		  return first.equals(second);
		}
	}