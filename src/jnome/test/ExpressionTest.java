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
package jnome.test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import chameleon.core.context.LookupException;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.type.Type;

/**
 * @author marko
 */

public abstract class ExpressionTest extends MetaModelTest {
  
  public ExpressionTest(String arg) {
    super(arg); 
  }

//  public void add(FileSet set, String name) {
//    String filename =  "**" + File.separator + name.replace('.', File.separatorChar)+".java";
//    set.include(new PatternPredicate(_srcDir, new FileNamePattern(filename)));
//  }

public abstract Set getTestTypes() throws LookupException;

  
  public void myTestDescendants() {
    List descendants = _mm.descendants();
    Iterator iter = descendants.iterator();
    while(iter.hasNext()) {
      Object o = iter.next();
      if(! (o instanceof Element)) {
        System.out.println("Bug in getDescendants");
      }
      assertTrue(o instanceof Element);
    }
  }
  
  public void testExpressionTypes() throws Exception {
    //myTestDescendants(); // Stupid Junit creates a new test object for every test (which includes parsing).
    Set types = getTestTypes();
    System.out.println("Starting to test "+types.size() + " types.");
    Iterator iter = types.iterator();
    long startTime = System.currentTimeMillis();
    int count = 0;
    while (iter.hasNext()) {
      Type type = (Type) iter.next();
      System.out.println(count+" Testing "+type.getFullyQualifiedName());
      processType(type);
      count++;
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Testing took "+(endTime-startTime)+" milliseconds.");
  }

  private int _count = 0;
  
  public void processType(Type type) throws Exception {
    _count++;
    Expression expr = null;
    Object o = null;
    try {
      List<Expression> exprs = type.descendants(Expression.class);
      //System.out.println(_count + " Testing: "+type.getFullyQualifiedName() +" : " + exprs.toString() + " expressions.");
      for(Expression expression : exprs) {
        expr = expression;
//        System.out.println(_count + " Testing: "+type.getFullyQualifiedName() + " : " + new JavaCodeWriter().toCode(expr));
//        System.out.println("Ancestor method: "+new JavaCodeWriter().toCode(expr.getNearestAncestor(Method.class)));
        assertTrue(expr.getType() != null);
      }
    } catch (ClassCastException e) {
    	e.printStackTrace();
//      try {
//        System.out.println("Cast error: "+o.getClass().getName());
//      } catch (RuntimeException e1) {
//        //e1.printStackTrace();
//      }
      Type ttt = expr.getType();
      throw e;
    }
    catch (Exception e) {
      e.printStackTrace();
      expr.getType();
      throw e; 
    }
    
  }
  
}
