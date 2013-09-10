/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.relation.WeakPartialOrder;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ParameterSubstitution;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.UnionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.CapturedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.LazyInstantiatedAlias;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.WildCardType;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;

public class JavaSubtypingRelation extends SubtypeRelation {
	
	
	
	public boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = trace;
	boolean result = false;
			if(
				(second instanceof LazyInstantiatedAlias)) {
					TypeParameter secondParam = ((LazyInstantiatedAlias)second).parameter();
					for(Pair<Type, TypeParameter> pair: slowTrace) {
						if(first.sameAs(pair.first()) && secondParam.sameAs(pair.second())) {
							return true;
						}
					}
					slowTrace.add(new Pair<Type, TypeParameter>(first, secondParam));
			}
			if(
					(first instanceof LazyInstantiatedAlias)) {
						TypeParameter firstParam = ((LazyInstantiatedAlias)first).parameter();
						for(Pair<Type, TypeParameter> pair: slowTrace) {
							if(second.sameAs(pair.first()) && firstParam.sameAs(pair.second())) {
								return true;
							}
						}
						slowTrace.add(new Pair<Type, TypeParameter>(second, firstParam));
				}
			if(second instanceof InstantiatedParameterType) {
				TypeParameter secondParam = ((InstantiatedParameterType)second).parameter();
				for(Pair<Type, TypeParameter> pair: slowTrace) {
					if(first.sameAs(pair.first()) && secondParam.sameAs(pair.second())) {
						return true;
					}
				}
				if(first.sameAs(second)) {
					return true;
				}
				slowTrace.add(new Pair<Type, TypeParameter>(first, secondParam));
				result = first.upperBoundNotHigherThan(((InstantiatedParameterType) second).aliasedType(), slowTrace);
				return result;
			}
			if(first instanceof InstantiatedParameterType) {
				TypeParameter firstParam = ((InstantiatedParameterType)first).parameter();
				for(Pair<Type, TypeParameter> pair: slowTrace) {
					if(firstParam.sameAs(pair.second()) && second.sameAs(pair.first()))
					{
						return true;
					}
				}
				if(first.sameAs(second)) {
					return true;
				}
				slowTrace.add(new Pair<Type, TypeParameter>(second, firstParam));
				result = ((InstantiatedParameterType) first).aliasedType().upperBoundNotHigherThan(second, slowTrace);
				return result;
			}
			if(first.sameAs(second)) {
				result = true;
			} 
			else if (first instanceof WildCardType) {
				result = ((WildCardType)first).upperBound().upperBoundNotHigherThan(second,slowTrace);
			} else if (second instanceof WildCardType) {
				//TODO Both lines make the tests succeed, but the first line makes no sense.
//				result = first.upperBoundNotHigherThan(((WildCardType)second).lowerBound(),slowTrace);
				result = first.upperBoundNotHigherThan(((WildCardType)second).upperBound(),slowTrace);
			}
			// The relations between arrays and object are covered by the subtyping relations
			// that are added to ArrayType objects.
			else if (first instanceof ArrayType && second instanceof ArrayType && first.is(first.language(Java.class).REFERENCE_TYPE) == Ternary.TRUE) {
				ArrayType first2 = (ArrayType)first;
				ArrayType second2 = (ArrayType)second;
				result = first2.elementType().upperBoundNotHigherThan(second2.elementType(),slowTrace);
			} else if(second instanceof IntersectionType) {
				List<Type> types = ((IntersectionType)second).types();
				int size = types.size();
				result = size > 0;
				for(int i=0; result && i<size;i++) {
					result = first.upperBoundNotHigherThan(types.get(i),slowTrace);
				}
			} else if(first instanceof IntersectionType) {
				List<Type> types = ((IntersectionType)first).types();
				int size = types.size();
				result = false;
				for(int i=0; (!result) && i<size;i++) {
					result = types.get(i).upperBoundNotHigherThan(second,slowTrace);
				}
			} else if(second instanceof UnionType) {
				List<Type> types = ((UnionType)second).types();
				int size = types.size();
				result = false;
				for(int i=0; (!result) && i<size;i++) {
					result = first.upperBoundNotHigherThan(types.get(i),slowTrace);
				}
			} else if(first instanceof UnionType) {
				List<Type> types = ((UnionType)first).types();
				int size = types.size();
				result = size > 0;
				for(int i=0; result && i<size;i++) {
					result = types.get(i).upperBoundNotHigherThan(second, slowTrace);
				}
			} else if(second instanceof RawType) {
				Set<Type> supers = first.getAllSuperTypes();
				supers.add(first);
				Iterator<Type> typeIterator = supers.iterator();
				while((!result) && typeIterator.hasNext()) {
					Type current = typeIterator.next();
					result = second.baseType().sameAs(current.baseType());
				}
			}
			else {
				//SPEED iterate over the supertype graph 
				Set<Type> supers = first.getSelfAndAllSuperTypesView();
				Type snd = captureConversion(second);

				Iterator<Type> typeIterator = supers.iterator();
				while((!result) && typeIterator.hasNext()) {
					Type current = typeIterator.next();
					result = (snd instanceof RawType && second.baseType().sameAs(current.baseType())) || sameBaseTypeWithCompatibleParameters(current, snd, slowTrace);
				}
			}
		return result;
	}
	
