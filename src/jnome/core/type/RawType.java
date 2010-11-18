package jnome.core.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jnome.core.language.Java;

import org.rejuse.association.SingleAssociation;
import org.rejuse.logic.ternary.Ternary;

import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.method.Method;
import chameleon.core.reference.SimpleReference;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeElement;
import chameleon.oo.type.TypeWithBody;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.util.Pair;

public class RawType extends TypeWithBody implements JavaType {

	public static RawType create(Type original) {
		while(original != original.origin()) {
			original = (Type) original.origin();
		}
		Java language = original.language(Java.class);
		RawType result = language.getRawCache(original);
		if(result == null) {
			if(original instanceof RawType) {
				result = (RawType) original;
			} else {
				if(original.is(language.INSTANCE) == Ternary.TRUE) {
					Type outmostType = original.farthestAncestor(chameleon.oo.type.Type.class);
					if(outmostType == null) {
						outmostType = original;
					}
					RawType outer;
					if(outmostType instanceof RawType) {
						outer = (RawType) outmostType;
					} else {
						outer = new RawType(outmostType);
					}
					RawType current = outer;
					List<Type> outerTypes = original.ancestors(Type.class);
					outerTypes.add(0, original);

					int size = outerTypes.size();
					for(int i = size - 2; i>=0;i--) {
						SimpleReference<RawType> simpleRef = new SimpleReference<RawType>(outerTypes.get(i).signature().name(), RawType.class);
						simpleRef.setUniParent(current);
						try {
							current = simpleRef.getElement();
						} catch (LookupException e) {
							e.printStackTrace();
							throw new ChameleonProgrammerException("An inner type of a newly created outer raw type cannot be found",e);
						}
					}
					result = current;
				} else {
					// static
					result = new RawType(original);
				}
			}
			language.putRawCache(original, result);
		}
		return result;
	}

	private List<Member> _implicitMembers;
	
	@Override
	public List<Member> implicitMembers() {
		return new ArrayList<Member>(_implicitMembers);
	}
	
	@Override
	public <D extends Member> List<D> implicitMembers(DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(Collections.unmodifiableList(_implicitMembers));
	}



	/**
	 * Create a new raw type. The type parameters, super class and interface references, 
	 * and all members will be erased according to the definitions in the JLS.
	 */
	private RawType(Type original) {
		// first copy everything
		super(original.signature().clone());
		copyContents(original, true);
		copyImplicitMembers(original);
		_baseType = original;
		setUniParent(original.parent());
		setOrigin(original);
		// then erase everything.
		// 1) inheritance relations
		eraseInheritanceRelations();
		// 2) type parameters
		eraseTypeParameters(parameters(TypeParameter.class));
		// 3) members
		eraseMethods();
		// 4) member types
		makeDescendantTypesRaw();
	}
	
	private RawType(Type original, boolean useless) {
		super(original.signature().clone());
		copyContents(original, true);
		copyImplicitMembers(original);
		_baseType = original;
		setOrigin(original);

		setUniParent(original.parent());
		// 1) inheritance relations
		eraseInheritanceRelations();
		// 2) type parameters
		eraseTypeParameters(parameters(TypeParameter.class));
		// 3) members
		eraseMethods();
		// 4) member types
		setUniParent(null);
	}
	
	private void copyImplicitMembers(Type original) {
		_implicitMembers = new ArrayList<Member>();
		List<Member> implicits = original.implicitMembers();
		for(Member m: implicits) {
			Member clone = m.clone();
			clone.setUniParent(body());
			_implicitMembers.add(clone);
		}
	}

	private void makeDescendantTypesRaw() {
		List<Type> childTypes = directlyDeclaredElements(Type.class);
		Java language = language(Java.class);
		for(Type type:childTypes) {
			if(type.is(language.INSTANCE) == Ternary.TRUE) {
			  // create raw type that does not erase anything
			  RawType raw = new RawType((Type) type.origin(),false);
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
//				for(TypeParameter tp: method.typeParameters()) {
//					if(tp instanceof FormalTypeParameter) {
//						System.out.println("Nie zjust!");
//					}
//				}
				for(FormalParameter param: method.formalParameters()) {
					JavaTypeReference typeReference = (JavaTypeReference) param.getTypeReference();
					param.setTypeReference(typeReference.erasedReference());
				}
				// erase return type reference
				method.setReturnTypeReference(((JavaTypeReference)method.returnTypeReference()).erasedReference());
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
		Java language = language(Java.class);
		for(TypeParameter typeParameter: parameters) {
			FormalTypeParameter param = (FormalTypeParameter) typeParameter;
			JavaTypeReference upperBoundReference = (JavaTypeReference) param.upperBoundReference();
			JavaTypeReference erased = upperBoundReference.erasedReference();
			BasicTypeArgument argument = language.createBasicTypeArgument(erased);
			ErasedTypeParameter newParameter = new ErasedTypeParameter(typeParameter.signature().clone(),argument);
			argument.setUniParent(newParameter);
			SingleAssociation parentLink = typeParameter.parentLink();
			parentLink.getOtherRelation().replace(parentLink, newParameter.parentLink());
		}
	}

	@Override
	public Type clone() {
		return new RawType(baseType());
	}
	
	public boolean uniSameAs(Element otherType) throws LookupException {
		return (otherType instanceof RawType) && (baseType().sameAs(((RawType)otherType).baseType()));
	}
	
	@Override
	public int hashCode() {
		return 87937+baseType().hashCode();
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

	/**
	 * The erasure of a raw type is the raw type itself.
	 */
 /*@
   @ public behavior
   @
   @ post \result == this;
   @*/
	public Type erasure() {
		return this;
	}

	public boolean uniSameAs(Type aliasedType, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		return uniSameAs(aliasedType);
	}

}
