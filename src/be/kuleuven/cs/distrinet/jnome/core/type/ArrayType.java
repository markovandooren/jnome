package be.kuleuven.cs.distrinet.jnome.core.type;


import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.method.SimpleNameMethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.RegularMemberVariable;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Final;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;

/**
 * @author Marko van Dooren
 */
public class ArrayType extends RegularType {
  public ArrayType(Type type) {
    super(new SimpleNameSignature(getArrayName(type.name())));
    //FIXME: copy the modifiers?
    //addModifier(type.getAccessModifier());
    _type = type;
    setUniParent(type.parent());
    Java language = type.language(Java.class);
		JavaTypeReference jtr = language.createTypeReference("int");
		
		// JLS7 10.7 Array Members
    RegularMemberVariable var = new RegularMemberVariable(new SimpleNameSignature("length"), jtr);
    var.addModifier(new Final());
    add(var);
    
    String fullyQualifiedName = type.getFullyQualifiedName();
		JavaTypeReference returnType = new ArrayTypeReference((JavaTypeReference) language.createTypeReference(fullyQualifiedName));
    Method clone = new NormalMethod(new SimpleNameMethodHeader("clone", returnType));
    add(clone);
    // JLS3 4.10.3 p.64
    // FIXME May these should be implicit inheritance relations. Not that important, though, as
    //       these cannot be manipulated by programmers anyway.
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
