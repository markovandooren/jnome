package jnome.core.expression;

import java.util.List;
import java.util.Set;

import jnome.core.type.ArrayType;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;
import org.rejuse.java.collections.Visitor;

import chameleon.core.element.Element;
import chameleon.core.expression.Assignable;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.language.ObjectOrientedLanguage;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.support.expression.ArrayIndex;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class ArrayAccessExpression extends Expression<ArrayAccessExpression> implements Assignable<ArrayAccessExpression,Element> {

  public ArrayAccessExpression(Expression target) {
    setTarget(target);
  }

	private SingleAssociation<ArrayAccessExpression,Expression> _target = new SingleAssociation<ArrayAccessExpression,Expression>(this);


  public Expression getTarget() {
    return _target.getOtherEnd();
  }

  public void setTarget(Expression expression) {
    _target.connectTo(expression.parentLink());
  }

	/**
	 * INDICES
	 */
	private OrderedMultiAssociation<ArrayAccessExpression,ArrayIndex> _indicesLink = new OrderedMultiAssociation<ArrayAccessExpression,ArrayIndex>(this);

  public OrderedMultiAssociation getIndicesLink() {
    return _indicesLink;
  }

  public void addIndex(ArrayIndex index) {
	    _indicesLink.add(index.parentLink());
	  }

	  public void removeIndex(ArrayIndex index) {
	    _indicesLink.remove(index.parentLink());
	  }
	  
  public List<ArrayIndex> getIndices() {
    return _indicesLink.getOtherEnds();
  }

  protected Type actualType() throws LookupException {
    ArrayType componentType = (ArrayType)getTarget().getType();
    int dim = componentType.getDimension() - getIndices().size();
    if (dim > 0) {
      Type result = new ArrayType(componentType.getComponentType(), dim);
      return result;
    }
    else {
      return componentType.getComponentType();
    }
  }

  public boolean superOf(InvocationTarget target) throws LookupException {
    if(!(target instanceof ArrayAccessExpression)) {
      return false;
    }
    ArrayAccessExpression acc =(ArrayAccessExpression)target;
    List varInits = getIndices();
    List otherVarInits = acc.getIndices();
    for(int i=0; i< varInits.size(); i++) {
      if(! ((InvocationTarget)varInits.get(i)).compatibleWith((InvocationTarget)otherVarInits.get(i))) {
        return false;
      }
    }
    if((getTarget() == null) && (acc.getTarget() == null)) {
      return true;
    } else if((getTarget() != null) && (acc.getTarget() != null)) {
      return getTarget().compatibleWith(acc.getTarget());
    } else {
      return false;
    }
  }

  public ArrayAccessExpression clone() {
    InvocationTarget target = null;
    if(getTarget() != null) {
      target = getTarget().clone();
    }
    final ArrayAccessExpression result = new ArrayAccessExpression((Expression)target);
    new Visitor() {
      public void visit(Object element) {
        //result.addIndex(((Expression)element).cloneExpr());
    	  result.addIndex(((ArrayIndex)element).clone());
      }
    }.applyTo(getIndices());
    return result;
  }

  public Assignable cloneAssignable() {
    return (Assignable)clone();
  }

  public List children() {
    List result = Util.createNonNullList(getTarget());
    result.addAll(getIndices());
    return result;
  }

  public Set getDirectExceptions() throws LookupException {
    return Util.createNonNullSet(language(ObjectOrientedLanguage.class).getNullInvocationException());
  }

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    AccessibilityDomain result = new All();
//    if(getTarget() != null) {
//      result = result.intersect(getTarget().getAccessibilityDomain());
//    }
//    Iterator iter = getIndices().iterator();
//    while(iter.hasNext()) {
//      result = result.intersect(((Expression)iter.next()).getAccessibilityDomain());
//    }
//    return result;
//  }
  
}
