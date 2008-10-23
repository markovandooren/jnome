/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 *
 * This file is part of Jnome.
 *
 * Jnome is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Jnome is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Jnome; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package jnome.core.expression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jnome.core.type.ArrayType;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.java.collections.Visitor;

import chameleon.core.MetamodelException;
import chameleon.core.expression.Expression;
import chameleon.core.expression.ExpressionContainer;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.type.Type;
import chameleon.core.variable.Variable;

/**
 * @author Marko van Dooren
 */
public class ArrayInitializer extends Expression implements ExpressionContainer {

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
    _inits.add(init.getParentLink());
  }

  public void removeInitializer(Expression init) {
    _inits.remove(init.getParentLink());
  }

  public List<Expression> getVariableInitializers() {
    return _inits.getOtherEnds();
  }

  public Type getType() throws MetamodelException {
    if (getParent() instanceof ArrayCreationExpression) {
      return ((ArrayCreationExpression)getParent()).getType();
    }
    else if (getParent() instanceof ArrayInitializer) {
      ArrayType temp = (ArrayType)((ArrayInitializer)getParent()).getType();
      return new ArrayType(temp.getComponentType(), temp.getDimension() - 1);
    }
    else if (getParent() instanceof Expression) {
      return ((Expression)getParent()).getType();
    }
    else if (getParent() instanceof Variable) {
      return ((Variable)getParent()).getType();
    }
    else {
      throw new RuntimeException();
    }
  }

  public boolean superOf(InvocationTarget target) throws MetamodelException {
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

  public List<? extends Expression> getChildren() {
    return getVariableInitializers();
  }

  public Set<Type> getDirectExceptions() throws MetamodelException {
    return new HashSet<Type>();
  }

//  public AccessibilityDomain getAccessibilityDomain() throws MetamodelException {
//    AccessibilityDomain result = new All();
//    Iterator iter = getVariableInitializers().iterator();
//    while(iter.hasNext()) {
//      result = result.intersect(((Expression)iter.next()).getAccessibilityDomain());
//    }
//    return result;
//  }

}
