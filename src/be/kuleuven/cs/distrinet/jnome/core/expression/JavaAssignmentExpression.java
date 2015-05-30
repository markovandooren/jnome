package be.kuleuven.cs.distrinet.jnome.core.expression;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.variable.Variable;
import org.aikodi.chameleon.oo.expression.Expression;
import org.aikodi.chameleon.support.expression.AssignmentExpression;

/**
 * A class of assignment expressions in Java. Java assignment expressions
 * can resolve their variable when the left-hand side is an array access
 * expression.
 * 
 * @author Marko van Dooren
 */
public class JavaAssignmentExpression extends AssignmentExpression {

  /**
   * Create a new assignment expression.
   * 
   * @param variableReference An expression that points to a variable.
   * @param value An expression that represents the value that is assigned to
   *              the variable.
   * @return An assignment expression whose variable expression is set to the
   * given variable reference, and whose value is set to the given value. 
   */
  public JavaAssignmentExpression(Expression variableReference, Expression value) {
    super(variableReference, value);
  }

  @Override
  protected JavaAssignmentExpression cloneSelf() {
    return new JavaAssignmentExpression(null,null);
  }

  @Override
  public Variable variable() throws LookupException {
    final Expression var = variableExpression();
    if(var instanceof ArrayAccessExpression) {
      ArrayAccessExpression a = (ArrayAccessExpression) var;
      return (Variable) ((CrossReference)a.getTarget()).getElement();
    } else {
      return super.variable();
    }
  }
}
