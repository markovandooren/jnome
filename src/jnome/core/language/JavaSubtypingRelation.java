/**
 * 
 */
package jnome.core.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jnome.core.expression.invocation.NonLocalJavaTypeReference;
import jnome.core.type.ArrayType;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.RawType;

import org.apache.log4j.Logger;
import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ConstructedType;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.IntersectionType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.CapturedTypeParameter;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.TypeConstraint;
import chameleon.oo.type.generics.TypeConstraintWithReferences;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.generics.WildCardType;
import chameleon.util.Pair;

public class JavaSubtypingRelation extends WeakPartialOrder<Type> {
	
	
	
	public boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		boolean result = false;
		if(first instanceof ConstructedType && second instanceof ConstructedType) {
			TypeParameter firstParam = ((ConstructedType)first).parameter();
			TypeParameter secondParam = ((ConstructedType)second).parameter();
			for(Pair<TypeParameter, TypeParameter> pair: trace) {
				if((firstParam.sameAs(pair.first()) && secondParam.sameAs(pair.second())) || (firstParam.sameAs(pair.second()) && secondParam.sameAs(pair.first())) ) {
					return true;
				}
			}
			trace.add(new Pair<TypeParameter, TypeParameter>(firstParam, secondParam));
		}
		if(first.equals(second)) {
			result = true;
		} else if (first.equals(first.language(ObjectOrientedLanguage.class).getNullType())) {
			result = true;
		} else if (first instanceof WildCardType) {
			result = contains(((WildCardType)first).upperBound(), second);
		} else if (second instanceof WildCardType) {
			result = contains(first, ((WildCardType)second).lowerBound());
		}
		// The relations between arrays and object are covered by the subtyping relations
		// that are added to ArrayType objects.
		else if (first instanceof ArrayType && second instanceof ArrayType && first.is(first.language(Java.class).REFERENCE_TYPE) == Ternary.TRUE) {
			ArrayType first2 = (ArrayType)first;
			ArrayType second2 = (ArrayType)second;
			result = contains(first2.elementType(), second2.elementType());
		} else if(second instanceof IntersectionType) {
			List<Type> types = ((IntersectionType)second).types();
			int size = types.size();
			result = size > 0;
			for(int i=0; result && i<size;i++) {
				result = contains(first,types.get(i));
			}
		} else if(first instanceof IntersectionType) {
			List<Type> types = ((IntersectionType)first).types();
			int size = types.size();
			result = false;
			for(int i=0; (!result) && i<size;i++) {
				result = contains(types.get(i),second);
			}
		}
		else {
			//SPEED iterate over the supertype graph 
//			if(! (second instanceof ConstructedType)) {
			
			
		  Type captured = captureConversion(first);
			Set<Type> supers = captured.getAllSuperTypes();
			supers.add(captured);
			
//				Set<Type> supers = first.getAllSuperTypes();
//				supers.add(first);
				
			Iterator<Type> typeIterator = supers.iterator();
			while((!result) && typeIterator.hasNext()) {
				Type current = typeIterator.next();
				result = (second instanceof RawType && second.baseType().sameAs(current.baseType()))|| sameBaseTypeWithCompatibleParameters(current, second, trace);
			}
			//			}
		}
		return result;
	}
	
	private static Logger _logger = Logger.getLogger("lookup.subtyping");
	
	public static Logger getLogger() {
		return _logger;
	}
	
	// Can't use set for now because hashCode is not OK.
	private Map<Type, Set<Type>> _cache = new HashMap<Type,Set<Type>>();
	
	@Override
	public boolean contains(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.equals(second)) {
			result = true;
		} else if (first.equals(first.language(ObjectOrientedLanguage.class).getNullType())) {
			result = true;
		} else if (first instanceof WildCardType) {
			result = contains(((WildCardType)first).upperBound(), second);
		} else if (second instanceof WildCardType) {
			result = contains(first, ((WildCardType)second).lowerBound());
		}
		// The relations between arrays and object are covered by the subtyping relations
		// that are added to ArrayType objects.
		else if (first instanceof ArrayType && second instanceof ArrayType && first.is(first.language(Java.class).REFERENCE_TYPE) == Ternary.TRUE) {
			ArrayType first2 = (ArrayType)first;
			ArrayType second2 = (ArrayType)second;
			result = contains(first2.elementType(), second2.elementType());
		} else if(second instanceof IntersectionType) {
			List<Type> types = ((IntersectionType)second).types();
			int size = types.size();
			result = size > 0;
			for(int i=0; result && i<size;i++) {
				result = contains(first,types.get(i));
			}
		} else if(first instanceof IntersectionType) {
			List<Type> types = ((IntersectionType)first).types();
			int size = types.size();
			result = false;
			for(int i=0; (!result) && i<size;i++) {
				result = contains(types.get(i),second);
			}
		}
		else {
			//SPEED iterate over the supertype graph 
//			if(! (second instanceof ConstructedType)) {
			
			  Type captured = captureConversion(first);
				Set<Type> supers = captured.getAllSuperTypes();
				supers.add(captured);
				
//				Set<Type> supers = first.getAllSuperTypes();
//				supers.add(first);
				
				Iterator<Type> typeIterator = supers.iterator();
				while((!result) && typeIterator.hasNext()) {
					Type current = typeIterator.next();
					result = (second instanceof RawType && second.baseType().sameAs(current.baseType()))|| sameBaseTypeWithCompatibleParameters(current, second, new ArrayList());
				}
//			}
		}
