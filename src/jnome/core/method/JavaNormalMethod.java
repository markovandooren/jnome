package jnome.core.method;

import jnome.core.language.Java;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.DeclarationWithParametersSignature;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersSignature;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.member.MemberRelationSelector;
import chameleon.core.member.OverridesRelation;
import chameleon.core.method.Method;
import chameleon.core.method.RegularMethod;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.method.NormalMethod;

public class JavaNormalMethod<E extends RegularMethod<E,H,S,NormalMethod>, H extends DeclarationWithParametersHeader<H, S>, S extends DeclarationWithParametersSignature> extends NormalMethod<E,H,S> {

	public JavaNormalMethod(H header, TypeReference returnType) {
		super(header,returnType);
	}
	
	protected E cloneThis() {
    return (E) new JavaNormalMethod(header().clone(), (TypeReference)returnTypeReference().clone());
  }

	public MemberRelationSelector<Method> overridesSelector() {
		return new MemberRelationSelector<Method>(Method.class,this,_overridesSelector);
	}

  public OverridesRelation<? extends Member> overridesRelation() {
  	return _overridesSelector;
  }
  
	private static OverridesRelation<Method> _overridesSelector = new OverridesRelation<Method>(Method.class) {
		
		@Override
		public boolean containsBasedOnRest(Method first, Method second) throws LookupException {
			boolean result = isOverridable(second); 
		if(result) {
			result =  first.sameKind(second) ;// && first.nearestAncestor(Type.class).subTypeOf(second.nearestAncestor(Type.class));
			if(result) {
				DeclarationWithParametersSignature signature1 = first.signature();
				DeclarationWithParametersSignature<?> signature2 = second.signature();
				result = signature1.sameParameterBoundsAs(signature2);
				if(!result) {
					DeclarationWithParametersSignature erasure2 = signature2.language(Java.class).erasure((SimpleNameDeclarationWithParametersSignature) signature2);
					result = signature1.sameParameterBoundsAs(erasure2);
				}
			}
		}
		return result;
		}

		@Override
		public boolean containsBasedOnName(Signature first, Signature second) throws LookupException {
			return first.name().equals(second.name());
		}
	};

//	private CreationStackTrace _trace;
//
//	private boolean _connected = false;
//	private boolean _disconnected = false;
//	
//	@Override
//  protected SingleAssociation<E,Element> createParentLink() {
//  	return new SingleAssociation<E,Element>((E) this) {
//
//  		@Override
//  		protected void register(Association<? extends Element, ? super E> other) {
//  			_connected = true;
//  			super.register(other);
//  			if(other == null) {
//  				_trace = new CreationStackTrace();
//  			}
//  		}
//    	
//  		@Override 
//  	  protected void unregister(Association<? extends Element,? super E> other) {
//  			_disconnected = true;
//  			if(other == getOtherRelation()) {
//  				_trace = new CreationStackTrace();
//  			}
//  			super.unregister(other);
//  		}
//
//    };  }

}
