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

import org.jnome.input.parser.ExtendedAST;
import org.jnome.mm.method.JavaMethodLocalContext;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.element.ElementImpl;
import chameleon.core.method.Method;
import chameleon.core.method.MethodContext;
import chameleon.core.method.MethodLocalContext;
import chameleon.core.method.RegularMethod;
import chameleon.core.modifier.AccessModifier;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Public;


/**
 * @author Marko van Dooren
 */
public class RegularMethodAcquirer extends MethodAcquirer {
  
  public RegularMethodAcquirer(Factory factory, boolean isInterface, ILinkage linkage) {
    super(factory, linkage);
    _isInterface = isInterface; 
  }
  
  private boolean _isInterface;

  protected Method createMethod(ElementImpl context, String name, AccessModifier accessibility, ExtendedAST methodAST, Type type) {
     try {
		JavaTypeReference returnType = extractType(null, methodAST);
		  //FIXME no use of the ContextFactory yet. This is because we get a NullPointerException if we do
	   	  //      reason: the parent of an AnonymousInnerType is a ConstructorInvocation, this class has not
	   	  //              got his parent yet, so when asking it will be null
		MethodLocalContext mlc = new JavaMethodLocalContext();
//		MethodLocalContext mlc = type.getContextFactory().getMethodLocalContext();
		
		 Method method = new RegularMethod(accessibility, name, returnType, new MethodContext(), mlc);
		 if(type != null){
		 	type.addMethod(method);
		 }
		 if(_isInterface) {
		   method.addModifier(new Public());
		   method.addModifier(new Abstract());
		 }
		 return method;
	} catch (NullPointerException e) {
		System.err.println("RegularMethodAcquirer createMethod errr***");
		return null;
	}
  }

}
