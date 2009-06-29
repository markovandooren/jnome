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

import java.util.List;

import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.modifier.Default;
import org.jnome.mm.modifier.Protected;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.LookupException;
import chameleon.core.element.ElementImpl;
import chameleon.core.expression.Expression;
import chameleon.core.modifier.AccessModifier;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Public;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public class Acquirer {
 
  //the element parent  	
  protected ElementImpl elemParent;

  
  protected void setDecorator(ElementImpl el, ExtendedAST ast, String dectype){
			int offset = ast.extractBeginOffset(_linkage);
			int length = ast.extractEndOffset(_linkage)-offset;
			_linkage.decoratePosition(offset, length, dectype, el);
 	  }  

protected void setSmallDecorator(ElementImpl el, ExtendedAST ast, String dectype){
			int offset = ast.getBeginOffset(_linkage);
			int length = ast.getLength();
			_linkage.decoratePosition(offset, length, dectype, el);

}  

public Acquirer(Factory factory, ILinkage linkage) {

    _factory = factory;
    _linkage = linkage;
  }

	/**
	 * 
	 * @uml.property name="_factory"
	 * @uml.associationEnd 
	 * @uml.property name="_factory" multiplicity="(1 1)"
	 */
	private Factory _factory;
	protected ILinkage _linkage;

  
  public Factory getFactory() {
    return _factory;
  }

  protected AccessModifier extractAccess(ElementImpl context, ExtendedAST objectTypeAST) {
    if(containsModifier(objectTypeAST, JavaTokenTypes.LITERAL_public)) {
      return new Public();
    }
    if(containsModifier(objectTypeAST, JavaTokenTypes.LITERAL_protected)) {
      return new Protected();
    }
    if(containsModifier(objectTypeAST, JavaTokenTypes.LITERAL_private)) {
      return new Private();
    }
    else {
      return new Default();
    }
  }

  protected boolean containsModifier(ExtendedAST objectTypeAST, int type) {
    ExtendedAST modifierAST =    objectTypeAST.firstChildOfType(JavaTokenTypes.MODIFIERS);
    boolean result =    modifierAST.firstChildOfType(type)    != null;
    return result;
  }

  protected String extractName(ExtendedAST objectTypeAST) {
    ExtendedAST nameAST    = objectTypeAST.firstChildOfType(JavaTokenTypes.IDENT);
    return nameAST.getText();
  }
//  
//  protected int extractNameLineNumber(ExtendedAST objectTypeAST) {
//    ExtendedAST nameAST    = objectTypeAST.firstChildOfType(JavaTokenTypes.IDENT);
//    return nameAST.getLineNumber();
//  }
//  
//  protected int extractNameColumnNumber(ExtendedAST objectTypeAST) {
//    ExtendedAST nameAST    = objectTypeAST.firstChildOfType(JavaTokenTypes.IDENT);
//    return nameAST.getColumnNumber();
//  }
  

  protected JavaTypeReference extractType(ElementImpl context, ExtendedAST methodAST) {
    ExtendedAST typeAST    = methodAST.firstChildOfType(JavaTokenTypes.TYPE);
    
    // remove the root of the tree
    typeAST = typeAST.firstChild();
  
    // how many bracketgroups "[]" follow the type?
    int nbArrays = 0;
    while (typeAST.getType() == JavaTokenTypes.ARRAY_DECLARATOR){
      nbArrays++;
      typeAST = typeAST.firstChild();
    }
  
    //make a vector containing the parts of the (fully qualified)
    //name
    List list = typeAST.getListOfStrings();
    String typeName = Util.concat(list);
  
    while(nbArrays > 0) {
      typeName += "[]";
      nbArrays--;
    }
    
    return new JavaTypeReference(typeName);
  }

  protected Expression extractInitCode(ElementImpl context, ExtendedAST variableAST) {
		Expression expr;
		ExtendedAST assignAST, exprAST;
		assignAST = variableAST.firstChildOfType(JavaTokenTypes.ASSIGN);
		if (assignAST == null) {
		  expr = null;
		}
		else {
		  // check whether the assigned value is an expression or
		  // an array initializer
		  if (assignAST.firstChild().getType() == JavaTokenTypes.EXPR) {
		    // an expression is assigned
		    exprAST = assignAST.firstChild().firstChild();
		  }
		  else {
		    // an array initializer is assigned
		    exprAST = assignAST.firstChild();
		  }
		  expr = getFactory().getExpressionAcquirer(_linkage).acquire(exprAST);
		}
		return expr;
  }

public String flattenDot(ExtendedAST ast) {

    if(ast.getType() == JavaTokenTypes.DOT) {
        return flattenDot(ast.firstChild()) + "." + flattenDot(ast.firstChild().nextSibling());  
    } else if(ast.getType() == JavaTokenTypes.ARRAY_DECLARATOR) {
      return flattenDot(ast.firstChild())+"[]";
    }
    else {
        return ast.getText();
    }
  }

}
