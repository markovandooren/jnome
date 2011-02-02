package jnome.core.expression;

import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.expression.Expression;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class DimensionInitializer extends NamespaceElementImpl<DimensionInitializer> {

	//FIXME: what is this class for anyway?
  public DimensionInitializer(Expression expr) {
	  setExpression(expr);
  }


	/**
	 * EXPRESSION
	 */
	private SingleAssociation<DimensionInitializer,Expression> _expression = new SingleAssociation<DimensionInitializer,Expression>(this);

  public Expression getExpression() {
    return _expression.getOtherEnd();
  }

  public void setExpression(Expression expression) {
    if (expression != null) {
      _expression.connectTo(expression.parentLink());
    }
    else {
      _expression.connectTo(null);
    }
  }


  /**
   * @return
   */
  public DimensionInitializer clone() {
    Expression expr = null;
    if(getExpression() != null) {
      expr = getExpression().clone();
    }
    return new DimensionInitializer(expr);
  }

  public Namespace getNamespace() {
    return nearestAncestor(NamespaceElement.class).getNamespace();
  }

 /*@
   @ also public behavior
   @
   @ post getExpression() != null ==> \result.contains(getExpression());
   @ post \result.size() == 1;
   @*/
  public List children() {
    return Util.createNonNullList(getExpression());
  }

  @Override
  public VerificationResult verifySelf() {
	  return Valid.create();
  }
  
}
