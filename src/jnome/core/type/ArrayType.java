package jnome.core.type;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chameleon.core.MetamodelException;
import chameleon.core.accessibility.AccessibilityDomain;
import chameleon.core.type.Type;
import chameleon.core.type.TypeSignature;
import chameleon.core.variable.MemberVariable;
import chameleon.core.variable.VariableSignature;
import chameleon.support.modifier.Final;

/**
 * @author Marko van Dooren
 */
public class ArrayType extends Type {
//	TODO: this class should *NOT* be a member. This is just a quickfix
  public ArrayType(Type type, int dimension) {
    super(new TypeSignature(getArrayName(type.getName(), dimension)));
    //FIXME: copy the modifiers?
    //addModifier(type.getAccessModifier());
    _arrayDimension = dimension;
    _type = type;
    setUniParent(type.getParent());
    JavaTypeReference jtr =new JavaTypeReference("int");
    MemberVariable var = new MemberVariable(new VariableSignature("length"), jtr);
    var.addModifier(new Final());
    add(var);
    addSuperType(new JavaTypeReference("java.lang.Object"));
    addSuperType(new JavaTypeReference("java.lang.Cloneable"));
    addSuperType(new JavaTypeReference("java.io.Serializable"));
  }
  
  /**
	 * @param string
	 * @param dimension
	 * @return
	 */
	private static String getArrayName(String string, int dimension) {
		StringBuffer result = new StringBuffer(string);
    for(int i = 1; i<= dimension; i++) {
      result.append("[]"); 
    }
    return result.toString();
	}

	/**
	 * 
	 * @uml.property name="_type"
	 * @uml.associationEnd 
	 * @uml.property name="_type" multiplicity="(1 1)"
	 */
	private Type _type;

  
  public Type getComponentType() {
    return _type; 
  }
  
//	public boolean accessibleFrom(Element other) throws NotResolvedException {
//    return getComponentType().accessibleFrom(other);
//	}
  
  public Type getElementType() {
    if(getDimension() == 1) {
      return getComponentType(); 
    }
    else {
      return new ArrayType(getComponentType(), getDimension() - 1);
    }
  }
  
  public int getDimension() {
    return _arrayDimension; 
  }
  
  private int _arrayDimension;
  
  public boolean equals(Object o) {
    return (o instanceof ArrayType) &&
           ((ArrayType)o).getComponentType().equals(getComponentType()) &&
           ((ArrayType)o).getDimension() == getDimension();
  }
  
  public boolean assignableTo(Type other) throws MetamodelException {
    return super.assignableTo(other) ||
           ( // Reference type
             getElementType().subTypeOf(getNamespace().getDefaultNamespace().findType("java.lang.Object")) &&
             (other instanceof ArrayType) &&
             getComponentType().assignableTo(((ArrayType)other).getComponentType())
           ) ||           
           ( // Primitive type
             (! getElementType().subTypeOf(getNamespace().getDefaultNamespace().findType("java.lang.Object"))) &&
             (other instanceof ArrayType) &&
             getComponentType().equals(((ArrayType)other).getComponentType()) &&
             ((ArrayType)other).getDimension() == getDimension()
           );
  }

  protected Type cloneThis() {
    return new ArrayType(getComponentType(),getDimension());
  }
  
//  public AccessibilityDomain getTypeAccessibilityDomain() throws MetamodelException {
//    return getElementType().getTypeAccessibilityDomain();
//  }
  
}