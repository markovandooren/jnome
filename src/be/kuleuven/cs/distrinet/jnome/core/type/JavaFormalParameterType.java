package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.generics.FormalParameterType;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;

public class JavaFormalParameterType extends FormalParameterType implements JavaType {

  public JavaFormalParameterType(String name, Type aliasedType, FormalTypeParameter param) {
    super(name, aliasedType, param);
  }

  @Override
  public Type erasure() {
    return this;
  }
  
  @Override
  public FormalParameterType cloneSelf() {
    return new JavaFormalParameterType(name(), aliasedType(), parameter());
  }

  @Override
  public boolean upperBoundAtLeastAsHighAs(Type other, TypeFixer trace) throws LookupException {
    if(trace.contains(other, parameter())) {
      return true;
    }
    trace.add(other, parameter());
    return other.upperBoundNotHigherThan(aliasedType(), trace);
  }

//  @Override
//  public Type upperBound() throws LookupException {
//    return parameter().upperBound();
//  }
//
//  @Override
//  public Type lowerBound() throws LookupException {
//    return parameter().lowerBound();
//  }
}
