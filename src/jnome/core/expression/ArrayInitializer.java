package jnome.core.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jnome.core.type.ArrayType;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.java.collections.Visitor;

import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.variable.Variable;
import chameleon.support.variable.VariableDeclaration;

/**
 * @author Marko van Dooren
 */
public class ArrayInitializer extends Expression<ArrayInitializer> {

	public ArrayInitializer() {
	}

	/**
	 * VARIABLE INITIALIZER
	 * 
	 * @uml.property name="_inits"
	 * @uml.associationEnd 
	 * @uml.property name="_inits" multiplicity="(1 1)"
	 */
	private OrderedReferenceSet<ArrayInitializer,Expression> _inits = new OrderedReferenceSet<ArrayInitializer,Expression>(this);

  public OrderedReferenceSet getInitializersLink() {
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
      return new ArrayType(temp.getComponentType(), temp.getDimension() - 1);
    }
    else if (parent() instanceof Expression) {
      return ((Expression)parent()).getType();
    }
    else if (parent() instanceof Variable) {
      return ((Variable)parent()).getType();
    }
    else if (parent() instanceof VariableDeclaration<?>) {
      return ((VariableDeclaration<?>)parent()).parent().typeReference().getType();
    }
    else {
    	System.out.println(parent().getClass().getName());
      throw new ChameleonProgrammerException("Cannot determine type of array initializer based on the parent.");
    }
  }

  public boolean superOf(InvocationTarget target) throws LookupException {
    if(!(target instanceof ArrayInitializer)) {
      return false;
    }
    ArrayInitializer acc =(ArrayInitializer)target;
    List varInits = getVariableInitializers();
    List otherVarInits = acc.getVariableInitializers();
    for(int i=0; i< varInits.size(); i++) {
      if(! ((InvocationTarget)varInits.get(i)).compatibleWith((InvocationTarget)otherVarInits.get(i))) {
        return false;
      }
    }
    return true;
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

//  public AccessibilityDomain getAccessibilityDomain() throws LookupException {
//    AccessibilityDomain result = new All();
//    Iterator iter = getVariableInitializers().iterator();
//    while(iter.hasNext()) {
//      result = result.intersect(((Expression)iter.next()).getAccessibilityDomain());
//    }
//    return result;
//  }

}
