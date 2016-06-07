package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.DeclarationCollector;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.member.simplename.method.RegularMethodInvocation;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

public class JavaMethodInvocation extends RegularMethodInvocation {

	public JavaMethodInvocation(String name, CrossReferenceTarget target) {
		super(name, target);
	}

	@Override
	protected JavaMethodInvocation cloneSelf() {
		return new JavaMethodInvocation(name(), null);
	}

	@Override
	protected DeclarationSelector<NormalMethod> createSelector() {
		return new JavaMethodSelector<NormalMethod>(this,NormalMethod.class);
	}

	@Override
	public NormalMethod getElement() throws LookupException {
		NormalMethod result = (NormalMethod) getCache();
		if(result != null) {
			return result;
		}
		synchronized(this) {
			if(result != null) {
				return result;
			}

			DeclarationCollector collector = new DeclarationCollector(selector());
			CrossReferenceTarget target = getTarget();
			if(target == null) {
				lexicalContext().lookUp(collector);
			} else {
				target.targetContext().lookUp(collector);
			}
			result = (NormalMethod) collector.result();
			setCache((NormalMethod) result);
			return result;
		}
	}


	/**
	 * Determine the types of the formal parameter of the given method in the context of the enclosing method invocation.
	 * This involves substituting the formal type parameters in the type of the formal parameters
	 * with the actual type arguments.
	 * 
	 * If explicit type arguments are present, then these are used as the actual type arguments. Otherwise, the actual
	 * type arguments are inferred.
	 * @throws LookupException 
	 */
	public static List<Type> formalParameterTypesInContext(Method method,TypeAssignmentSet actualTypeParameters) throws LookupException {
		List<Type> result;
		if(method.nbTypeParameters() > 0 && actualTypeParameters!=null) {
			Java7 language = method.language(Java7.class);
			// Substitute
			List<FormalParameter> formalParameters = method.formalParameters();
			List<TypeReference> references = new ArrayList<TypeReference>();
			for(FormalParameter par: formalParameters) {
				TypeReference tref = par.getTypeReference();
				TypeReference clone = Util.clone(tref);
				ReferenceStub stub = new ReferenceStub(clone);
				stub.setUniParent(tref.parent());
				references.add(clone);
			}
			result = new ArrayList<Type>();
			for(TypeReference tref: references) {
				TypeReference subst = tref;
				for(TypeParameter par: actualTypeParameters.assigned()) {
					subst = NonLocalJavaTypeReference.replace(language.reference(actualTypeParameters.type(par)), par, (JavaTypeReference) subst);
				}
				result.add(subst.getElement());
			}

		} else {
			result = method.header().formalParameterTypes();
		}
		return result;
	}

	private static class ReferenceStub extends ElementImpl {

		public ReferenceStub(TypeReference tref) {
			setTypeReference(tref);
		}

		private Single<TypeReference> _tref = new Single<TypeReference>(this);

		public TypeReference typeReference() {
			return _tref.getOtherEnd();
		}

		public void setTypeReference(TypeReference tref) {
			set(_tref, tref);
		}

		@Override
		public ReferenceStub cloneSelf() {
			return new ReferenceStub(null);
		}

		@Override
		public Verification verifySelf() {
			return Valid.create();
		}
	}
}
