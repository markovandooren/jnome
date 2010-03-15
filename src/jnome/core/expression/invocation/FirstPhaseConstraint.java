package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.TypeParameter;
import chameleon.oo.language.ObjectOrientedLanguage;

/**
 * A = type()
 * F = typeReference()
 * 
 * @author Marko van Dooren
 */
public abstract class FirstPhaseConstraint extends Constraint {
	
	/**
	 * 
	 * @param type
	 * @param tref
	 */
	public FirstPhaseConstraint(Type type, JavaTypeReference tref) {
	  _type = type;
	  _typeReference = tref;
	}
	
	private Type _type;
	
	public Type type() {
		return _type;
	}
	
	private JavaTypeReference _typeReference;
	
	public JavaTypeReference typeReference() {
		return _typeReference;
	}
	
	public List<SecondPhaseConstraint> process() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
		if(! type().equals(type().language(ObjectOrientedLanguage.class).getNullType())) {
			result.addAll(processSpecifics());
		}
		return result;
	}
	
	public abstract List<SecondPhaseConstraint> processSpecifics() throws LookupException;
	
	public boolean involvesTypeParameter(TypeReference tref) throws LookupException {
		return ! involvedTypeParameters(tref).isEmpty();
	}
	
	public List<TypeParameter> involvedTypeParameters(TypeReference tref) throws LookupException {
		List<CrossReference> list = tref.descendants(CrossReference.class, new UnsafePredicate<CrossReference, LookupException>() {

			@Override
			public boolean eval(CrossReference object) throws LookupException {
				return parent().typeParameters().contains(object.getDeclarator());
			}
		});
		List<TypeParameter> parameters = new ArrayList<TypeParameter>();
		for(CrossReference cref: list) {
			parameters.add((TypeParameter) cref.getElement());
		}
		return parameters;
	}

	public Java language() {
		return type().language(Java.class);
	}
	
  protected Type typeWithSameBaseTypeAs(Type example, Collection<Type> toBeSearched) {
		Type baseType = example.baseType();
  	for(Type type:toBeSearched) {
			if(type.baseType().equals(baseType)) {
  			return type;
  		}
  	}
  	return null;
  }

}