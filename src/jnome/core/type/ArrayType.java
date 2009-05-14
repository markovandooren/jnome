package jnome.core.type;


import chameleon.core.MetamodelException;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.inheritance.SubtypeRelation;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.support.modifier.Final;

/**
 * @author Marko van Dooren
 */
public class ArrayType extends RegularType {
//	TODO: this class should *NOT* be a member. This is just a quickfix
  public ArrayType(Type type, int dimension) {
    super(new SimpleNameSignature(getArrayName(type.getName(), dimension)));
    //FIXME: copy the modifiers?
    //addModifier(type.getAccessModifier());
    _arrayDimension = dimension;
    _type = type;
    setUniParent(type.parent());
    JavaTypeReference jtr =new JavaTypeReference("int");
    RegularMemberVariable var = new RegularMemberVariable(new SimpleNameSignature("length"), jtr);
    var.addModifier(new Final());
    add(var);
    addInheritanceRelation(new SubtypeRelation(new JavaTypeReference("java.lang.Object")));
    addInheritanceRelation(new SubtypeRelation(new JavaTypeReference("java.lang.Cloneable")));
    addInheritanceRelation(new SubtypeRelation(new JavaTypeReference("java.io.Serializable")));
  }
  
	@Override
	public ArrayType clone() {
		ArrayType result = cloneThis();
		result.copyContents(this);
		return result;
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
  	TypeReference ref = new TypeReference("java.lang.Object");
  	ref.setUniParent(getNamespace().rootNamespace());
  	Type objType = ref.getType();
    return super.assignableTo(other) ||
           ( // Reference type
             getElementType().subTypeOf(objType) &&
             (other instanceof ArrayType) &&
             getComponentType().assignableTo(((ArrayType)other).getComponentType())
           ) ||           
           ( // Primitive type
             (! getElementType().subTypeOf(objType)) &&
             (other instanceof ArrayType) &&
             getComponentType().equals(((ArrayType)other).getComponentType()) &&
             ((ArrayType)other).getDimension() == getDimension()
           );
  }

  protected ArrayType cloneThis() {
    return new ArrayType(getComponentType(),getDimension());
  }
  
//  public AccessibilityDomain getTypeAccessibilityDomain() throws MetamodelException {
//    return getElementType().getTypeAccessibilityDomain();
//  }
  
}
