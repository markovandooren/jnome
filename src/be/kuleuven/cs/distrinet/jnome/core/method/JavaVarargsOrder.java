package be.kuleuven.cs.distrinet.jnome.core.method;

/**
 * 
 */

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.relation.WeakPartialOrder;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;

public class JavaVarargsOrder extends WeakPartialOrder<List<Type>> {

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
