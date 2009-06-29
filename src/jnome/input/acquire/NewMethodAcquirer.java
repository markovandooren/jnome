package org.jnome.input.acquire;


import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;

import chameleon.core.LookupException;
import chameleon.core.method.Method;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;


public class NewMethodAcquirer extends Acquirer { 
	/**
	 * @param factory
	 */
	public NewMethodAcquirer(Factory factory, ILinkage linkage) {
	  super(factory, linkage);
	}	
	
	public void acquireNewMethod(Type type, ExtendedAST current, Method oldMethod)throws  LookupException{ 
		try {
			//System.out.println("Acquiring "+current.getText());
			Method newMethod = null;
			Type newType = (Type) oldMethod.getAllParents().get(0);
			  if(isMethod(current)) {
			    newMethod = acquireMethod(type, current);
			    newType.replaceMethod(oldMethod, newMethod);
			  } else if(isConstructor(current)) {
			    newMethod = acquireConstructor(type, current);
			    ((Type)newType).replaceMethod( oldMethod,  newMethod);
			  }
		} catch (NullPointerException e) {
			System.err.println("newmethodAcquirer acquirenewmethod errr****");
		}


	}
	
	protected boolean isMethod(ExtendedAST ast) {
	  return ast.getType() == JavaTokenTypes.METHOD_DEF;
	}
	  
	protected Method acquireMethod(Type type, ExtendedAST methodAST) {
	  try {
		RegularMethodAcquirer acquirer = getFactory().getRegularMethodAcquirer(false/*isInterface()*/, _linkage);
		  return acquirer.getNewMethod(type, methodAST);
	} catch (NullPointerException e) {
		System.err.println("newmethodAcquirer acquireMethod errr***");
		return null;
	}
	}
	
	protected Method acquireConstructor(Type type, ExtendedAST constructorAST) {
	  try {
		return getFactory().getConstructorAcquirer(_linkage).getNewMethod(type, constructorAST);
	} catch (NullPointerException e) {
		System.err.println("newmethodacquirer acquireConstructor errr***");
		return null;
	}
	}
	  
	protected boolean isConstructor(ExtendedAST ast) {
	  return ast.getType() == JavaTokenTypes.CTOR_DEF;
	}
}
