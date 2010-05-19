/**
 * 
 */
package jnome.core.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jnome.core.expression.invocation.NonLocalJavaTypeReference;
import jnome.core.type.ArrayType;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.CapturedType;
import jnome.core.type.JavaTypeReference;
import jnome.core.type.NullType;
import jnome.core.type.RawType;

import org.apache.log4j.Logger;
import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ConstructedType;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.IntersectionType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.UnionType;
import chameleon.oo.type.generics.ActualType;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.CapturedTypeParameter;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.TypeConstraint;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.generics.WildCardType;
import chameleon.util.Pair;

public class JavaSubtypingRelation extends WeakPartialOrder<Type> {
	
	
	
	public boolean upperBoundNotHigherThan(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = trace;
	boolean result = false;
		if(first instanceof NullType) {
			result = true;
		} else {
			if(second instanceof ActualType) {
				TypeParameter secondParam = ((ActualType)second).parameter();
				for(Pair<Type, TypeParameter> pair: slowTrace) {
					if(first.sameAs(pair.first()) && secondParam.sameAs(pair.second())) {
//						System.out.println("Match: true");
						return true;
					}
				}
				slowTrace.add(new Pair<Type, TypeParameter>(first, secondParam));
			}
			if(first instanceof ActualType && second instanceof ConstructedType) {
				TypeParameter firstParam = ((ActualType)first).parameter();
				for(Pair<Type, TypeParameter> pair: slowTrace) {
					if(firstParam.sameAs(pair.second()) && second.sameAs(pair.first()))
					{
//						System.out.println("Match: true");
						return true;
					}
				}
				slowTrace.add(new Pair<Type, TypeParameter>(second, firstParam));
			}
			if(first.equals(second)) {
				result = true;
			} else if(first instanceof ActualType) {
				result = upperBoundNotHigherThan(((ActualType) first).aliasedType(), second, slowTrace);
			} else if(second instanceof ActualType) {
				result = upperBoundNotHigherThan(first, ((ActualType) second).aliasedType(), slowTrace);
			}	else if (first.equals(first.language(ObjectOrientedLanguage.class).getNullType())) {
				result = true;
			} else if (first instanceof WildCardType) {
				result = upperBoundNotHigherThan(((WildCardType)first).upperBound(), second,slowTrace);
			} else if (second instanceof WildCardType) {
				result = upperBoundNotHigherThan(first, ((WildCardType)second).lowerBound(),slowTrace);
			}
			// The relations between arrays and object are covered by the subtyping relations
			// that are added to ArrayType objects.
			else if (first instanceof ArrayType && second instanceof ArrayType && first.is(first.language(Java.class).REFERENCE_TYPE) == Ternary.TRUE) {
				ArrayType first2 = (ArrayType)first;
				ArrayType second2 = (ArrayType)second;
				result = upperBoundNotHigherThan(first2.elementType(), second2.elementType(),slowTrace);
			} else if(second instanceof IntersectionType) {
				List<Type> types = ((IntersectionType)second).types();
				int size = types.size();
				result = size > 0;
				for(int i=0; result && i<size;i++) {
					result = upperBoundNotHigherThan(first,types.get(i),slowTrace);
				}
			} else if(first instanceof IntersectionType) {
				List<Type> types = ((IntersectionType)first).types();
				int size = types.size();
				result = false;
				for(int i=0; (!result) && i<size;i++) {
					result = upperBoundNotHigherThan(types.get(i),second,slowTrace);
				}
			} else if(second instanceof UnionType) {
				List<Type> types = ((UnionType)second).types();
				int size = types.size();
				result = false;
				for(int i=0; (!result) && i<size;i++) {
					result = upperBoundNotHigherThan(first,types.get(i),slowTrace);
				}
			} else if(first instanceof UnionType) {
				List<Type> types = ((UnionType)first).types();
				int size = types.size();
				result = size > 0;
				for(int i=0; result && i<size;i++) {
					result = upperBoundNotHigherThan(types.get(i),second,slowTrace);
				}
			}
			else {
				//SPEED iterate over the supertype graph 
				Set<Type> supers = getAllSuperTypes(first);
				Type snd = captureConversion(second);

				Iterator<Type> typeIterator = supers.iterator();
				while((!result) && typeIterator.hasNext()) {
					Type current = typeIterator.next();
					result = (snd instanceof RawType && second.baseType().sameAs(current.baseType())) || sameBaseTypeWithCompatibleParameters(current, snd, slowTrace);
				}
			}
		}
//		System.out.println("Match: "+result);
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
		if(first instanceof NullType) {
			result = true;
		} else {
			if(first.equals(second)) {
				result = true;
			}
			else if(first instanceof ActualType) {
				result = contains(((ActualType) first).aliasedType(),second);
			} else if(second instanceof ActualType) {
				result = contains(first,((ActualType) second).aliasedType());
			}
			 else if (first.equals(first.language(ObjectOrientedLanguage.class).getNullType())) {
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
			} else if(second instanceof UnionType) {
				List<Type> types = ((UnionType)first).types();
				int size = types.size();
				result = false;
				for(int i=0; (!result) && i<size;i++) {
					result = contains(first,types.get(i));
				}
			} else if(first instanceof UnionType) {
				List<Type> types = ((UnionType)second).types();
				int size = types.size();
				result = size > 0;
				for(int i=0; result && i<size;i++) {
					result = contains(types.get(i),second);
				}
			}
			else {
					//SPEED iterate over the supertype graph 
					Set<Type> supers = getAllSuperTypes(first);
					Type snd = captureConversion(second);

					Iterator<Type> typeIterator = supers.iterator();
					while((!result) && typeIterator.hasNext()) {
						Type current = typeIterator.next();
						result = (snd instanceof RawType && second.baseType().sameAs(current.baseType())) || sameBaseTypeWithCompatibleParameters(current, snd, new ArrayList());
					}
			}
		}
		return result;
	}
	
