package be.kuleuven.cs.distrinet.jnome.core.expression;

import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.support.expression.ArrayIndex;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;
import org.aikodi.rejuse.association.OrderedMultiAssociation;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;

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
    	throw new ChameleonProgrammerException("Type reference of array creation expression is null.");
    }
  }

  public ArrayCreationExpression cloneSelf() {
    return new ArrayCreationExpression(null);
  }

  public Set<Type> getDirectExceptions() throws LookupException {
  	TypeReference ref = language(Java7.class).createTypeReferenceInNamespace("java.lang.NegativeArraySizeException", view().namespace());
  	ref.setUniParent(root());
    return Util.createNonNullSet(ref.getElement());
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
