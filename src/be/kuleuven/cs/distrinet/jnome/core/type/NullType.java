package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.UnionType;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.variable.FormalParameter;
import org.aikodi.chameleon.support.member.simplename.operator.infix.InfixOperator;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.chameleon.util.Pair;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

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
  public boolean upperBoundNotHigherThan(Type other, List<Pair<Type, TypeParameter>> trace) throws LookupException {
  	return true;
  }
  
  @Override
  public boolean properSubTypeOf(Type other) throws LookupException {
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
