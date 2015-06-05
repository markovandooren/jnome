package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import static be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations.exists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.TypeInstantiation;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeArgumentWithTypeReference;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.CapturedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.EqualityConstraint;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaEqualityTypeArgument;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.Predicate;

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
	public Type F() {
		return _F;
	}
	
	private Type _F;
	
	public List<SecondPhaseConstraint> process() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
		// If A is the type of null, no constraint is implied on Tj.
		Type A = A();
		View view = A.view();
    ObjectOrientedLanguage l = view.language(ObjectOrientedLanguage.class);
		
		if(! A.equals(l.getNullType(view.namespace()))) {
			result.addAll(processFirstLevel());
		}
		return result;
	}
	
	public List<SecondPhaseConstraint> processFirstLevel() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
//		Declaration declarator = typeReference().getDeclarator();
		if(F() instanceof TypeVariable && parent().typeParameters().contains(((TypeVariable)F()).parameter())) {
			// Otherwise, if F=Tj, then the constraint Tj :> A is implied.
				result.add(FequalsTj(((TypeVariable)F()).parameter(), ARef()));
		}
		else if(F() instanceof ArrayType) {
			// If F=U[], where the type U involves Tj, then if A is an array type V[], or
			// a type variable with an upper bound that is an array type V[], where V is a
			// reference type, this algorithm is applied recursively to the constraint V<<U

			if(A() instanceof ArrayType && involvesTypeParameter(F())) {
				Type componentType = ((ArrayType)A()).elementType();
				if(componentType.is(language().REFERENCE_TYPE) == Ternary.TRUE) {
					JavaTypeReference componentTypeReference = Util.clone(ARef()).componentTypeReference();
					componentTypeReference.setUniParent(ARef().parent());
					FirstPhaseConstraint recursive = Array(componentTypeReference, ((ArrayType)F()).elementType());
					result.addAll(recursive.process());
					// FIXME: can't we unwrap the entire array dimension at once? This seems rather inefficient.
				}
			}
		} else if(A().is(language().PRIMITIVE_TYPE) != Ternary.TRUE){
				// i is the index of the parameter we are processing.
				// V= the type reference of the i-th type parameter of some supertype G of A.
			List<TypeArgument> actualsOfF = new ArrayList<TypeArgument>();
			for(TypeParameter par: F().parameters(TypeParameter.class)) {
				if(par instanceof InstantiatedTypeParameter) {
				  actualsOfF.add(((InstantiatedTypeParameter)par).argument());
				} 
				else if(par instanceof CapturedTypeParameter) {
					List<TypeConstraint> constraints = ((CapturedTypeParameter) par).constraints();
					if(constraints.size() == 1 && constraints.get(0) instanceof EqualityConstraint) {
						EqualityConstraint eq = (EqualityConstraint) constraints.get(0);
						EqualityTypeArgument arg = language().createEqualityTypeArgument(Util.clone(eq.typeReference()));
						arg.setUniParent(eq);
						actualsOfF.add(arg);
					}
				}
			}
				int i = 0;
				for(TypeArgument typeArgumentOfFormalParameter: actualsOfF) {
					if(typeArgumentOfFormalParameter instanceof EqualityTypeArgument) {
						JavaTypeReference U = (JavaTypeReference) ((EqualityTypeArgument)typeArgumentOfFormalParameter).typeReference();
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
//		else {
//			result.addAll(processSpecifics());
//		}
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
	
//	public abstract List<SecondPhaseConstraint> processSpecifics() throws LookupException;
	
	public boolean involvesTypeParameter(JavaTypeReference tref) throws LookupException {
		return ! involvedTypeParameters(tref).isEmpty();
	}
	
	public boolean involvesTypeParameter(Type type) throws LookupException {
    return ((type instanceof TypeVariable) && (parent().typeParameters().contains(((TypeVariable) type).parameter())))
        || ((type instanceof ArrayType) && (involvesTypeParameter(((ArrayType) type).elementType())))
        || ((type instanceof TypeInstantiation) && (involvesTypeParameter(type.baseType())))
        || exists(type.parameters(TypeParameter.class), object -> (object instanceof InstantiatedTypeParameter)
            && involvesTypeParameter(((InstantiatedTypeParameter) object).argument()));
	}
	
	public boolean involvesTypeParameter(TypeArgument arg) throws LookupException {
		return (arg instanceof TypeArgumentWithTypeReference) &&
		    involvesTypeParameter((JavaTypeReference) ((TypeArgumentWithTypeReference)arg).typeReference());
	}
	
	public List<TypeParameter> involvedTypeParameters(JavaTypeReference tref) throws LookupException {
		Predicate<BasicJavaTypeReference, LookupException> predicate = 
		    object ->  parent().typeParameters().contains(object.getDeclarator());
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

	public Java7 language() throws LookupException {
		return A().language(Java7.class);
	}
	
	public View view() throws LookupException {
		return A().view();
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
