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

import java.util.List;
import java.util.Set;

import jnome.core.type.ArrayType;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.association.Reference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.MetamodelException;
import chameleon.core.expression.Assignable;
import chameleon.core.expression.Expression;
import chameleon.core.expression.ExpressionContainer;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.type.Type;
import chameleon.support.expression.ArrayIndex;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class ArrayAccessExpression extends Expression implements ExpressionContainer, Assignable {

  public ArrayAccessExpression(Expression target) {
    setTarget(target);
  }

	private Reference<ArrayAccessExpression,Expression> _target = new Reference<ArrayAccessExpression,Expression>(this);


  public Expression getTarget() {
    return _target.getOtherEnd();
  }

  public void setTarget(Expression expression) {
    _target.connectTo(expression.getParentLink());
  }

	/**
	 * INDICES
	 */
	private OrderedReferenceSet<ArrayAccessExpression,ArrayIndex> _indicesLink = new OrderedReferenceSet<ArrayAccessExpression,ArrayIndex>(this);

  public OrderedReferenceSet getIndicesLink() {
    return _indicesLink;
  }

  public void addIndex(ArrayIndex index) {
	    _indicesLink.add(index.getParentLink());
	  }

	  public void removeIndex(ArrayIndex index) {
	    _indicesLink.remove(index.getParentLink());
	  }
	  
  public List<ArrayIndex> getIndices() {
    return _indicesLink.getOtherEnds();
  }

  public Type getType() throws MetamodelException {
    ArrayType componentType = (ArrayType)getTarget().getType();
    int dim = componentType.getDimension() - getIndices().size();
    if (dim > 0) {
      Type result = new ArrayType(componentType.getComponentType(), dim);
      return result;
    }
    else {
      return componentType.getComponentType();
    }
  }

  public boolean superOf(InvocationTarget target) throws MetamodelException {
    if(!(target instanceof ArrayAccessExpression)) {
      return false;
    }
    ArrayAccessExpression acc =(ArrayAccessExpression)target;
    List varInits = getIndices();
    List otherVarInits = acc.getIndices();
    for(int i=0; i< varInits.size(); i++) {
      if(! ((InvocationTarget)varInits.get(i)).compatibleWith((InvocationTarget)otherVarInits.get(i))) {
        return false;
      }
    }
    if((getTarget() == null) && (acc.getTarget() == null)) {
      return true;
    } else if((getTarget() != null) && (acc.getTarget() != null)) {
      return getTarget().compatibleWith(acc.getTarget());
    } else {
      return false;
    }
  }

  public ArrayAccessExpression clone() {
    InvocationTarget target = null;
    if(getTarget() != null) {
      target = getTarget().clone();
    }
    final ArrayAccessExpression result = new ArrayAccessExpression((Expression)target);
    new Visitor() {
      public void visit(Object element) {
        //result.addIndex(((Expression)element).cloneExpr());
    	  result.addIndex(((ArrayIndex)element).clone());
      }
    }.applyTo(getIndices());
    return result;
  }

  public Assignable cloneAssignable() {
    return (Assignable)clone();
  }

  public List getChildren() {
    List result = Util.createNonNullList(getTarget());
    result.addAll(getIndices());
    return result;
  }

  public Set getDirectExceptions() throws MetamodelException {
    return Util.createNonNullSet(language().getNullInvocationException());
  }

//  public AccessibilityDomain getAccessibilityDomain() throws MetamodelException {
//    AccessibilityDomain result = new All();
//    if(getTarget() != null) {
//      result = result.intersect(getTarget().getAccessibilityDomain());
//    }
//    Iterator iter = getIndices().iterator();
//    while(iter.hasNext()) {
//      result = result.intersect(((Expression)iter.next()).getAccessibilityDomain());
//    }
//    return result;
//  }
  
}
