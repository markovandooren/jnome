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

import jnome.output.JavaCodeWriter;

import org.rejuse.predicate.PrimitiveTotalPredicate;

import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
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

public abstract Set<Type> getTestTypes() throws LookupException;

  
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
    Set <Type> types = getTestTypes();
    getLogger().info("Starting to test "+types.size() + " types.");
    Iterator<Type> iter = types.iterator();
    long startTime = System.currentTimeMillis();
    int count = 0;
    while (iter.hasNext()) {
      Type type = iter.next();
      getLogger().info(count+" Testing "+type.getFullyQualifiedName());
      processType(type);
      count++;
    }
    long endTime = System.currentTimeMillis();
    getLogger().info("Testing took "+(endTime-startTime)+" milliseconds.");
  }

  private int _count = 0;
  
  public void processType(Type type) throws Exception {
    _count++;
    Expression expr = null;
    Object o = null;
    try {
      List<Expression> exprs = type.descendants(Expression.class);
      new PrimitiveTotalPredicate<Expression>() {

				@Override
				public boolean eval(Expression expr) {
					return ! (expr.parent() instanceof Expression);
				}
			}.filter(exprs);
      for(Expression expression : exprs) {
        getLogger().info(_count + " Testing: "+toCode(expression));
        assertTrue(expression.getType() != null);
      }
    }
    catch (Exception e) {
      throw e; 
    }
    
  }
  
// Pull up this test, and make the toCode abstract. 
//  public abstract String toCode(Expression expr) throws LookupException;

	public String toCode(Expression expr) throws LookupException {
		return _writer.toCode(expr);
	}
	
	private JavaCodeWriter _writer = new JavaCodeWriter();

}
