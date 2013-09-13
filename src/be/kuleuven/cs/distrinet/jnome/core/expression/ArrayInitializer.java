package be.kuleuven.cs.distrinet.jnome.core.expression;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.Expression;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.Variable;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.variable.JavaVariableDeclaration;

/**
 * @author Marko van Dooren
 */
public class ArrayInitializer extends Expression {

	public ArrayInitializer() {
	}

	/**
	 * VARIABLE INITIALIZER
	 */
	private Multi<Expression> _inits = new Multi<Expression>(this);

  public void addInitializer(Expression init) {
    add(_inits,init);
  }

  public void removeInitializer(Expression init) {
    remove(_inits,init);
  }

  public List<Expression> getVariableInitializers() {
    return _inits.getOtherEnds();
  }

  protected Type actualType() throws LookupException {
    Element parent = parent();
		if (parent instanceof ArrayCreationExpression) {
      return ((ArrayCreationExpression)parent).getType();
    }
    else if (parent instanceof ArrayInitializer) {
      Type type = ((ArrayInitializer)parent).getType();
			ArrayType temp = (ArrayType)type;
      return temp.elementType();
    }
    else if (parent instanceof Expression) {
      return ((Expression)parent).getType();
    }
    else if (parent instanceof Variable) {
      return ((Variable)parent).getType();
    }
    else if (parent instanceof JavaVariableDeclaration) {
//      return nearestAncestor(VariableDeclarator.class).typeReference().getType();
    	return nearestAncestor(JavaVariableDeclaration.class).typeReference().getType();
    }
    else {
      throw new ChameleonProgrammerException("Cannot determine type of array initializer based on the parent.");
    }
  }

  public ArrayInitializer cloneSelf() {
    return new ArrayInitializer();
  }

  public Set<Type> getDirectExceptions() throws LookupException {
    return new HashSet<Type>();
  }

	@Override
	public Verification verifySelf() {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		Iterator<Expression> iter = getVariableInitializers().iterator();
		while(iter.hasNext()) {
			builder.append(iter.next().toString());
			if(iter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append('}');
		return builder.toString();
	}
	
}
