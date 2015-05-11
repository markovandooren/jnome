package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.FormalParameterType;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.LazyFormalAlias;

public class JavaLazyFormalAlias extends LazyFormalAlias implements JavaType {

  public JavaLazyFormalAlias(String name, FormalTypeParameter param) {
    super(name, param);
  }

  @Override
  public Type erasure() {
    return this;
  }
  
  @Override
  public FormalParameterType cloneSelf() {
    return new JavaLazyFormalAlias(name(), parameter());
  }

}
