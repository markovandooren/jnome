package org.aikodi.java.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReferenceWithName;
import org.aikodi.chameleon.oo.type.BoxableTypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.workspace.View;
import org.aikodi.java.core.language.Java7;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public interface JavaTypeReference extends BoxableTypeReference {

  default JavaTypeReference toArray(int arrayDimension) {
    JavaTypeReference result;
    if(arrayDimension > 0) {
      result = new ArrayTypeReference(clone(this), arrayDimension);
    } else {
      result = this;
    }
    return result;
  }
	
	public JavaTypeReference erasedReference();
	
	public JavaTypeReference componentTypeReference();
	
	public default BoxableTypeReference box() throws LookupException {
	    Java7 language = language(Java7.class);
        return language.box(this, view().namespace());
	}
}
