
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
import org.jnome.mm.modifier.Transient;
import org.jnome.mm.modifier.Volatile;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.expression.Expression;
import chameleon.core.modifier.AccessModifier;
import chameleon.core.modifier.MemberVariableModifier;
import chameleon.core.type.Type;
import chameleon.core.variable.MemberVariable;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Static;

/**
 * @author Marko van Dooren
 */
public class MemberVariableAcquirer extends Acquirer {

  /**
   * @param factory
   */
  public MemberVariableAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }

  /**
    * @param type
    * @param variableAST
    */
  /*@
    @ public behavior
    @*/
  public void acquire(Type type, ExtendedAST variableAST) {
	  try {
		//System.out.println("Acquiring "+variableAST.getText());
		  // acquire variable name
		  String name = extractName(variableAST);

		  //variableAST.showChildren(0);
		  
		  // acquire accessibility modifier
		  AccessModifier accessibility = extractAccess(null, variableAST);
		  JavaTypeReference varType = extractType(null, variableAST);
		  Expression expr = extractInitCode(null, variableAST);
		  
		  MemberVariable var = new MemberVariable(name, varType, expr);
		  
		  var.addModifier(accessibility);
		  acquireOtherModifiers(var, variableAST);
		  var.setTypeReference(varType);
		  type.addVariable(var);
		  
		  setALLDecorator(variableAST, var); 
		  setDecorators(variableAST, var);  
		  // create a new variable
		  // TODO acquireDocumentationBlock(otv, variableAST);
	} catch (NullPointerException e) {
		System.err.println("***MembervariableAcquirer acquire went wrong***");
		
	}
	}

private void setDecorators(ExtendedAST ast, MemberVariable var){
	ExtendedAST[] children = null;
	ExtendedAST[] modifiers = null;
	ExtendedAST type = null;
	ExtendedAST varName = null;
	ExtendedAST assignment = null;

	try {
		children = ast.children();
		modifiers = children[0].children();
		type = children[1].children()[0];
		varName = children[2];
		assignment = null;
	if (children.length>3) assignment = children[3].firstChild();
	} catch (ArrayIndexOutOfBoundsException e){}
	
	

		if (modifiers != null)
    	for (int i=0; i<modifiers.length; i++)
    	{
    		ExtendedAST currentChild = modifiers[i];
    		setDecorator(var, currentChild, Decorator.ACCESSTYPE_DECORATOR);
    	}	
    	
		if (type != null)
    	{
			
			int offset, eoffset;
     		
	    	if (type.getText().equals("[")){ // array
	    		offset = type.firstChild().extractBeginOffset(_linkage);
	    		eoffset = type.extractEndOffset(_linkage);  
	    	} else
	    	{
	    		//offset = type.firstChild().extractBeginOffset(_linkage);
	    		offset = type.extractBeginOffset(_linkage);
	    		eoffset = offset + type.getLength();
	    	}
			
			_linkage.decoratePosition(offset, eoffset-offset,Decorator.TYPE_DECORATOR,var);
    	}
    	
    	// var name
		if (varName != null)		
			setSmallDecorator(var,varName, Decorator.NAME_DECORATOR);
    	
    	if(!(assignment == null))
    	{
			int offset = assignment.extractBeginOffset(_linkage);
			int eoffset = assignment.extractEndOffset(_linkage);
			_linkage.decoratePosition(offset, eoffset-offset, Decorator.ASSIGNMENT_DECORATOR, var);
    	}


}  



private void setALLDecorator(ExtendedAST variableAST, MemberVariable var) {


      int offset = variableAST.extractBeginOffset(_linkage);
      int eoffset = variableAST.extractEndOffset(_linkage);
      int length = eoffset- offset;
      _linkage.decoratePosition(offset, eoffset-offset, Decorator.ALL_DECORATOR, var);

}



protected void acquireOtherModifier(MemberVariable var, ExtendedAST objectTypeAST, int mod, MemberVariableModifier modifier) {
    if(containsModifier(objectTypeAST, mod)) {
      var.addModifier(modifier);
    }
  }
  

	


  protected void acquireOtherModifiers(MemberVariable var, ExtendedAST objectTypeAST) {
    acquireOtherModifier(var, objectTypeAST, JavaTokenTypes.FINAL, new Final());
    acquireOtherModifier(var, objectTypeAST, JavaTokenTypes.LITERAL_static, new Static());
    acquireOtherModifier(var, objectTypeAST, JavaTokenTypes.LITERAL_volatile, new Volatile());
    acquireOtherModifier(var, objectTypeAST, JavaTokenTypes.LITERAL_transient, new Transient());
  }
  

  
  
}
