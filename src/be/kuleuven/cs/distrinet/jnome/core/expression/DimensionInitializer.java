package be.kuleuven.cs.distrinet.jnome.core.expression;

import be.kuleuven.cs.distrinet.chameleon.core.element.ElementImpl;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

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
  protected DimensionInitializer cloneSelf() {
    return new DimensionInitializer(null);
  }

  @Override
  public Verification verifySelf() {
	  return Valid.create();
  }
  
}
