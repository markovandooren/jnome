package jnome.core.type;


import org.rejuse.logic.ternary.Ternary;

import jnome.core.language.Java;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.oo.variable.RegularMemberVariable;
import chameleon.support.modifier.Final;

/**
 * @author Marko van Dooren
 */
public class ArrayType extends RegularType {
//	TODO: this class should not be a member. This is just a quickfix
  public ArrayType(Type type) {
    super(new SimpleNameSignature(getArrayName(type.name())));
    //FIXME: copy the modifiers?
    //addModifier(type.getAccessModifier());
    _type = type;
    setUniParent(type.parent());
    Java language = type.language(Java.class);
		JavaTypeReference jtr = language.createTypeReference("int");
    RegularMemberVariable var = new RegularMemberVariable(new SimpleNameSignature("length"), jtr);
    var.addModifier(new Final());
    add(var);
    // JLS3 4.10.3 p.64
    addInheritanceRelation(new SubtypeRelation(language.createTypeReference("java.lang.Object")));
    addInheritanceRelation(new SubtypeRelation(language.createTypeReference("java.lang.Cloneable")));
    addInheritanceRelation(new SubtypeRelation(language.createTypeReference("java.io.Serializable")));
  }
  
  public ArrayType(Type componentType, int dimension) {
  	this(consAux(componentType,dimension-1));
  }
  
  private static Type consAux(Type componentType, int dimension) {
  	if(dimension <0) {
  		throw new Error();
  	}
  	if(dimension == 0) {
  		return componentType;
  	} else {
  		return new ArrayType(componentType,dimension);
  	}
  }
  
//	@Override
//	public ArrayType clone() {
//		ArrayType result = cloneThis();
//		result.copyContents(this);
//		return result;
//	}

  /**
	 * @param string
	 * @param dimension
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
  
//	public boolean accessibleFrom(Element other) throws NotResolvedException {
//    return getComponentType().accessibleFrom(other);
//	}
  
//  public Type getElementType() {
//    if(dimension() == 1) {
//      return elementType(); 
//    }
//    else {
//      return new ArrayType(elementType(), dimension() - 1);
//    }
//  }
  
  public int dimension() {
    Type elementType = elementType();
		if(elementType instanceof ArrayType) {
			return 1+(((ArrayType) elementType).dimension());
		} else {
			return 1;
		}
  }
//  
//  private int _arrayDimension;
  
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
  	Type objType = language(ObjectOrientedLanguage.class).getDefaultSuperClass();
    return super.assignableTo(other) ||
           ( // Reference type
             elementType().subTypeOf(objType) &&
             (other instanceof ArrayType) &&
             elementType().assignableTo(((ArrayType)other).elementType())
           ) ||           
           ( // Primitive type
             (! elementType().subTypeOf(objType)) &&
             (other instanceof ArrayType) &&
             elementType().equals(((ArrayType)other).elementType())
           );
  }

  protected ArrayType cloneThis() {
    return new ArrayType(elementType());
  }

//  @Override
//  public boolean auxSubTypeOf(Type second) throws LookupException {
//  	boolean result = false;
//  	if (second instanceof ArrayType && this.is(language(Java.class).REFERENCE_TYPE) == Ternary.TRUE) {
//  		ArrayType second2 = (ArrayType)second;
//  		result = elementType().subTypeOf(second2.elementType());
//  	} 
//  	return result;
//  }
  
  @Override
  	public boolean auxSubTypeOf(Type second) throws LookupException {
  	 boolean result = false;
  	 if (second instanceof ArrayType && is(language(Java.class).REFERENCE_TYPE) == Ternary.TRUE) {
  		 ArrayType second2 = (ArrayType)second;
  		 result = elementType().subTypeOf(second2.elementType());
  	 }
  	 if(! result) {
  		 result = super.auxSubTypeOf(second);
  	 }
  	 return result;
  	}
  
}
