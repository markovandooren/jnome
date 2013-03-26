package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.ElementImpl;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationCollector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.RegularMethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public class JavaMethodInvocation extends RegularMethodInvocation {

	public JavaMethodInvocation(String name, CrossReferenceTarget target) {
		super(name, target);
//  	parentLink().addListener(new AssociationListener<Element>() {
//      
//  		public void notifyElementAdded(Element element) {
//  			_trace = new CreationStackTrace();
//  		}
//  
//  		public void notifyElementRemoved(Element element) {
//  			_trace = new CreationStackTrace();
//  		}
//  
//  		public void notifyElementReplaced(Element oldElement, Element newElement) {
//  			_trace = new CreationStackTrace();
//  		}
//  		
//  	});

	}

	@Override
  protected JavaMethodInvocation cloneInvocation(CrossReferenceTarget target) {
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
	   
		DeclarationCollector<X> collector = new DeclarationCollector<X>(selector);
  	CrossReferenceTarget target = getTarget();
  	if(target == null) {
      lexicalLookupStrategy().lookUp(collector);
  	} else {
  		target.targetContext().lookUp(collector);
  	}
  	result = collector.result();
//		if (result != null) {
//			if(cache) {
				result = (X) ((JavaMethodSelector)selector).instance((NormalMethod) result);
//			}
	  	//OPTIMISATION
	  	if(cache) {
	  		setCache((NormalMethod) result);
	  	}
	    return result;
//		}
//		else {
//			//repeat lookup for debugging purposes.
//			//Config.setCaching(false);
//	  	if(target == null) {
//	      result = lexicalLookupStrategy().lookUp(selector);
//	  	} else {
//	  		result = target.targetContext().lookUp(selector);
//	  	}
//			throw new LookupException("Method returned by invocation of "+ name()+" is null", this);
//		}
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
	public static List<Type> formalParameterTypesInContext(NormalMethod method,TypeAssignmentSet actualTypeParameters) throws LookupException {
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
					subst = NonLocalJavaTypeReference.replace(language.reference(actualTypeParameters.type(par)), par, (JavaTypeReference) tref);
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
		public ReferenceStub clone() {
			return new ReferenceStub(typeReference().clone());
		}

		@Override
		public VerificationResult verifySelf() {
			return Valid.create();
		}
	}
}
