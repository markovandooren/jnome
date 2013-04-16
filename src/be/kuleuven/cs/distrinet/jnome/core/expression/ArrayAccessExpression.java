package be.kuleuven.cs.distrinet.jnome.core.expression;

import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.rejuse.java.collections.Visitor;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.validation.BasicProblem;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Assignable;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.expression.ArrayIndex;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;

/**
 * @author Marko van Dooren
 */
public class ArrayAccessExpression extends Expression implements Assignable {

  public ArrayAccessExpression(Expression target) {
    setTarget(target);
  }

	private Single<Expression> _target = new Single<Expression>(this,false);


  public Expression getTarget() {
    return _target.getOtherEnd();
  }

  public void setTarget(Expression expression) {
    set(_target,expression);
  }

	/**
	 * INDICES
	 */
	private Multi<ArrayIndex> _indicesLink = new Multi<ArrayIndex>(this);

  public void addIndex(ArrayIndex index) {
  	add(_indicesLink,index);
  }

  public void removeIndex(ArrayIndex index) {
  	remove(_indicesLink,index);
  }
	  
  public List<ArrayIndex> getIndices() {
    return _indicesLink.getOtherEnds();
  }

  protected Type actualType() throws LookupException {
    ArrayType tmp = (ArrayType)getTarget().getType();
    int dim = getIndices().size();
    while (dim > 1) {
      tmp = (ArrayType) tmp.elementType();
      dim--;
    }
    return tmp.elementType();
  }

  public ArrayAccessExpression clone() {
    CrossReferenceTarget target = null;
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

  public Set getDirectExceptions() throws LookupException {
  	View view = view();
    ObjectOrientedLanguage language = view.language(ObjectOrientedLanguage.class);
		return Util.createNonNullSet(language.getNullInvocationException(view.namespace()));
  }

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		try {
			Type targetType = getTarget().getType();
			if(targetType instanceof ArrayType) {
				int targetDimension = ((ArrayType)targetType).dimension();
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