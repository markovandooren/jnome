package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Signature;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.relation.WeakPartialOrder;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.MethodHeader;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeIndirection;
import org.aikodi.chameleon.oo.type.generics.ActualTypeArgument;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaSubtypingRelation;
import be.kuleuven.cs.distrinet.jnome.core.language.JavaSubtypingRelation.UncheckedConversionIndicator;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.NullType;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
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

	@Override
	public boolean isGreedy() {
		return false;
	}

//	@Override
//	public List<? extends SelectionResult> declarators(List<? extends Declaration> selectionCandidates) throws LookupException {
//		List<SelectionResult> result = new ArrayList<>();
//		for(SelectionResult r: selection(selectionCandidates)) {
//			result.add(((MethodSelectionResult)r).template().declarator());
//		}
//		return result;
//	}

	protected abstract MethodInvocation invocation();

	public abstract boolean correctSignature(Signature signature) throws LookupException;

	public List<? extends SelectionResult> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
		List<MethodSelectionResult> tmp = new ArrayList<MethodSelectionResult>();
		if(! selectionCandidates.isEmpty()) {
			int size = selectionCandidates.size();
			List<M> candidates = new ArrayList<M>(size);
			int nbActuals = invocation().nbActualParameters();
			for(int i = 0; i< size; i++) {
				Declaration decl = selectionCandidates.get(i);
				if(_type.isInstance(decl)) {
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
				MethodSelectionResult matchingApplicableBySubtyping = matchingApplicableBySubtyping(decl,java);
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
					MethodSelectionResult matchingApplicableByConversion = matchingApplicableByConversion(decl,java);
					if(matchingApplicableByConversion != null) {
						tmp.add(matchingApplicableByConversion);
					}
				}
				// variable arity
				if(tmp.isEmpty()) {
					for(int i = 0; i< size; i++) {
						M decl = candidates.get(i);
						MethodSelectionResult variableApplicableBySubtyping = variableApplicableBySubtyping(decl,java);
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

	private TypeAssignmentSet actualTypeParameters(M originalMethod, boolean includeNonreference) throws LookupException {
		MethodHeader methodHeader = (MethodHeader) originalMethod.header().clone();
		methodHeader.setOrigin(originalMethod.header());
		methodHeader.setUniParent(originalMethod.parent());
		List<TypeParameter> parameters = methodHeader.typeParameters();
		TypeAssignmentSet typeAssignment;
		int size = parameters.size();
		if(size > 0 && (parameters.get(0) instanceof FormalTypeParameter)) {
			if(invocation().hasTypeArguments()) {
				List<ActualTypeArgument> typeArguments = invocation().typeArguments();
				typeAssignment = new TypeAssignmentSet(parameters);
				int theSize = typeArguments.size();
				for(int i=0; i< theSize; i++) {
					typeAssignment.add(new ActualTypeAssignment(parameters.get(i),typeArguments.get(i).upperBound()));
				}
			} else {
				Java7 language = originalMethod.language(Java7.class);
				// perform type inference
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(),methodHeader);
				List<Expression> actualParameters = invocation().getActualParameters();
				List<Type> formalParameters = methodHeader.formalParameterTypes();
				int theSize = actualParameters.size();
				for(int i=0; i< theSize; i++) {
					// if the formal parameter type is reference type, add a constraint
					Type argType = actualParameters.get(i).getType();
					if(includeNonreference || argType.is(language.REFERENCE_TYPE) == Ternary.TRUE) {
					  //FIXME Check if substitution is every done on the type reference, and
					  // adapt if necessary.
						constraints.add(new SSConstraint(language.reference(argType), formalParameters.get(i)));
					}
				}
				typeAssignment = constraints.resolve();
			}
			List<TypeParameter> originalParameters = originalMethod.typeParameters();
			for(int i = 0; i < size; i++) {
				typeAssignment.substitute(parameters.get(i), originalParameters.get(i));
			}
		} else {
			typeAssignment = null;
		}
		return typeAssignment;
	}

	/**
	 * JLS 15.12.2.2 Phase 1: Identify Matching Arity Methods Applicable by Subtyping
	 */
	private MethodSelectionResult matchingApplicableBySubtyping(M method, Java7 java) throws LookupException {
		if(method.nbFormalParameters() == invocation().nbActualParameters()) {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
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
				match = actualType.subTypeOf(formalType);
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


	private MethodSelectionResult matchingApplicableByConversion(M method, Java7 java) throws LookupException {
		if(method.nbFormalParameters() == invocation().nbActualParameters()) {
		TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true);
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




	public MethodSelectionResult variableApplicableBySubtyping(M method, Java7 java) throws LookupException {
		boolean match = method.lastFormalParameter() instanceof MultiFormalParameter;
		if(match) {
			TypeAssignmentSet actualTypeParameters = actualTypeParameters(method,true);
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

	public MethodSelectionResult createSelectionResult(Method method, TypeAssignmentSet typeAssignment, int phase, boolean requiredUncheckedConversion) {
		return new BasicMethodSelectionResult(method, typeAssignment,phase,requiredUncheckedConversion);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void filter(List<? extends SelectionResult> selected) throws LookupException {
		applyOrder((List)selected);
	}

	protected void applyOrder(List<MethodSelectionResult> tmp) throws LookupException {
		order().removeBiggerElements(tmp);
	}

	public WeakPartialOrder<MethodSelectionResult> order() {
		return new JavaMostSpecificMethodOrder<MethodSelectionResult>(invocation());
	}

	public Class<M> selectedClass() {
		return _type;
	}

	@Override
	public boolean canSelect(Class<? extends Declaration> type) {
		return _type.isAssignableFrom(type);
	}
}
