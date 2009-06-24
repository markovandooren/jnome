package jnome.core.expression;


import org.rejuse.association.Reference;

import jnome.core.type.JavaTypeReference;
import chameleon.core.MetamodelException;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.type.TypeReference;
import chameleon.support.expression.LiteralWithTypeReference;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends LiteralWithTypeReference {

  public ClassLiteral(TypeReference tref) {
    super("class");
    setTarget(tref);
    setTypeReference(new JavaTypeReference("java.lang.Class"));
  }

  public boolean superOf(InvocationTarget target) throws MetamodelException {
    return (target instanceof ClassLiteral) && 
           ((ClassLiteral)target).getType().equals(getType());
  }

  public ClassLiteral clone() {
    ClassLiteral result = new ClassLiteral(target().clone());
    result.setTypeReference((JavaTypeReference)getTypeReference().clone());
    return result;
  }
 
	/**
	 * TARGET
	 */
	private Reference<LiteralWithTypeReference,TypeReference> _typeReference = new Reference<LiteralWithTypeReference,TypeReference>(this);

  
  public TypeReference target() {
    return _typeReference.getOtherEnd();
  }
  
  public void setTarget(TypeReference type) {
    _typeReference.connectTo(type.parentLink());
  }

//  public AccessibilityDomain getAccessibilityDomain() throws MetamodelException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