	public Type captureConversion(Type type) throws LookupException {
		// create a derived type
		Type result = type;
		if(result instanceof DerivedType) {
			List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
			if(! (type.parameter(1) instanceof CapturedTypeParameter)) {
				Type base = type.baseType();
				List<TypeParameter> baseParameters = base.parameters();
				Iterator<TypeParameter> formals = baseParameters.iterator();
				List<TypeParameter> actualParameters = type.parameters();
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
				result = new CapturedType(typeParameters, base);
				result.setUniParent(type.parent());
				for(TypeParameter newParameter: typeParameters) {
					for(TypeParameter oldParameter: baseParameters) {
						JavaTypeReference<?> tref = new BasicJavaTypeReference(oldParameter.signature().name());
						tref.setUniParent(newParameter);
						if(newParameter instanceof CapturedTypeParameter) {
							List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
							for(TypeConstraint constraint : constraints) {
								if(toBeSubstituted.contains(constraint)) {
									NonLocalJavaTypeReference.replace(tref, oldParameter, (JavaTypeReference<?>) constraint.typeReference());
								}
//								replace(tref, oldParameter, (JavaTypeReference<?>) constraint.typeReference());
							}
						} else {
							throw new Error();
//							TypeReference t = ((BasicTypeArgument)((InstantiatedTypeParameter)newParameter).argument()).typeReference();
//							replace(tref, oldParameter, (JavaTypeReference<?>) t);
						}
					}
				}
			}
		}
		return result;
	}
	
//	private void replace(JavaTypeReference replacement, final Declaration declarator, JavaTypeReference<?> in) throws LookupException {
//		UnsafePredicate<BasicJavaTypeReference, LookupException> predicate = new UnsafePredicate<BasicJavaTypeReference, LookupException>() {
//@Override
//public boolean eval(BasicJavaTypeReference object) throws LookupException {
//		return object.getDeclarator().sameAs(declarator);
//}
//};
//		List<BasicJavaTypeReference> crefs = in.descendants(BasicJavaTypeReference.class, 
//				predicate);
//		if(in instanceof BasicJavaTypeReference) {
//			BasicJavaTypeReference in2 = (BasicJavaTypeReference) in;
//			if(predicate.eval(in2)) {
//				crefs.add(in2);
//			}
//		}
// 		for(BasicJavaTypeReference cref: crefs) {
// 			System.out.println("Capture replacing reference to parameter "+((SimpleNameSignature)cref.signature()).name() + " of "+cref.getElement().nearestAncestor(Type.class).getFullyQualifiedName());
//			JavaTypeReference substitute;
//			if(replacement.isDerived()) {
//			  substitute = new CaptureReference(replacement.clone());
//			} else {
//			  substitute = new CaptureReference(replacement.clone());
//			}
//			SingleAssociation crefParentLink = cref.parentLink();
//			crefParentLink.getOtherRelation().replace(crefParentLink, substitute.parentLink());
//		}
//	}

	
//	public static class CaptureReference extends NonLocalJavaTypeReference {
//
//		public CaptureReference(JavaTypeReference tref) {
//			super(tref,null);
//		}
//
//		@Override
//		public Element lookupParent() {
//			return nearestAncestor(TypeParameter.class);
//		}
//
//		@Override
//		public CaptureReference clone() {
//			return new CaptureReference(actualReference().clone());
//		}
//		
//	}

