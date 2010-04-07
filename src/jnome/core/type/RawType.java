package jnome.core.type;

import java.util.Collection;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.type.AbstractType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeElement;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.type.inheritance.InheritanceRelation;
import chameleon.core.variable.FormalParameter;

public class RawType extends AbstractType {

	
	/**
	 * Create a new raw type. The type parameters, super class and interface references, 
	 * and all members will be erased according to the definitions in the JLS.
	 */
	private RawType(Type original) {
		// first copy everything
		super(original.sign ature().clone());
		copyContents(original, true);
		_baseType = original;
		setOrigin(original);
		// then erase everything.
		// 1) inheritance relations
		eraseInheritanceRelations();
		// 2) type parameters
		eraseTypeParameters(parameters());
		// 3) members
		eraseMethods();
		// 4) member types
	}

	private Type _baseType;
	
	@Override
	public Type baseType() {
		return _baseType;
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

	@Override
	public Type clone() {
		return new RawType(baseType());
	}
	
	public boolean uniSameAs(Element otherType) throws LookupException {
		return (otherType instanceof RawType) && (baseType().sameAs(((RawType)otherType).baseType()));
	}

	public boolean convertibleThroughUncheckedConversionAndSubtyping(Type second) throws LookupException {
		Collection<Type> supers = getAllSuperTypes();
		supers.add(this);
		for(Type type: supers) {
			if(type.baseType().sameAs(second.baseType())) {
				return true;
			}
		}
		return false;
	}
}
