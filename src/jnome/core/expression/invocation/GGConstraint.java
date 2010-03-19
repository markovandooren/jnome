/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.DerivedType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;

public class GGConstraint extends FirstPhaseConstraint {

	public GGConstraint(JavaTypeReference A, Type F) {
		super(A,F);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		return null;
	}
	
	public SubtypeConstraint FequalsTj(TypeParameter declarator, Type type) {
		return new SubtypeConstraint(declarator, type);
	}

	@Override
	public FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference) {
		return new GGConstraint(componentType, componentTypeReference);
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
						  result.addAll(recursive.process());
						}
					} else {
						TypeParameter ithTypeParameterOfG = G.parameters().get(index);
						if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
							ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
							if(arg instanceof BasicTypeArgument) {
								EQConstraint recursive = new EQConstraint((JavaTypeReference) ((BasicTypeArgument)arg).typeReference(), U.getElement());
								result.addAll(recursive.process());
							} else if(arg instanceof ExtendsWildCard) {
								GGConstraint recursive = new GGConstraint((JavaTypeReference) ((ExtendsWildCard)arg).typeReference(), U.getElement());
								result.addAll(recursive.process());
							} else if(arg instanceof SuperWildCard) {
								SSConstraint recursive = new SSConstraint((JavaTypeReference) ((SuperWildCard)arg).typeReference(), U.getElement());
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
					for(TypeParameter par: V.parameters()) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						BasicTypeArgument basic = (BasicTypeArgument) inst.argument();
						TypeReference typeReference = basic.typeReference();
						ExtendsWildCard ext = new ExtendsWildCard(typeReference.clone());
						ext.setUniParent(typeReference.parent());
						TypeParameter newP = new InstantiatedTypeParameter(par.signature().clone(),ext);
						V.replaceParameter(par, newP);
					}
					if(F().subTypeOf(V)) {
					  GGConstraint recursive = new GGConstraint(ARef(), V);
					  result.addAll(recursive.process());
					}
				} else {
					TypeParameter ithTypeParameterOfG = G.parameters().get(index);
					if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
						ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
						if(arg instanceof ExtendsWildCard) {
							GGConstraint recursive = new GGConstraint((JavaTypeReference) ((ExtendsWildCard)arg).typeReference(), U.getElement());
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
					for(TypeParameter par: V.parameters()) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						BasicTypeArgument basic = (BasicTypeArgument) inst.argument();
						TypeReference typeReference = basic.typeReference();
						SuperWildCard ext = new SuperWildCard(typeReference.clone());
						ext.setUniParent(typeReference.parent());
						TypeParameter newP = new InstantiatedTypeParameter(par.signature().clone(),ext);
						V.replaceParameter(par, newP);
					}
					if(F().subTypeOf(V)) {
					  GGConstraint recursive = new GGConstraint(ARef(), V);
					  result.addAll(recursive.process());
					}
				} else {
					TypeParameter ithTypeParameterOfG = G.parameters().get(index);
					if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
						ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
						if(arg instanceof SuperWildCard) {
							SSConstraint recursive = new SSConstraint((JavaTypeReference) ((SuperWildCard)arg).typeReference(), U.getElement());
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