	public boolean sameBaseTypeWithCompatibleParameters(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
//		List<Pair<TypeParameter, TypeParameter>> slowTrace = trace;
		boolean result = false;
		if(first.baseType().equals(second.baseType())) {
			result = compatibleParameters(first, second, slowTrace);// || rawType(second); equality in formal parameter should take care of this.
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

	private boolean compatibleParameters(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
		List<Pair<Type, TypeParameter>> slowTrace = new ArrayList<Pair<Type, TypeParameter>>(trace);
//		List<Pair<TypeParameter, TypeParameter>> slowTrace = trace;
		boolean result;
		List<TypeParameter> firstFormal= first.parameters();
		List<TypeParameter> secondFormal= second.parameters();
		result = true;
		Iterator<TypeParameter> firstIter = firstFormal.iterator();
		Iterator<TypeParameter> secondIter = secondFormal.iterator();
		while(result && firstIter.hasNext()) {
			TypeParameter<?> firstParam = firstIter.next();
			TypeParameter<?> secondParam = secondIter.next();
			result = firstParam.compatibleWith(secondParam, slowTrace);
		}
		return result;
	}
	
  public Set<Type> getAllSuperTypes(Type type) throws LookupException {
  	Set<Type> result = _superTypeCache.get(type);
  	if(result == null) {
  		result = new HashSet<Type>();
   		accumulateAllSuperTypes(type, result);
   		_superTypeCache.put(type,result);
  	}
  	result = new HashSet<Type>(result);
  	return result;
//  	Set<Type> result = new HashSet<Type>();
//  	accumulateAllSuperTypes(type, result);
//  	return result;
  }

  private Map<Type,Set<Type>> _superTypeCache = new HashMap<Type, Set<Type>>();

  private void accumulateAllSuperTypes(Type t, Set<Type> acc) throws LookupException {
  	if(t instanceof DerivedType) {
  		t = captureConversion(t);
  	}
  	acc.add(t);
  	List<Type> temp = t.getDirectSuperTypes();
//  	acc.addAll(temp);
//  	for(Type type:temp) {
//  		  type.accumulateAllSuperTypes(acc);
//  	}
  	for(Type type:temp) {
//  		if(! acc.contains(type)) {
//  			acc.add(type);
  		  accumulateAllSuperTypes(type,acc);
//  		}
  	}
  }
  
//  public List<Type> getDirectSuperTypes(Type type) throws LookupException {
//  	return type.getDirectSuperTypes();
//  }


}