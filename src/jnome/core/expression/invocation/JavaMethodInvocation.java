package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.type.ArrayType;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.RawType;
import jnome.core.variable.MultiFormalParameter;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.Signature;
import chameleon.core.expression.ActualArgument;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.support.member.simplename.SimpleNameMethodSignature;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;

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

	
  public class JavaMethodSelector extends DeclarationSelector<NormalMethod> {

  	protected NormalMethod selection(Declaration declarator) throws LookupException {
  		throw new ChameleonProgrammerException();
  	}
  	
    public List<NormalMethod> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
    	List<NormalMethod> tmp = new ArrayList<NormalMethod>();
    	List<NormalMethod> candidates = new ArrayList<NormalMethod>();
    	for(Declaration decl: selectionCandidates) {
    		if(decl instanceof NormalMethod) {
    			NormalMethod method = (NormalMethod) decl;
    			int nbActuals = nbActualParameters();
    			int nbFormals = method.nbFormalParameters();
    			// If caching is enable, selected based on the name will already have been
    			// done by the container, so we check for names after the arguments.
    			if ((
    					(nbFormals == nbActuals) ||
    					(   (nbFormals > 1) 
    							&&
    							(method.lastFormalParameter() instanceof MultiFormalParameter)
    							&& 
    							(nbActuals >= nbFormals - 1)
    					)
    			) && selectedBasedOnName(method.signature())){
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
    	order().removeBiggerElements(tmp);
    	return tmp;
    }

    public boolean selectedRegardlessOfName(NormalMethod declaration) throws LookupException {
    	throw new ChameleonProgrammerException();
//  		boolean result = declaration.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR) != Ternary.TRUE;
//  		if(result) {
//  			Signature signature = declaration.signature();
//  			if(signature instanceof SimpleNameMethodSignature) {
//  				List<FormalParameter> formals = declaration.formalParameters();
//  				
//  				//declaration.scope().contains(JavaMethodInvocation.this);
//  				
//          int nbActuals = nbActualParameters();
//          int nbFormals = formals.size();
//          //List<Type> formalTypes = sig.parameterTypes();
//          // We take a shortcut here, and skip the heavy calculations if the number of 
//          // actual and formal parameters isn't equal
//          if(nbActuals == nbFormals){
//          	// Phases 1 and 2 and 3
//						result = phases1and2and3(declaration);
//          } else if
//          // varargs
//          	 (   (nbFormals > 1) 
//          			   &&
//          			 (formals.get(nbFormals - 1) instanceof MultiFormalParameter)
//          			   && 
//          			 (nbActuals >= nbFormals - 1)
//          	 )
//          	 {
//          	// only Phase 3
//						result = variableApplicableBySubtyping(declaration);
//          } else {
//          	result = false;
//          }
////          if(result) {
////          	// Check the explicit parameters.
////          	// FIXME isn't this done already?
////          	List<ActualTypeArgument> actualTypeArguments = typeArguments();
////          	int actualTypeArgumentsSize = actualTypeArguments.size();
////						if(actualTypeArgumentsSize > 0) {
////          		List<TypeParameter> formalTypeParameters = declaration.typeParameters();
////          		result = actualTypeArgumentsSize == formalTypeParameters.size();
////          		if(result) {
////          			for(int i=0; result && i < actualTypeArgumentsSize; i++) {
////          				result = formalTypeParameters.get(i).canBeAssigned(actualTypeArguments.get(i));
////          			}
////          		}
////          	}
////          }
//  			}
//  		}
//  		return result;
    }

//		private boolean phases1and2and3(NormalMethod method) throws LookupException {
//			//return MoreSpecificTypesOrder.create().contains(actuals,formalTypes);
//			return matchingApplicableBySubtyping(method) ||
//			       matchingApplicableByConversion(method) ||
//			       variableApplicableBySubtyping(method);
//		}
		

		private TypeAssignmentSet actualTypeParameters(NormalMethod<?, ?, ?> method, boolean includeNonreference) throws LookupException {
			List<ActualTypeArgument> typeArguments = typeArguments();
			Java language = method.language(Java.class);
			List<TypeParameter> parameters = method.typeParameters();
			TypeAssignmentSet formals = new TypeAssignmentSet(parameters);
			List<TypeParameter> methodTypeParameters = method.typeParameters();
			if(methodTypeParameters.size() > 0) {
				if(typeArguments.size() > 0) {
					int size = typeArguments.size();
					for(int i=0; i< size; i++) {
						formals.add(new ActualTypeAssignment(parameters.get(i),typeArguments.get(i).upperBound()));
					}
				} else {
					// perform type inference
					FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(JavaMethodInvocation.this,method);
					List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
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
			}
			return formals;
		}

		private boolean matchingApplicableBySubtyping(NormalMethod method) throws LookupException {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
			List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
			boolean result = true;
			int size = formalParameterTypesInContext.size();
			List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
			for(int i=0; result && i < size; i++) {
				Type formalType = formalParameterTypesInContext.get(i);
				Type actualType = actualParameters.get(i).getExpression().getType();
				result = actualType.subTypeOf(formalType) || convertibleThroughUncheckedConversionAndSubtyping(actualType, formalType);
			}
			if(result) {
				result = actualTypeParameters.valid();
			}
			return result;
		}
		
		private boolean convertibleThroughUncheckedConversionAndSubtyping(Type first, Type second) throws LookupException {
			boolean result = false;
			if(first instanceof RawType) {
				result = ((RawType)first).convertibleThroughUncheckedConversionAndSubtyping(second);
			}
			return result;
		}
		
		private boolean matchingApplicableByConversion(NormalMethod method) throws LookupException {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true);
			List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
			boolean result = true;
			int size = formalParameterTypesInContext.size();
			List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
			for(int i=0; result && i < size; i++) {
				Type formalType = formalParameterTypesInContext.get(i);
				Type actualType = actualParameters.get(i).getExpression().getType();
				result = convertibleThroughMethodInvocationConversion(actualType, formalType);
			}
			if(result) {
				result = actualTypeParameters.valid();
			}
			return result;
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
			return type.getAllSuperTypes();
		}
		

		public boolean variableApplicableBySubtyping(NormalMethod method) throws LookupException {
			boolean result = method.lastFormalParameter() instanceof MultiFormalParameter;
			if(result) {
				TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true);
				List<Type> formalParameterTypesInContext = formalParameterTypesInContext(method,actualTypeParameters);
				int size = formalParameterTypesInContext.size();
				List<ActualArgument> actualParameters = actualArgumentList().getActualParameters();
				int actualSize = actualParameters.size();
				// For the non-varags arguments, use method invocation conversion
				for(int i=0; result && i < size-1; i++) {
					Type formalType = formalParameterTypesInContext.get(i);
					Type actualType = actualParameters.get(i).getExpression().getType();
					result = convertibleThroughMethodInvocationConversion(actualType, formalType);
				}
				Type formalType = formalParameterTypesInContext.get(size-1);
				for(int i = size-1; result && i< actualSize-1;i++) {
					Type actualType = actualParameters.get(i).getExpression().getType();
					result = convertibleThroughMethodInvocationConversion(actualType, formalType);
				}
				if(result) {
					result = actualTypeParameters.valid();
				}
			}
			return result;
		}
    
  	@Override
    public boolean selectedBasedOnName(Signature signature) throws LookupException {
  		boolean result = false;
  		if(signature instanceof SimpleNameMethodSignature) {
  			SimpleNameMethodSignature sig = (SimpleNameMethodSignature)signature;
  			result = sig.name().equals(name());
  		}
  		return result;
    }

    @Override
    public WeakPartialOrder<NormalMethod> order() {
//      return new WeakPartialOrder<NormalMethod>() {
//        @Override
//        public boolean contains(NormalMethod first, NormalMethod second)
//            throws LookupException {
//          return MoreSpecificTypesOrder.create().contains(((MethodHeader) first.header()).formalParameterTypes(), ((MethodHeader) second.header()).formalParameterTypes());
//        }
//      };
    	return new JavaMostSpecificMethodOrder(JavaMethodInvocation.this);
    }
		@Override
		public Class<NormalMethod> selectedClass() {
			return NormalMethod.class;
		}

		@Override
		public String selectionName() {
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
			if(!(first.lastFormalParameter() instanceof MultiFormalParameter) && ! (second.lastFormalParameter() instanceof MultiFormalParameter)) {
				result = containsFixedArity(first, second);
			} else if((first.lastFormalParameter() instanceof MultiFormalParameter) && (second.lastFormalParameter() instanceof MultiFormalParameter)){
				result = containsVariableArity(first, second);
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
				clone.setUniParent(tref.parent());
				references.add(clone);
			}
			result = new ArrayList<Type>();
			for(TypeReference tref: references) {
				for(TypeParameter par: actualTypeParameters.assigned()) {
					NonLocalJavaTypeReference.replace(language.reference(actualTypeParameters.type(par)), par, (JavaTypeReference<?>) tref);
				}
				result.add(tref.getElement());
			}
			
		} else {
			result = method.header().formalParameterTypes();
		}
		return result;
	}

}