package jnome.core.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jnome.core.type.ArrayType;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.java.collections.Visitor;

import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.Variable;
import chameleon.core.variable.VariableDeclaration;
import chameleon.core.variable.VariableDeclarator;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.Type;

/**
 * @author Marko van Dooren
 */
public class ArrayInitializer extends Expression<ArrayInitializer> {

	public ArrayInitializer() {
	}

	/**
	 * VARIABLE INITIALIZER
	 */
	private OrderedMultiAssociation<ArrayInitializer,Expression> _inits = new OrderedMultiAssociation<ArrayInitializer,Expression>(this);

  public OrderedMultiAssociation getInitializersLink() {
    return _inits;
  }

  public void addInitializer(Expression init) {
    _inits.add(init.parentLink());
  }

  public void removeInitializer(Expression init) {
    _inits.remove(init.parentLink());
  }

  public List<Expression> getVariableInitializers() {
    return _inits.getOtherEnds();
  }

  protected Type actualType() throws LookupException {
    if (parent() instanceof ArrayCreationExpression) {
      return ((ArrayCreationExpression)parent()).getType();
    }
    else if (parent() instanceof ArrayInitializer) {
      ArrayType temp = (ArrayType)((ArrayInitializer)parent()).getType();
      return temp.elementType();
    }
    else if (parent() instanceof Expression) {
      return ((Expression)parent()).getType();
    }
    else if (parent() instanceof Variable) {
      return ((Variable)parent()).getType();
    }
    else if (parent() instanceof VariableDeclaration<?>) {
      return nearestAncestor(VariableDeclarator.class).typeReference().getType();
    }
    else {
    	System.out.println(parent().getClass().getName());
      throw new ChameleonProgrammerException("Cannot determine type of array initializer based on the parent.");
    }
  }

  public ArrayInitializer clone() {
    final ArrayInitializer result = new ArrayInitializer();
    new Visitor() {
      public void visit(Object element) {
        result.addInitializer(((Expression)element).clone());
      }
    }.applyTo(getVariableInitializers());
    return result;
  }

  public List<? extends Expression> children() {
    return getVariableInitializers();
  }

  public Set<Type> getDirectExceptions() throws LookupException {
    return new HashSet<Type>();
  }

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    AccessibilityDomain result = new All();
//    Iterator iter = getVariableInitializers().iterator();
//    while(iter.hasNext()) {
//      result = result.intersect(((Expression)iter.next()).getAccessibilityDomain());
//    }
//    return result;
//  }

}
