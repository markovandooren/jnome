/*
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.jnome.decorator.Decorator;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.modifier.StrictFP;
import org.jnome.mm.modifier.Synchronized;
import org.jnome.mm.type.JavaTypeReference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.MetamodelException;
import chameleon.core.element.ElementImpl;
import chameleon.core.method.Implementation;
import chameleon.core.method.Method;
import chameleon.core.method.NativeImplementation;
import chameleon.core.method.RegularImplementation;
import chameleon.core.method.exception.TypeExceptionDeclaration;
import chameleon.core.modifier.AccessModifier;
import chameleon.core.modifier.MethodModifier;
import chameleon.core.statement.Block;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Native;
import chameleon.support.modifier.Static;
import chameleon.util.Util;


/**
 * @author marko
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class MethodAcquirer extends Acquirer {

	/**
   * @param factory
   */
  public MethodAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }
 
  

  /**
	    * @param type
	    * @param methodAST
	    */
	public void acquire(Type type, ExtendedAST methodAST) {
		try {
			//System.out.println("Acquiring Za Messod "+methodAST.getText());
			// acquire method name
			String name = extractName(methodAST);
			
			//System.out.println("lengte mehtodAST van "+name+" : "+methodAST.getLengthChildren());
			
			//System.out.println("Acquiring "+name);

			// find accessibility modifier
			AccessModifier accessibility = extractAccess(null, methodAST);
			int access_length =accessibility.toString().length();
			// TODO using postfixmethod for now. In Java, you can't declare any other methods anyway.
   
			Method method = createMethod(type, name, accessibility, methodAST,type);
			
			/** sets the name, access and all decorator **/
			setAllDecorator(methodAST,name, method);
			setDecorators(methodAST, method);
			
			
			acquireOtherModifiers(method, methodAST);
			
			// acquire formal parameters
			acquireFormalParameters(method, methodAST);
			
			// acquire method body
			acquireImplementation(method, methodAST);
			
			// create the method (and acquire the return type if this exists)
			
			// acquire documentationblock
			// TODO acquireDocumentationBlock(method, methodAST);

			// acquire throws-clause
			acquireThrowsClause(method, methodAST);
		} catch (NullPointerException e) {
			System.err.println("MethodAcquirer acquire went wrong***");
			e.printStackTrace();
			throw new Error();
		}

	  }


  private void setAllDecorator(ExtendedAST methodAST, String name, Method method) {
	  setDecorator(method, methodAST, Decorator.ALL_DECORATOR);
  }


	public boolean hasImplementation(ExtendedAST methodAST) {
		return getBodyAST(methodAST) != null;
	}
	
	public ExtendedAST getBodyAST(ExtendedAST methodAST) {
		ExtendedAST startImplAST = methodAST.firstChildOfType(JavaTokenTypes.END_CURLY);
		if(startImplAST != null) {
			return startImplAST.firstChildOfType(JavaTokenTypes.SLIST);
		} else {
			return null;
		}
	}


	public void setDecorators(ExtendedAST methodAST, Method method) {
			ExtendedAST[] children = methodAST.children();
			ExtendedAST[] modifiers = new ExtendedAST[0];
			ExtendedAST type;
			try {
				modifiers = children[0].children();
				type = children[1].children()[0];
			}catch(ArrayIndexOutOfBoundsException aiobe){
				type = null;
			}
			ExtendedAST varName = children[2];
			ExtendedAST[] parameters = children[3].children();
			ExtendedAST throwsExceptions;
			final ExtendedAST implementation = getBodyAST(methodAST);
			
			boolean noImplementation = hasImplementation(methodAST);
			
			
			if (noImplementation){ //abstractumundo
				//implementation = null;
				if (children.length >= 5 && children[4].getText().equals("throws"))
					throwsExceptions = children[4];
				else
					throwsExceptions = null;
				
			}
			else
			{
				if (children.length == 4){// modifiers type name parameters => geen implentatie dus interface methode
					 throwsExceptions = null;
					 //implementation = null;					
				}
				else if ( children[4].getText().equals("throws")){
					 throwsExceptions = children[4];
					 //if(children.length >= 5) {
					 //  implementation = children[5];
					 //}
				}
				else {
					 throwsExceptions = null;
					 //implementation = children[4];
				}
			}
	
				//modifiers
		    	for (int i=0; i<modifiers.length; i++)
		    	{
		    		ExtendedAST currentChild = modifiers[i];
		    		setSmallDecorator(method, currentChild, Decorator.ACCESSTYPE_DECORATOR);
		    	}	
		    	
		    	//type
		    	if(type!=null)
		    	{
		    		
					int offset, eoffset;
		     		
			    	if (type.getText().equals("[")){ // array
			    		offset = type.firstChild().extractBeginOffset(_linkage);
			    		eoffset = type.extractBeginOffset(_linkage);
			    	} else
			    	{
			    		offset = type.extractBeginOffset(_linkage);
			    		eoffset = offset + type.getLength();
			    	}
					
					_linkage.decoratePosition(offset, eoffset-offset, Decorator.TYPE_DECORATOR, method);
		    	}
		    	
		    	// var name
		    	{
		    		setSmallDecorator(method, varName, Decorator.NAME_DECORATOR);
		    	}   
		    	
	//	    	//parameters
		    	for (int i=0; i<parameters.length; i++)
		    	{
		    		ExtendedAST currentParamDef = parameters[i]; 
//					int offset=currentParamDef.extractBeginOffset(_linkage);
//					int eoffset = currentParamDef.extractEndOffset(_linkage);
//					int length = eoffset - offset ;
//					Decorator classKeywordDec = new Decorator(offset,length,method,Decorator.PARAMETER_DEF_DECORATOR);
//					method.setDecorator(classKeywordDec,Decorator.PARAMETER_DEF_DECORATOR);
//					_linkage.addPosition(Decorator.CHAMELEON_CATEGORY,classKeywordDec);
					ParameterAcquirer acquirer = getFactory().getParameterAcquirer(_linkage);
					acquirer.acquire(method,currentParamDef);
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
						int eoffset = currentException.extractBeginOffset(_linkage);
						int length = eoffset - offset ;
						_linkage.decoratePosition(offset, eoffset-offset, Decorator.EXCEPTION_DECORATOR, method);
						
			    	}
		    	}
		    	//implementation
		    	if (implementation!=null) {
		    		
					int offset=implementation.extractBeginOffset(_linkage);
					int eoffset=implementation.extractEndOffset(_linkage);
					int length = eoffset - offset ;
					_linkage.decoratePosition(offset, eoffset-offset, Decorator.IMPLEMENTATION_DECORATOR, method);
					

		    	}
		    	

		}
	

	public Method getNewMethod(Type type, ExtendedAST methodAST) {
	    try {
			// acquire method name
			String name = extractName(methodAST);
			
			//System.out.println("Acquiring "+name);

			// find accessibility modifier
			AccessModifier accessibility = extractAccess(null, methodAST);
			// TODO using postfixmethod for now. In Java, you can't declare any other methods anyway.
			
			Method method = createMethod(type, name, accessibility, methodAST,type);
			int access_length =accessibility.toString().length();
			// MethodAST houdt niet start en lengte van methode bij, want 0-0 0
			//method.setDecorator(new MethodDecorator(methodAST.getLineNumber(),methodAST.getColumnNumber(),methodAST.getLength(),method),"method");
			//System.out.println("method: "+methodAST.getLineNumber()+"-"+methodAST.getColumnNumber()+"  "+methodAST.getLength());
			
			/** sets the name, access and all decorator **/
			setAllDecorator(methodAST,name, method);
			setDecorators(methodAST, method);
			
			acquireOtherModifiers(method, methodAST);
			
			// acquire formal parameters
			acquireFormalParameters(method, methodAST);
			
			// acquire method body
			acquireImplementation(method, methodAST);
			
			// create the method (and acquire the return type if this exists)
			// acquire documentationblock
			// TODO acquireDocumentationBlock(method, methodAST);

			// acquire throws-clause
			acquireThrowsClause(method, methodAST);
			// TODO SPLIT CONSTRUCTORS AND METHODS
			
			return method;
		} catch (NullPointerException e) {
			System.err.println("MethodAcquirer getnewMethod went wrong***");
			return null;
		}
	  }
  
	protected abstract Method createMethod(ElementImpl context, String name, AccessModifier accessibility, ExtendedAST methodAST, Type type);

	protected void acquireImplementation(Method method, ExtendedAST methodAST) {
		Implementation implementation = null;

		if (method.hasModifier(new Native())) {
			implementation = new NativeImplementation();
		} else if (method.hasModifier(new Abstract())) {
			// An abstract method has no implementation, thus null.
		} else {
			// acquire the method body
			ExtendedAST startImplAST = methodAST.firstChildOfType(JavaTokenTypes.END_CURLY);
			if (startImplAST != null) {
				ExtendedAST bodyAST = startImplAST.firstChildOfType(JavaTokenTypes.SLIST);

				Block methodBody = null;
				if (bodyAST != null) {
					StatementAcquirer acquirer = getFactory().getStatementAcquirer(_linkage);
					methodBody = (Block) acquirer.acquire(bodyAST);
					// setImplementationDecorators(methodBody, bodyAST);
					implementation = new RegularImplementation(methodBody);
				} else {
					// Method is abstract and declared in an interface without the
					// abstract keyword.
				}
			}
		}
		method.setImplementation(implementation);
	}





	/**
	 * @param method
	 * @param methodAST
	 */
	protected void acquireThrowsClause(final Method method, ExtendedAST methodAST) {
	    try {
			ExtendedAST throwsClauseAST
			= methodAST.firstChildOfType(JavaTokenTypes.LITERAL_throws);

			// acquire the exceptions in the subtree
			if (throwsClauseAST != null) { //there is a non empty throwsclause
			  // add the thrown exceptions one by one
			  //throwsClauseAST = throwsClauseAST.firstChild();
			  ArrayList throwsClausesList = new ArrayList(Arrays.asList(throwsClauseAST.childrenOfType(JavaTokenTypes.IDENT)));
			  throwsClausesList.addAll(Arrays.asList(throwsClauseAST.childrenOfType(JavaTokenTypes.DOT)));
			
			  new Visitor() {
			    public void visit(Object element) {
			      //make a vector containing the parts of the (fully qualified)
			      //name
			      List names = ((ExtendedAST)element).getListOfStrings();
			      String name = Util.concat(names);
			      // create a new UnresolvedClass
			      JavaTypeReference type = new JavaTypeReference(name);
			      TypeExceptionDeclaration ted = new TypeExceptionDeclaration(type);
			      method.getExceptionClause().add(ted);
			  }
			}.applyTo(throwsClausesList);
			
			}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		} catch (NullPointerException e2){
			System.err.println("MethodAcquirer acquireThrowsClause went wrong ***");
			
		}
	  }

	/**
	    * @param method
	    * @param methodAST
	    */
	protected void acquireFormalParameters(Method method, ExtendedAST methodAST) {
	    try {
			// get the subtree with root PARAMETERS
			ExtendedAST allParametersAST   = methodAST.firstChildOfType(JavaTokenTypes.PARAMETERS);

			// get an array with all subtrees with root PARAMETER_DEF
			ExtendedAST[] parameterDefArray =    allParametersAST.childrenOfType(JavaTokenTypes.PARAMETER_DEF);

			// acquire formal parameters one by one
			for (int i = 0; i < parameterDefArray.length; i++) {
			  ExtendedAST parameterAST = parameterDefArray[i];
			  
			  // create a new ParameterAcquirer
			  ParameterAcquirer acquirer = getFactory().getParameterAcquirer(_linkage);
			  
			  
			  method.addParameter(acquirer.acquire(method, parameterAST));
			}
		} catch (NullPointerException e) {
			System.err.println("methodacquirer acquireformalparameters went wrong ***");
		}
	  }

	protected void acquireOtherModifier(Method method, ExtendedAST objectTypeAST, int mod, MethodModifier modifier) {
	    if(containsModifier(objectTypeAST, mod)) {
	      method.addModifier(modifier);
	      

	    }
	  }





	protected void acquireOtherModifiers(Method method, ExtendedAST objectTypeAST) {
	    acquireOtherModifier(method, objectTypeAST, JavaTokenTypes.LITERAL_native, new Native());
	    acquireOtherModifier(method, objectTypeAST, JavaTokenTypes.ABSTRACT, new Abstract());
	    acquireOtherModifier(method, objectTypeAST, JavaTokenTypes.LITERAL_static, new Static());
	    acquireOtherModifier(method, objectTypeAST, JavaTokenTypes.STRICTFP, new StrictFP());
	    acquireOtherModifier(method, objectTypeAST, JavaTokenTypes.LITERAL_synchronized, new Synchronized());
	  }

	protected String extractName(ExtendedAST methodAST) {
	    ExtendedAST nameAST	 = methodAST.firstChildOfType(JavaTokenTypes.IDENT);
	    if(nameAST == null) {
	    	System.out.println("nameAST null");
	    }
	    return nameAST.getText();
	  }
	

}
