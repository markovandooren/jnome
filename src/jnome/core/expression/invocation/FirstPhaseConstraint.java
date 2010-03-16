package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import jnome.core.language.Java;
import jnome.core.type.ArrayType;
import jnome.core.type.JavaTypeReference;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.SuperWildCard;
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
	
	public Type A() {
		return _type;
	}
	
	private JavaTypeReference _typeReference;
	
	public JavaTypeReference typeReference() {
		return _typeReference;
	}
	
	public Type F() throws LookupException {
		return typeReference().getElement();
	}
	
	public List<SecondPhaseConstraint> process() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
		// If A is the type of null, no constraint is implied on Tj.
		if(! A().equals(A().language(ObjectOrientedLanguage.class).getNullType())) {
			
			result.addAll(processSpecifics());
		}
		return result;
	}
	
	public List<SecondPhaseConstraint> processFirstLevel() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
		Declaration declarator = typeReference().getDeclarator();
		if(parent().typeParameters().contains(declarator)) {
			// Otherwise, if F=Tj, then the constraint Tj :> A is implied.
				result.add(FequalsTj(declarator, A()));
		}
		else if(typeReference().arrayDimension() > 0) {
			// If F=U[], where the type U involves Tj, then if A is an array type V[], or
			// a type variable with an upper bound that is an array type V[], where V is a
			// reference type, this algorithm is applied recursively to the constraint V<<U

			// The "involves Tj" condition for U is the same as "involves Tj" for F.
			if(A() instanceof ArrayType && involvesTypeParameter(typeReference())) {
				Type componentType = ((ArrayType)A()).componentType();
				if(componentType.is(language().REFERENCE_TYPE) == Ternary.TRUE) {
					JavaTypeReference componentTypeReference = typeReference().clone();
					componentTypeReference.setUniParent(typeReference());
					componentTypeReference.decreaseArrayDimension(1);
					FirstPhaseConstraint recursive = Array(componentType, componentTypeReference);
					result.addAll(recursive.process());
					// FIXME: can't we unwrap the entire array dimension at once? This seems rather inefficient.
				}
			}
		} else if(A().is(language().PRIMITIVE_TYPE) != Ternary.TRUE){
			List<ActualTypeArgument> actuals = typeReference().typeArguments();
				// i is the index of the parameter we are processing.
				// V= the type reference of the i-th type parameter of some supertype G of A.
				int i = 0;
				for(ActualTypeArgument typeArgumentOfFormalParameter: typeReference().typeArguments()) {
					i++;
					if(typeArgumentOfFormalParameter instanceof BasicTypeArgument) {
						JavaTypeReference U = (JavaTypeReference) ((BasicTypeArgument)typeArgumentOfFormalParameter).typeReference();
						if(involvesTypeParameter(U)) {
						  caseSSFormalBasic(result, U, i);
						}
					} else if(typeArgumentOfFormalParameter instanceof ExtendsWildCard) {
						JavaTypeReference U = (JavaTypeReference) ((ExtendsWildCard)typeArgumentOfFormalParameter).typeReference();
						if(involvesTypeParameter(U)) {
						  caseSSFormalExtends(result, U, i);
						}
					} else if(typeArgumentOfFormalParameter instanceof SuperWildCard) {
						JavaTypeReference U = (JavaTypeReference) ((SuperWildCard)typeArgumentOfFormalParameter).typeReference();
						if(involvesTypeParameter(U)) {
							caseSSFormalSuper(result, U, i);
						}
					}
				}
		}
		else {
			result.addAll(processSpecifics());
		}
		return result;
	}
	
	public abstract void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException;
	
	public abstract void caseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException;
	
	public abstract void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException;
	
	public abstract SecondPhaseConstraint FequalsTj(Declaration declarator, Type type);
	
	public abstract FirstPhaseConstraint Array(Type componentType, JavaTypeReference componentTypeReference);
	
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
		return A().language(Java.class);
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