package org.aikodi.java.core.type;

import org.aikodi.chameleon.oo.type.TypeReference;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public interface JavaTypeReference extends TypeReference {

  default JavaTypeReference toArray(int arrayDimension) {
    JavaTypeReference result;
    if(arrayDimension > 0) {
      result = new ArrayTypeReference(clone(this), arrayDimension);
    } else {
      result = this;
    }
    return result;
  }
	
	TypeReference erasedReference();
}
