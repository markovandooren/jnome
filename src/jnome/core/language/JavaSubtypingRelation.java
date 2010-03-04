/**
 * 
 */
package jnome.core.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import chameleon.core.lookup.LookupException;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.ConstructedType;
import chameleon.core.type.DerivedType;
import chameleon.core.type.Type;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.TypeParameter;
import chameleon.oo.language.ObjectOrientedLanguage;

public class JavaSubtypingRelation extends WeakPartialOrder<Type> {
	
	private static Logger _logger = Logger.getLogger("lookup.subtyping");
	
	public static Logger getLogger() {
		return _logger;
	}
	
	@Override
	public boolean contains(Type first, Type second) throws LookupException {
		// OPTIMIZE
		//getLogger().debug("Subtype check: "+first.getFullyQualifiedName()+" <: " + second.getFullyQualifiedName());
		boolean result = false;
		if(first.equals(second)) {
			result = true;
		} else if (first.equals(first.language(ObjectOrientedLanguage.class).getNullType())) {
			return true;
		}	else {
		Set<Type> supers = first.getAllSuperTypes();
		supers.add(first);
		Iterator<Type> typeIterator = supers.iterator();
		if(! (second instanceof ConstructedType)) {
		  while((!result) && typeIterator.hasNext()) {
			  Type current = typeIterator.next();
			  result = sameBaseTypeWithCompatibleParameters(current, second);
		   }
		}
		}
		return result;
	}
	
	public boolean subtypeMatch(Type first, Type second) throws LookupException {
		return sameBaseTypeWithCompatibleParameters(captureConversion(first), second);
	}
	
	public Type captureConversion(Type type) throws LookupException {
		// create a derived type
		List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
		Type base = type.baseType();
		Iterator<TypeParameter> formals = base.parameters().iterator();
		Iterator<TypeParameter> actuals = type.parameters().iterator();
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
		Type result = new DerivedType(typeParameters, type);
		return result;
	}

	public boolean sameBaseTypeWithCompatibleParameters(Type first, Type second) throws LookupException {
		boolean result = false;
		if(first.baseType().equals(second.baseType())) {
			result = compatibleParameters(first, second);// || rawType(second); equality in formal parameter should take care of this.
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

	private boolean compatibleParameters(Type first, Type second) throws LookupException {
		boolean result;
		List<TypeParameter> firstFormal= first.parameters();
		List<TypeParameter> secondFormal= second.parameters();
		result = true;
		Iterator<TypeParameter> firstIter = firstFormal.iterator();
		Iterator<TypeParameter> secondIter = secondFormal.iterator();
		while(result && firstIter.hasNext()) {
			TypeParameter firstParam = firstIter.next();
			TypeParameter secondParam = secondIter.next();
			result = firstParam.compatibleWith(secondParam);
		}
		return result;
	}
}