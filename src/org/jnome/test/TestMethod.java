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
package org.jnome.test;

import java.util.ArrayList;
import java.util.List;

import org.jnome.input.JavaMetaModelFactory;
import org.rejuse.java.collections.RobustVisitor;
import org.rejuse.predicate.TypePredicate;

import chameleon.core.MetamodelException;
import chameleon.core.expression.RegularMethodInvocation;
import chameleon.core.method.Method;
import chameleon.core.type.Type;

/**
 * @author marko
 */
public class TestMethod extends Test {

  /**
   * @param arg0
   * @throws Exception
   */
  public TestMethod(String arg0) throws Exception {
    super(arg0);
    //String path1 = "testsource\\gen\\";
    String path1 = "c:\\chameleon\\testsource\\gen\\java\\lang\\Object.java";
    String path2 = "c:\\chameleon\\test\\TestMethod.java";
    ArrayList al = new ArrayList();
    al.add(path1);
	al.add(path2);
	_files = JavaMetaModelFactory.loadFiles(al, ".java", true);
   // _files.add(new File("src/org/seastar/test/TestMethod.seastar"));
    _mm = new JavaMetaModelFactory().getMetaModel(new DummyLinkage(), _files);
  }

  public void testMethod() throws Exception {
    Type TestMethod = (Type)_mm.findTypeLocally("TestMethod");
    List allExpressions = TestMethod.getAllExpressions();
    new TypePredicate(RegularMethodInvocation.class).filter(allExpressions);
    System.out.println("Testing "+allExpressions.size() + " method invocations.");
    new RobustVisitor() {
      public Object visit(Object element) throws Exception {
        RegularMethodInvocation invocation = (RegularMethodInvocation)element;
        if (invocation.getName().indexOf("invalid") == -1) {
          try {
            //Object parent = invocation.getParent();
            Type invocationType = (Type)invocation.getType();
            assertTrue("Method invocation" + invocation.getName() + " does not return type Object.", invocationType == _mm.findType("java.lang.Object"));
            return null;
          }
          catch (Exception e) {
            Type invocationType = (Type)invocation.getType();
            e.printStackTrace();
            throw e;
          }
        }
        else {
          try {
            Type invocationType = (Type)invocation.getType();
            Method method = invocation.getMethod();
            assertTrue(false);
            return null;
          }
          catch (MetamodelException e) {
            return null;
          }
          catch (Exception e) {
            e.printStackTrace();
            throw e;
          }
        }
      }

      public void unvisit(Object element, Object undo) {
      }
    }.applyTo(allExpressions);

  }

}
