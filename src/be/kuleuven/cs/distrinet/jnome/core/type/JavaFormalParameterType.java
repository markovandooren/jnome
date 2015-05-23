package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.oo.type.Type;
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

}