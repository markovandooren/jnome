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
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
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

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		try {
			Type targetType = getTarget().getType();
			if(targetType instanceof ArrayType) {
				int targetDimension = ((ArrayType)targetType).getDimension();
				int dimension = getIndices().size();
				if(targetDimension < dimension) {
					result = result.and(new BasicProblem(this, "The array dimension of the type expression of the expression is smaller than the number of indices: "+targetDimension+" < "+dimension));
				}
			} else {
				result = result.and(new BasicProblem(this, "An array access can only be applied to an expression whose type is an array type. The found type is "+targetType.getFullyQualifiedName()));
			}
		} catch (LookupException e) {
			result = result.and(new BasicProblem(this, "Cannot compute the type of the target expression of the array access.")); 
		}
		return result;
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
