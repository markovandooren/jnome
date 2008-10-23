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

import java.util.Set;

import junit.framework.TestCase;

import org.jnome.input.JavaMetaModelFactory;

import chameleon.core.namespace.Namespace;

/**
 * @author marko
 */
public class Test extends TestCase {

	/**
	 * Constructor for Test.
	 * @param arg0
	 */
	public Test(String arg0) throws Exception {
		super(arg0);
    //FileSet java = new FileSet();
    //java.include(new PatternPredicate(_javaLangDir, new FileNamePattern(_pattern)));
		
	_files = JavaMetaModelFactory.load(_javaLangDir, ".java", true);
	 DummyLinkage dummy = new DummyLinkage();
	//  	_mm = new MetaModelFactory().getMetaModel(dummy,_files);
    
	}

//  private File _jdkDir = new File("/home/marko/java/source/src");
//  private File _javaLangDir = new File("/home/marko/java/source/src/java/lang");
//  private String _pattern = "**/*.java";

	  private String _jdkDir = "C:\\Chameleon\\testsource\\gen\\";
	  private String _javaLangDir = "C:\\Chameleon\\testsource\\gen\\java\\lang\\Object.java";
	  
	protected Set _files;

	protected Namespace _mm;

  
//  public void testExpressionTypes() throws Exception {
//    Object o = null;
//    try {
//			Set exprs = _mm.getDirectlyContainedExpressions();
//			Iterator iter = exprs.iterator();
//			while(iter.hasNext()) {
//        o = iter.next();
//			  Expression expr = (Expression)o;
//			  if ((!(expr instanceof RegularMethodInvocation)) || (((RegularMethodInvocation)expr).getName().indexOf("invalid") == -1)) {
//			  	expr.getType();
//			  }
//			}
//		} catch (ClassCastException e) {
//			//System.out.println(o.getClass().getName());
//      throw e;
//		}
//  }
  
}
