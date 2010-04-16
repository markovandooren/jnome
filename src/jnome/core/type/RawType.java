package jnome.core.type;

import java.util.Collection;
import java.util.List;

import jnome.core.language.Java;

import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.modifier.Modifier;
import chameleon.core.reference.SimpleReference;
import chameleon.core.type.AbstractType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeElement;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.type.inheritance.InheritanceRelation;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;

public class RawType extends AbstractType {


	public static RawType create(Type original) {
		if(original instanceof RawType) {
			return (RawType) original;
		}
		Java language = original.language(Java.class);
		if(original.is(language.INSTANCE) == Ternary.TRUE) {
			Type outmostType = original.furthestAncestor(Type.class);
			if(outmostType == null) {
				outmostType = original;
			}
			RawType outer;
			if(outmostType instanceof RawType) {
				outer = (RawType) outmostType;
			} else {
				outer = new RawType(outmostType);;
			}
			RawType current = outer;
			List<Type> outerTypes = original.ancestors(Type.class);

			int size = outerTypes.size();
			for(int i = size - 1; i>=0;i--) {
				SimpleReference<RawType> simpleRef = new SimpleReference<RawType>(outerTypes.get(i).signature().name(), RawType.class);
				simpleRef.setUniParent(current);
				try {
					current = simpleRef.getElement();
				} catch (LookupException e) {
					e.printStackTrace();
					throw new ChameleonProgrammerException("An inner type of a newly created outer raw type cannot be found",e);
				}
			}
			return current;
		} else {
			// static
			return new RawType(original);
		}
	}

	/**
	 * Create a new raw type. The type parameters, super class and interface references, 
	 * and all members will be erased according to the definitions in the JLS.
	 */
	private RawType(Type original) {
		// first copy everything
		super(original.signature().clone());
		copyContents(original, true);
		_baseType = original;
		setUniParent(original.parent());
		setOrigin(original);
		// then erase everything.
		// 1) inheritance relations
		eraseInheritanceRelations();
		// 2) type parameters
		eraseTypeParameters(parameters());
		// 3) members
		eraseMethods();
		// 4) member types
		makeDescendantTypesRaw();
	}
	
	private RawType(Type original, boolean useless) {
		super(original.signature().clone());
		copyContents(original, true);
		_baseType = original;
		setOrigin(original);
		// no need to set the parent, it will be attacted to an outer type anyway.
	}
	
	private void makeDescendantTypesRaw() {
		List<Type> childTypes = directlyDeclaredElements(Type.class);
		Java language = language(Java.class);
		for(Type type:childTypes) {
			if(type.is(language.INSTANCE) == Ternary.TRUE) {
			  // create raw type that does not erase anything
			  RawType raw = new RawType(type,false);
			  SingleAssociation<Type, Element> parentLink = type.parentLink();
			  parentLink.getOtherRelation().replace(parentLink, raw.parentLink());
			  raw.makeDescendantTypesRaw();
			}
		}
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
			JavaTypeReference erasedReference = superClassReference.erasedReference();
			relation.setSuperClassReference(erasedReference);
		}
	}

	private void eraseTypeParameters(List<TypeParameter> parameters) {
		for(TypeParameter typeParameter: parameters) {
			FormalTypeParameter param = (FormalTypeParameter) typeParameter;
			JavaTypeReference upperBoundReference = (JavaTypeReference) param.upperBoundReference();
			JavaTypeReference erased = upperBoundReference.erasedReference();
			BasicTypeArgument argument = new BasicTypeArgument(erased);
			InstantiatedTypeParameter newParameter = new InstantiatedTypeParameter(typeParameter.signature().clone(),argument);
			replaceParameter(typeParameter, newParameter);
			argument.setUniParent(parent());
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
