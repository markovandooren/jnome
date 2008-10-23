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

import chameleon.core.MetamodelException;
import chameleon.core.expression.VariableReference;
import chameleon.core.method.Method;
import chameleon.core.method.RegularImplementation;
import chameleon.core.statement.Block;
import chameleon.core.type.Type;
import chameleon.core.variable.LocalVariable;
import chameleon.core.variable.MemberVariable;
import chameleon.core.variable.Variable;
import chameleon.support.modifier.Private;
import chameleon.support.statement.LocalVariableDeclarationStatement;

/**
 * @author marko
 */
public class Test1 extends Test {

	/**
	 * Constructor for Test1.
	 * @param arg0
	 */
	public Test1(String arg0) throws Exception {
		super(arg0);
		//String path2 = "testsource\\gen\\";
		String path2 = "C:\\Chameleon\\testsource\\gen\\java\\lang\\Object.java";
		String path1 = "C:\\Chameleon\\test\\A.java";
		ArrayList al = new ArrayList();
		
		//File file = new File("filename.txt");
		//System.out.println(file.getAbsolutePath());
		al.add(path1);
		al.add(path2);
		_files = JavaMetaModelFactory.loadFiles(al, ".java", true);
		//_files.add(new File("src/org/jnome/test/Test1.seastar"));
	
  }
  
  public void setUp() throws Exception {
	  DummyLinkage dummy = new DummyLinkage();
  	_mm = new JavaMetaModelFactory().getMetaModel(dummy,_files);
  	
  }
  
  public void testPackage() throws MetamodelException {
    Type A = (Type)_mm.findTypeLocally("A");
    assertTrue(A.getFullyQualifiedName().equals("A"));
    assertTrue(A.getNamespace().equals(_mm));
  }
  
  public void testVariableLookup() throws MetamodelException {
    Type A = (Type)_mm.findTypeLocally("A");
    MemberVariable var = A.getVariable("a");
    assertTrue(var.is(new Private()));
    assertTrue(var.modifiers().size() == 1);
  }
  
  public void testMethodM() throws MetamodelException {
    Type A = (Type)_mm.findTypeLocally("A");
    Method method = A.getApplicableRegularMethod("m", new ArrayList());
    RegularImplementation implementation = (RegularImplementation)method.getImplementation();
    Block block = implementation.getBody();
    List statements = block.getStatements();
    assertTrue(implementation.getAllStatements().size() == 3);
    assertTrue(implementation.getAllExpressions().size() == 2);
    
    assertTrue(statements.size() == 2);
    LocalVariableDeclarationStatement aDecl = (LocalVariableDeclarationStatement)statements.get(0);
    List varsA = aDecl.getVariables();
    assertTrue(varsA.size() == 1);
    LocalVariable varA = (LocalVariable)varsA.get(0);
    assertTrue(varA.getName().equals("a"));
    Type type = (Type)varA.getType();
    Type javaLangObject = (Type)_mm.findType("java.lang.Object");
    assertTrue(type == javaLangObject);
    LocalVariableDeclarationStatement bDecl = (LocalVariableDeclarationStatement)statements.get(1);
    List varsB = bDecl.getVariables();
    LocalVariable varB = (LocalVariable)varsB.get(0);
    assertTrue(varB.getName().equals("b"));
    type = (Type)varB.getType();
    assertTrue(type == javaLangObject);
    VariableReference ref = (VariableReference)varB.getInitialization();
    Variable var = ref.getVariable();
    MemberVariable memberVar = A.getVariable("a");
    assertTrue(var == memberVar);
    Variable findA = bDecl.lexicalContext().findVariable("a"); 
    assertTrue(findA == varA);
  }
  
}
