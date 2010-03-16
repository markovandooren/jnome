/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.List;

import jnome.core.type.JavaTypeReference;
import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;

public class GGConstraint extends FirstPhaseConstraint {

	public GGConstraint(Type type, JavaTypeReference tref) {
		super(type,tref);
	}

	@Override
	public List<SecondPhaseConstraint> processSpecifics() throws LookupException {
		return null;
	}
	
	public SubtypeConstraint FequalsTj(Declaration declarator, Type type) {
		return new SubtypeConstraint((TypeParameter) declarator, type);
	}

	public FirstPhaseConstraint Array(Type componentType, JavaTypeReference componentTypeReference) {
		return new GGConstraint(componentType, componentTypeReference);
	}

	@Override
	public void caseSSFormalBasic(List<SecondPhaseConstraint> result, JavaTypeReference U, int index) throws LookupException {
		try {
			if(A().parameters().isEmpty()) {
				// If A is an instance of a non-generic type, then no constraint is implied on Tj.
			} else {
				
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