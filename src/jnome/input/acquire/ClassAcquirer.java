/*
 * Copyright 2000-2004 the Jnome development team.
 *
 * @author Marko van Dooren
 * @author Nele Smeets
 * @author Kristof Mertens
 * @author Jan Dockx
 * @author Manuel Van Wesemael
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
package org.jnome.input.acquire;

import java.util.List;

import org.jnome.decorator.Decorator;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public abstract class ClassAcquirer extends TypeAcquirer {
  /**
   * @param factory
   */
  public ClassAcquirer(Factory factory,ILinkage linkage) {
    super(factory,linkage);
  }

  protected void acquireSuperObjectTypes(Type type, ExtendedAST objectTypeAST) {

	try {
		// acquire the superinterfaces
		setDecorators(type,objectTypeAST);
//		setAllDecorator(type, objectTypeAST);
		ExtendedAST superInterfacesAST    = objectTypeAST.firstChildOfType(JavaTokenTypes.IMPLEMENTS_CLAUSE);
		acquireSuperInterfaces(type, superInterfacesAST);

		// acquire the superclass
		acquireSuperClass(type, objectTypeAST);
	} catch (NullPointerException e) {
		System.err.println("***ClassAcquirer acquireSuperobjectTypes went wrong***");
	}
  }




//	private void setAllDecorator(Type type, ExtendedAST objectTypeAST) {
//		setDecorator(type, objectTypeAST, Decorator.ALL_DECORATOR);
//	}

	private void setDecorators(Type type, ExtendedAST objectTypeAST) {
		
    	ExtendedAST[] children = null;
    	ExtendedAST[] modifiers = null;
    	ExtendedAST classCodeword = null;
    	ExtendedAST className = null;
    	ExtendedAST extendsClause = null;
    	ExtendedAST[] implementsClauseC = null;
    	ExtendedAST implementation = null;
		
		
		    try {
		    	try {
			    	children = objectTypeAST.children();
			    	modifiers = children[0].children();
			    	classCodeword = children[1];
			    	className = children[2];
			    	extendsClause = children[3];
			    	implementsClauseC = children[4].children();
			    	implementation = children[5];
		    	} catch (ArrayIndexOutOfBoundsException e){}
		    	
		    	
		    	if (modifiers != null)
		    	for (int i=0; i<modifiers.length; i++)
		    	{
		    		ExtendedAST currentChild = modifiers[i];
		    		setSmallDecorator(type, currentChild, Decorator.ACCESSTYPE_DECORATOR);
		    	}
		    	
		    	// class keyword
		    	if (classCodeword != null)		    	
		    	{
		    		setSmallDecorator(type, classCodeword, Decorator.CODEWORD_DECORATOR);
		    	}	
		    	
		    	// class name
		    	if (className != null)		    	
		    	{
		    		setSmallDecorator(type, className, Decorator.NAME_DECORATOR);
		    	}
		    	
	    	
		    	// extends clause keyword
		    	if (extendsClause != null)
		    	if (extendsClause.children().length>0) {
					int offset = className.extractEndOffset(_linkage);
		    		ExtendedAST object = extendsClause.children()[0];
					int offsetc =object.extractBeginOffset(_linkage);
					int length = offsetc-offset;
					_linkage.decoratePosition(offset,length, Decorator.CODEWORD_DECORATOR, type);
					_linkage.decoratePosition(offsetc,object.getLength(), Decorator.TYPE_DECORATOR, type);

		    	}
	
	    	
		    	// implements clause keyword
		    	if (implementsClauseC != null)
		    	if (implementsClauseC.length > 0) 	{
		    		int offset = 0;
		    		if (extendsClause.children().length>0) {
		    			ExtendedAST object = extendsClause.children()[0];
						offset = object.extractEndOffset(_linkage); 
					} else {
						offset = className.extractEndOffset(_linkage); 
					}
		    		ExtendedAST object = implementsClauseC[0];
					int offsetc =object.extractBeginOffset(_linkage); 
					_linkage.decoratePosition(offset,offsetc-offset, Decorator.CODEWORD_DECORATOR, type);
		    	}	    	
		    	
		    	if (implementsClauseC != null)
		    	for (int i=0; i<implementsClauseC.length; i++)
		    	{
		    		ExtendedAST currentChild = implementsClauseC[i];
					int offset =currentChild.extractBeginOffset(_linkage); 
					_linkage.decoratePosition(offset,currentChild.getLength(), Decorator.TYPE_DECORATOR, type);
		    	}	
		    	
		    	// implementation keyword
		    	if (implementation != null)
		    	{
		    		setDecorator(type, implementation, Decorator.IMPLEMENTATION_DECORATOR);
		    	}	    	
	
				
				
				
				

			}catch(ArrayIndexOutOfBoundsException aiobe){
				System.err.println("*** ClassAcquirer setDectorators went wrong****");
			}
		    
		    
		
	  }
	
	
	protected void acquireSuperClass(Type type, ExtendedAST objectTypeAST) {
	    // get the subtree with root EXTENDS_CLAUSE
	    ExtendedAST extendsClauseAST    = objectTypeAST.firstChildOfType(JavaTokenTypes.EXTENDS_CLAUSE);
	    //System.out.println("#####" + objectTypeAST.toStringTree());
	    if (extendsClauseAST != null) { 
	      //System.out.println("#####" + objectTypeAST.toStringTree());
	      
	      // remove the root
	      extendsClauseAST = extendsClauseAST.firstChild();
	      
	      if (extendsClauseAST != null) { // there's a superclass different from
	        List names = extendsClauseAST.getListOfStrings();
	        String name = Util.concat(names);
	        type.addSuperType(new JavaTypeReference(name));
	      }
	      else { // there is no explicit superclass, so the class extends from
	        // java.lang.Object (except when it is java.lang.Object itself)
	        try {
	          if (! type.getFullyQualifiedName().equals("java.lang.Object")) {
	            type.addSuperType(new JavaTypeReference("java.lang.Object"));
	          }
	        }
	        catch(NullPointerException exc) {
	         //For Local classes, the LocalClassStatement will have a null parent while acquiring this.
	          type.addSuperType(new JavaTypeReference("java.lang.Object"));
	        }
	      }
	    }
	  }
  
  protected boolean isInterface() {
    return false; 
  }
  
}
