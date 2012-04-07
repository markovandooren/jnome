package jnome.core.expression;

import chameleon.core.element.ElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.expression.Expression;
import chameleon.util.association.Single;

/**
 * @author Marko van Dooren
 */
public class DimensionInitializer extends ElementImpl {

	//FIXME: what is this class for anyway?
  public DimensionInitializer(Expression expr) {
	  setExpression(expr);
  }


	/**
	 * EXPRESSION
	 */
	private Single<Expression> _expression = new Single<Expression>(this);

  public Expression getExpression() {
    return _expression.getOtherEnd();
  }

  public void setExpression(Expression expression) {
    set(_expression,expression);
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

  @Override
  public VerificationResult verifySelf() {
	  return Valid.create();
  }
  
}
