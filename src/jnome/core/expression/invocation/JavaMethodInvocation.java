package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jnome.core.language.Java;
import jnome.core.type.ArrayType;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.RawType;
import jnome.core.variable.MultiFormalParameter;

import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.TypePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.expression.ActualArgument;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
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
  	return new JavaMethodSelector();
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

	
  public class JavaMethodSelector extends DeclarationSelector<NormalMethod> {

  	protected NormalMethod selection(Declaration declarator) throws LookupException {
  		throw new ChameleonProgrammerException();
  	}
  	
  	
  	
    @Override
		public List<? extends Declaration> declarators(List<? extends Declaration> selectionCandidates) throws LookupException {
			return selection(selectionCandidates);
		}



		public List<NormalMethod> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
    	List<NormalMethod> tmp = new ArrayList<NormalMethod>();
    	if(! selectionCandidates.isEmpty()) {
    		List<NormalMethod> candidates = new ArrayList<NormalMethod>();
    		int nbActuals = nbActualParameters();
    		for(Declaration decl: selectionCandidates) {
    			if(decl instanceof NormalMethod) {
    				NormalMethod method = (NormalMethod) decl;
    				int nbFormals = method.nbFormalParameters();
    				// If caching is enable, selected based on the name will already have been
    				// done by the container, so we check for names after the arguments.
    				if ((
    						(nbFormals == nbActuals) ||
    						((nbFormals > 1) 
    								&&
    								(method.lastFormalParameter() instanceof MultiFormalParameter)
    								&& 
    								(nbActuals >= nbFormals - 1)
    						)
    				) && correctSignature(method.signature())){
    					candidates.add(method);
    				}
    			}
    		}
    		for(NormalMethod decl: candidates) {
    			if(matchingApplicableBySubtyping(decl)) {
    				tmp.add(decl);
    			}
    		}
    		// conversion
    		if(tmp.isEmpty()) {
    			for(NormalMethod decl: candidates) {
    				if(matchingApplicableByConversion(decl)) {
    					tmp.add(decl);
    				}
    			}
    			// variable arity
    			if(tmp.isEmpty()) {
    				for(NormalMethod decl: candidates) {
    					if(variableApplicableBySubtyping(decl)) {
    						tmp.add(decl);
    					}
    				}
    			}
    		}
    		applyOrder(tmp);
    	}
    	return tmp;
    }
    
    public NormalMethod instance(NormalMethod method) throws LookupException {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
			return instantiatedMethodTemplate(method, actualTypeParameters);
    }


		private TypeAssignmentSet actualTypeParameters(NormalMethod<?, ?, ?> method, boolean includeNonreference) throws LookupException {
			List<ActualTypeArgument> typeArguments = typeArguments();
			Java language = method.language(Java.class);
			List<TypeParameter> parameters = method.typeParameters();
			TypeAssignmentSet formals;
//			List<TypeParameter> methodTypeParameters = method.typeParameters();
			if(parameters.size() > 0 && (parameters.get(0) instanceof FormalTypeParameter)) {
				formals = new TypeAssignmentSet(parameters);
				if(typeArguments.size() > 0) {
					int size = typeArguments.size();
					for(int i=0; i< size; i++) {
						formals.add(new ActualTypeAssignment(parameters.get(i),typeArguments.get(i).upperBound()));
					}
				} else {
					// perform type inference
					FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(JavaMethodInvocation.this,method);
					List<ActualArgument> actualParameters = getActualParameters();
					List<Type> formalParameters = method.header().formalParameterTypes();
					int size = actualParameters.size();
					for(int i=0; i< size; i++) {
						// if the formal parameter type is reference type, add a constraint
						Type argType = actualParameters.get(i).getExpression().getType();
						if(includeNonreference || argType.is(language.REFERENCE_TYPE) == Ternary.TRUE) {
							constraints.add(new SSConstraint(language.reference(argType), formalParameters.get(i)));
						}
					}
					formals = constraints.resolve();
				}
			} else {
				formals = new TypeAssignmentSet(Collections.EMPTY_LIST);
			}
			return formals;
		}

		private boolean matchingApplicableBySubtyping(NormalMethod method) throws LookupException {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
			List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
			boolean match = true;
			int size = formalParameterTypesInContext.size();
			List<ActualArgument> actualParameters = getActualParameters();
			for(int i=0; match && i < size; i++) {
				Type formalType = formalParameterTypesInContext.get(i);
				Type actualType = actualParameters.get(i).getExpression().getType();
				match = actualType.subTypeOf(formalType) || convertibleThroughUncheckedConversionAndSubtyping(actualType, formalType);
			}
			if(match) {
				match = actualTypeParameters.valid();
			} 
//			  &&  {
//				result = method;
//				if(actualTypeParameters.hasAssignments()) {
//					// create instance
//					result = instantiatedMethodTemplate(method, actualTypeParameters);
//				}
//			}
			return match;
		}

		private NormalMethod instantiatedMethodTemplate(NormalMethod method, TypeAssignmentSet actualTypeParameters) throws LookupException {
			NormalMethod result=method;
			int nbTypeParameters = actualTypeParameters.nbAssignments();
			if(nbTypeParameters > 0) {
				result = (NormalMethod) method.clone();
				result.setOrigin(method);
				result.setUniParent(method.parent());
				for(int i=1; i <= nbTypeParameters;i++) {
					TypeParameter originalPar = method.typeParameter(i);
					TypeParameter clonedPar = result.typeParameter(i);
					// we detach the signature from the clone.
					Type assignedType = actualTypeParameters.type(originalPar);
					Java language = language(Java.class);
					JavaTypeReference reference = language.reference(assignedType);
					Element parent = reference.parent();
					reference.setUniParent(null);
					BasicTypeArgument argument = language.createBasicTypeArgument(reference);
					argument.setUniParent(parent);
					TypeParameter newPar = new InstantiatedTypeParameter(clonedPar.signature(), argument);
					SingleAssociation parentLink = clonedPar.parentLink();
					parentLink.getOtherRelation().replace(parentLink, newPar.parentLink());
				}
			}
			return result;
		}
		
		private boolean convertibleThroughUncheckedConversionAndSubtyping(Type first, Type second) throws LookupException {
			boolean result = false;
			if(first instanceof RawType) {
				result = ((RawType)first).convertibleThroughUncheckedConversionAndSubtyping(second);
			} else if(first instanceof ArrayType && second instanceof ArrayType) {
				ArrayType first2 = (ArrayType)first;
				ArrayType second2 = (ArrayType)second;
				result = convertibleThroughUncheckedConversionAndSubtyping(first2.elementType(), second2.elementType());
			} else if(first instanceof RegularType) {
				Set<Type> supers = language(Java.class).subtypeRelation().getAllSuperTypes(first);
				for(Type type: supers) {
					if(type.baseType().sameAs(second.baseType())) {
						return true;
					}
				}
			}
			return result;
		}
		
		private boolean matchingApplicableByConversion(NormalMethod method) throws LookupException {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true);
			List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
			boolean match = true;
			int size = formalParameterTypesInContext.size();
			List<ActualArgument> actualParameters = getActualParameters();
			for(int i=0; match && i < size; i++) {
				Type formalType = formalParameterTypesInContext.get(i);
				Type actualType = actualParameters.get(i).getExpression().getType();
				match = convertibleThroughMethodInvocationConversion(actualType, formalType);
			}
			if(match) {
				match = actualTypeParameters.valid();
			} 
