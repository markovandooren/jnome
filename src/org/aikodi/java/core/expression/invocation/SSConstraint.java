package org.aikodi.java.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.AbstractInstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.CapturedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;
import org.aikodi.java.core.type.ErasedTypeParameter;
import org.aikodi.java.core.type.JavaTypeReference;
import org.aikodi.rejuse.logic.ternary.Ternary;

import com.google.common.collect.ImmutableSet;

/**
 * A << F See Java language Specification (v3.0 p. 453)
 * 
 * JavaTypeReference << Type 
 * 
 * @author Marko van Dooren
 */
public class SSConstraint extends FirstPhaseConstraint {

	public SSConstraint(TypeReference A, Type F) {
		super(A,F);
	}

	@Override
	public List<SecondPhaseConstraint> processFirstLevel() throws LookupException {
		List<SecondPhaseConstraint> result;
		if(A().isTrue(language().PRIMITIVE_TYPE())) {
			result = new ArrayList<SecondPhaseConstraint>();
			// JLS7 p.468 If A is a primitive type, then A is converted to a reference type U via
			// boxing conversion and this algorithm is applied recursively to the constraint
			// U << F
			SSConstraint recursive = new SSConstraint(language().box(ARef(), ARef().view().namespace()), F());
			recursive.setUniParent(parent());
			result.addAll(recursive.process());
		} else {
			result = super.processFirstLevel(); 
		}
		return result;
	}

	@Override
	public FirstPhaseConstraint Array(TypeReference componentType, Type componentTypeReference) {
		SSConstraint ssConstraint = new SSConstraint(componentType, componentTypeReference);
		ssConstraint.setUniParent(parent());
		return ssConstraint;
	}


	@Override
	public SupertypeConstraint FequalsTj(TypeParameter declarator, TypeReference type) {
		return new SupertypeConstraint(declarator, type);
	}

	/**
	 * If F has the form G<...,Yk-1,? super U,Yk+1....>, where U involves Tj, then if A has a supertype that is one of
	 * 
	 *  1) G<...,Xk-1,V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V>>U
	 *  2) G<...,Xk-1,? super V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V>>U
	 */
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, TypeReference U,
			int index) throws LookupException {
		Type G = GsuperTypeOfA();
		if(G != null) {
			TypeParameter ithTypeParameterOfG = G.parameters(TypeParameter.class).get(index);
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				//				Util.debug(ithTypeParameterOfG instanceof ErasedTypeParameter);
				TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				// 1)
				if(arg instanceof EqualityTypeArgument) {
					JavaTypeReference V = (JavaTypeReference) ((EqualityTypeArgument)arg).typeReference();
					GGConstraint recursive = new GGConstraint(V, U.getElement());
					parent().addGenerated(recursive);
					recursive.setUniParent(parent());
					result.addAll(recursive.process());
				} 
				// 2)
				else if (arg instanceof ExtendsWildcard) {
					JavaTypeReference V = (JavaTypeReference) ((ExtendsWildcard)arg).typeReference();
					GGConstraint recursive = new GGConstraint(V, U.getElement());
					parent().addGenerated(recursive);
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
	@Override
	public void caseSSFormalExtends(List<SecondPhaseConstraint> result, TypeReference U,
			int index) throws LookupException {

		Type G = GsuperTypeOfA();
		if(G != null) {
			TypeParameter ithTypeParameterOfG = G.parameters(TypeParameter.class).get(index);
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				//				Util.debug(ithTypeParameterOfG instanceof ErasedTypeParameter);
				TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				// 1)
				if(arg instanceof EqualityTypeArgument) {
					TypeReference V = (TypeReference) ((EqualityTypeArgument)arg).typeReference();
					SSConstraint recursive = new SSConstraint(V, U.getElement());
					recursive.setUniParent(parent());
					result.addAll(recursive.process());
				} 
				// 2)
				else if (arg instanceof ExtendsWildcard) {
					TypeReference V = (TypeReference) ((ExtendsWildcard)arg).typeReference();
					SSConstraint recursive = new SSConstraint(V, U.getElement());
					recursive.setUniParent(parent());
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
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, TypeReference U,
			int index) throws LookupException {
		Type G = GsuperTypeOfA();
		if(G != null) {
			try {
				TypeParameter ithTypeParameterOfG = G.parameters(TypeParameter.class).get(index);
				if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
					TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
					if(arg instanceof EqualityTypeArgument) {
						TypeReference V = (TypeReference) ((EqualityTypeArgument)arg).typeReference();
						EQConstraint recursive = new EQConstraint(V, U.getElement());
						parent().addGenerated(recursive);
						recursive.setUniParent(parent());
						result.addAll(recursive.process());
					} 
				} else if(ithTypeParameterOfG instanceof CapturedTypeParameter) {
					CapturedTypeParameter captured = (CapturedTypeParameter) ithTypeParameterOfG;
					Type t = captured.selectionDeclaration();
					TypeReference V = language().reference(t);
					EQConstraint recursive = new EQConstraint(V, U.getElement());
					parent().addGenerated(recursive);
					recursive.setUniParent(parent());
					result.addAll(recursive.process());
				}
			}
			catch(IndexOutOfBoundsException exc) {
				return;
			}
		}
	}

	private Type GsuperTypeOfA() throws LookupException {
		return typeWithSameBaseTypeAs(F(), A().getSelfAndAllSuperTypesView());
	}

	@Override
	public String toString() {
		return this.ARef().toString() +" << " +this.F().toString();
	}

}
