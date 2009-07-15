package jnome.core.expression;

import java.util.List;
import java.util.Set;

import jnome.core.type.JavaTypeReference;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.association.Reference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.support.expression.ArrayIndex;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class ArrayCreationExpression extends Expression<ArrayCreationExpression>{

  public ArrayCreationExpression(JavaTypeReference type) {
    setTypeReference(type);
  }

	/**
	 * TYPE
	 *
	 */
	private Reference<ArrayCreationExpression,JavaTypeReference> _typeReference = new Reference<ArrayCreationExpression,JavaTypeReference>(this);


  public JavaTypeReference getTypeReference() {
    return _typeReference.getOtherEnd();
  }

    public void setTypeReference(JavaTypeReference type) {
        Reference<? extends TypeReference, ? super ArrayCreationExpression> tref = type.parentLink();
        Reference<? extends JavaTypeReference, ? super ArrayCreationExpression> ref = (Reference<? extends JavaTypeReference, ? super ArrayCreationExpression>)tref;
        _typeReference.connectTo(ref);
    }

	/**
	 * DIMENSION INITIALIZERS
	 */
	private OrderedReferenceSet<ArrayCreationExpression,ArrayIndex> _dimensionInitializers = new OrderedReferenceSet<ArrayCreationExpression,ArrayIndex>(
		this);


  public OrderedReferenceSet getDimensionInitializersLink() {
    return _dimensionInitializers;
  }

  public void addDimensionInitializer(ArrayIndex init) {
	    _dimensionInitializers.add(init.parentLink());
	  }

	  public void removeDimensionInitializer(ArrayIndex init) {
	    _dimensionInitializers.remove(init.parentLink());
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
	private Reference<ArrayCreationExpression,ArrayInitializer> _init = new Reference<ArrayCreationExpression,ArrayInitializer>(this);

  public ArrayInitializer getInitializer() {
    return _init.getOtherEnd();
  }

  public void setInitializer(ArrayInitializer initializer) {
    if (initializer != null) {
      _init.connectTo(initializer.parentLink());
    }
    else {
      _init.connectTo(null);
    }
  }

  public Type getType() throws LookupException {
    return getTypeReference().getType();
  }

  public boolean superOf(InvocationTarget target) throws LookupException {
    if(!(target instanceof ArrayCreationExpression)) {
      return false;
    }
    ArrayCreationExpression acc = (ArrayCreationExpression)target;
    if(! getType().equals(acc.getType())) {
      return false;
    }
    List dimInits = getDimensionInitializers();
    List otherDimInits = acc.getDimensionInitializers();
    for(int i=0; i< dimInits.size(); i++) {
      if(! ((InvocationTarget)dimInits.get(i)).compatibleWith((InvocationTarget)otherDimInits.get(i))) {
        return false;
      }
    }
    if((getInitializer() == null) && (acc.getInitializer() == null)) {
      return true;
    } else if((getInitializer() != null) && (acc.getInitializer() != null)) {
      return getInitializer().compatibleWith(acc.getInitializer());
    } else {
      return false;
    }
  }

  public ArrayCreationExpression clone() {
    final ArrayCreationExpression result = new ArrayCreationExpression((JavaTypeReference)getTypeReference().clone());
    if(getInitializer() != null) {
      result.setInitializer((ArrayInitializer)getInitializer().clone());
    }
    new Visitor() {
      public void visit(Object element) {
        //result.addDimensionInitializer(((DimensionInitializer)element).cloneDimInit());
    	  result.addDimensionInitializer(((ArrayIndex)element).clone());
      }
    }.applyTo(getDimensionInitializers());
    return result;
  }

 /*@
   @ also public behavior
   @
   @ post \result.containsAll(getDimensionInitializers());
   @ post getInitializer() != null ==> \result.contains(getInitializer());
   @*/
  public List<? extends Element> children() {
    final List<? extends Element> result = getDimensionInitializers();
    Util.addNonNull(getInitializer(), result);
    return result;
  }

  public Set getDirectExceptions() throws LookupException {
  	TypeReference ref = new TypeReference("java.lang.NegativeArraySizeException");
  	ref.setUniParent(getNamespace().defaultNamespace());
    return Util.createNonNullSet(ref.getType());
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
