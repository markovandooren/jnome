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

import chameleon.core.element.ElementImpl;
import chameleon.core.method.Method;
import chameleon.core.method.MethodContext;
import chameleon.core.method.MethodLocalContext;
import chameleon.core.method.RegularMethod;
import chameleon.core.modifier.AccessModifier;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Constructor;

public class ConstructorAcquirer extends MethodAcquirer {
	/**
	 * @param factory
	 */
	public ConstructorAcquirer(Factory factory, ILinkage linkage) {
		super(factory, linkage);
	}
	
	protected Method createMethod(ElementImpl context, String name, AccessModifier accessibility, ExtendedAST methodAST, Type type) {
		try {
			TypeReference tr = new TypeReference(null, name);
			MethodLocalContext mlc = getFactory().getLanguage().contextFactory().getMethodLocalContext();
			Method constructor = new RegularMethod(accessibility, name, tr,new MethodContext(), mlc); 
			constructor.addModifier(new Constructor());
			if(type != null){
				type.addMethod(constructor);
			}
			return constructor;
		} catch (NullPointerException e) {
			System.err.println("***constructorAcquirer createmethode went wrong***");
			e.printStackTrace();
			throw new Error();
		}
	}

	public void setDecorators(ExtendedAST methodAST, Method method) {
	
			ExtendedAST[] children = null;
			ExtendedAST[] modifiers = null;
			ExtendedAST varName = null;
			ExtendedAST[] parameters = null;
			ExtendedAST throwsExceptions = null;
			ExtendedAST implementation = null;
			
			try {
				children = methodAST.children();
				modifiers = children[0].children();
				varName = children[1];
				parameters = children[2].children();
				if (children[3].getText().equals("throws")){
					 throwsExceptions = children[3];
					 implementation = children[4];
				}
				else{
					 throwsExceptions = null;
					 implementation = children[3];
				}			
			} catch (ArrayIndexOutOfBoundsException e){}
			
	
	
			
	
				//modifiers
				if (modifiers!=null)
		    	for (int i=0; i<modifiers.length; i++)
		    	{
		    		ExtendedAST currentChild = modifiers[i];
		    		setDecorator(method, currentChild, Decorator.ACCESSTYPE_DECORATOR);
		    	}	
		    	
	
		    	
		    	// var name
				if (varName!=null)
		    	{
					setSmallDecorator(method, varName, Decorator.NAME_DECORATOR);
		    	}   
		    	
	//	    	//parameters
				if (parameters!=null)
		    	for (int i=0; i<parameters.length; i++)
		    	{
		    		ExtendedAST currentParamDef = parameters[i]; 
					ParameterAcquirer acquirer = getFactory().getParameterAcquirer(_linkage);
					acquirer.acquire(method, currentParamDef);
		    	}
		    	
		    	//exceptions thrown
		    	if(throwsExceptions!=null){
		    		int throwsOffset = _linkage.getLineOffset(throwsExceptions.getLineNumber()-1)+ throwsExceptions.getColumnNumber()-1;
		    		int throwsLength = throwsExceptions.getLength();
					_linkage.decoratePosition(throwsOffset, throwsLength, Decorator.CODEWORD_DECORATOR, method);
		    		
		    		ExtendedAST[] definedExceptions = throwsExceptions.children();
			    	for (int i=0; i<definedExceptions.length; i++)
			    	{
			    		ExtendedAST currentException = definedExceptions[i]; 
			    		
						int offset=currentException.extractBeginOffset(_linkage);
						int eoffset=currentException.extractEndOffset(_linkage);
						int length = eoffset - offset ;
						_linkage.decoratePosition(offset, eoffset-offset, Decorator.EXCEPTION_DECORATOR, method);
			    	}
		    	}
		    	
		    	//implementation
		    	if (implementation!=null)
		    	{
					int offset=implementation.extractBeginOffset(_linkage);
					int eoffset=implementation.extractBeginOffset(_linkage);
					int length = eoffset - offset ;
					_linkage.decoratePosition(offset, eoffset-offset, Decorator.IMPLEMENTATION_DECORATOR, method);
		    	}
		    	
		    	
	
			
		}
	
}
