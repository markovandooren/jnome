package be.kuleuven.cs.distrinet.jnome.core.method;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.member.DeclarationWithParametersSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.member.MemberRelationSelector;
import be.kuleuven.cs.distrinet.chameleon.oo.member.OverridesRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.member.SimpleNameDeclarationWithParametersSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.method.RegularMethod;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.util.CreationStackTrace;

public class JavaNormalMethod extends NormalMethod {
		
	public JavaNormalMethod(MethodHeader header) {
		super(header);
	}
	
	protected JavaNormalMethod cloneSelf() {
    return new JavaNormalMethod(null);
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
