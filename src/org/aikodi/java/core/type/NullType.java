package org.aikodi.java.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.UnionType;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperator;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.java.core.language.Java7;

/**
 * @author Marko van Dooren
 */
public class NullType extends RegularType implements JavaType {

  public NullType(Java7 lang) {
    super("null type");
    addModifier(new Public());
    addInfixOperator("boolean", "==", "java.lang.Object",lang);
    addInfixOperator("boolean", "!=", "java.lang.Object",lang);
    addInfixOperator("java.lang.String", "+", "java.lang.String",lang);
  }
  
  private void addInfixOperator(String returnType, String symbol, String argType,Java7 lang) {
  	JavaTypeReference jtr = lang.createTypeReference(returnType);
  	Public pub = new Public();
  	InfixOperator op = new InfixOperator(new SimpleNameMethodHeader(symbol,jtr));
  	op.addModifier(pub);
  	op.header().addFormalParameter(new FormalParameter("arg", lang.createTypeReference(argType)));
  	op.addModifier(new Native());
  	add(op);
  }

  public boolean assignableTo(Type other) {
    return other.isTrue(language(Java7.class).REFERENCE_TYPE); 
  }

  protected NullType cloneThis() {
    return new NullType((Java7) language());
  }
  
  @Override
  public boolean uniSubtypeOf(Type other, TypeFixer trace) throws LookupException {
  	return other.isTrue(language(Java7.class).REFERENCE_TYPE);
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
	
	@Override
	public Type union(Type type) throws LookupException {
		return type;
	}
	
	@Override
	public Type unionDoubleDispatch(Type type) throws LookupException {
		return type;
	}
	
	@Override
	public Type unionDoubleDispatch(UnionType type) throws LookupException {
		return type;
	}

}
