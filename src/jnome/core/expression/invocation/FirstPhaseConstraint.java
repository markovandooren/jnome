package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.type.ArrayType;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.JavaTypeReference;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.ConstructedType;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.SuperWildcard;
import chameleon.oo.type.generics.TypeParameter;

/**
 * A = type()
 * F = typeReference()
 * 
 * @author Marko van Dooren
 */
public abstract class FirstPhaseConstraint extends Constraint<FirstPhaseConstraint, FirstPhaseConstraintSet> {
	
	/**
	 * 
	 * @param type
	 * @param tref
	 */
	public FirstPhaseConstraint(JavaTypeReference A, Type F) {
	  _A = A;
	  _F = F;
	}
	
	private JavaTypeReference _A;
	
	public Type A() throws LookupException {
		return _A.getElement();
	}
	
	public JavaTypeReference ARef() {
		return _A;
	}
	
//	private JavaTypeReference _typeReference;
//	
//	public JavaTypeReference typeReference() {
//		return _typeReference;
//	}
//	
	public Type F() throws LookupException {
		return _F;
	}
	
	private Type _F;
	
	public List<SecondPhaseConstraint> process() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
		// If A is the type of null, no constraint is implied on Tj.
		if(! A().equals(A().language(ObjectOrientedLanguage.class).getNullType())) {
			
			result.addAll(processFirstLevel());
		}
		return result;
	}
	
	public List<SecondPhaseConstraint> processFirstLevel() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
//		Declaration declarator = typeReference().getDeclarator();
		if(F() instanceof ConstructedType && parent().typeParameters().contains(((ConstructedType)F()).parameter())) {
			// Otherwise, if F=Tj, then the constraint Tj :> A is implied.
				result.add(FequalsTj(((ConstructedType)F()).parameter(), ARef()));
		}
		else if(F() instanceof ArrayType) {
			// If F=U[], where the type U involves Tj, then if A is an array type V[], or
			// a type variable with an upper bound that is an array type V[], where V is a
			// reference type, this algorithm is applied recursively to the constraint V<<U

			if(A() instanceof ArrayType && involvesTypeParameter(F())) {
				Type componentType = ((ArrayType)A()).elementType();
				if(componentType.is(language().REFERENCE_TYPE) == Ternary.TRUE) {
					JavaTypeReference componentTypeReference = ARef().clone().componentTypeReference();
					componentTypeReference.setUniParent(ARef().parent());
					FirstPhaseConstraint recursive = Array(componentTypeReference, ((ArrayType)F()).elementType());
					result.addAll(recursive.process());
					// FIXME: can't we unwrap the entire array dimension at once? This seems rather inefficient.
				}
			}
		} else if(A().is(language().PRIMITIVE_TYPE) != Ternary.TRUE){
				// i is the index of the parameter we are processing.
				// V= the type reference of the i-th type parameter of some supertype G of A.
			List<ActualTypeArgument> actualsOfF = new ArrayList<ActualTypeArgument>();
			for(TypeParameter par: F().parameters()) {
				if(par instanceof InstantiatedTypeParameter) {
				  actualsOfF.add(((InstantiatedTypeParameter)par).argument());
				}
			}
				int i = 0;
				for(ActualTypeArgument typeArgumentOfFormalParameter: actualsOfF) {
					if(typeArgumentOfFormalParameter instanceof BasicTypeArgument) {
						JavaTypeReference U = (JavaTypeReference) ((BasicTypeArgument)typeArgumentOfFormalParameter).typeReference();
						if(involvesTypeParameter(U)) {
						  caseSSFormalBasic(result, U, i);
						}
					} else if(typeArgumentOfFormalParameter instanceof ExtendsWildcard) {
						JavaTypeReference U = (JavaTypeReference) ((ExtendsWildcard)typeArgumentOfFormalParameter).typeReference();
						if(involvesTypeParameter(U)) {
						  caseSSFormalExtends(result, U, i);
						}
					} else if(typeArgumentOfFormalParameter instanceof SuperWildcard) {
						JavaTypeReference U = (JavaTypeReference) ((SuperWildcard)typeArgumentOfFormalParameter).typeReference();
						if(involvesTypeParameter(U)) {
							caseSSFormalSuper(result, U, i);
						}
					}
					i++;
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
	
	public abstract SecondPhaseConstraint FequalsTj(TypeParameter declarator, JavaTypeReference type);
	
	public abstract FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference);
	
	public abstract List<SecondPhaseConstraint> processSpecifics() throws LookupException;
	
	public boolean involvesTypeParameter(JavaTypeReference tref) throws LookupException {
		return ! involvedTypeParameters(tref).isEmpty();
	}
	
	public boolean involvesTypeParameter(Type type) throws LookupException {
		if((type instanceof ConstructedType) && (parent().typeParameters().contains(((ConstructedType)type).parameter()))) {
			return true;
		} 
		if((type instanceof ArrayType) && (involvesTypeParameter(((ArrayType)type).elementType()))) {
			return true;
		}
		if((type instanceof DerivedType) && (involvesTypeParameter(type.baseType()))) {
			return true;
		}
		 
    	return new UnsafePredicate<TypeParameter, LookupException>() {

				@Override
				public boolean eval(TypeParameter object) throws LookupException {
					if(object instanceof InstantiatedTypeParameter) {
						ActualTypeArgument arg = ((InstantiatedTypeParameter)object).argument();
						return involvesTypeParameter(arg);
					} else {
						return false;
					}
				}
			}.exists(type.parameters());
	}
	
	public boolean involvesTypeParameter(ActualTypeArgument arg) throws LookupException {
		if(arg instanceof ActualTypeArgumentWithTypeReference) {
			return involvesTypeParameter((JavaTypeReference) ((ActualTypeArgumentWithTypeReference)arg).typeReference());
		} else {
			return false;
		}
	}
	
	public List<TypeParameter> involvedTypeParameters(JavaTypeReference<?> tref) throws LookupException {
		UnsafePredicate<BasicJavaTypeReference, LookupException> predicate = new UnsafePredicate<BasicJavaTypeReference, LookupException>() {

			@Override
			public boolean eval(BasicJavaTypeReference object) throws LookupException {
				return parent().typeParameters().contains(object.getDeclarator());
			}
		};
		List<BasicJavaTypeReference> list = tref.descendants(BasicJavaTypeReference.class, predicate);
		if((tref instanceof BasicJavaTypeReference) && predicate.eval((BasicJavaTypeReference) tref)) {
			list.add((BasicJavaTypeReference) tref);
		}
		List<TypeParameter> parameters = new ArrayList<TypeParameter>();
		for(BasicJavaTypeReference cref: list) {
			parameters.add((TypeParameter) cref.getDeclarator());
		}
		return parameters;
	}

	public Java language() throws LookupException {
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