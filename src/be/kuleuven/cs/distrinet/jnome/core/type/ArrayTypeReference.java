package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.HashSet;
import java.util.Set;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.util.association.Single;

/**
 * A class of references to array types.
 * 
 * @author Marko van Dooren
 */
public class ArrayTypeReference  extends ElementImpl implements JavaTypeReference {
	
	/**
	 * <p>Create a new reference to an array with the given dimension of objects of 
	 * the given element type.</p>
	 * 
	 * <p>If the array dimension is N, and N is greater than 1, N-1 additional
	 * array type references will be created such that each array dimension
	 * is represented by one array type reference.</p>
	 * 
	 * @param elementType A reference to the type of the elements in the array. 
	 *                    The reference cannot be null. 
	 * @param arrayDimension The dimension of the array. The dimension must be
	 *                    greater than 0.
	 */
 /*@
   @ public behavior
   @
   @ pre arrayDimension > 0;
   @*/
	public ArrayTypeReference(JavaTypeReference elementType, int arrayDimension) {
		if(arrayDimension > 1) {
			set(_elementType,new ArrayTypeReference(elementType, arrayDimension - 1));
		} else {
			set(_elementType,elementType);
		}
	}
	
	public ArrayTypeReference(JavaTypeReference elementType) {
		this(elementType,1);
	}
	
  public JavaTypeReference elementTypeReference() {
  	return _elementType.getOtherEnd();
  }
  
  private Single<JavaTypeReference> _elementType = new Single<JavaTypeReference>(this);

	public JavaTypeReference erasedReference() {
		JavaTypeReference erasedReference = elementTypeReference().erasedReference();
		ArrayTypeReference result = new ArrayTypeReference(erasedReference);
		return result;
	}

	protected ArrayTypeReference cloneSelf() {
		return new ArrayTypeReference(null);
	}

	public Type getElement() throws LookupException {
		return ArrayType.create(elementTypeReference().getElement());
	}


	public JavaTypeReference componentTypeReference() {
		return elementTypeReference().componentTypeReference();
	}

	@Override
	public String toString(Set<Element> visited) {
		return elementTypeReference().toString(visited)+"[]";
	}

	public String toString() {
		return toString(new HashSet<>());
	}

}
