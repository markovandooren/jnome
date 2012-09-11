package jnome.core.type;

import java.util.List;

import jnome.core.language.Java;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.lookup.LookupException;
import chameleon.oo.method.SimpleNameMethodHeader;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.variable.FormalParameter;
import chameleon.support.member.simplename.operator.infix.InfixOperator;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Public;
import chameleon.util.Pair;

/**
 * @author Marko van Dooren
 */
public class NullType extends RegularType {

  public NullType(Java lang) {
    super("null type");
    addModifier(new Public());
    addInfixOperator("boolean", "==", "java.lang.Object",lang);
    addInfixOperator("boolean", "!=", "java.lang.Object",lang);
    addInfixOperator("java.lang.String", "+", "java.lang.String",lang);
  }
  
  private void addInfixOperator(String returnType, String symbol, String argType,Java lang) {
  	JavaTypeReference jtr = lang.createTypeReference(returnType);
  	Public pub = new Public();
  	InfixOperator op = new InfixOperator(new SimpleNameMethodHeader(symbol,jtr));
  	op.addModifier(pub);
  	op.header().addFormalParameter(new FormalParameter(new SimpleNameSignature("arg"), lang.createTypeReference(argType)));
  	op.addModifier(new Native());
  	add(op);
  }

  public boolean assignableTo(Type other) {
    return true; 
  }

  protected NullType cloneThis() {
    return new NullType((Java) language());
  }
  
  @Override
  public boolean upperBoundNotHigherThan(Type other, List<Pair<Type, TypeParameter>> trace) throws LookupException {
  	return true;
  }
  
  @Override
  public boolean auxSubTypeOf(Type other) throws LookupException {
  	return true;
  }

//	/**
//	 * The erasure of a null type is the null type itself.
//	 */
// /*@
//   @ public behavior
//   @
//   @ post \result == this;
//   @*/
//	public Type erasure() {
//		return this;
//	}

}
