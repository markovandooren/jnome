package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.InstantiatedParameterType;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

public class JavaInstantiatedParameterType extends InstantiatedParameterType implements JavaType {

  
  public JavaInstantiatedParameterType(String name, Type aliasedType, TypeParameter parameter) {
    super(name, aliasedType, parameter);
  }

  @Override
  public JavaInstantiatedParameterType cloneSelf() {
    return new JavaInstantiatedParameterType(name(), aliasedType(),parameter());
  }

  @Override
  public Type erasure() {
    return this;
  }
  
//  @Override
//  public Type lowerBound() throws LookupException {
//    return aliasedType().lowerBound();
//  }
//
//  @Override
//  public Type upperBound() throws LookupException {
//    return aliasedType().upperBound();
//  }
}
