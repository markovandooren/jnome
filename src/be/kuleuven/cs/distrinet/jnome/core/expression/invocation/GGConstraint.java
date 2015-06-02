package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.SuperWildcard;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

/**
 * A >> F 
 */
public class GGConstraint extends FirstPhaseConstraint {

	public GGConstraint(JavaTypeReference A, Type F) {
		super(A,F);
	}

//	@Override
//	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
//		return null;
//	}
	
	public SubtypeConstraint FequalsTj(TypeParameter declarator, JavaTypeReference type) {
		return new SubtypeConstraint(declarator, type);
	}

	@Override
	public FirstPhaseConstraint Array(JavaTypeReference componentType, Type componentTypeReference) {
		GGConstraint ggConstraint = new GGConstraint(componentType, componentTypeReference);
		parent().addGenerated(ggConstraint);
		ggConstraint.setUniParent(parent());
		return ggConstraint;
	}

	@Override
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		try {
			if(A().parameters(TypeParameter.class).isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				Type G = F().baseType();
				Type H = A().baseType();
				if(G.subTypeOf(H)) {
					if(! G.sameAs(H)) {
						// No need to include F() itself since the base types aren't equal. 
						// G(S1,..,Sindex-1,U,Sindex+1,...,Sn) -> H
						
						List<TypeParameter> formalArgs = new ArrayList<TypeParameter>();
						for(TypeParameter par: G.parameters(TypeParameter.class)) {
							TypeParameter clone = (TypeParameter) par.clone();
							formalArgs.add(clone);
						}
						Type GG = G.language(ObjectOrientedLanguage.class).createDerivedType(TypeParameter.class,formalArgs, G);
						GG.setUniParent(G.parent());
					  // replace the index-th parameter with a clone of type reference U.
						TypeParameter oldParameter = GG.parameters(TypeParameter.class).get(index);
					  EqualityTypeArgument actual = (EqualityTypeArgument) U.parent();
						TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.name(), actual);
					  GG.replaceParameter(TypeParameter.class,oldParameter, newParameter);
						Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
						if(F().subTypeOf(V)) {
						  GGConstraint recursive = new GGConstraint(ARef(), V);
						  parent().addGenerated(recursive);
							recursive.setUniParent(parent());
						  result.addAll(recursive.process());
						}
					} else {
						TypeParameter ithTypeParameterOfA = A().parameters(TypeParameter.class).get(index);
						if(ithTypeParameterOfA instanceof InstantiatedTypeParameter) {
							TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfA).argument();
							if(arg instanceof EqualityTypeArgument) {
								EQConstraint recursive = new EQConstraint((JavaTypeReference) ((EqualityTypeArgument)arg).typeReference(), U.getElement());
								parent().addGenerated(recursive);
								recursive.setUniParent(parent());
								result.addAll(recursive.process());
							} else if(arg instanceof ExtendsWildcard) {
								GGConstraint recursive = new GGConstraint((JavaTypeReference) ((ExtendsWildcard)arg).typeReference(), U.getElement());
								parent().addGenerated(recursive);
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
			if(A().parameters(TypeParameter.class).isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				Type G = F().baseType();
				Type H = A().baseType();
				if(! G.sameAs(H)) {
					// No need to include F() itself since the base types aren't equal. 
					// G(S1,..,Sindex-1,U,Sindex+1,...,Sn) -> H
					
					List<TypeParameter> formalArgs = new ArrayList<TypeParameter>();
					for(TypeParameter par: G.parameters(TypeParameter.class)) {
						TypeParameter clone = (TypeParameter) par.clone();
						formalArgs.add(clone);
					}
					Type GG = G.language(ObjectOrientedLanguage.class).createDerivedType(TypeParameter.class,formalArgs, G);
					GG.setUniParent(G.parent());
				  // replace the index-th parameter with a clone of type reference U.
					TypeParameter oldParameter = GG.parameters(TypeParameter.class).get(index);
				  EqualityTypeArgument actual = (EqualityTypeArgument) U.parent();
					TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.name(), actual);
				  GG.replaceParameter(TypeParameter.class,oldParameter, newParameter);
					Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
					// Replace actual parameters with extends wildcards
					for(TypeParameter par: V.parameters(TypeParameter.class)) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						EqualityTypeArgument basic = (EqualityTypeArgument) inst.argument();
						TypeReference typeReference = basic.typeReference();
						ExtendsWildcard ext = par.language(Java7.class).createExtendsWildcard(Util.clone(typeReference));
						ext.setUniParent(typeReference.parent());
						TypeParameter newP = new InstantiatedTypeParameter(par.name(),ext);
						V.replaceParameter(TypeParameter.class,par, newP);
					}
					if(F().subTypeOf(V)) {
					  GGConstraint recursive = new GGConstraint(ARef(), V);
					  parent().addGenerated(recursive);
						recursive.setUniParent(parent());
					  result.addAll(recursive.process());
					}
				} else {
					TypeParameter ithTypeParameterOfA = A().parameters(TypeParameter.class).get(index);
					if(ithTypeParameterOfA instanceof InstantiatedTypeParameter) {
						TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfA).argument();
						if(arg instanceof ExtendsWildcard) {
							GGConstraint recursive = new GGConstraint((JavaTypeReference) ((ExtendsWildcard)arg).typeReference(), U.getElement());
							parent().addGenerated(recursive);
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

	@Override
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		try {
			if(A().parameters(TypeParameter.class).isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				Type G = F().baseType();
				Type H = A().baseType();
				if(! G.sameAs(H)) {
					// No need to include F() itself since the base types aren't equal. 
					// G(S1,..,Sindex-1,U,Sindex+1,...,Sn) -> H
					
					List<TypeParameter> formalArgs = new ArrayList<TypeParameter>();
					for(TypeParameter par: G.parameters(TypeParameter.class)) {
						TypeParameter clone = (TypeParameter) par.clone();
						formalArgs.add(clone);
					}
					Type GG = G.language(ObjectOrientedLanguage.class).createDerivedType(TypeParameter.class,formalArgs, G);
					GG.setUniParent(G.parent());
				  // replace the index-th parameter with a clone of type reference U.
					TypeParameter oldParameter = GG.parameters(TypeParameter.class).get(index);
				  EqualityTypeArgument actual = (EqualityTypeArgument) U.parent();
					TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.name(), actual);
				  GG.replaceParameter(TypeParameter.class,oldParameter, newParameter);
					Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
					// Replace actual parameters with extends wildcards
					for(TypeParameter par: V.parameters(TypeParameter.class)) {
						InstantiatedTypeParameter inst = (InstantiatedTypeParameter) par;
						EqualityTypeArgument basic = (EqualityTypeArgument) inst.argument();
						TypeReference typeReference = basic.typeReference();
						SuperWildcard ext = par.language(Java7.class).createSuperWildcard(Util.clone(typeReference));
						ext.setUniParent(typeReference.parent());
						TypeParameter newP = new InstantiatedTypeParameter(par.name(),ext);
						V.replaceParameter(TypeParameter.class,par, newP);
					}
					if(F().subTypeOf(V)) {
					  GGConstraint recursive = new GGConstraint(ARef(), V);
					  parent().addGenerated(recursive);
						recursive.setUniParent(parent());
					  result.addAll(recursive.process());
					}
				} else {
					TypeParameter ithTypeParameterOfA = A().parameters(TypeParameter.class).get(index);
					if(ithTypeParameterOfA instanceof InstantiatedTypeParameter) {
						TypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfA).argument();
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
	
	@Override
	public String toString() {
		return this.ARef().toString() +" >> " +this.F().toString();
	}

}
