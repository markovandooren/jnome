package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Signature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.relation.WeakPartialOrder;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
import be.kuleuven.cs.distrinet.jnome.core.variable.MultiFormalParameter;
import be.kuleuven.cs.distrinet.rejuse.association.SingleAssociation;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

public abstract class AbstractJavaMethodSelector extends DeclarationSelector<NormalMethod> {

	public AbstractJavaMethodSelector() {
		super();
	}
	
	@Override
	public boolean isGreedy() {
		return false;
	}

	@Override
	public List<? extends Declaration> declarators(List<? extends Declaration> selectionCandidates) throws LookupException {
		return selection(selectionCandidates);
	}
	
	protected abstract MethodInvocation invocation();
	
	public abstract boolean correctSignature(Signature signature) throws LookupException;

	public List<NormalMethod> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
		return selection(selectionCandidates,null);
	}
	
	public List<NormalMethod> selection(List<? extends Declaration> selectionCandidates, Map<Declaration,Declaration> cache) throws LookupException {
		List<NormalMethod> tmp = new ArrayList<NormalMethod>();
		if(! selectionCandidates.isEmpty()) {
			List<NormalMethod> candidates = new ArrayList<NormalMethod>();
			int nbActuals = invocation().nbActualParameters();
			for(Declaration decl: selectionCandidates) {
				if(decl instanceof NormalMethod) {
					NormalMethod method = (NormalMethod) decl;
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
			
			/**
			 * JLS 15.12.2.2 Phase 1: Identify Matching Arity Methods Applicable by Subtyping
			 */
			for(NormalMethod decl: candidates) {
				if(matchingApplicableBySubtyping(decl)) {
					tmp.add(decl);
				}
			}
			// JLS 15.12.2.2: Only if no candidates are applicable by subtyping
			//                does the search consider other candidates.
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

	/**
	 * FIXME This should implement 15.12.2.6 Method Result and Throws Types. This implementation doesn't always
	 * use the correct type assignment algorithm. Basically it is fixed now and does not take into account how
	 * the method was selected.
	 * @param method
	 * @return
	 * @throws LookupException
	 */
	public NormalMethod instance(NormalMethod method) throws LookupException {
		TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
		return instantiatedMethodTemplate(method, actualTypeParameters);
	}

	private TypeAssignmentSet actualTypeParameters(NormalMethod originalMethod, boolean includeNonreference) throws LookupException {
		MethodHeader methodHeader = (MethodHeader) originalMethod.header().clone();
		methodHeader.setOrigin(originalMethod.header());
		methodHeader.setUniParent(originalMethod.parent());
		List<TypeParameter> parameters = methodHeader.typeParameters();
		TypeAssignmentSet formals;
		//			List<TypeParameter> methodTypeParameters = method.typeParameters();
		if(parameters.size() > 0 && (parameters.get(0) instanceof FormalTypeParameter)) {
			if(invocation().hasTypeArguments()) {
				List<ActualTypeArgument> typeArguments = invocation().typeArguments();
				formals = new TypeAssignmentSet(parameters);
				int size = typeArguments.size();
				for(int i=0; i< size; i++) {
					formals.add(new ActualTypeAssignment(parameters.get(i),typeArguments.get(i).upperBound()));
				}
			} else {
				Java language = originalMethod.language(Java.class);
				// perform type inference
				FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(),methodHeader);
				List<Expression> actualParameters = invocation().getActualParameters();
				List<Type> formalParameters = methodHeader.formalParameterTypes();
				int size = actualParameters.size();
				for(int i=0; i< size; i++) {
					// if the formal parameter type is reference type, add a constraint
					Type argType = actualParameters.get(i).getType();
					if(includeNonreference || argType.is(language.REFERENCE_TYPE) == Ternary.TRUE) {
						constraints.add(new SSConstraint(language.reference(argType), formalParameters.get(i)));
					}
				}
				formals = constraints.resolve();
			}
		} else {
			formals = new TypeAssignmentSet(Collections.EMPTY_LIST);
		}
		int size = parameters.size();
		List<TypeParameter> originalParameters = originalMethod.typeParameters();

		for(int i = 0; i < size; i++) {
			formals.substitute(parameters.get(i), originalParameters.get(i));
		}
		return formals;
	}

	/**
	 * JLS 15.12.2.2 Phase 1: Identify Matching Arity Methods Applicable by Subtyping
	 */
	private boolean matchingApplicableBySubtyping(NormalMethod method) throws LookupException {
				TypeAssignmentSet actualTypeParameters = actualTypeParameters(method, false);
				List<Type> formalParameterTypesInContext = JavaMethodInvocation.formalParameterTypesInContext(method,actualTypeParameters);
				boolean match = true;
				List<Expression> actualParameters = invocation().getActualParameters();
				int nbFormals = formalParameterTypesInContext.size();
				int nbActuals = actualParameters.size();
				for(int i=0; match && i < nbActuals; i++) {
					Type formalType;
					if(i >= nbFormals) {
						formalType = formalParameterTypesInContext.get(nbFormals - 1);
					} else {
					  formalType = formalParameterTypesInContext.get(i);
					}
					Type actualType = actualParameters.get(i).getType();
					match = actualType.subTypeOf(formalType) || convertibleThroughUncheckedConversionAndSubtyping(actualType, formalType);
				}
				// This may be inefficient, be it is literally what the language spec says
				// so for now I do it exactly the same way.
				if(match) {
					match = actualTypeParameters.valid();
				} 
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
				Java language = invocation().language(Java.class);
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
			Set<Type> supers = first.getSelfAndAllSuperTypesView();
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
				List<Type> formalParameterTypesInContext = JavaMethodInvocation.formalParameterTypesInContext(method,actualTypeParameters);
				boolean match = true;
				int size = formalParameterTypesInContext.size();
				List<Expression> actualParameters = invocation().getActualParameters();
				for(int i=0; match && i < size; i++) {
					Type formalType = formalParameterTypesInContext.get(i);
					Type actualType = actualParameters.get(i).getType();
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
					if(convertibleThroughUncheckedConversionAndSubtyping(type, second)) {
						result = true;
						break;
					}
				}
				if(! result) {
					// FIXME is this still necessary? first has already been added to the previous check G) unchecked conversion after identity
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
		View view = type.view();
		Java language = view.language(Java.class);
		String name = type.getFullyQualifiedName();
		Namespace ns = view.namespace();
		if(type.is(language.NUMERIC_TYPE) == Ternary.TRUE) {
			if(! name.equals("double")) {
				result.add(language.findType("double",ns));
				if(! name.equals("float")) {
					result.add(language.findType("float",ns));
					if(! name.equals("long")) {
						result.add(language.findType("long",ns));
						if(! name.equals("int")) {
							result.add(language.findType("int",ns));
							// char and short do not convert to short via widening.
							if(name.equals("byte")) {
								result.add(language.findType("short",ns));
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
					List<Type> formalParameterTypesInContext = JavaMethodInvocation.formalParameterTypesInContext(method,actualTypeParameters);
					int size = formalParameterTypesInContext.size();
					List<Expression> actualParameters = invocation().getActualParameters();
					int actualSize = actualParameters.size();
					// For the non-varags arguments, use method invocation conversion
					for(int i=0; match && i < size-1; i++) {
						Type formalType = formalParameterTypesInContext.get(i);
						Type actualType = actualParameters.get(i).getType();
						match = convertibleThroughMethodInvocationConversion(actualType, formalType);
					}
					Type formalType = ((ArrayType)formalParameterTypesInContext.get(size-1)).elementType();
					for(int i = size-1; match && i< actualSize;i++) {
						Type actualType = actualParameters.get(i).getType();
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

	@Override
	public WeakPartialOrder<NormalMethod> order() {
		return new JavaMostSpecificMethodOrder(invocation());
	}

	@Override
	public Class<NormalMethod> selectedClass() {
		return NormalMethod.class;
	}

}