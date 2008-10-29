package jnome.core.type;

import chameleon.core.language.Language;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeSignature;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableSignature;
import chameleon.support.member.simplename.SimpleNameSignature;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;

/**
 * @author Marko van Dooren
 */
public class NullType extends RegularType {

  public NullType(Language lang) {
    super(new TypeSignature("null type"));
    addModifier(new Public());
    addInfixOperator("boolean", "==", "java.lang.Object",lang);
    addInfixOperator("boolean", "!=", "java.lang.Object",lang);
    addInfixOperator("java.lang.String", "+", "java.lang.String",lang);
  }
  
  private void addInfixOperator(String returnType, String symbol, String argType,Language lang) {
	  JavaTypeReference jtr =new JavaTypeReference(returnType);
	  Public pub = new Public();
     InfixOperator op = new InfixOperator(new SimpleNameSignature(symbol),jtr);
     op.addModifier(pub);
     op.signature().addParameter(new FormalParameter(new VariableSignature("arg"), new JavaTypeReference(argType)));
     op.addModifier(new Native());
     add(op);
   }

  public boolean assignableTo(Type other) {
    return true; 
  }

	public NullType clone() {
		NullType result = cloneThis();
		result.copyContents(this);
		return result;
	}

  protected NullType cloneThis() {
    return new NullType(language());
  }
  
}
