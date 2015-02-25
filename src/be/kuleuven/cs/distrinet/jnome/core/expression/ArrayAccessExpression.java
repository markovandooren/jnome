package be.kuleuven.cs.distrinet.jnome.core.expression;

import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.expression.Assignable;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.expression.ArrayIndex;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;

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

  protected ArrayAccessExpression cloneSelf() {
    return new ArrayAccessExpression(null);
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
	public Verification verifySelf() {
		Verification result = Valid.create();
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
