/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;

public class GGConstraint extends FirstPhaseConstraint {

	public GGConstraint(Type type, Type tref) {
		super(type,tref);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		return null;
	}
	
	public SubtypeConstraint FequalsTj(TypeParameter declarator, Type type) {
		return new SubtypeConstraint(declarator, type);
	}

	@Override
	public FirstPhaseConstraint Array(Type componentType, Type componentTypeReference) {
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
						
						// We know that F() is a derived type.
						Type GG = F().clone();
					  // replace the index-th parameter with a clone of type reference U.
						TypeParameter oldParameter = GG.parameters().get(index);
					  BasicTypeArgument actual = (BasicTypeArgument) U.parent();
						TypeParameter newParameter = new InstantiatedTypeParameter(oldParameter.signature().clone(), actual);
					  GG.replaceParameter(oldParameter, newParameter);
						Type V=typeWithSameBaseTypeAs(H, GG.getAllSuperTypes());
						if(F().subTypeOf(V)) {
						  GGConstraint recursive = new GGConstraint(A(), V);
						  result.addAll(recursive.process());
						}
					} else {
						TypeParameter ithTypeParameterOfG = G.parameters().get(index);
						if(ithTypeParameterOfG instanceof InstantiatedTypeParameter) {
							ActualTypeArgument arg = ((InstantiatedTypeParameter)ithTypeParameterOfG).argument();
							if(arg instanceof BasicTypeArgument) {
								EQConstraint recursive = new EQConstraint(((BasicTypeArgument)arg).typeReference().getElement(), U.getElement());
								result.addAll(recursive.process());
							} else if(arg instanceof ExtendsWildCard) {
								GGConstraint recursive = new GGConstraint(((ExtendsWildCard)arg).typeReference().getElement(), U.getElement());
								result.addAll(recursive.process());
							} else if(arg instanceof SuperWildCard) {
								SSConstraint recursive = new SSConstraint(((SuperWildCard)arg).typeReference().getElement(), U.getElement());
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
		compile
	}

	@Override
	public void caseSSFormalSuper(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		compile
	}

}