//	private static Logger _logger = Logger.getLogger("lookup.subtyping");
//	
//	public static Logger getLogger() {
//		return _logger;
//	}
	
//	public void flushCache() {
//		_cache = new HashMap<Type,Set<Type>>();
//	}

	// Can't use set for now because hashCode is not OK.
//	private Map<Type, Set<Type>> _cache = new HashMap<Type,Set<Type>>();
	
	@Override
	public boolean contains(Type first, Type second) throws LookupException {
//		Set<Type> zuppas;
//		synchronized (this) {
//			try {
//				zuppas = _cache.get(first);
//				// The following code is meant to tunnel a LookupException through the equals method, which is used
//				// by the code in java.util.Set. The exception is first wrapped in a ChameleonProgrammerException,
//				// and unwrapped when it arrives here.
//			} catch(ChameleonProgrammerException exc) {
//				Throwable cause = exc.getCause();
//				if(cause instanceof LookupException) {
//					throw (LookupException)cause;
//				} else {
//					throw exc;
//				}
//			}
//		}
//		if(zuppas != null && zuppas.contains(second)) {
//			return true;
//		}
		boolean result = false;
		//SPEED iterate over the supertype graph 
		Set<Type> supers = first.getSelfAndAllSuperTypesView();
		Type snd = captureConversion(second);

		Iterator<Type> typeIterator = supers.iterator();
		while((!result) && typeIterator.hasNext()) {
			Type current = typeIterator.next();
			result = (snd instanceof RawType && second.baseType().sameAs(current.baseType())) || sameBaseTypeWithCompatibleParameters(current, snd, new ArrayList<Pair<Type, TypeParameter>>());
		}
//		if(result) {
//			synchronized(this) {
//				zuppas = _cache.get(first);
//				if(zuppas == null) {
//					zuppas = new HashSet<Type>();
//					_cache.put(first, zuppas);
//				}
//				zuppas.add(second);
//			}
//		}
		return result;
	}
	
	public Type captureConversion(Type type) throws LookupException {
		// create a derived type
		Type result = type;
		if(result instanceof DerivedType) {
			List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
			if(! (type.parameter(TypeParameter.class,1) instanceof CapturedTypeParameter)) {
				Type base = type.baseType();
				List<TypeParameter> baseParameters = base.parameters(TypeParameter.class);
				Iterator<TypeParameter> formals = baseParameters.iterator();
				List<TypeParameter> actualParameters = type.parameters(TypeParameter.class);
				Iterator<TypeParameter> actuals = actualParameters.iterator();
				// substitute parameters by their capture bounds.
				// ITERATOR because we iterate over 'formals' and 'actuals' simultaneously.
				List<TypeConstraint> toBeSubstituted = new ArrayList<TypeConstraint>();
				while(actuals.hasNext()) {
					TypeParameter formalParam = formals.next();
					if(!(formalParam instanceof FormalTypeParameter)) {
						throw new LookupException("Type parameter of base type is not a formal parameter.");
					}
					TypeParameter actualParam = actuals.next();
					if(!(actualParam instanceof InstantiatedTypeParameter)) {
						throw new LookupException("Type parameter of type instantiation is not an instantiated parameter: "+actualParam.getClass().getName());
					}
					typeParameters.add(((InstantiatedTypeParameter) actualParam).capture((FormalTypeParameter) formalParam,toBeSubstituted));
				}
				result = type.language(Java.class).createdCapturedType(new ParameterSubstitution<>(TypeParameter.class,typeParameters), base);
				result.setUniParent(type.parent());
				for(TypeParameter newParameter: typeParameters) {
					for(TypeParameter oldParameter: baseParameters) {
						JavaTypeReference tref = new BasicJavaTypeReference(oldParameter.signature().name());
						tref.setUniParent(newParameter);
						if(newParameter instanceof CapturedTypeParameter) {
							List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
							for(TypeConstraint constraint : constraints) {
								if(toBeSubstituted.contains(constraint)) {
									NonLocalJavaTypeReference.replace(tref, oldParameter, (JavaTypeReference) constraint.typeReference());
								}
							}
						} else {
							throw new ChameleonProgrammerException();
						}
					}
				}
			}
		}
		return result;
	}
	
	public static class CaptureReference extends NonLocalJavaTypeReference {

		public CaptureReference(JavaTypeReference tref) {
			super(tref,null);
		}

		@Override
		public Element lookupParent() {
			return nearestAncestor(TypeParameter.class);
		}

		@Override
		protected CaptureReference cloneSelf() {
			return new CaptureReference(null);
		}
		
	}

	public boolean sameBaseTypeWithCompatibleParameters(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
//		List<Pair<TypeParameter, TypeParameter>> slowTrace = trace;
		boolean result = false;
		if(first.baseType().sameAs(second.baseType())) {
			result = compatibleParameters(first, second, slowTrace);// || rawType(second); equality in formal parameter should take care of this.
		}
		return result;
	}
	
	public boolean rawType(Type type) {
		for(TypeParameter parameter: type.parameters(TypeParameter.class)) {
			if(! (parameter instanceof FormalTypeParameter)) {
				return false;
			}
		}
		return true;
	}

	private boolean compatibleParameters(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
		boolean result;
		List<TypeParameter> firstFormal= first.parameters(TypeParameter.class);
		List<TypeParameter> secondFormal= second.parameters(TypeParameter.class);
		result = true;
		Iterator<TypeParameter> firstIter = firstFormal.iterator();
		Iterator<TypeParameter> secondIter = secondFormal.iterator();
		while(result && firstIter.hasNext()) {
			TypeParameter firstParam = firstIter.next();
			TypeParameter secondParam = secondIter.next();
			result = firstParam.compatibleWith(secondParam, slowTrace);
		}
		return result;
	}
	
	@Override
	public Type leastUpperBound(List<? extends TypeReference> Us) throws LookupException {
		List<Type> MEC = new ArrayList<Type>(MEC((List<? extends JavaTypeReference>) Us));
		List<Type> candidates = new ArrayList<Type>();
		for(Type W:MEC) {
			candidates.add(Candidate(W,(List<? extends JavaTypeReference>) Us));
		}
		return intersection(candidates);
	}

	private Set<Type> MEC(List<? extends JavaTypeReference> Us) throws LookupException {
		final Set<Type> EC = EC(Us);
		new AbstractPredicate<Type, LookupException>() {
			@Override
			public boolean eval(final Type first) throws LookupException {
				return ! new AbstractPredicate<Type, LookupException>() {
					@Override
					public boolean eval(Type second) throws LookupException {
						return (! first.sameAs(second)) && (second.subTypeOf(first));
					}
				}.exists(EC);
			}
		}.filter(EC);
		return EC;
	}
	
	private Set<Type> EC(List<? extends JavaTypeReference> Us) throws LookupException {
		List<Set<Type>> ESTs = new ArrayList<Set<Type>>();
		for(JavaTypeReference URef: Us) {
			ESTs.add(EST(URef));
		}
		Set<Type> result;
		int size = ESTs.size();
		if(size > 0) {
			result = ESTs.get(0);
			for(int i = 1; i< size; i++) {
				result.retainAll(ESTs.get(i));
			}
		} else {
		  result = new HashSet<Type>();
		}
		return result;
		// Take intersection
	}

	private Set<Type> EST(JavaTypeReference U) throws LookupException {
		Set<Type> STU = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type type:STU) {
			Type erasure = U.language(Java.class).erasure(type);
			result.add(erasure);
		}
		return result;
	}
	
	private Type intersection(List<Type> candidates)
			throws LookupException {
		if(candidates.isEmpty()) {
			throw new LookupException("No candidates for the inferred type");
		} else if(candidates.size() == 1) {
			return candidates.get(0);
		} else {
		  return IntersectionType.create(candidates);
		}
	}
	


	private Set<Type> ST(JavaTypeReference U) throws LookupException {
		Set<Type> result = U.getElement().getAllSuperTypes();
		result.add(U.getElement());
		return result;
	}

	private Type CandidateInvocation(Type G, List<? extends JavaTypeReference> Us) throws LookupException {
		return lci(Inv(G,Us));
	}
	
	private Type Candidate(Type W, List<? extends JavaTypeReference> Us) throws LookupException {
		if(W.parameters(TypeParameter.class).size() > 0) {
			return CandidateInvocation(W, Us);
		} else {
			return W;
		}
	}
	
	private Set<Type> Inv(Type G, List<? extends JavaTypeReference> Us) throws LookupException {
		Set<Type> result = new HashSet<Type>();
		for(JavaTypeReference U: Us) {
			result.addAll(Inv(G, U));
		}
		return result;
	}
	
	private Set<Type> Inv(Type G, JavaTypeReference U) throws LookupException {
		Type base = G.baseType();
		Set<Type> superTypes = ST(U);
		//FIXME: Because we use a set, bugs may seem to disappear when debugging.
		//       Do we have to use a set anyway? The operations applied to it
		//       further on should work exactly the same whether there are duplicates or not
		Set<Type> result = new HashSet<Type>();
		for(Type superType: superTypes) {
			if(superType.baseType().sameAs(base)) {
				if(superType instanceof InstantiatedParameterType) {
					while(superType instanceof InstantiatedParameterType) {
						superType = ((InstantiatedParameterType) superType).aliasedType();
					}
				}
				result.add(superType);
			}
		}
		return result; 
	}
	
	private Type lci(Set<Type> types) throws LookupException {
		List<Type> list = new ArrayList<Type>(types);
		return lci(list);
	}
	
	private Type lci(List<Type> types) throws LookupException {
		int size = types.size();
		if(size > 0) {
			Type lci = types.get(0);
			for(int i = 1; i < size; i++) {
				lci = lci(lci, types.get(i));
			}
			return lci;
		} else {
			throw new ChameleonProgrammerException("The list of types to compute lci is empty.");
		}
	}
	
	private Type lci(Type first, Type second) throws LookupException {
		Type result = first;
		if(first.nbTypeParameters(TypeParameter.class) > 0) {
			result = Util.clone(first);
			result.setUniParent(first.parent());
			List<ActualTypeArgument> firstArguments = arguments(first);
			List<ActualTypeArgument> secondArguments = arguments(second);
			int size = firstArguments.size();
			if(secondArguments.size() != size) {
				throw new ChameleonProgrammerException("The number of type parameters from the first list: "+size+" is different from the number of type parameters in the second list: "+secondArguments.size());
			}
			List<TypeParameter> newParameters = lcta(firstArguments, secondArguments);
			result.replaceAllParameters(TypeParameter.class,newParameters);
		}
		return result;
	}
	
	private List<ActualTypeArgument> arguments(Type type) {
		List<TypeParameter> parameters = type.parameters(TypeParameter.class);
		List<ActualTypeArgument> result = new ArrayList<ActualTypeArgument>();
		for(TypeParameter parameter: parameters) {
			result.add(Java.argument(parameter));
		}
		return result;
	}
	
	private List<TypeParameter> lcta(List<ActualTypeArgument> firsts, List<ActualTypeArgument> seconds) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			ActualTypeArgument ith = firsts.get(i);
			Element parent = ith.parent();
			result.add(new InstantiatedTypeParameter(Util.clone(((TypeParameter)parent).signature()),lcta(ith, seconds.get(i))));
		}
		return result;
	}
	
	private ActualTypeArgument lcta(ActualTypeArgument first, ActualTypeArgument second) throws LookupException {
		ActualTypeArgument result;
		if(first instanceof BasicTypeArgument || second instanceof BasicTypeArgument) {
			if(first instanceof BasicTypeArgument && second instanceof BasicTypeArgument) {
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = Util.clone(first);
				} else {
					List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
					list.add((JavaTypeReference) ((BasicTypeArgument)first).typeReference());
					list.add((JavaTypeReference) ((BasicTypeArgument)second).typeReference());
					Java java = first.language(Java.class);
					Type leastUpperBound = leastUpperBound(list);
					result = java.createExtendsWildcard(java.reference(leastUpperBound));
				}
			} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				ExtendsWildcard ext = (ExtendsWildcard)(basic == first ? second : first);
				Java java = first.language(Java.class);
				Type leastUpperBound = leastUpperBound(typeReferenceList(basic,ext));
				result = java.createExtendsWildcard(java.reference(leastUpperBound));
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				SuperWildcard ext = (SuperWildcard)(basic == first ? second : first);
				result = first.language(Java.class).createSuperWildcard(first.language(Java.class).glb(typeReferenceList(basic,ext)));
			} else {
				result = null;
			}
		} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
			if(first instanceof ExtendsWildcard && second instanceof ExtendsWildcard) {
				List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
				list.add((JavaTypeReference) ((ExtendsWildcard)first).typeReference());
				list.add((JavaTypeReference) ((ExtendsWildcard)second).typeReference());
				Java java = first.language(Java.class);
				Type leastUpperBound = leastUpperBound(list);
				result = java.createExtendsWildcard(java.reference(leastUpperBound));
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				ExtendsWildcard ext = (ExtendsWildcard) (first instanceof ExtendsWildcard? first : second);
				SuperWildcard sup = (SuperWildcard)(ext == first ? second : first);
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = first.language(Java.class).createBasicTypeArgument(Util.clone(ext.typeReference()));
				} else {
					result = first.language(Java.class).createPureWildcard();
				}
			} else {
				result = null;
			}
		} else if (first instanceof SuperWildcard && second instanceof SuperWildcard) {
			result = first.language(Java.class).createSuperWildcard(first.language(Java.class).glb(typeReferenceList((SuperWildcard)first,(SuperWildcard)second)));
		} else {
			result = null;
		}
		if(result == null) {
		  throw new ChameleonProgrammerException("lcta is not defined for the given actual type arguments of types " + first.getClass().getName() + " and " + second.getClass().getName());
		}
		result.setUniParent(first.parent()); 
		return result;
	}

	private List<JavaTypeReference> typeReferenceList(ActualTypeArgumentWithTypeReference first, ActualTypeArgumentWithTypeReference second) throws LookupException {
		List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
		list.add((JavaTypeReference) first.typeReference());
		list.add((JavaTypeReference) second.typeReference());
		return list;
	}
	

}
