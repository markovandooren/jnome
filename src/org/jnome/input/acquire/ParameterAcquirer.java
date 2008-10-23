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
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.element.ElementImpl;
import chameleon.core.modifier.VariableModifier;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.Variable;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Final;

/**
 * @author Marko van Dooren
 */
public class ParameterAcquirer extends Acquirer {

  /**
   * @param factory
   */
  public ParameterAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }

  /**
    * @param parameterAST
    * @return
    */
  /*@
    @ public behavior
    @*/
  public FormalParameter acquire(ElementImpl context, ExtendedAST parameterAST) {
	  try {
		//System.out.println("Acquiring "+parameterAST.getText());
		  // acquire name
		String name = extractName(parameterAST);
		JavaTypeReference type = extractType(null, parameterAST);
		FormalParameter result = new FormalParameter(name, type);   
		acquireOtherModifiers(result, parameterAST);
		setDecorators(parameterAST,result);

		return result;
	} catch (NullPointerException e) {
		System.err.println("parameterAcquirer acquire errr****");
		return null;
	}
  }
  
  private void setDecorators(ExtendedAST parameterAST, FormalParameter param) {
		ExtendedAST[] children = null;
		ExtendedAST[] modifiers = null;
		ExtendedAST type = null;
		ExtendedAST paramName = null;

		try {
			children = parameterAST.children();
			modifiers = children[0].children();
			type = children[1].children()[0];
			paramName = children[2];
		} catch (ArrayIndexOutOfBoundsException e){}

		
			if (modifiers != null)
	    	for (int i=0; i<modifiers.length; i++)
	    	{
	    		ExtendedAST currentChild = modifiers[i];
	    		setDecorator(param, currentChild, Decorator.ACCESSTYPE_DECORATOR);
	    	}	
	    	
			if (type!=null)
	    	{
				int offset, eoffset;
	     		
		    	if (type.getText().equals("[")){ // array
					offset=type.firstChild().extractBeginOffset(_linkage);
					eoffset=type.extractBeginOffset(_linkage);
		    	} else
		    	{
					offset=type.extractBeginOffset(_linkage);
					eoffset=offset+type.getLength();
		    	}

				_linkage.decoratePosition(offset, eoffset-offset, Decorator.TYPE_DECORATOR, param);
	    	}
	    	
	    	// var name
			if (paramName!=null)
	    	{
				setSmallDecorator(param, paramName, Decorator.NAME_DECORATOR);
	    	}	


  }

protected void acquireOtherModifier(Variable var, ExtendedAST objectTypeAST, int mod, VariableModifier modifier) {
    if(containsModifier(objectTypeAST, mod)) {
      var.addModifier(modifier);
    }
  }

  protected void acquireOtherModifiers(Variable var, ExtendedAST objectTypeAST) {
    acquireOtherModifier(var, objectTypeAST, JavaTokenTypes.FINAL, new Final());
  }

   
  

}
