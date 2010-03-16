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
		Type type = A();
		if(A().is(language().PRIMITIVE_TYPE) == Ternary.TRUE) {
			// If A is a primitive type, then A is converted to a reference type U via
			// boxing conversion and this algorithm is applied recursively to the constraint
			// U << F
			SSConstraint recursive = new SSConstraint(language().box(A()), typeReference());
			result.addAll(recursive.process());
		} 
		return result;
	}
	
	public FirstPhaseConstraint Array(Type componentType, JavaTypeReference componentTypeReference) {
		return new SSConstraint(componentType, componentTypeReference);
	}


	public SupertypeConstraint FequalsTj(Declaration declarator, Type type) {
		return new SupertypeConstraint((TypeParameter) declarator, type);
	}

	/**
	 * If F has the form G<...,Yk-1,? super U,Yk+1....>, where U involves Tj, then if A has a supertype that is one of
	 * 
	 *  1) G<...,Xk-1,V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V>>U
	 *  2) G<...,Xk-1,? super V,Xk+1,...>, where V is a type expression. Then this algorithm is
	 *     applied recursively to the constraint V>>U
	 */
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		Type G = GsuperTypeOfA();
		TypeParameter ithTypeParameterOfG = G.parameters().get(index);

		if(G != null) {
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
	public void caseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {

		Type G = GsuperTypeOfA();
		TypeParameter ithTypeParameterOfG = G.parameters().get(index);

		if(G != null) {
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
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		// U = basic.typeReference()
		Type G = GsuperTypeOfA();
		try {
			TypeParameter ithTypeParameterOfG = G.parameters().get(index);

			if(G != null) {
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
		catch(IndexOutOfBoundsException exc) {
			return;
		}
	}

	private Type GsuperTypeOfA() throws LookupException {
		Set<Type> supers = A().getAllSuperTypes();
		Type G = typeWithSameBaseTypeAs(F(), supers);
		return G;
	}
	
}