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
package org.jnome.input.acquire;

import org.jnome.decorator.Decorator;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.type.JavaClass;
import org.jnome.mm.type.JavaTypeLocalContext;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.modifier.AccessModifier;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Interface;

/**
 * @author Marko van Dooren
 */
public class InterfaceAcquirer extends TypeAcquirer {
  
  /**
   * @param factory
   */
  public InterfaceAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }

  public Type createType(String name, AccessModifier access) {
	  JavaClass result = new JavaClass(name, access, new JavaTypeLocalContext()); //TODO : said cu.getNamespacePart() before, but does not seem to make sense
	  result.addModifier(new Interface());
	    return result;
  }
  
  protected void acquireSuperObjectTypes (Type type, ExtendedAST objectTypeAST) {
    try {
		// acquire the superinterfaces
    	setDecorators(type, objectTypeAST);
    	setAllDecorator(type, objectTypeAST);
		ExtendedAST superInterfacesAST = objectTypeAST.firstChildOfType(JavaTokenTypes.EXTENDS_CLAUSE);
		acquireSuperInterfaces(type, superInterfacesAST);
		if(type.getSuperTypeReferences().isEmpty()) {
		  type.addSuperType(new JavaTypeReference("java.lang.Object"));
		}
	} catch (NullPointerException e) {
		System.err.println("InterfaceAcquirer acquireSuperObjectTypes went wrong ***");
		
	}
  }

  protected boolean isInterface() {
    return true; 
  }
  
  protected void acquireDefaultConstructor(Type type) {
  }

	private void setAllDecorator(Type type, ExtendedAST objectTypeAST) {
		setDecorator(type, objectTypeAST, Decorator.ALL_DECORATOR);
	}

	private void setDecorators(Type type, ExtendedAST objectTypeAST) {
		
    	ExtendedAST[] children = null;
    	ExtendedAST[] modifiers = null;
    	ExtendedAST className = null;
    	ExtendedAST extendsClause = null;
    	ExtendedAST implementation = null;
		
		
		    try {
		    	try {
			    	children = objectTypeAST.children();
			    	modifiers = children[0].children();
			    	className = children[1];
			    	extendsClause = children[2];
			    	implementation = children[3];
		    	} catch (ArrayIndexOutOfBoundsException e){}
		    	
		    	
		    	if (modifiers != null)
		    	for (int i=0; i<modifiers.length; i++)
		    	{
		    		ExtendedAST currentChild = modifiers[i];
		    		setSmallDecorator(type, currentChild, Decorator.ACCESSTYPE_DECORATOR);
		    	}
		    	
		    	// interface keyword
		    	{
		    		int begin = ((modifiers==null || modifiers.length==0) ? 
		    				_linkage.getLineOffset(className.getLineNumber()) : 
		    				modifiers[modifiers.length-1].extractEndOffset(_linkage));
		    		_linkage.decoratePosition(begin, 10, Decorator.CODEWORD_DECORATOR, type);
		    	}	
		    	
		    	// interface name
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
	
		    	// implementation keyword
		    	if (implementation != null)
		    	{
		    		setDecorator(type, implementation, Decorator.IMPLEMENTATION_DECORATOR);
		    	}	    	
	
				
				
				
				

			}catch(ArrayIndexOutOfBoundsException aiobe){
				System.err.println("*** ClassAcquirer setDectorators went wrong****");
			}
		    
		    
		
	  }
	  
  
}
