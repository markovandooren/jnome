package jnome.core.expression;

import java.util.List;

import org.rejuse.association.Reference;

import chameleon.core.MetamodelException;
import chameleon.core.context.LexicalContext;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.ExpressionContainer;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacepart.NamespacePartElementImpl;
import chameleon.core.type.Type;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class DimensionInitializer extends NamespacePartElementImpl<DimensionInitializer,ArrayCreationExpression> implements ExpressionContainer<DimensionInitializer,ArrayCreationExpression> {

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
      _expression.connectTo(expression.getParentLink());
    }
    else {
      _expression.connectTo(null);
    }
  }

  public Type getNearestType() {
    return getParent().getNearestType();
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
    return getParent().getNamespace();
  }

 /*@
   @ also public behavior
   @
   @ post getExpression() != null ==> \result.contains(getExpression());
   @ post \result.size() == 1;
   @*/
  public List getChildren() {
    return Util.createNonNullList(getExpression());
  }
  
}
