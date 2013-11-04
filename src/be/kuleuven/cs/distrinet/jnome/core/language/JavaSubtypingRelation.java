/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.language.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeIndirection;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.UnionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.FormalTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.LazyInstantiatedAlias;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.WildCardType;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaDerivedType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.NullType;
import be.kuleuven.cs.distrinet.jnome.core.type.PureWildcard;
import be.kuleuven.cs.distrinet.jnome.core.type.RawType;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class JavaSubtypingRelation extends SubtypeRelation {

	public JavaSubtypingRelation(Java java) {
		_java = java;
	}

	private Java _java;

	public Java java() {
		return _java;
	}

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
		else if (first instanceof ArrayType && second instanceof ArrayType && first.is(java().REFERENCE_TYPE) == Ternary.TRUE) {
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
			Set<Type> supers = first.getSelfAndAllSuperTypesView();
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
		if(result instanceof JavaDerivedType) {
			result = ((JavaDerivedType)result).captureConversion();
//			List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
//			if(! (type.parameter(TypeParameter.class,1) instanceof CapturedTypeParameter)) {
//				Type base = type.baseType();
//				List<TypeParameter> baseParameters = base.parameters(TypeParameter.class);
//				Iterator<TypeParameter> formals = baseParameters.iterator();
//				List<TypeParameter> actualParameters = type.parameters(TypeParameter.class);
//				Iterator<TypeParameter> actuals = actualParameters.iterator();
//				// substitute parameters by their capture bounds.
//				// ITERATOR because we iterate over 'formals' and 'actuals' simultaneously.
//				List<TypeConstraint> toBeSubstituted = new ArrayList<TypeConstraint>();
//				while(actuals.hasNext()) {
//					TypeParameter formalParam = formals.next();
//					if(!(formalParam instanceof FormalTypeParameter)) {
//						throw new LookupException("Type parameter of base type is not a formal parameter.");
//					}
//					TypeParameter actualParam = actuals.next();
//					if(!(actualParam instanceof InstantiatedTypeParameter)) {
//						throw new LookupException("Type parameter of type instantiation is not an instantiated parameter: "+actualParam.getClass().getName());
//					}
//					typeParameters.add(((InstantiatedTypeParameter) actualParam).capture((FormalTypeParameter) formalParam,toBeSubstituted));
//				}
//				result = java().createdCapturedType(new ParameterSubstitution<>(TypeParameter.class,typeParameters), base);
//				result.setUniParent(type.parent());
//				for(TypeParameter newParameter: typeParameters) {
//					for(TypeParameter oldParameter: baseParameters) {
//						JavaTypeReference tref = new BasicJavaTypeReference(oldParameter.signature().name());
//						tref.setUniParent(newParameter);
//						if(newParameter instanceof CapturedTypeParameter) {
//							List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
//							for(TypeConstraint constraint : constraints) {
//								if(toBeSubstituted.contains(constraint)) {
//									NonLocalJavaTypeReference.replace(tref, oldParameter, (JavaTypeReference) constraint.typeReference());
//								}
//							}
//						} else {
//							throw new ChameleonProgrammerException();
//						}
//					}
//				}
//			}
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
		//		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
		List<Pair<Type, TypeParameter>> slowTrace = trace;
		boolean result = false;
		if(first.baseType().sameAs(second.baseType())) {
			result = compatibleParameters(first, second, slowTrace);// || rawType(second); equality in formal parameter should take care of this.
		} else {
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
		//		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
		List<Pair<Type, TypeParameter>> slowTrace = trace;
		boolean result;
		List<TypeParameter> firstFormal= first.parameters(TypeParameter.class);
		List<TypeParameter> secondFormal= second.parameters(TypeParameter.class);
		result = true;
		int size = firstFormal.size();
		for(int i=0; result && i < size; i++) {
			result = firstFormal.get(i).compatibleWith(secondFormal.get(i), slowTrace);
		}
		//		Iterator<TypeParameter> firstIter = firstFormal.iterator();
		//		Iterator<TypeParameter> secondIter = secondFormal.iterator();
		//		while(result && firstIter.hasNext()) {
		//			TypeParameter firstParam = firstIter.next();
		//			TypeParameter secondParam = secondIter.next();
		//			result = firstParam.compatibleWith(secondParam, slowTrace);
		//		}
		return result;
	}

	//	private Type leastUpperBoundRecursive(List<? extends TypeReference> Us, List<List<? extends TypeReference>> trace) throws LookupException {
	//		return leastUpperBound(Us, trace);
	//	}
	//	private Type leastUpperBound(List<? extends TypeReference> Us, List<List<? extends TypeReference>> trace) throws LookupException {
	//		_tracer.push();
	//		List<Type> MEC = new ArrayList<Type>(MEC((List<? extends JavaTypeReference>) Us));
	//		List<Type> candidates = new ArrayList<Type>();
	//		for(Type W:MEC) {
	//			candidates.add(Candidate(W,(List<? extends JavaTypeReference>) Us));
	//		}
	//		_tracer.pop();
	//		return intersection(candidates);
	//	}


	public Type leastUpperBound(List<? extends TypeReference> Us, Binder root) throws LookupException {
		List<Type> MEC = new ArrayList<Type>(MEC((List<? extends JavaTypeReference>) Us));
		List<Type> candidates = new ArrayList<Type>();
		for(Type W:MEC) {
			candidates.add(Candidate(W,(List<? extends JavaTypeReference>) Us,root));
		}
		return intersection(candidates);
	}


	@Override
	public Type leastUpperBound(List<? extends TypeReference> Us) throws LookupException {
		return leastUpperBound(Us, null);
		//		_tracer.push();
		//		List<Type> MEC = new ArrayList<Type>(MEC((List<? extends JavaTypeReference>) Us));
		//		List<Type> candidates = new ArrayList<Type>();
		//		for(Type W:MEC) {
		//			candidates.add(Candidate(W,(List<? extends JavaTypeReference>) Us));
		//		}
		//		_tracer.pop();
		//		return intersection(candidates);
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
			Type erasure = java().erasure(type);
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
		return U.getElement().getSelfAndAllSuperTypesView();
	}

	private Type CandidateInvocation(Type G, List<? extends JavaTypeReference> Us, Binder root) throws LookupException {
		return lci(Inv(G,Us),root);
	}

	private Type Candidate(Type W, List<? extends JavaTypeReference> Us, Binder root) throws LookupException {
		if(W.parameters(TypeParameter.class).size() > 0) {
			return CandidateInvocation(W, Us,root);
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
				while(superType instanceof InstantiatedParameterType) {
					superType = ((InstantiatedParameterType) superType).aliasedType();
				}
				result.add(superType);
			}
		}
		return result; 
	}

	private Type lci(Set<Type> types, Binder root) throws LookupException {
		List<Type> list = new ArrayList<Type>(types);
		return lci(list,root);
	}

	private Type lci(List<Type> types, Binder root) throws LookupException {
		int size = types.size();
		if(size > 0) {
			Type lci = types.get(0);
			for(int i = 1; i < size; i++) {
				lci = lci(lci, types.get(i),root);
			}
			return lci;
		} else {
			throw new ChameleonProgrammerException("The list of types to compute lci is empty.");
		}
	}

	private Type lci(Type first, Type second, Binder root) throws LookupException {
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
			List<TypeParameter> newParameters = lcta(firstArguments, secondArguments,root);
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

	private List<TypeParameter> lcta(List<ActualTypeArgument> firsts, List<ActualTypeArgument> seconds, Binder root) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			ActualTypeArgument ith = firsts.get(i);
			Element parent = ith.parent();
			result.add(new InstantiatedTypeParameter(((TypeParameter)parent).name(),lcta(ith, seconds.get(i),root)));
		}
		return result;
	}

	private ActualTypeArgument lcta(ActualTypeArgument first, ActualTypeArgument second, Binder root) throws LookupException { // , List<List<? extends TypeReference>> trace
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
					result = new Binder(list,root).argument();
				}
			} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				ExtendsWildcard ext = (ExtendsWildcard)(basic == first ? second : first);
				//				Type leastUpperBound = leastUpperBound(typeReferenceList(basic,ext));
				result = new Binder(typeReferenceList(basic,ext),root).argument();
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				SuperWildcard ext = (SuperWildcard)(basic == first ? second : first);
				result = java().createSuperWildcard(java().glb(typeReferenceList(basic,ext)));
			} else {
				result = null;
			}
		} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
			if(first instanceof ExtendsWildcard && second instanceof ExtendsWildcard) {
				List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
				list.add((JavaTypeReference) ((ExtendsWildcard)first).typeReference());
				list.add((JavaTypeReference) ((ExtendsWildcard)second).typeReference());
				//				Java java = first.language(Java.class);
				//				Type leastUpperBound = leastUpperBound(list);
				//				result = java.createExtendsWildcard(java.reference(leastUpperBound));
				result = new Binder(list,root).argument();

			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				ExtendsWildcard ext = (ExtendsWildcard) (first instanceof ExtendsWildcard? first : second);
				SuperWildcard sup = (SuperWildcard)(ext == first ? second : first);
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = java().createBasicTypeArgument(Util.clone(ext.typeReference()));
				} else {
					result = java().createPureWildcard();
				}
			} else {
				result = null;
			}
		} else if (first instanceof SuperWildcard && second instanceof SuperWildcard) {
			result = java().createSuperWildcard(java().glb(typeReferenceList((SuperWildcard)first,(SuperWildcard)second)));
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


	private class Binder {

		public Binder(List<? extends JavaTypeReference> refs, Binder next) {
			_refs = ImmutableList.copyOf(refs);
			_next = next;
		}

		public ActualTypeArgument argument() throws LookupException {
			return argument(_refs, this);
		}

		private boolean _active;

		private boolean _looped;

		protected ActualTypeArgument argument(List<? extends JavaTypeReference> refs, Binder root) throws LookupException {
			if(_active && equal(refs,_refs)) {
				_looped = true;
				return loop();
			} else if(_next != null) {
				_active = true;
				return _next.argument(refs, root);
			} else {
				_active = true;
				Type leastUpperBound = leastUpperBound(refs, root);
				if(!_looped) {
					return createArgument(leastUpperBound);
				} else {
					return loop();
				}

			}
		}

		private boolean equal(List<? extends JavaTypeReference> first, List<? extends JavaTypeReference> second) throws LookupException {
			int size = first.size();
			boolean result = (size == second.size());
			if(result){
				List<Type> Tfirst = new ArrayList<>();
				for(int i=0; i<size;i++) {
					Tfirst.add(first.get(i).getElement());
				}
				for(int i=0; i<size;i++) {
					if(! Tfirst.contains(second.get(i).getElement())) {
						result = false;
						break;
					}
				}
			}
			return result;
		}

		protected ActualTypeArgument loop() {
			return new PureWildcard();
		}

		protected ActualTypeArgument createArgument(Type type) throws LookupException {
			return java().createExtendsWildcard(java().reference(type));
		}

		private Binder _next;

		private List<? extends JavaTypeReference> _refs;
	}

	public static class UncheckedConversionIndicator {

		public void set() {
			_set=true;
		}

		public boolean isSet() {
			return _set;
		}

		private boolean _set;
	}

	public boolean convertibleThroughUncheckedConversionAndSubtyping(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first instanceof RawType) {
			result = ((RawType)first).convertibleThroughUncheckedConversionAndSubtyping(second);
		} else if(first instanceof ArrayType && second instanceof ArrayType) {
			ArrayType first2 = (ArrayType)first;
			ArrayType second2 = (ArrayType)second;
			result = convertibleThroughUncheckedConversionAndSubtyping(first2.elementType(), second2.elementType());
		} else {
			Set<Type> supers = first.getSelfAndAllSuperTypesView();
			for(Type type: supers) {
				if(type.baseType().sameAs(second.baseType())) {
					return true;
				}
			}
		}
		return result;
	}

	public boolean convertibleThroughMethodInvocationConversion(Type first, Type second, UncheckedConversionIndicator indicator) throws LookupException {
		boolean result = false;
		// JLS 4.1 & JLS 4.10 : Null type is convertible to any reference type.
		// FIXME Bad design! Delegating reference widening to the type itself would get rid
		// of this stupid case.
		boolean uncheckedConversion = false;
		if(first instanceof NullType) {
			return second.isTrue(java().REFERENCE_TYPE);
		}
		if(first instanceof TypeIndirection) {
			return convertibleThroughMethodInvocationConversion(((TypeIndirection)first).aliasedType(), second, indicator);
		} else if(second instanceof TypeIndirection) {
			return convertibleThroughMethodInvocationConversion(first, ((TypeIndirection)second).aliasedType(), indicator);
		}

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
					if(convertibleThroughUncheckedConversionAndSubtyping(type, second)) {
						uncheckedConversion = true;
						result = true;
						break;
					}
				}
				if(! result) {
					// FIXME is this still necessary? first has already been added to the previous check G) unchecked conversion after identity
					result = convertibleThroughUncheckedConversionAndSubtyping(first, second);
					if(result) {
						uncheckedConversion = true;
					}
				}
			}
		}
		if(uncheckedConversion) {
			indicator.set();
		}
		return result;
	}


	public boolean convertibleThroughBoxingAndOptionalWidening(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.is(java().PRIMITIVE_TYPE) == Ternary.TRUE) {
			Type tmp = java().box(first);
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
		if(first.is(java().UNBOXABLE_TYPE) == Ternary.TRUE) {
			Type tmp = java().unbox(first);
			if(tmp.sameAs(second)) {
				result = true;
			} else {
				result = convertibleThroughWideningPrimitiveConversion(tmp, second);
			}
		}
		return result;
	}

	public boolean convertibleThroughWideningReferenceConversion(Type first, Type second) throws LookupException {
		Collection<Type> referenceWideningConversionCandidates = referenceWideningConversionCandidates(first);
		for(Type type: referenceWideningConversionCandidates) {
			if(type.sameAs(second) || type.subTypeOf(second)) {
				return true;
			}
		}
		return false;
	}

	public Collection<Type> referenceWideningConversionCandidates(Type type) throws LookupException {
		return type.getSelfAndAllSuperTypesView();
	}

	public boolean convertibleThroughWideningPrimitiveConversion(Type first, Type second) throws LookupException {
		return primitiveWideningConversionCandidates(first).contains(second);
	}

	public Collection<Type> primitiveWideningConversionCandidates(Type type) throws LookupException {
		if(_primitiveWideningConversionCandidates == null) {
			_primitiveWideningConversionCandidates = new HashMap<>();
			JavaView view = type.view(JavaView.class);
			Type pDouble = view.primitiveType("double");
			Type pFloat = view.primitiveType("float");
			Type pLong = view.primitiveType("long");
			Type pInt = view.primitiveType("int");
			Type pByte = view.primitiveType("byte");
			Type pShort = view.primitiveType("short");
			Type pChar = view.primitiveType("char");

			Builder<Type> builder = ImmutableList.<Type>builder();

			builder.add(pDouble);
			//{double}
			ImmutableList<Type> list = builder.build();
			_primitiveWideningConversionCandidates.put(pFloat, list);

			builder.add(pFloat);
			//{float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pLong, list);

			builder.add(pLong);
			//{long,float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pInt, list);

			builder.add(pInt);
			//{int,long,float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pChar, list);
			_primitiveWideningConversionCandidates.put(pShort, list);

			builder.add(pShort);
			//{short,int,long,float,double}
			list = builder.build();
			_primitiveWideningConversionCandidates.put(pByte, list);
		}
		List<Type> result = _primitiveWideningConversionCandidates.get(type);
		if(result == null) {
			result = ImmutableList.of();
		}
		return result;

		//		Collection<Type> result = new ArrayList<Type>();
		//		View view = type.view();
		//		String name = type.getFullyQualifiedName();
		//		Namespace ns = view.namespace();
		//		if(type.is(java().NUMERIC_TYPE) == Ternary.TRUE) {
		//			if(! name.equals("double")) {
		//				result.add(java().findType("double",ns));
		//				if(! name.equals("float")) {
		//					result.add(java().findType("float",ns));
		//					if(! name.equals("long")) {
		//						result.add(java().findType("long",ns));
		//						if(! name.equals("int")) {
		//							result.add(java().findType("int",ns));
		//							// char and short do not convert to short via widening.
		//							if(name.equals("byte")) {
		//								result.add(java().findType("short",ns));
		//							}
		//						}
		//					}
		//				}
		//			}
		//		}
		//		return result;
	}

	private Map<Type, List<Type>> _primitiveWideningConversionCandidates;


}
