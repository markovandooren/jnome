package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.method.SimpleNameMethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.operator.infix.InfixOperator;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Native;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.util.Pair;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;

/**
 * @author Marko van Dooren
 */
public class NullType extends RegularType implements JavaType {

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
    return other.isTrue(language(Java.class).REFERENCE_TYPE); 
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
  	return other.isTrue(language(Java.class).REFERENCE_TYPE);
  }

	/**
	 * The erasure of a null type is the null type itself.
	 */
 /*@
   @ public behavior
   @
   @ post \result == this;
   @*/
	public Type erasure() {
		return this;
	}

}
