package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.util.association.Single;

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
