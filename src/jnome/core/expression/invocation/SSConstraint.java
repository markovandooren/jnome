package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jnome.core.type.ArrayType;
import jnome.core.type.JavaTypeReference;

import org.rejuse.logic.ternary.Ternary;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;

/**
 * A << F
 * 
 * Type << JavaTypeReference
 * 
 * @author Marko van Dooren
 */
public class SSConstraint extends FirstPhaseConstraint {

	public SSConstraint(Type type, JavaTypeReference tref) {
		super(type,tref);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
		Declaration declarator = typeReference().getDeclarator();
		Type type = type();
		if(type().is(language().PRIMITIVE_TYPE) == Ternary.TRUE) {
			// If A is a primitive type, then A is converted to a reference type U via
			// boxing conversion and this algorithm is applied recursively to the constraint
			// U << F
			SSConstraint recursive = new SSConstraint(language().box(type()), typeReference());
			result.addAll(recursive.process());
		} else if(parent().typeParameters().contains(declarator)) {
			// Otherwise, if F=Tj, then the constraint Tj :> A is implied.
				result.add(new SupertypeConstraint((TypeParameter) declarator, type()));
		} else if(typeReference().arrayDimension() > 0) {
			// If F=U[], where the type U involves Tj, then if A is an array type V[], or
			// a type variable with an upper bound that is an array type V[], where V is a
			// reference type, this algorithm is applied recursively to the constraint V<<U

			// The "involves Tj" condition for U is the same as "involves Tj" for F.
			if(type instanceof ArrayType && involvesTypeParameter(typeReference())) {
				Type componentType = ((ArrayType)type).componentType();
				if(componentType.is(language().REFERENCE_TYPE) == Ternary.TRUE) {
					JavaTypeReference componentTypeReference = typeReference().clone();
					componentTypeReference.setUniParent(typeReference());
					componentTypeReference.decreaseArrayDimension(1);
					SSConstraint recursive = new SSConstraint(componentType, componentTypeReference);
					result.addAll(recursive.process());
					// FIXME: can't we unwrap the entire array dimension at once? This seems rather inefficient.
				}
			}
		} else {
			List<ActualTypeArgument> actuals = typeReference().typeArguments();
			Set<Type> supers = type().getAllSuperTypes();
			Type G = typeWithSameBaseTypeAs(type(), supers);
			if(G != null) {
				// i is the index of the parameter we are processing.
				// V= the type reference of the i-th type parameter of some supertype G of A.
				int i = 0;
				for(ActualTypeArgument typeArgumentOfFormalParameter: typeReference().typeArguments()) {
					i++;
					TypeParameter ithTypeParameterOfG = G.parameters().get(i);
					if(typeArgumentOfFormalParameter instanceof BasicTypeArgument) {
						caseSSFormalBasic(result, (BasicTypeArgument)typeArgumentOfFormalParameter, ithTypeParameterOfG);
					} else if(typeArgumentOfFormalParameter instanceof ExtendsWildCard) {
						caseSSFormalExtends(result, (ExtendsWildCard) typeArgumentOfFormalParameter, ithTypeParameterOfG);
					} else if(typeArgumentOfFormalParameter instanceof SuperWildCard) {
						caseSSFormalSuper(result, (SuperWildCard) typeArgumentOfFormalParameter, ithTypeParameterOfG);
					}
				}
			}
		}
		return result;
	}

	/**
	 * If F has the form G<...,Yk-1,? super U,Yk+1....>, where U involves Tj, then if A has a supertype that is one of
	 * 
	 *  1) G<...,Xk-1,V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V>>U
	 *  2) G<...,Xk-1,? super V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V>>U
	 */
	private void caseSSFormalSuper(List<SecondPhaseConstraint> result, SuperWildCard typeArgumentOfFormalParameter,
			TypeParameter ithTypeParameterOfG) throws LookupException {
		JavaTypeReference U = (JavaTypeReference) typeArgumentOfFormalParameter.typeReference();
		if(involvesTypeParameter(U)) {
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				// 1)
				if(arg instanceof BasicTypeArgument) {
					Type V = arg.type();
					GGConstraint recursive = new GGConstraint(V, U);
					result.addAll(recursive.process());
				} 
				// 2)
				else if (arg instanceof ExtendsWildCard) {
					Type V = ((ExtendsWildCard)arg).upperBound();
					GGConstraint recursive = new GGConstraint(V, U);
					result.addAll(recursive.process());
				}
				// Otherwise, no constraint is implied on Tj.
			}
		}
	}
	
  /**
	 * If F has the form G<...,Yk-1,? extends U,Yk+1....>, where U involves Tj, then if A has a supertype that is one of
	 * 
	 *  1) G<...,Xk-1,V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V<<U
	 *  2) G<...,Xk-1,? extends V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V<<U
	 */
	private void caseSSFormalExtends(List<SecondPhaseConstraint> result, ExtendsWildCard typeArgumentOfFormalParameter,
			TypeParameter ithTypeParameterOfG) throws LookupException {
		JavaTypeReference U = (JavaTypeReference) typeArgumentOfFormalParameter.typeReference();
		if(involvesTypeParameter(U)) {
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				// 1)
				if(arg instanceof BasicTypeArgument) {
					Type V = arg.type();
					SSConstraint recursive = new SSConstraint(V, U);
					result.addAll(recursive.process());
				} 
				// 2)
				else if (arg instanceof ExtendsWildCard) {
					Type V = ((ExtendsWildCard)arg).upperBound();
					SSConstraint recursive = new SSConstraint(V, U);
					result.addAll(recursive.process());
				}
				// Otherwise, no constraint is implied on Tj.
			}
		}
	}

	/**
	 * If F has the form G<...,Yk-1,U,Yk+1....>, 1<=k<=n where U is a type expression that involves Tj,
	 * the in A has a supertype of the form G<...,Xk-1,V,Xk+1,...> where V is a type expression, this algorithm 
	 * is applied recursively to the constraint V = U. 
	 */
	private void caseSSFormalBasic(List<SecondPhaseConstraint> result, BasicTypeArgument typeArgumentOfFormalParameter,
			TypeParameter ithTypeParameterOfG) throws LookupException {
		// U = basic.typeReference()
		JavaTypeReference U = (JavaTypeReference) typeArgumentOfFormalParameter.typeReference();
		if(involvesTypeParameter(U)) {
			// Get the i-th type parameter of zuppa: V.
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				if(arg instanceof BasicTypeArgument) {
					Type V = arg.type();
					EQConstraint recursive = new EQConstraint(V, U);
					result.addAll(recursive.process());
				}
			}
		}
	}
	
}