package be.kuleuven.cs.distrinet.jnome.core.expression;

import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.association.OrderedMultiAssociation;
import be.kuleuven.cs.distrinet.rejuse.java.collections.Visitor;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

/**
 * @author Marko van Dooren
 */
public class ArrayCreationExpression extends Expression {
	
  public ArrayCreationExpression(JavaTypeReference type) {
    setTypeReference(type);
  }

	/**
	 * TYPE
	 *
	 */
	private Single<JavaTypeReference> _typeReference = new Single<JavaTypeReference>(this);


  public JavaTypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

  public void setTypeReference(JavaTypeReference type) {
  	set(_typeReference,type);
  }

	/**
	 * DIMENSION INITIALIZERS
	 */
	private Multi<ArrayIndex> _dimensionInitializers = new Multi<ArrayIndex>(
		this);


  public OrderedMultiAssociation getDimensionInitializersLink() {
    return _dimensionInitializers;
  }

  public void addDimensionInitializer(ArrayIndex init) {
  	add(_dimensionInitializers,init);
  }

  public void removeDimensionInitializer(ArrayIndex init) {
  	remove(_dimensionInitializers,init);
  }

  public List<ArrayIndex> getDimensionInitializers() {
    return _dimensionInitializers.getOtherEnds();
  }

	/**
	 * ARRAY INITIALIZER
	 *
	 * @uml.property name="_init"
	 * @uml.associationEnd
	 * @uml.property name="_init" multiplicity="(0 -1)" elementType="org.jnome.mm.expression.ArrayInitializer"
	 */
	private Single<ArrayInitializer> _init = new Single<ArrayInitializer>(this);

  public ArrayInitializer getInitializer() {
    return _init.getOtherEnd();
  }

  public void setInitializer(ArrayInitializer initializer) {
    set(_init,initializer);
  }

  protected Type actualType() throws LookupException {
    JavaTypeReference tref = getTypeReference();
    if(tref != null) {
		  return new ArrayType(tref.getElement(), _dimensionInitializers.size());
    } else {
    	throw new LookupException("Type reference of array creation expression is null.");
    }
  }

  public ArrayCreationExpression cloneSelf() {
    return new ArrayCreationExpression(null);
  }

  public Set<Type> getDirectExceptions() throws LookupException {
  	TypeReference ref = language(Java.class).createTypeReferenceInNamespace("java.lang.NegativeArraySizeException", view().namespace());
  	ref.setUniParent(namespace().defaultNamespace());
    return Util.createNonNullSet(ref.getType());
  }

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    AccessibilityDomain result = getTypeReference().getType().getTypeAccessibilityDomain();
//    Iterator iter = getDimensionInitializers().iterator();
//    while(iter.hasNext()) {
//      DimensionInitializer init = (DimensionInitializer)iter.next();
//      if(init != null) {
//        result = result.intersect(init.getExpression().getAccessibilityDomain());
//      }
//    }
//    if(getInitializer() != null) {
//      result = result.intersect(getInitializer().getAccessibilityDomain());
//    }
//    return result;
//  }

}
