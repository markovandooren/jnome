package jnome.core.type;

import java.util.List;

import jnome.core.language.Java;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.CreationStackTrace;
import chameleon.util.Util;

public class ArrayTypeReference  extends NamespaceElementImpl<ArrayTypeReference,Element> implements JavaTypeReference<ArrayTypeReference> {
	
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
			_elementType.connectTo(new ArrayTypeReference(elementType, arrayDimension - 1).parentLink());
		} else {
			_elementType.connectTo(elementType.parentLink());
		}
	}
	
	public ArrayTypeReference(JavaTypeReference componentType) {
		_elementType.connectTo(componentType.parentLink());
	}
	
//  public int arrayDimension() {
//  	return 1+elementTypeReference().arrayDimension();
//  }
  
  public JavaTypeReference elementTypeReference() {
  	return _elementType.getOtherEnd();
  }
  
  private SingleAssociation<ArrayTypeReference, JavaTypeReference> _elementType = new SingleAssociation<ArrayTypeReference, JavaTypeReference>(this);

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
		IntersectionTypeReference<IntersectionTypeReference> intersectionTypeReference = language(Java.class).createIntersectionReference(clone(), other.clone());
		return intersectionTypeReference;
	}

	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference<?> other) {
		IntersectionTypeReference<?> result = other.clone();
		result.add(clone());
		return result;
	}

	public Declaration getDeclarator() throws LookupException {
		return getElement();
//		return elementTypeReference().getDeclarator();
	}

	public List<? extends Element> children() {
		return Util.createNonNullList(elementTypeReference());
	}

	public JavaTypeReference componentTypeReference() {
		return elementTypeReference().componentTypeReference();
	}

//	@Override
//	public void setName(String name) {
//		elementTypeReference().setName(name);
//	}
}
