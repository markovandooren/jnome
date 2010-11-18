package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.util.Util;

public class JavaMethodInvocation extends RegularMethodInvocation<JavaMethodInvocation> {

	public JavaMethodInvocation(String name, InvocationTarget target) {
		super(name, target);
	}

	@Override
  protected JavaMethodInvocation cloneInvocation(InvocationTarget target) {
  	// target is already cloned.
		return new JavaMethodInvocation(name(), target);
  }

  @Override
  protected DeclarationSelector<NormalMethod> createSelector() {
  	return new JavaMethodSelector(this);
  }

  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
  	X result = null;
  	
  	//OPTIMISATION
  	boolean cache = selector.equals(selector());
  	if(cache) {
  		result = (X) getCache();
  	}
	  if(result != null) {
	   	return result;
	  }
	   
  	InvocationTarget target = getTarget();
  	if(target == null) {
      result = lexicalLookupStrategy().lookUp(selector);
  	} else {
  		result = target.targetContext().lookUp(selector);
  	}
		if (result != null) {
			if(cache) {
				result = (X) ((JavaMethodSelector)selector).instance((NormalMethod) result);
			}
	  	//OPTIMISATION
	  	if(cache) {
	  		setCache((NormalMethod) result);
	  	}
	    return result;
		}
		else {
			//repeat lookup for debugging purposes.
			//Config.setCaching(false);
	  	if(target == null) {
	      result = lexicalLookupStrategy().lookUp(selector);
	  	} else {
	  		result = target.targetContext().lookUp(selector);
	  	}
			throw new LookupException("Method returned by invocation of "+ name()+" is null", this);
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
	public static List<Type> formalParameterTypesInContext(NormalMethod<?,?,?> method,TypeAssignmentSet actualTypeParameters) throws LookupException {
		List<TypeParameter> parameters = method.typeParameters();
		List<Type> result;
		if(parameters.size() > 0) {
			Java language = method.language(Java.class);
			// Substitute
			List<FormalParameter> formalParameters = method.formalParameters();
			List<TypeReference> references = new ArrayList<TypeReference>();
			for(FormalParameter par: formalParameters) {
				TypeReference tref = par.getTypeReference();
				TypeReference clone = tref.clone();
				ReferenceStub stub = new ReferenceStub(clone);
				stub.setUniParent(tref.parent());
				references.add(clone);
			}
			result = new ArrayList<Type>();
			for(TypeReference tref: references) {
				TypeReference subst = tref;
				for(TypeParameter par: actualTypeParameters.assigned()) {
					subst = NonLocalJavaTypeReference.replace(language.reference(actualTypeParameters.type(par)), par, (JavaTypeReference<?>) tref);
				}
				result.add(subst.getElement());
			}
			
		} else {
			result = method.header().formalParameterTypes();
		}
		return result;
	}
	
	private static class ReferenceStub extends NamespaceElementImpl<ReferenceStub, Element> {

		public ReferenceStub(TypeReference tref) {
			setTypeReference(tref);
		}
		
		private SingleAssociation<ReferenceStub, TypeReference> _tref = new SingleAssociation<ReferenceStub, TypeReference>(this);
		
		public TypeReference typeReference() {
			return _tref.getOtherEnd();
		}
		
		public void setTypeReference(TypeReference tref) {
			setAsParent(_tref, tref);
		}
		
		@Override
		public ReferenceStub clone() {
			return new ReferenceStub(typeReference().clone());
		}

		@Override
		public VerificationResult verifySelf() {
			return Valid.create();
		}

		public List<? extends Element> children() {
			return Util.createNonNullList(typeReference());
		}
		
	}

}