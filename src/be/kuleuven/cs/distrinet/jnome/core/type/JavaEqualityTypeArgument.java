package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.EqualityTypeArgument;
import org.aikodi.chameleon.util.CreationStackTrace;

public class JavaEqualityTypeArgument extends EqualityTypeArgument {

  public JavaEqualityTypeArgument(TypeReference ref) {
    super(ref);
  }

  @Override
  protected JavaEqualityTypeArgument cloneSelf() {
    return new JavaEqualityTypeArgument(null);
  }

}
