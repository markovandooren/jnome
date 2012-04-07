package jnome.core.type;

import jnome.core.language.Java;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.ElementImpl;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.association.Single;

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
  	  result = new ArrayTypeReference(clone(), dimension);
  	} else {
  		result = this;
  	}
  	return result;
	}

	public ArrayTypeReference clone() {
		return new ArrayTypeReference(elementTypeReference().clone());
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	public Type getElement() throws LookupException {
		return new ArrayType(elementTypeReference().getElement());
	}

	public Type getType() throws LookupException {
		return getElement();
	}

	public TypeReference intersection(TypeReference other) {
		return other.intersectionDoubleDispatch(this);
	}

	public TypeReference intersectionDoubleDispatch(TypeReference other) {
		IntersectionTypeReference intersectionTypeReference = language(Java.class).createIntersectionReference(clone(), other.clone());
		return intersectionTypeReference;
	}

	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference other) {
		IntersectionTypeReference result = other.clone();
		result.add(clone());
		return result;
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
	public LookupStrategy targetContext() throws LookupException {
		return getElement().targetContext();
	}
}
