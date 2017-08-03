package org.aikodi.java.core.method;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.member.DeclarationComparator;
import org.aikodi.chameleon.oo.member.MemberRelationSelector;
import org.aikodi.chameleon.oo.member.OverridesRelation;
import org.aikodi.chameleon.oo.member.SignatureWithParameters;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.type.JavaType;

public class JavaMethod extends NormalMethod {
		
	public JavaMethod(MethodHeader header) {
		super(header);
	}
	
	protected JavaMethod cloneSelf() {
    return new JavaMethod(null);
  }

	@Override
	public Type returnType() throws LookupException {
		return ((JavaType)super.returnType()).captureConversion();
	}
	
	public MemberRelationSelector<Method> overridesSelector() {
		return new MemberRelationSelector<Method>(Method.class,this,_overridesSelector);
	}

	
	
	private static OverridesRelation<Method> _overridesSelector = new OverridesRelation<Method>(Method.class) {
		
		@Override
		public boolean containsBasedOnRest(Method first, Method second) throws LookupException {
			return first.name().equals(second.name()) && (isOverridable(second) && subSignature(first, second)) || (isOverridable(first) && subSignature(second, first));
		}
	};
	

	public MemberRelationSelector<? extends Declaration> aliasSelector() {
		return new MemberRelationSelector<Method>(Method.class,this,_aliasRelation);
	};

	private static DeclarationComparator<Method> _aliasRelation = new DeclarationComparator<Method>(Method.class) {
		
		@Override
		public boolean containsBasedOnRest(Method first, Method second) throws LookupException {
			return first.name().equals(second.name()) && subSignature(first, second) || subSignature(second, first);
		}


	};

	/** 
	 * FIXME This should be used in a validation rule. The check that is done during
	 * lookup is {@link Declaration#compatibleSignature(Declaration)}. In correct Java code,
	 * the sameAs check for types will give the correct result because any infinite types
	 * must have the same structure in both methods anyway. 
	 * If the code is not correct, however, that may not be the case. The lookup will then 
	 * (correctly) give two results and fail, but the actual error
	 * is not in the invocation but in the definition. For now, that is not detected.
	 */
	private static boolean subSignature(Method first, Method second) throws LookupException {
		boolean result = first.sameKind(second) && ((Type)first.lexical().nearestAncestor(Type.class))
				.subtypeOf((Type)second.lexical().nearestAncestor(Type.class));
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
