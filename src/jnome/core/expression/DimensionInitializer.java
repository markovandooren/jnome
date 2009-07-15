package jnome.core.expression;

import java.util.List;

import org.rejuse.association.Reference;

import chameleon.core.expression.Expression;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacepart.NamespaceElementImpl;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class DimensionInitializer extends NamespaceElementImpl<DimensionInitializer,ArrayCreationExpression> {

  public DimensionInitializer() {
    this(null);
  }

  public DimensionInitializer(Expression expr) {
	  setExpression(expr);
  }


	/**
	 * EXPRESSION
	 * 
	 * @uml.property name="_expression"
	 * @uml.associationEnd 
	 * @uml.property name="_expression" multiplicity="(0 -1)" elementType="chameleon.core.expression.Expression"
	 */
	private Reference<DimensionInitializer,Expression> _expression = new Reference<DimensionInitializer,Expression>(this);

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
    return parent().getNamespace();
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
  
}
