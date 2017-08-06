package org.aikodi.java.core.type;

import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Declarator;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.tag.TagImpl;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.ClassWithBody;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.java.core.language.Java7;
import org.aikodi.rejuse.association.SingleAssociation;
import org.aikodi.rejuse.logic.ternary.Ternary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class RawType extends ClassWithBody implements JavaType {

  private ImmutableList<Declaration> _implicitMembers;

  @Override
  public List<Declaration> implicitMembers() {
    return _implicitMembers;
  }

  @Override
  public <D extends Declaration> List<? extends SelectionResult> implicitMembers(DeclarationSelector<D> selector) throws LookupException {
    return selector.selection(_implicitMembers);
  }

  /**
   * Create a new raw type. The type parameters, super class and interface references, 
   * and all members will be erased according to the definitions in the JLS.
   */
  public RawType(Type original) {
    // first copy everything
    super(original.name());
    copyContents(original, true);
    //		copyImplicitInheritanceRelations(original);
    copyImplicitMembers(original);
    _baseType = original;
    setUniParent(original.lexical().parent());
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

  /**
   * @{inheritDoc}
   */
  @Override
  public void setUniParent(Element parent) {
    super.setUniParent(parent);
  }

  @Override
  public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
    if(explicitNonMemberInheritanceRelations().isEmpty() && (! "Object".equals(name())) && (! getFullyQualifiedName().equals("java.lang.Object"))) {
      TypeReference objectTypeReference = language(ObjectOrientedLanguage.class).createTypeReference("java.lang.Object");
      InheritanceRelation relation = new SubtypeRelation(objectTypeReference);
      relation.setUniParent(this);
      relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
      List<InheritanceRelation> result = ImmutableList.of(relation);
      return result;
    } else {
      return Collections.EMPTY_LIST;
    }
  }

  @Override
  public String getFullyQualifiedName() {
  	return _baseType.getFullyQualifiedName();
  }
  
  @Override
  public boolean hasInheritanceRelation(InheritanceRelation relation) throws LookupException {
    return super.hasInheritanceRelation(relation) || relation.hasMetadata(IMPLICIT_CHILD);
  }

  public final static String IMPLICIT_CHILD = "IMPLICIT CHILD";

  //	private void copyImplicitInheritanceRelations(Type original) {
  //		for(InheritanceRelation i: original.implicitNonMemberInheritanceRelations()) {
  //			InheritanceRelation clone = i.clone();
  //			addInheritanceRelation(clone);
  //		}
  //	}

  private RawType(Type original, boolean useless) {
    super(original.name());
    copyContents(original, true);
    copyImplicitMembers(original);
    _baseType = original;
    setOrigin(original);

    setUniParent(original.lexical().parent());
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
    Builder<Declaration> builder = ImmutableList.<Declaration>builder();
    List<Declaration> implicits = original.implicitMembers();
    for(Declaration m: implicits) {
    	Declaration clone = clone(m);
      clone.setUniParent(body());
      builder.add(clone);
    }
    _implicitMembers = builder.build();
  }

  private void makeDescendantTypesRaw() {
    List<Type> childTypes = directlyDeclaredElements(Type.class);
    Java7 language = language(Java7.class);
    for(Type type:childTypes) {
      if(type.is(language.INSTANCE) == Ternary.TRUE) {
        // create raw type that does not erase anything
        RawType raw = new RawType((Type) type.origin(),false);
        SingleAssociation parentLink = type.parentLink();
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
    for(Declarator element: directlyDeclaredElements()) {
      if(element instanceof Method) {
        Method method = (Method)element;
        eraseTypeParameters(method.typeParameters());
        for(FormalParameter param: method.formalParameters()) {
          JavaTypeReference typeReference = (JavaTypeReference) param.getTypeReference();
          JavaTypeReference erasedReference = typeReference.erasedReference();
          param.setTypeReference(erasedReference);
        }
        // erase return type reference
        method.setReturnTypeReference(((JavaTypeReference)method.returnTypeReference()).erasedReference());
      }
    }
  }

  protected void eraseInheritanceRelations() {
    //FIXME Why aren't member inheritance relations such as subobjects erased?
    //      It probably has a reason but I was so stupid not to document it.
    for(SubtypeRelation relation: nonMemberInheritanceRelations(SubtypeRelation.class)) {
      JavaTypeReference superClassReference = (JavaTypeReference) relation.superClassReference();
      JavaTypeReference erasedReference = superClassReference.erasedReference();
      relation.setSuperClassReference(erasedReference);
    }
  }

  protected void eraseTypeParameters(List<TypeParameter> parameters) {
    Java7 language = language(Java7.class);
    for(TypeParameter typeParameter: parameters) {
      FormalTypeParameter param = (FormalTypeParameter) typeParameter;
      JavaTypeReference upperBoundReference = (JavaTypeReference) param.upperBoundReference();
      JavaTypeReference erased = upperBoundReference.erasedReference();
      EqualityTypeArgument argument = language.createEqualityTypeArgument(erased);
      ErasedTypeParameter newParameter = new ErasedTypeParameter(typeParameter.name(),argument);
      argument.setUniParent(newParameter);
      SingleAssociation parentLink = typeParameter.parentLink();
      parentLink.getOtherRelation().replace(parentLink, newParameter.parentLink());
    }
  }

  @Override
  protected RawType cloneSelf() {
    return new RawType(baseType());
  }

  public boolean uniSameAs(Element otherType) throws LookupException {
    return (otherType instanceof RawType) && uniSameAs(((RawType)otherType).baseType(), new TypeFixer());
  }

  @Override
  public int hashCode() {
    return 87937+baseType().hashCode();
  }

  public boolean convertibleThroughUncheckedConversionAndSubtyping(Type second) throws LookupException {
    return superTypeJudge().get(second) != null;
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

  @Override
  public boolean uniSameAs(Type otherType, TypeFixer trace) throws LookupException {
    return (otherType instanceof RawType) && (baseType().sameAs(((RawType)otherType).baseType(), trace));
  }


  @Override
  public boolean uniSupertypeOf(Type other, TypeFixer trace) throws LookupException {
    return other.superTypeJudge().get(this) != null;
  }
}
