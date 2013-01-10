package jnome.core.method;

import jnome.core.language.Java;
import chameleon.core.declaration.Signature;
import chameleon.core.lookup.LookupException;
import chameleon.oo.member.DeclarationWithParametersSignature;
import chameleon.oo.member.Member;
import chameleon.oo.member.MemberRelationSelector;
import chameleon.oo.member.OverridesRelation;
import chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import chameleon.oo.method.Method;
import chameleon.oo.method.MethodHeader;
import chameleon.oo.method.RegularMethod;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.util.CreationStackTrace;

public class JavaNormalMethod extends NormalMethod {
		
	public JavaNormalMethod(MethodHeader header) {
		super(header);
	}
	
	protected JavaNormalMethod cloneThis() {
    return new JavaNormalMethod((MethodHeader) header().clone());
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
			result =  first.sameKind(second) && ((Type)first.nearestAncestor(Type.class)).subTypeOf((Type)second.nearestAncestor(Type.class));
			if(result) {
				DeclarationWithParametersSignature signature1 = first.signature();
				DeclarationWithParametersSignature signature2 = second.signature();
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

}
