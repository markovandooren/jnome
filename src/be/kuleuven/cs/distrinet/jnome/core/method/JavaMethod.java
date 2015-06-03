package be.kuleuven.cs.distrinet.jnome.core.method;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.member.DeclarationComparator;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.member.MemberRelationSelector;
import org.aikodi.chameleon.oo.member.OverridesRelation;
import org.aikodi.chameleon.oo.member.SignatureWithParameters;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class JavaMethod extends NormalMethod {
		
	public JavaMethod(MethodHeader header) {
		super(header);
	}
	
	protected JavaMethod cloneSelf() {
    return new JavaMethod(null);
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
			return first.name().equals(second.name()) && (isOverridable(second) && subSignature(first, second)) || (isOverridable(first) && subSignature(second, first));
		}
	};
	

	public MemberRelationSelector<? extends Member> aliasSelector() {
		return new MemberRelationSelector<Method>(Method.class,this,_aliasRelation);
	};

	private static DeclarationComparator<Method> _aliasRelation = new DeclarationComparator<Method>(Method.class) {
		
		@Override
		public boolean containsBasedOnRest(Method first, Method second) throws LookupException {
			return first.name().equals(second.name()) && subSignature(first, second) || subSignature(second, first);
		}


	};

	private static boolean subSignature(Method first, Method second) throws LookupException {
		boolean result = first.sameKind(second) && ((Type)first.nearestAncestor(Type.class)).subtypeOf((Type)second.nearestAncestor(Type.class));
		if(result) {
			SignatureWithParameters signature1 = first.signature();
			SignatureWithParameters signature2 = second.signature();
			result = signature1.sameParameterBoundsAs(signature2);
			if(!result) {
			   SignatureWithParameters erasure2 = signature2.language(Java7.class).erasure((SignatureWithParameters) signature2);
				result = signature1.sameParameterBoundsAs(erasure2);
			}
		}
		return result;
	}
	

}
