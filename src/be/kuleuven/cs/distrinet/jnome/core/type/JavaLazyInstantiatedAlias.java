package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.LazyInstantiatedAlias;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

public class JavaLazyInstantiatedAlias extends LazyInstantiatedAlias implements JavaType {

  public JavaLazyInstantiatedAlias(String name, TypeParameter param) {
    super(name, param);
  }

  @Override
  public Type erasure() {
    return this;
  }
  
  @Override
  public JavaLazyInstantiatedAlias cloneSelf() {
    return new JavaLazyInstantiatedAlias(name(), parameter());
  }

}
