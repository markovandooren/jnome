package jnome.core.method;

import jnome.core.language.Java;

import org.rejuse.association.Association;
import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.SelectorWithoutOrder;
import chameleon.core.member.OverridesRelation;
import chameleon.core.member.OverridesRelationSelector;
import chameleon.core.method.Method;
import chameleon.core.method.MethodHeader;
import chameleon.core.method.MethodSignature;
import chameleon.core.method.RegularMethod;
import chameleon.core.variable.MemberVariable;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.util.CreationStackTrace;

public class JavaNormalMethod<E extends RegularMethod<E,H,S,NormalMethod>, H extends MethodHeader<H, E, S>, S extends MethodSignature> extends NormalMethod<E,H,S> {

	public JavaNormalMethod(H header, TypeReference returnType) {
		super(header,returnType);
	}
	
	protected E cloneThis() {
    return (E) new JavaNormalMethod(header().clone(), (TypeReference)returnTypeReference().clone());
  }

	public OverridesRelationSelector<Method> overridesSelector() {
		return new OverridesRelationSelector<Method>(Method.class,this,_overridesSelector);
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
		public boolean containsBasedOnName(Signature first, Signature second) throws LookupException {
			return first.name().equals(second.name());
		}
	};

	private CreationStackTrace _trace;

	private boolean _connected = false;
	private boolean _disconnected = false;
	
	@Override
  protected SingleAssociation<E,Element> createParentLink() {
  	return new SingleAssociation<E,Element>((E) this) {

  		@Override
  		protected void register(Association<? extends Element, ? super E> other) {
  			_connected = true;
  			super.register(other);
  			if(other == null) {
  				_trace = new CreationStackTrace();
  			}
  		}
    	
  		@Override 
  	  protected void unregister(Association<? extends Element,? super E> other) {
  			_disconnected = true;
  			if(other == getOtherRelation()) {
  				_trace = new CreationStackTrace();
  			}
  			super.unregister(other);
  		}

    };  }

}
