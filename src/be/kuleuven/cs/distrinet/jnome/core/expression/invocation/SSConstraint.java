package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

/**
 * A << F See Java language Specification (v3.0 p. 453)
 * 
 * JavaTypeReference << Type 
 * 
 * @author Marko van Dooren
 */
public class SSConstraint extends FirstPhaseConstraint {
	
	public SSConstraint(JavaTypeReference A, Type F) {
		super(A,F);
	}
	
	@Override
	public List<SecondPhaseConstraint> processFirstLevel() throws LookupException {
		List<SecondPhaseConstraint> result;
		if(A().is(language().PRIMITIVE_TYPE) == Ternary.TRUE) {
			result = new ArrayList<SecondPhaseConstraint>();
			// If A is a primitive type, then A is converted to a reference type U via
			// boxing conversion and this algorithm is applied recursively to the constraint
			// U << F
			SSConstraint recursive = new SSConstraint(language().box(ARef(), view().namespace()), F());
			recursive.setUniParent(parent());
			result.addAll(recursive.process());
		} else {
			result = super.processFirstLevel(); 
		}
		return result;
	}

//	@Override
//	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
//		List<SecondPhaseConstraint> result = new ArrayList<SecondPhaseConstraint>();
//		if(A().is(language().PRIMITIVE_TYPE) == Ternary.TRUE) {
//			// If A is a primitive type, then A is converted to a reference type U via
//			// boxing conversion and this algorithm is applied recursively to the constraint
//			// U << F
//			SSConstraint recursive = new SSConstraint(language().box(ARef(), view().namespace()), F());
//			recursive.setUniParent(parent());
//			result.addAll(recursive.process());
//		} 
//		return result;
//	}
	
	@Override
	public FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference) {
		SSConstraint ssConstraint = new SSConstraint(componentType, componentTypeReference);
		ssConstraint.setUniParent(parent());
		return ssConstraint;
	}


	public SupertypeConstraint FequalsTj(TypeParameter declarator, JavaTypeReference type) {
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
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		Type G = GsuperTypeOfA();
		if(G != null) {
			TypeParameter ithTypeParameterOfG = G.parameters(TypeParameter.class).get(index);
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				// 1)
				if(arg instanceof BasicTypeArgument) {
					JavaTypeReference V = (JavaTypeReference) ((BasicTypeArgument)arg).typeReference();
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
	public void caseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {

		Type G = GsuperTypeOfA();
		if(G != null) {
			TypeParameter ithTypeParameterOfG = G.parameters(TypeParameter.class).get(index);
			if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
				ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
				// 1)
				if(arg instanceof BasicTypeArgument) {
					JavaTypeReference V = (JavaTypeReference) ((BasicTypeArgument)arg).typeReference();
					SSConstraint recursive = new SSConstraint(V, U.getElement());
					recursive.setUniParent(parent());
					result.addAll(recursive.process());
				} 
				// 2)
				else if (arg instanceof ExtendsWildcard) {
					JavaTypeReference V = (JavaTypeReference) ((ExtendsWildcard)arg).typeReference();
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
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U,
			int index) throws LookupException {
		// U = basic.typeReference()
		Type G = GsuperTypeOfA();
		if(G != null) {
			try {
				TypeParameter ithTypeParameterOfG = G.parameters(TypeParameter.class).get(index);

				if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
					// Get the i-th type parameter of zuppa: V.
					ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
					if(arg instanceof BasicTypeArgument) {
						JavaTypeReference V = (JavaTypeReference) ((BasicTypeArgument)arg).typeReference();
						EQConstraint recursive = new EQConstraint(V, U.getElement());
						parent().addGenerated(recursive);
						recursive.setUniParent(parent());
						result.addAll(recursive.process());
					} else if(arg instanceof ExtendsWildcard) {
						JavaTypeReference V = new JavaExtendsReference(((ExtendsWildcard)arg).typeReference());
						V.setUniParent(ithTypeParameterOfG);
						EQConstraint recursive = new EQConstraint(V, U.getElement());
						parent().addGenerated(recursive);
						recursive.setUniParent(parent());
						result.addAll(recursive.process());
					} else if(arg instanceof SuperWildcard) {
						JavaTypeReference V = new JavaSuperReference(((SuperWildcard)arg).typeReference());
						V.setUniParent(ithTypeParameterOfG);
						EQConstraint recursive = new EQConstraint(V, U.getElement());
						parent().addGenerated(recursive);
						recursive.setUniParent(parent());
						result.addAll(recursive.process());
					}
				}
			}
			catch(IndexOutOfBoundsException exc) {
				return;
			}
		}
	}

	private Type GsuperTypeOfA() throws LookupException {
		Set<Type> supers = A().getAllSuperTypes();
		supers.add(A());
		Type G = typeWithSameBaseTypeAs(F(), supers);
		return G;
	}
	
	@Override
	public String toString() {
		return this.ARef().toString() +" << " +this.F().toString();
	}

}
