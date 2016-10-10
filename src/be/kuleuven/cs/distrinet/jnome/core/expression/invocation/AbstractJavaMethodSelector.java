package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;
import org.junit.Ignore;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaSubtypingRelation;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaSubtypingRelation.UncheckedConversionIndicator;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.variable.MultiFormalParameter;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

public abstract class AbstractJavaMethodSelector<M extends Method> implements DeclarationSelector<M> {

	public AbstractJavaMethodSelector(Class<M> type) {
		super();
		if(type == null) {
			throw new IllegalArgumentException("The type of methods selected cannot be null.");
		}
		_type = type;

	}

	private Class<M> _type;
//	private WeakPartialOrder<MethodSelectionResult> _order;

	@Override
	public boolean isGreedy() {
		return false;
	}

	protected abstract MethodInvocation<M> invocation();

	public abstract boolean correctSignature(Signature signature) throws LookupException;

	public List<? extends SelectionResult<M>> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
		List<MethodSelectionResult<M>> tmp = new ArrayList<>();
		if(! selectionCandidates.isEmpty()) {
			int size = selectionCandidates.size();
			List<M> candidates = new ArrayList<M>(size);
			int nbActuals = invocation().nbActualParameters();
			for(int i = 0; i< size; i++) {
				Declaration decl = selectionCandidates.get(i);
//				Util.debug(decl.name().equals("antMatchers") && invocation() instanceof JavaMethodInvocation && ((JavaMethodInvocation)invocation()).name().equals("antMatchers"));
				if(selectedClass().isInstance(decl)) {
					M method = (M) decl;
					int nbFormals = method.nbFormalParameters();
					// If caching is enable, selected based on the name will already have been
					// done by the container, so we check for names after the arguments.
					if ((
							(nbFormals == nbActuals) ||
							((nbFormals > 0) 
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
			Java7 java = invocation().language(Java7.class);
			/**
			 * JLS 15.12.2.2 Phase 1: Identify Matching Arity Methods Applicable by Subtyping
			 */
			size = candidates.size();
			for(int i = 0; i< size; i++) {
				M decl = candidates.get(i);
				MethodSelectionResult<M> matchingApplicableBySubtyping = matchingApplicableBySubtyping(decl,java);
				if(matchingApplicableBySubtyping != null) {
					tmp.add(matchingApplicableBySubtyping);
				}
			}
			// JLS 15.12.2.2: Only if no candidates are applicable by subtyping
			//                does the search consider other candidates.
			// conversion
			if(tmp.isEmpty()) {
				for(int i = 0; i< size; i++) {
					M decl = candidates.get(i);
					MethodSelectionResult<M> matchingApplicableByConversion = matchingApplicableByConversion(decl,java);
					if(matchingApplicableByConversion != null) {
						tmp.add(matchingApplicableByConversion);
					}
				}
				// variable arity
				if(tmp.isEmpty()) {
					for(int i = 0; i< size; i++) {
						M decl = candidates.get(i);
						MethodSelectionResult<M> variableApplicableBySubtyping = variableApplicableBySubtyping(decl,java);
						if(variableApplicableBySubtyping != null) {
							tmp.add(variableApplicableBySubtyping);
						}
					}
				}
			}
			applyOrder(tmp);
		}
		return tmp;
	}

	//	/**
	//	 * FIXME This should implement 15.12.2.6 Method Result and Throws Types. This implementation doesn't always
	//	 * use the correct type assignment algorithm. Basically it is fixed now and does not take into account how
	//	 * the method was selected.
	//	 * @param method
	//	 * @return
	//	 * @throws LookupException
	//	 */
	//	public M instance(M method) throws LookupException {
	//		TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
	//		return instantiatedMethodTemplate(method, actualTypeParameters);
	//	}

	private TypeAssignmentSet actualTypeParameters(M originalMethod, boolean includeNonreference, boolean variableArity) throws LookupException {
		MethodHeader clonedHeader = originalMethod.header();
		List<TypeParameter> clonedTypeParameters = clonedHeader.typeParameters();
		TypeAssignmentSet typeAssignment;
		int size = clonedTypeParameters.size();
		if(size > 0 && (clonedTypeParameters.get(0) instanceof FormalTypeParameter)) {
			if(invocation().hasTypeArguments()) {
				List<TypeArgument> typeArguments = invocation().typeArguments();
				typeAssignment = new TypeAssignmentSet(clonedTypeParameters);
				int nbActualTypeArguments = typeArguments.size();
				for(int i=0; i< nbActualTypeArguments; i++) {
					typeAssignment.add(new ActualTypeAssignment(clonedTypeParameters.get(i),typeArguments.get(i).upperBound()));
				}
			} else {
				Java7 language = originalMethod.language(Java7.class);
				// perform type inference
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(),clonedHeader);
				List<Expression> actualArguments = invocation().getActualParameters();
				List<Type> formalParameterTypes = clonedHeader.formalParameterTypes();
				int maxFormalParameterIndex = formalParameterTypes.size() - 1;
				if(variableArity && originalMethod.lastFormalParameter() instanceof MultiFormalParameter) {
					maxFormalParameterIndex--;
				}
				int nbActualParameters = actualArguments.size();
				for(int i=0; i< nbActualParameters; i++) {
					// if the formal parameter type is reference type, add a constraint
					Type argType = actualArguments.get(i).getType();
					if(includeNonreference || argType.is(language.REFERENCE_TYPE) == Ternary.TRUE) {
					  //FIXME Check if substitution is ever done on the type reference, and
					  // adapt if necessary.
					  int index = i;
					  if(variableArity && index > maxFormalParameterIndex) {
					    index = maxFormalParameterIndex;
							constraints.add(new SSConstraint(language.reference(argType), ((ArrayType)formalParameterTypes.get(index+1)).elementType()));
					  } else {
					  	constraints.add(new SSConstraint(language.reference(argType), formalParameterTypes.get(index)));
					  }
					}
				}
				typeAssignment = constraints.resolve();
			}
		} else {
			typeAssignment = null;
		}
		return typeAssignment;
	}

	/**
	 * JLS 15.12.2.2 Phase 1: Identify Matching Arity Methods Applicable by Subtyping
	 */
	private MethodSelectionResult<M> matchingApplicableBySubtyping(M method, Java7 java) throws LookupException {
		if(method.nbFormalParameters() == invocation().nbActualParameters()) {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false, false);
			//SLOW We can probably cache the substituted type instead/as well.
			List<Type> formalParameterTypesInContext = JavaMethodInvocation.formalParameterTypesInContext(method,actualTypeParameters);
			boolean match = true;
			List<Expression> actualParameters = invocation().getActualParameters();
			int nbFormals = formalParameterTypesInContext.size();
			int nbActuals = actualParameters.size();
			JavaSubtypingRelation subtypeRelation = java.subtypeRelation();
			boolean uncheckedConversion = false;
			for(int i=0; match && i < nbActuals; i++) {
				Type formalType;
				if(i >= nbFormals) {
					formalType = formalParameterTypesInContext.get(nbFormals - 1);
				} else {
					formalType = formalParameterTypesInContext.get(i);
				}
				Type actualType = actualParameters.get(i).getType();
				match = actualType.subtypeOf(formalType);
				if(! match) {
					match = subtypeRelation.convertibleThroughUncheckedConversionAndSubtyping(actualType, formalType);
					if(match) {
						uncheckedConversion = true;
					}
				}
			}
			// This may be inefficient, be it is literally what the language spec says
			// so for now I do it exactly the same way.
			if(match && actualTypeParameters != null) {
				match = actualTypeParameters.valid();
			}
			if(match) {
				return createSelectionResult(method, actualTypeParameters,1,uncheckedConversion);
			} else {
				return null;
			}
		} 
		else {
			return null;
		}
	}


	private MethodSelectionResult<M> matchingApplicableByConversion(M method, Java7 java) throws LookupException {
		if(method.nbFormalParameters() == invocation().nbActualParameters()) {
		TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true,false);
		List<Type> formalParameterTypesInContext = JavaMethodInvocation.formalParameterTypesInContext(method,actualTypeParameters);
		boolean match = true;
		int size = formalParameterTypesInContext.size();
		List<Expression> actualParameters = invocation().getActualParameters();
		JavaSubtypingRelation subtypeRelation = java.subtypeRelation();
		UncheckedConversionIndicator indicator = new UncheckedConversionIndicator();
		for(int i=0; match && i < size; i++) {
			Type formalType = formalParameterTypesInContext.get(i);
			Type actualType = actualParameters.get(i).getType();
			match = subtypeRelation.convertibleThroughMethodInvocationConversion(actualType, formalType,indicator);
		}
		if(match && actualTypeParameters != null) {
			match = actualTypeParameters.valid();
		} 
		if(match) {
			return createSelectionResult(method, actualTypeParameters,2, indicator.isSet());
		} else {
			return null;
		}
	} 
	else {
		return null;
	}
}




	protected MethodSelectionResult<M> variableApplicableBySubtyping(M method, Java7 java) throws LookupException {
		boolean match = method.lastFormalParameter() instanceof MultiFormalParameter;
		if(match) {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true,true);
			List<Type> formalParameterTypesInContext = JavaMethodInvocation.formalParameterTypesInContext(method,actualTypeParameters);
			int size = formalParameterTypesInContext.size();
			List<Expression> actualParameters = invocation().getActualParameters();
			int actualSize = actualParameters.size();
			// For the non-varags arguments, use method invocation conversion
			UncheckedConversionIndicator indicator = new UncheckedConversionIndicator();
			JavaSubtypingRelation subtypeRelation = java.subtypeRelation();
			for(int i=0; match && i < size-1; i++) {
				Type formalType = formalParameterTypesInContext.get(i);
				Type actualType = actualParameters.get(i).getType();
				match = subtypeRelation.convertibleThroughMethodInvocationConversion(actualType, formalType,indicator);
			}
			Type formalType = ((ArrayType)formalParameterTypesInContext.get(size-1)).elementType();
			for(int i = size-1; match && i< actualSize;i++) {
				Type actualType = actualParameters.get(i).getType();
				match = subtypeRelation.convertibleThroughMethodInvocationConversion(actualType, formalType,indicator);
			}
			if(match && actualTypeParameters != null) {
				match = actualTypeParameters.valid();
			} 
			if(match) {
				return createSelectionResult(method, actualTypeParameters,3,indicator.isSet());
			} else {
				return null;
			}
		}
		return null;
	}

