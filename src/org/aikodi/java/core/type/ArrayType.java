package org.aikodi.java.core.type;


import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguageImpl;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;
import org.aikodi.chameleon.oo.variable.RegularMemberVariable;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.modifier.Final;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.language.Java7;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @author Marko van Dooren
 */
public class ArrayType extends AbstractJavaType implements JavaType {
  ArrayType(Type type) {
    super(getArrayName(type.name()));
    //FIXME: copy the modifiers?
    //addModifier(type.getAccessModifier());
    _type = type;
    setUniParent(type.lexical().parent());
    Java7 language = type.language(Java7.class);
		JavaTypeReference jtr = language.createTypeReference("int");
		
		// JLS7 10.7 Array Members
    RegularMemberVariable var = new RegularMemberVariable("length", jtr);
    var.addModifier(new Final());
    add(var);
    
//    JavaTypeReference reference = (JavaTypeReference) language.reference(type);
//    reference.setUniParent(null);
    //FIXME: does this work correctly with respect to substitution during type interference?
    // I think it does.
    JavaTypeReference reference = new DirectJavaTypeReference(type);
		JavaTypeReference returnType = new ArrayTypeReference(reference);
    Method clone = new NormalMethod(new SimpleNameMethodHeader("clone", returnType));
    add(clone);
    // JLS3 4.10.3 p.64
    // FIXME Maybe these should be implicit inheritance relations. Not that important, though, as
    //       these cannot be manipulated by programmers anyway.
    addInheritanceRelation(new SubtypeRelation(language.createTypeReference("java.lang.Object")));
    addInheritanceRelation(new SubtypeRelation(language.createTypeReference("java.lang.Cloneable")));
    addInheritanceRelation(new SubtypeRelation(language.createTypeReference("java.io.Serializable")));
  }
  
  public static ArrayType create(Type type) {
  	if(type instanceof RegularJavaType) {
  		return ((RegularJavaType)type).toArray();
  	}
  	return new ArrayType(type);
  }
  
  public ArrayType(Type componentType, int dimension) throws LookupException {
  	this(consAux(componentType,dimension-1));
  }
  
  private static Type consAux(Type componentType, int dimension) throws LookupException {
  	if(dimension <0) {
  		throw new Error();
  	}
  	if(dimension == 0) {
  		return componentType;
  	} else {
  		return new ArrayType(componentType,dimension);
  	}
  }
  
  /**
	 * @param string
	 * @return
	 */
	private static String getArrayName(String string) {
		StringBuffer result = new StringBuffer(string);
        result.append("[]");
        return result.toString();
	}

	/**
	 * 
	 * @uml.property name="_type"
	 * @uml.associationEnd 
	 * @uml.property name="_type" multiplicity="(1 1)"
	 */
	private Type _type;

  
  public Type elementType() {
    return _type; 
  }
    
  public int dimension() {
    Type elementType = elementType();
		if(elementType instanceof ArrayType) {
			return 1+(((ArrayType) elementType).dimension());
		} else {
			return 1;
		}
  }
  
  @Override
  public boolean uniSameAs(Element o) throws LookupException {
    return (o instanceof ArrayType) &&
           ((ArrayType)o).elementType().sameAs(elementType());
  }
  
  @Override
  public int hashCode() {
  	return 1+elementType().hashCode();
  }
  
  public boolean assignableTo(Type other) throws LookupException {
  	View view = view();
  	ObjectOrientedLanguage language = view.language(ObjectOrientedLanguage.class);
		Type objType = language.getDefaultSuperClass(view.namespace());
    return super.assignableTo(other) ||
           ( // Reference type
             elementType().subtypeOf(objType) &&
             (other instanceof ArrayType) &&
             elementType().assignableTo(((ArrayType)other).elementType())
           ) ||           
           ( // Primitive type
             (! elementType().subtypeOf(objType)) &&
             (other instanceof ArrayType) &&
             elementType().equals(((ArrayType)other).elementType())
           );
  }

  protected ArrayType cloneThis() {
  	return new ArrayType(elementType());
  }

  @Override
  public Type erasure() {
    return create(((JavaType)elementType()).erasure());
  }

  @Override
  public boolean subtypeOf(Type other, TypeFixer trace) throws LookupException {
  	if(other instanceof ArrayType && isTrue(language(Java7.class).REFERENCE_TYPE)) {
  		ArrayType second2 = (ArrayType)other;
  		return this.elementType().subtypeOf(second2.elementType(),trace);
  	} else {
  		return super.subtypeOf(other, trace);
  	}
  }
  
	protected List<Declaration> buildImplicitMembersCache() {
		Builder<Declaration> builder = ImmutableList.<Declaration> builder();
		NormalMethod classMethod = getClassMethod(this);
		if (classMethod != null) {
			builder.add(classMethod);
		}
		return builder.build();
	}

    public TypeReference reference() {
        TypeReference reference = language(ObjectOrientedLanguage.class).reference(elementType());
        Element oldParent = reference.lexical().parent();
        reference.setUniParent(null);
        ArrayTypeReference result = new ArrayTypeReference((JavaTypeReference) reference);
        result.setUniParent(oldParent);
        return result;
    }
}
