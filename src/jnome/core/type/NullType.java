package jnome.core.type;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.language.Language;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.variable.FormalParameter;
import chameleon.support.member.simplename.SimpleNameMethodHeader;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;

/**
 * @author Marko van Dooren
 */
public class NullType extends RegularType {

  public NullType(Language lang) {
    super(new SimpleNameSignature("null type"));
    addModifier(new Public());
    addInfixOperator("boolean", "==", "java.lang.Object",lang);
    addInfixOperator("boolean", "!=", "java.lang.Object",lang);
    addInfixOperator("java.lang.String", "+", "java.lang.String",lang);
  }
  
  private void addInfixOperator(String returnType, String symbol, String argType,Language lang) {
	  JavaTypeReference jtr =new JavaTypeReference(returnType);
	  Public pub = new Public();
     InfixOperator op = new InfixOperator(new SimpleNameMethodHeader(symbol),jtr);
     op.addModifier(pub);
     op.header().addParameter(new FormalParameter(new SimpleNameSignature("arg"), new JavaTypeReference(argType)));
     op.addModifier(new Native());
     add(op);
   }

  public boolean assignableTo(Type other) {
    return true; 
  }

  protected NullType cloneThis() {
    return new NullType(language());
  }
  
}