	protected MethodSelectionResult<M> createSelectionResult(M method, TypeAssignmentSet typeAssignment, int phase, boolean requiredUncheckedConversion) {
		return new BasicMethodSelectionResult<>(method, typeAssignment,phase,requiredUncheckedConversion);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void filter(List<? extends SelectionResult<M>> selected) throws LookupException {
		applyOrder((List)selected);
	}

	protected void applyOrder(List<MethodSelectionResult<M>> tmp) throws LookupException {
		int size = tmp.size();
		int i=0;
		outer: while(i< size) {
			MethodSelectionResult<M> e1 = tmp.get(i);
			for(int j=i+1; j < size; j++) {
				MethodSelectionResult<M> e2 = tmp.get(j);
				if(i != j) {
					boolean firstSmaller = contains(e1,e2);
					boolean secondSmaller = contains(e2,e1);
					if(secondSmaller && (! firstSmaller)) {
						tmp.remove(i);
						size--;
						continue outer;
					} else if(firstSmaller && (! secondSmaller)) {
						tmp.remove(j);
						size--;
					}
				}
			}
			i++;
		}
	}

	protected Class<M> selectedClass() {
		return _type;
	}

	@Override
	public boolean canSelect(Class<? extends Declaration> type) {
		return selectedClass().isAssignableFrom(type);
	}
	
	public boolean contains(MethodSelectionResult firstResult, MethodSelectionResult secondResult) throws LookupException {
		boolean result = false;
		if(firstResult != secondResult) {
			int firstPhase = firstResult.phase();
			int secondPhase = secondResult.phase();
			if(firstPhase < secondPhase) {
				result = true;
			} else if(firstPhase > secondPhase) {
				result = false;
			} else {
				Method first = firstResult.template();
				Method second = secondResult.template();
				if(! first.sameAs(second)) {
					if(!(first.lastFormalParameter() instanceof MultiFormalParameter) && ! (second.lastFormalParameter() instanceof MultiFormalParameter)) {
						result = containsFixedArity(first, second);
					} else if((first.lastFormalParameter() instanceof MultiFormalParameter) && (second.lastFormalParameter() instanceof MultiFormalParameter)){
						result = containsVariableArity(firstResult, secondResult);
					}
					
				}
			}
		}
		return result;
	}

	protected boolean containsVariableArity(MethodSelectionResult firstResult, MethodSelectionResult secondResult) throws LookupException {
		Method first = firstResult.template();
		Method second = secondResult.template();

		boolean result = true;
		Java7 language = (Java7) first.language(Java7.class);
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
			FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(),second.header());
			for(int i=0; i < k-1; i++) {
				constraints.add(new SSConstraint(language.reference(firstTypes.get(i)), secondTypes.get(i)));
			}
			TypeAssignmentSet As = constraints.resolve();
			result = As.valid();
			if(result) {
			  Ss = JavaMethodInvocation.formalParameterTypesInContext(second, As);
			} else {
				// If the actual type parameters are invalid, don't bother
				// substituting them
				Ss = null;
			}
		} else {
			Ss = new ArrayList<Type>(secondTypes);
		}
		for(int i=0; result && i<k-1;i++) {
			result = firstTypes.get(i).subtypeOf(Ss.get(i));
		}
		if(result && firstSize >= secondSize) {
			for(int i=k-1; result && i<n;i++) {
				result = firstTypes.get(i).subtypeOf(Ss.get(k-1));
			}
		} else {
			for(int i=k-1; result && i<n;i++) {
				result = firstTypes.get(k-1).subtypeOf(Ss.get(i));
			}
		}
		return result;
	}
	
	public boolean containsFixedArity(Method first, Method second) throws LookupException {
		boolean result = true;
		Java7 language = (Java7) first.language(Java7.class);
		List<Type> Ts = first.header().formalParameterTypes();
		List<Type> Us = second.header().formalParameterTypes();
		int size =Ts.size();
		boolean hasFormalTypeParameter = false;
		for(TypeParameter p: second.typeParameters()) {
			if(p instanceof FormalTypeParameter) {
				hasFormalTypeParameter = true;
				break;
			}
		}
		List<Type> Ss;
		if(hasFormalTypeParameter) {
			FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(), second.header());
			for(int i=0; i < size; i++) {
				constraints.add(new SSConstraint(language.reference(Ts.get(i)), Us.get(i)));
			}
			TypeAssignmentSet As = constraints.resolve();
			result = As.valid();
			if(result) {
			  Ss = JavaMethodInvocation.formalParameterTypesInContext(second, As);
			} else {
				// If the actual type parameters are invalid, don't bother
				// substituting them
				Ss = null;
			}
		} else {
			Ss = new ArrayList<Type>(Us);
		}
		for(int i=0; result && i<size;i++) {
			result = Ts.get(i).subtypeOf(Ss.get(i));
		}
		
		return result;
	}

}
