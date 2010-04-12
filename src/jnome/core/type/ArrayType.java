package jnome.core.type;


import jnome.core.language.Java;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.inheritance.SubtypeRelation;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.support.modifier.Final;

/**
 * @author Marko van Dooren
 */
public class ArrayType extends RegularType {
//	TODO: this class should not be a member. This is just a quickfix
  public ArrayType(Type type, int dimension) {
    super(new SimpleNameSignature(getArrayName(type.getName(), dimension)));
    //FIXME: copy the modifiers?
    //addModifier(type.getAccessModifier());
    _arrayDimension = dimension;
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

  
  public Type componentType() {
    return _type; 
  }
  
//	public boolean accessibleFrom(Element other) throws NotResolvedException {
//    return getComponentType().accessibleFrom(other);
//	}
  
  public Type getElementType() {
    if(dimension() == 1) {
      return componentType(); 
    }
    else {
      return new ArrayType(componentType(), dimension() - 1);
    }
  }
  
  public int dimension() {
    return _arrayDimension; 
  }
  
  private int _arrayDimension;
  
  @Override
  public boolean uniSameAs(Element o) throws LookupException {
    return (o instanceof ArrayType) &&
           ((ArrayType)o).dimension() == dimension() &&
           ((ArrayType)o).componentType().sameAs(componentType());
  }
  
  public boolean assignableTo(Type other) throws LookupException {
  	Type objType = language(ObjectOrientedLanguage.class).getDefaultSuperClass();
    return super.assignableTo(other) ||
           ( // Reference type
             getElementType().subTypeOf(objType) &&
             (other instanceof ArrayType) &&
             componentType().assignableTo(((ArrayType)other).componentType())
           ) ||           
           ( // Primitive type
             (! getElementType().subTypeOf(objType)) &&
             (other instanceof ArrayType) &&
             componentType().equals(((ArrayType)other).componentType()) &&
             ((ArrayType)other).dimension() == dimension()
           );
  }

  protected ArrayType cloneThis() {
    return new ArrayType(componentType(),dimension());
  }
  
//  public AccessibilityDomain getTypeAccessibilityDomain() throws LookupException {
//    return getElementType().getTypeAccessibilityDomain();
//  }
  
}
