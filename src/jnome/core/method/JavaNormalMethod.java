package jnome.core.method;

import jnome.core.language.Java;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Signature;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.SelectorWithoutOrder;
import chameleon.core.member.OverridesRelation;
import chameleon.core.method.Method;
import chameleon.core.method.MethodHeader;
import chameleon.core.method.MethodSignature;
import chameleon.core.method.RegularMethod;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaNormalMethod<E extends RegularMethod<E,H,S,NormalMethod>, H extends MethodHeader<H, E, S>, S extends MethodSignature> extends NormalMethod<E,H,S> {

	public JavaNormalMethod(H header, TypeReference returnType) {
		super(header,returnType);
	}
	
	protected E cloneThis() {
    return (E) new JavaNormalMethod(header().clone(), (TypeReference)returnTypeReference().clone());
  }

	public OverridesRelation<Method> overridesSelector() {
		return _overridesSelector;
	}

	private static OverridesRelation<Method> _overridesSelector = new OverridesRelation<Method>(Method.class) {
		
		@Override
		public boolean containsBasedOnRest(Method first, Method second) throws LookupException {
			boolean result = isOverridable(second); 
		if(result) {
			result =  first.sameKind(second);// && first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class));
			if(result) {
				MethodSignature signature1 = first.signature();
				MethodSignature<?,?> signature2 = second.signature();
				result = signature1.sameParameterBoundsAs(signature2);
				if(!result) {
					MethodSignature erasure2 = signature2.language(Java.class).erasure((SimpleNameMethodSignature) signature2);
					result = signature1.sameParameterBoundsAs(erasure2);
				}
			}
		}
		return result;
		}

		@Override
		public boolean containsBasedOnName(Method first, Method second) throws LookupException {
			return first.signature().name().equals(second.signature().name());
		}
	}; 
}