//		if(Config.cacheElementProperties()) {
//			Set<Type> superTypes = _cache.get(first);
//			if(superTypes == null) {
//				superTypes = new HashSet<Type>();
//				_cache.put(first, superTypes);
//			}
//			superTypes.add(second);
//		}
		return result;
	}
	
	public Type captureConversion(Type type) throws LookupException {
		// create a derived type
		Type result = type;
		if(result instanceof DerivedType) {
			List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
			Type base = type.baseType();
			Iterator<TypeParameter> formals = base.parameters().iterator();
			List<TypeParameter> actualParameters = type.parameters();
			Iterator<TypeParameter> actuals = actualParameters.iterator();
			// substitute parameters by their capture bounds.
			while(actuals.hasNext()) {
				TypeParameter formalParam = formals.next();
				if(!(formalParam instanceof FormalTypeParameter)) {
					throw new LookupException("Type parameter of base type is not a formal parameter.");
				}
				TypeParameter actualParam = actuals.next();
				if(!(actualParam instanceof InstantiatedTypeParameter)) {
					throw new LookupException("Type parameter of type instantiation is not an instantiated parameter.");
				}
				typeParameters.add(((InstantiatedTypeParameter) actualParam).capture((FormalTypeParameter) formalParam));
			}
			result = new DerivedType(typeParameters, base);
			result.setUniParent(type.parent());
			for(TypeParameter newParameter: typeParameters) {
				for(TypeParameter oldParameter: actualParameters) {
					JavaTypeReference tref = new BasicJavaTypeReference(oldParameter.signature().name());
					if(newParameter instanceof CapturedTypeParameter) {
						List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
						for(TypeConstraint constraint : constraints) {
							replace(tref, oldParameter, (JavaTypeReference<?>) ((TypeConstraintWithReferences)constraint).typeReference());
						}
					} else {
						TypeReference t = ((BasicTypeArgument)((InstantiatedTypeParameter)newParameter).argument()).typeReference();
						replace(tref, oldParameter, (JavaTypeReference<?>) t);
					}
				}
			}
		}
		return result;
	}
	
	private void replace(JavaTypeReference replacement, final Declaration declarator, JavaTypeReference<?> in) throws LookupException {
		List<BasicJavaTypeReference> crefs = in.descendants(BasicJavaTypeReference.class, 
				new UnsafePredicate<BasicJavaTypeReference, LookupException>() {
			@Override
			public boolean eval(BasicJavaTypeReference object) throws LookupException {
				return object.getDeclarator().sameAs(declarator);
			}
		});
		for(BasicJavaTypeReference cref: crefs) {
			JavaTypeReference substitute;
			if(replacement.isDerived()) {
			  substitute = new CaptureReference(replacement.clone());
			} else {
			  substitute = new CaptureReference(replacement.clone());
			}
			SingleAssociation crefParentLink = cref.parentLink();
			crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
		}
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
		public CaptureReference clone() {
			return new CaptureReference(actualReference().clone());
		}
		
	}

	public boolean sameBaseTypeWithCompatibleParameters(Type first, Type second, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		boolean result = false;
		if(first.baseType().equals(second.baseType())) {
			result = compatibleParameters(first, second, trace);// || rawType(second); equality in formal parameter should take care of this.
		}
		return result;
	}
	
	public boolean rawType(Type type) {
		for(TypeParameter parameter: type.parameters()) {
			if(! (parameter instanceof FormalTypeParameter)) {
				return false;
			}
		}
		return true;
	}

	private boolean compatibleParameters(Type first, Type second, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		boolean result;
		List<TypeParameter> firstFormal= first.parameters();
		List<TypeParameter> secondFormal= second.parameters();
		result = true;
		Iterator<TypeParameter> firstIter = firstFormal.iterator();
		Iterator<TypeParameter> secondIter = secondFormal.iterator();
		while(result && firstIter.hasNext()) {
			TypeParameter firstParam = firstIter.next();
			TypeParameter secondParam = secondIter.next();
			result = firstParam.compatibleWith(secondParam, trace);
		}
		return result;
	}
}