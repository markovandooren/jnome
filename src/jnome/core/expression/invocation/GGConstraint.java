/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.ExtendsWildcard;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.SuperWildcard;
import chameleon.oo.type.generics.TypeParameter;

public class GGConstraint extends FirstPhaseConstraint {

	public GGConstraint(JavaTypeReference A, Type F) {
		super(A,F);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		return null;
	}
	
	public SubtypeConstraint FequalsTj(TypeParameter declarator, JavaTypeReference type) {
		return new SubtypeConstraint(declarator, type);
	}

	@Override
	public FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference) {
		GGConstraint ggConstraint = new GGConstraint(componentType, componentTypeReference);
		ggConstraint.setUniParent(parent());
		return ggConstraint;
	}

	@Override
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		try {
			if(A().parameters().isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				Type G = F().baseType();
				Type H = A().baseType();
				if(G.subTypeOf(H)) {
					if(! G.sameAs(H)) {
						// No need to include F() itself since the base types aren't equal. 
						// G(S1,..,Sindex-1,U,Sindex+1,...,Sn) -> H
						
						List<TypeParameter> formalArgs = new ArrayList<TypeParameter>();
						for(TypeParameter par: G.parameters()) {
							TypeParameter clone = par.clone();
							formalArgs.add(clone);
						}
						Type GG = new DerivedType(formalArgs, G);
						GG.setUniParent(G.parent());
					  // replace the index-th parameter with a clone of type reference U.
						TypeParameter oldParameter = GG.parameters().get(index);
					  BasicTypeArgument actual = (BasicTypeArgument) U.parent();
						TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.signature().clone(), actual);
					  GG.replaceParameter(oldParameter, newParameter);
						Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
						if(F().subTypeOf(V)) {
						  GGConstraint recursive = new GGConstraint(ARef(), V);
							recursive.setUniParent(parent());
						  result.addAll(recursive.process());
						}
					} else {
						TypeParameter ithTypeParameterOfG = G.parameters().get(index);
						if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
							ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
							if(arg instanceof BasicTypeArgument) {
								EQConstraint recursive = new EQConstraint((JavaTypeReference) ((BasicTypeArgument)arg).typeReference(), U.getElement());
								recursive.setUniParent(parent());
								result.addAll(recursive.process());
							} else if(arg instanceof ExtendsWildcard) {
								GGConstraint recursive = new GGConstraint((JavaTypeReference) ((ExtendsWildcard)arg).typeReference(), U.getElement());
								recursive.setUniParent(parent());
								result.addAll(recursive.process());
							} else if(arg instanceof SuperWildcard) {
								SSConstraint recursive = new SSConstraint((JavaTypeReference) ((SuperWildcard)arg).typeReference(), U.getElement());
								recursive.setUniParent(parent());
								result.addAll(recursive.process());
							}
						}
					}
				}
			}
		}
		catch(IndexOutOfBoundsException exc) {
			return;
		}

	}

	@Override
	public void caseSSFormalExtends(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		try {
			if(A().parameters().isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				Type G = F().baseType();
				Type H = A().baseType();
				if(! G.sameAs(H)) {
					// No need to include F() itself since the base types aren't equal. 
					// G(S1,..,Sindex-1,U,Sindex+1,...,Sn) -> H
					
					List<TypeParameter> formalArgs = new ArrayList<TypeParameter>();
					for(TypeParameter par: G.parameters()) {
						TypeParameter clone = par.clone();
						formalArgs.add(clone);
					}
					Type GG = new DerivedType(formalArgs, G);
					GG.setUniParent(G.parent());
				  // replace the index-th parameter with a clone of type reference U.
					TypeParameter oldParameter = GG.parameters().get(index);
				  BasicTypeArgument actual = (BasicTypeArgument) U.parent();
					TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.signature().clone(), actual);
				  GG.replaceParameter(oldParameter, newParameter);
					Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
					// Replace actual parameters with extends wildcards
					for(TypeParameter<?> par: V.parameters()) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						BasicTypeArgument basic = (BasicTypeArgument) inst.argument();
						TypeReference typeReference = basic.typeReference();
						ExtendsWildcard ext = par.language(Java.class).createExtendsWildcard(typeReference.clone());
						ext.setUniParent(typeReference.parent());
						TypeParameter newP = new InstantiatedTypeParameter(par.signature().clone(),ext);
						V.replaceParameter(par, newP);
					}
					if(F().subTypeOf(V)) {
					  GGConstraint recursive = new GGConstraint(ARef(), V);
						recursive.setUniParent(parent());
					  result.addAll(recursive.process());
					}
				} else {
					TypeParameter ithTypeParameterOfG = G.parameters().get(index);
					if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
						ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
						if(arg instanceof ExtendsWildcard) {
							GGConstraint recursive = new GGConstraint((JavaTypeReference) ((ExtendsWildcard)arg).typeReference(), U.getElement());
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

	@Override
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		try {
			if(A().parameters().isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				Type G = F().baseType();
				Type H = A().baseType();
				if(! G.sameAs(H)) {
					// No need to include F() itself since the base types aren't equal. 
					// G(S1,..,Sindex-1,U,Sindex+1,...,Sn) -> H
					
					List<TypeParameter> formalArgs = new ArrayList<TypeParameter>();
					for(TypeParameter par: G.parameters()) {
						TypeParameter clone = par.clone();
						formalArgs.add(clone);
					}
					Type GG = new DerivedType(formalArgs, G);
					GG.setUniParent(G.parent());
				  // replace the index-th parameter with a clone of type reference U.
					TypeParameter oldParameter = GG.parameters().get(index);
				  BasicTypeArgument actual = (BasicTypeArgument) U.parent();
					TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.signature().clone(), actual);
				  GG.replaceParameter(oldParameter, newParameter);
					Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
					// Replace actual parameters with extends wildcards
					for(TypeParameter<?> par: V.parameters()) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						BasicTypeArgument basic = (BasicTypeArgument) inst.argument();
						TypeReference typeReference = basic.typeReference();
						SuperWildcard ext = par.language(Java.class).createSuperWildcard(typeReference.clone());
						ext.setUniParent(typeReference.parent());
						TypeParameter newP = new InstantiatedTypeParameter(par.signature().clone(),ext);
						V.replaceParameter(par, newP);
					}
					if(F().subTypeOf(V)) {
					  GGConstraint recursive = new GGConstraint(ARef(), V);
						recursive.setUniParent(parent());
					  result.addAll(recursive.process());
					}
				} else {
					TypeParameter ithTypeParameterOfG = G.parameters().get(index);
					if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
						ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
						if(arg instanceof SuperWildcard) {
							SSConstraint recursive = new SSConstraint((JavaTypeReference) ((SuperWildcard)arg).typeReference(), U.getElement());
							recursive.setUniParent(parent());
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

}