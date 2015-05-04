package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.type.IntersectionTypeReference;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class ArrayTypeReference  extends ElementImpl implements JavaTypeReference {
	
	/**
	 * If the given a
	 * @param elementType
	 * @param arrayDimension
	 */
 /*@
   @ public behavior
   @
   @ pre arrayDimension >= 1;
   @*/
	public ArrayTypeReference(JavaTypeReference elementType, int arrayDimension) {
		if(arrayDimension > 1) {
			set(_elementType,new ArrayTypeReference(elementType, arrayDimension - 1));
		} else {
			set(_elementType,elementType);
		}
	}
	
	public ArrayTypeReference(JavaTypeReference componentType) {
		set(_elementType,componentType);
	}
	
//  public int arrayDimension() {
//  	return 1+elementTypeReference().arrayDimension();
//  }
  
  public JavaTypeReference elementTypeReference() {
  	return _elementType.getOtherEnd();
  }
  
  private Single<JavaTypeReference> _elementType = new Single<JavaTypeReference>(this);

	public JavaTypeReference erasedReference() {
		JavaTypeReference erasedReference = elementTypeReference().erasedReference();
		ArrayTypeReference result = new ArrayTypeReference(erasedReference);
		return result;
	}

//	public Type erasure() throws LookupException {
//		return new ArrayType(elementTypeReference().erasure());
//	}

	public JavaTypeReference toArray(int dimension) {
  	JavaTypeReference result;
  	if(dimension > 0) {
  	  result = new ArrayTypeReference(clone(this), dimension);
  	} else {
  		result = this;
  	}
  	return result;
	}

	protected ArrayTypeReference cloneSelf() {
		return new ArrayTypeReference(null);
	}

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

	public Type getElement() throws LookupException {
		return ArrayType.create(elementTypeReference().getElement());
	}

	public Type getType() throws LookupException {
		return getElement();
	}

	public TypeReference intersectionDoubleDispatch(TypeReference other) {
		IntersectionTypeReference intersectionTypeReference = language(Java7.class).createIntersectionReference(clone(this), clone(other));
		return intersectionTypeReference;
	}

	public Declaration getDeclarator() throws LookupException {
		return getElement();
	}

	public JavaTypeReference componentTypeReference() {
		return elementTypeReference().componentTypeReference();
	}

	@Override
	public String toString() {
		return elementTypeReference().toString()+"[]";
	}

	@Override
	public LookupContext targetContext() throws LookupException {
		return getElement().targetContext();
	}
}
