package jnome.core.type;

import java.util.List;

import chameleon.core.method.Method;
import chameleon.core.type.DerivedType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeElement;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.type.inheritance.InheritanceRelation;
import chameleon.core.variable.FormalParameter;

public class RawType extends DerivedType {

	/**
	 * Create a new raw type. The type parameters, super class and interface references, 
	 * and all members will be erased according to the definitions in the JLS.
	 */
	public RawType(Type original) {
		// first copy everything
		super(original);
		// then erase everything.
		// 1) inheritance relations
		eraseInheritanceRelations();
		// 2) type parameters
		eraseTypeParameters(parameters());
		// 3) members
		eraseMethods();
	}

	private void eraseMethods() {
		for(TypeElement element: directlyDeclaredElements()) {
			if(element instanceof Method) {
				Method<?,?,?,?> method = (Method)element;
				eraseTypeParameters(method.typeParameters());
				for(FormalParameter param: method.formalParameters()) {
					JavaTypeReference typeReference = (JavaTypeReference) param.getTypeReference();
					param.setTypeReference(typeReference.erasedReference());
				}
			}
		}
	}

	private void eraseInheritanceRelations() {
		for(InheritanceRelation relation: inheritanceRelations()) {
			JavaTypeReference superClassReference = (JavaTypeReference) relation.superClassReference();
			relation.setSuperClassReference(superClassReference.erasedReference());
		}
	}

	private void eraseTypeParameters(List<TypeParameter> parameters) {
		for(TypeParameter typeParameter: parameters) {
			FormalTypeParameter param = (FormalTypeParameter) typeParameter;
			JavaTypeReference upperBoundReference = (JavaTypeReference) param.upperBoundReference();
			JavaTypeReference erased = upperBoundReference.erasedReference();
			replaceParameter(typeParameter, new InstantiatedTypeParameter(typeParameter.signature().clone(),new BasicTypeArgument(erased)));
		}
	}
}