//			  &&  {
//				result = method;
//				if(actualTypeParameters.hasAssignments()) {
//					// create instance
//					result = instantiatedMethodTemplate(method, actualTypeParameters);
//				}
//			}
			return match;
		}

		private boolean convertibleThroughMethodInvocationConversion(Type first, Type second) throws LookupException {
			boolean result = false;
			Java language = first.language(Java.class);
			// A) Identity conversion 
			if(first.sameAs(second)) {
				result = true;
			}
			// B) Widening conversion
			else if(convertibleThroughWideningPrimitiveConversion(first, second)) {
				// the result cannot be a raw type so no unchecked conversion is required.
				result = true;
			}
			// C) unboxing and optional widening conversion.
			else if(convertibleThroughUnboxingAndOptionalWidening(first,second)) {
				result = true;
			}
			// D) boxing and widening reference conversion.
			else if(convertibleThroughBoxingAndOptionalWidening(first,second)){
				// can't be raw, so no unchecked conversion can apply
				result = true;
			} else {
				// E) reference widening
				Collection<Type> candidates = referenceWideningConversionCandidates(first);
				candidates.add(first);
				if(candidates.contains(second)) {
					result = true;
				} else {
					// F) unchecked conversion after reference widening 
					for(Type type: candidates) {
						if(convertibleThroughUncheckedConversionAndSubtyping(first, second)) {
							result = true;
							break;
						}
					}
					if(! result) {
						//G) unchecked conversion after identity
						result = convertibleThroughUncheckedConversionAndSubtyping(first, second);
					}
				}
			}
			return result;
		}

		public boolean convertibleThroughBoxingAndOptionalWidening(Type first, Type second) throws LookupException {
			boolean result = false;
			Java language = first.language(Java.class);
			if(first.is(language.NUMERIC_TYPE) == Ternary.TRUE) {
				Type tmp = language.box(first);
				if(tmp.sameAs(second)) {
					result = true;
				} else {
					result = convertibleThroughWideningReferenceConversion(tmp, second);
				}
			}
			return result;
		}

		public boolean convertibleThroughUnboxingAndOptionalWidening(Type first, Type second) throws LookupException {
			boolean result = false;
			Java language = first.language(Java.class);
			if(first.is(language.UNBOXABLE_TYPE) == Ternary.TRUE) {
				Type tmp = language.unbox(first);
				if(tmp.sameAs(second)) {
					result = true;
				} else {
					result = convertibleThroughWideningPrimitiveConversion(tmp, second);
				}
			}
			return result;
		}
		
		public boolean convertibleThroughWideningPrimitiveConversion(Type first, Type second) throws LookupException {
			return primitiveWideningConversionCandidates(first).contains(second);
		}
		
		public Collection<Type> primitiveWideningConversionCandidates(Type type) throws LookupException {
			Collection<Type> result = new ArrayList<Type>();
			Java language = type.language(Java.class);
			String name = type.getFullyQualifiedName();
			if(type.is(language.NUMERIC_TYPE) == Ternary.TRUE) {
				if(! name.equals("double")) {
					result.add(language.findType("double"));
					if(! name.equals("float")) {
						result.add(language.findType("float"));
						if(! name.equals("long")) {
							result.add(language.findType("long"));
							if(! name.equals("int")) {
								result.add(language.findType("int"));
								// char and short do not convert to short via widening.
								if(name.equals("byte")) {
									result.add(language.findType("short"));
								}
							}
						}
					}
				}
			}
			return result;
		}

		public boolean convertibleThroughWideningReferenceConversion(Type first, Type second) throws LookupException {
			return referenceWideningConversionCandidates(first).contains(second);
		}

		public Collection<Type> referenceWideningConversionCandidates(Type type) throws LookupException {
			Set<Type> allSuperTypes = type.getAllSuperTypes();
			allSuperTypes.add(type);
			return allSuperTypes;
		}
		

		public boolean variableApplicableBySubtyping(NormalMethod method) throws LookupException {
			boolean match = method.lastFormalParameter() instanceof MultiFormalParameter;
			if(match) {
				TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true);
				List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
				int size = formalParameterTypesInContext.size();
				List<ActualArgument> actualParameters = getActualParameters();
				int actualSize = actualParameters.size();
				// For the non-varags arguments, use method invocation conversion
				for(int i=0; match && i < size-1; i++) {
					Type formalType = formalParameterTypesInContext.get(i);
					Type actualType = actualParameters.get(i).getExpression().getType();
					match = convertibleThroughMethodInvocationConversion(actualType, formalType);
				}
				Type formalType = ((ArrayType)formalParameterTypesInContext.get(size-1)).elementType();
				for(int i = size-1; match && i< actualSize;i++) {
					Type actualType = actualParameters.get(i).getExpression().getType();
					match = convertibleThroughMethodInvocationConversion(actualType, formalType);
				}
				if(match) {
					match = actualTypeParameters.valid();
				} 
//				  &&  {
//					result = method;
//					if(actualTypeParameters.hasAssignments()) {
//						// create instance
//						result = instantiatedMethodTemplate(method, actualTypeParameters);
//					}
//				}
			}
			return match;
		}
    
    private boolean correctSignature(Signature signature) throws LookupException {
  		boolean result = false;
  		if(signature instanceof SimpleNameMethodSignature) {
  			SimpleNameMethodSignature sig = (SimpleNameMethodSignature)signature;
  			result = sig.name().equals(name());
  		}
  		return result;
    }

    @Override
    public WeakPartialOrder<NormalMethod> order() {
    	return new JavaMostSpecificMethodOrder(JavaMethodInvocation.this);
    }
		@Override
		public Class<NormalMethod> selectedClass() {
			return NormalMethod.class;
		}

		@Override
		public String selectionName(DeclarationContainer<?,?> container) {
			return name();
		}
  }
  
  public static class JavaMostSpecificMethodOrder extends WeakPartialOrder<NormalMethod> {
  	
  	Invocation _invocation;
  	
  	public JavaMostSpecificMethodOrder(Invocation invocation) {
  		_invocation = invocation;
  	}

		@Override
		public boolean contains(NormalMethod first, NormalMethod second) throws LookupException {
			boolean result = false;
			if(! first.sameAs(second)) {
				if(!(first.lastFormalParameter() instanceof MultiFormalParameter) && ! (second.lastFormalParameter() instanceof MultiFormalParameter)) {
					result = containsFixedArity(first, second);
				} else if((first.lastFormalParameter() instanceof MultiFormalParameter) && (second.lastFormalParameter() instanceof MultiFormalParameter)){
					result = containsVariableArity(first, second);
				}
			}
			return result;
		}

		public boolean containsVariableArity(NormalMethod first, NormalMethod second) throws LookupException {
			boolean result = true;
			Java language = (Java) first.language(Java.class);
			List<Type> firstTypes = first.header().formalParameterTypes();
			List<Type> secondTypes = second.header().formalParameterTypes();
			int firstSize = firstTypes.size();
			firstTypes.set(firstSize-1, ((ArrayType)firstTypes.get(firstSize-1)).elementType());
			int secondSize = secondTypes.size();
			secondTypes.set(secondSize-1, ((ArrayType)secondTypes.get(secondSize-1)).elementType());
			int n;
			int k;
			if(firstSize >= secondSize) {
				n = firstSize;
				k = secondSize;
			} else {
				n = secondSize;
				k = firstSize;
			}
			List typeParameters = second.typeParameters();
			List<Type> Ss;
			if(typeParameters.size() > 0) {
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(_invocation,second);
				for(int i=0; i < k-1; i++) {
					constraints.add(new SSConstraint(language.reference(firstTypes.get(i)), secondTypes.get(i)));
				}
				TypeAssignmentSet As = constraints.resolve();
				result = As.valid();
				if(result) {
				  Ss = formalParameterTypesInContext(second, As);
				} else {
					// If the actual type parameters are invalid, don't bother
					// substituting them
					Ss = null;
				}
			} else {
				Ss = new ArrayList<Type>(secondTypes);
			}
			for(int i=0; result && i<k-1;i++) {
				result = firstTypes.get(i).subTypeOf(Ss.get(i));
			}
			if(result && firstSize >= secondSize) {
				for(int i=k-1; result && i<n;i++) {
					result = firstTypes.get(i).subTypeOf(Ss.get(k-1));
				}
			} else {
				for(int i=k-1; result && i<n;i++) {
					result = firstTypes.get(k-1).subTypeOf(Ss.get(i));
				}
			}
			return result;
		}
		
		public boolean containsFixedArity(NormalMethod first, NormalMethod second) throws LookupException {
			boolean result = true;
			Java language = (Java) first.language(Java.class);
			List<Type> Ts = first.header().formalParameterTypes();
			List<Type> Us = second.header().formalParameterTypes();
			int size =Ts.size();
			List typeParameters = second.typeParameters();
			new TypePredicate<TypeParameter, FormalTypeParameter>(FormalTypeParameter.class).filter(typeParameters);
			List<Type> Ss;
			if(typeParameters.size() > 0) {
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(_invocation, second);
				for(int i=0; i < size; i++) {
					constraints.add(new SSConstraint(language.reference(Ts.get(i)), Us.get(i)));
				}
				TypeAssignmentSet As = constraints.resolve();
				result = As.valid();
				if(result) {
				  Ss = formalParameterTypesInContext(second, As);
				} else {
					// If the actual type parameters are invalid, don't bother
					// substituting them
					Ss = null;
				}
			} else {
				Ss = new ArrayList<Type>(Us);
			}
			for(int i=0; result && i<size;i++) {
				result = Ts.get(i).subTypeOf(Ss.get(i));
			}
			
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