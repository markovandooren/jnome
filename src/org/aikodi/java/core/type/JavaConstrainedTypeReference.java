package org.aikodi.java.core.type;

import org.aikodi.chameleon.oo.type.ConstrainedTypeReference;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;

public class JavaConstrainedTypeReference extends ConstrainedTypeReference implements JavaTypeReference {

  public JavaConstrainedTypeReference() {
    
  }
  
  @Override
  public JavaTypeReference erasedReference() {
    JavaTypeReference result = cloneSelf();
    constraints().forEach(c -> {
      final TypeConstraint clone = c.clone(c);
      clone.setTypeReference(((JavaTypeReference)c.typeReference()).erasedReference());
    });
    return result;
  }

  @Override
  protected JavaConstrainedTypeReference cloneSelf() {
    return new JavaConstrainedTypeReference();
  }


  {
    _typeConstraints.enableCache();
  }
  

}
