/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.relation.WeakPartialOrder;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.DerivedType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.ParameterSubstitution;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.UnionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.CapturedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.LazyInstantiatedAlias;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeConstraint;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.WildCardType;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

public class JavaSubtypingRelation extends WeakPartialOrder<Type> {
	
	
	
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
	

}
