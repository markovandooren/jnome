package org.aikodi.java.core.method;

/**
 * 
 */

import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.relation.WeakPartialOrder;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.java.core.type.ArrayType;

public class JavaVarargsOrder implements WeakPartialOrder<List<Type>> {

	public static JavaVarargsOrder create() {
		return _protoType; 
	}
	
	private static JavaVarargsOrder _protoType = new JavaVarargsOrder();
	
	private JavaVarargsOrder() {
		
	}
	
	@Override
  public boolean contains(List<Type> first, List<Type> second)
      throws LookupException {
  	// for varags, the last type of the second list is an array type.
    int size = first.size();
		int nbNormalArguments = second.size() - 1;
		boolean result = size >= nbNormalArguments;
    for(int i = 0; result && i < nbNormalArguments; i++) {
      result = result && first.get(i).assignableTo(second.get(i));
    }
    Type other = ((ArrayType) second.get(nbNormalArguments)).elementType();
    for(int i = nbNormalArguments; result && i < size; i++) {
			result = result && first.get(i).assignableTo(other);
    }
    return result;
  }
}
