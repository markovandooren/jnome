package jnome.core.expression;


import jnome.core.type.JavaTypeReference;
import chameleon.core.MetamodelException;
import chameleon.core.expression.InvocationTarget;
import chameleon.support.expression.LiteralWithTypeReference;

/**
 * @author Marko van Dooren
 */
public class ClassLiteral extends LiteralWithTypeReference {

  public ClassLiteral() {
    super("class");
    setTypeReference(new JavaTypeReference("java.lang.Class"));
  }

  public boolean superOf(InvocationTarget target) throws MetamodelException {
    return (target instanceof ClassLiteral) && 
           ((ClassLiteral)target).getType().equals(getType());
  }

  public ClassLiteral clone() {
    ClassLiteral result = new ClassLiteral();
    result.setTypeReference((JavaTypeReference)getTypeReference().clone());
    return result;
  }

//  public AccessibilityDomain getAccessibilityDomain() throws MetamodelException {
//    return getTypeReference().getType().getTypeAccessibilityDomain();
//  }
}
