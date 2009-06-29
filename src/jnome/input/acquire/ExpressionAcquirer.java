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
import java.util.Iterator;

import org.jnome.decorator.Decorator;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.expression.ArrayAccessExpression;
import org.jnome.mm.expression.ArrayCreationExpression;
import org.jnome.mm.expression.ArrayInitializer;
import org.jnome.mm.expression.ClassLiteral;
import org.jnome.mm.expression.ConstructorInvocation;
import org.jnome.mm.expression.DimensionInitializer;
import chameleon.support.expression.SuperConstructorDelegation;
import org.jnome.mm.type.JavaTypeReference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.Config;
import chameleon.core.LookupException;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.element.ElementImpl;
import chameleon.core.expression.Assignable;
import chameleon.core.expression.ConditionalAndExpression;
import chameleon.core.expression.ConditionalOrExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.Invocation;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.PostfixOperatorInvocation;
import chameleon.core.expression.PrefixOperatorInvocation;
import chameleon.core.expression.RegularLiteral;
import chameleon.core.expression.RegularMethodInvocation;
import chameleon.core.expression.VariableReference;
import chameleon.linkage.ILinkage;
import chameleon.support.expression.ActualParameter;
import chameleon.support.expression.ArrayIndex;
import chameleon.support.expression.AssignmentExpression;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.ConditionalExpression;
import chameleon.support.expression.EmptyArrayIndex;
import chameleon.support.expression.FilledArrayIndex;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.expression.LiteralWithTypeReference;
import chameleon.support.expression.NullLiteral;
import chameleon.support.expression.SuperTarget;
import chameleon.support.expression.ThisConstructorDelegation;
import chameleon.support.expression.ThisLiteral;
import chameleon.support.member.simplename.infixoperator.InfixOperatorInvocation;

/**
 * @author Marko van Dooren
 */
public class ExpressionAcquirer extends Acquirer {

  /**
   * @param factory
   */
  public ExpressionAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }

  /**
   * @param extendedAST
   * @return
 * @throws LookupException 
   */
  public Expression acquire(ExtendedAST extendedAST) {
  	ElementImpl context=null;
	  //System.out.println("Acquiring in ExpressionAcquirer: "+extendedAST.getText());
    Expression expression = null;
    if(extendedAST == null) {
    	if(Config.DEBUG) {
    		System.out.println("extendedAST given to Java expression acquirer is null.");
    	}
    	return null;
    }
    if(isPrefixOperator(extendedAST)) {
      expression = acquirePrefixOperator(extendedAST);
    } 
    else if(isPostfixOperator(extendedAST)) {
      expression = acquirePostfixOperator(extendedAST);
    } 
    else if(isInfixOperator(extendedAST)) {
      expression = acquireInfixOperator(extendedAST);
    }
    else if(isMethodInvocation(extendedAST)) {
      expression = acquireMethodInvocation(extendedAST);
    }
    else if(isConstructorInvocation(extendedAST)) {
      expression = acquireConstructorInvocation(extendedAST);
    }
    else if(isIntLiteral(extendedAST)) {
      expression = acquireIntLiteral(extendedAST);
    } 
    else if(isLongLiteral(extendedAST)) {
      expression = acquireLongLiteral(extendedAST);
    }
    else if(isFloatLiteral(extendedAST)) {
      expression = acquireFloatLiteral(extendedAST);
    }
    else if(isDoubleLiteral(extendedAST)) {
      expression = acquireDoubleLiteral(extendedAST);
    }
    else if(isThisLiteral(extendedAST)) {
      expression = acquireThisLiteral(extendedAST);
    } 
    else if(isNullLiteral(extendedAST)) {
      expression = acquireNullLiteral(extendedAST);
    }
    else if(isClassLiteral(extendedAST)) {
      expression = acquireClassLiteral(extendedAST);
    }
    else if(isVariableReference(extendedAST)) {
      expression = acquireVariableReference(extendedAST);
    } 
    else if(isAssignment(extendedAST)) {
      expression = acquireAssignment(extendedAST);
    } 
    else if(isCondAndOperator(extendedAST)) {
      expression = acquireCondAndOperator(extendedAST);
    } 
    else if(isCondOrOperator(extendedAST)) {
      expression = acquireCondOrOperator(extendedAST);
    }
    else if(isConditionalOperator(extendedAST)) {
      expression = acquireConditionalOperator(extendedAST);
    } 
    else if(isCombiAssignment(extendedAST)) {
      expression = acquireCombiAssignment(extendedAST);
    } 
    else if(isClassCast(extendedAST)) {
      expression = acquireClassCast(extendedAST);
    } 
    else if(isArrayCreation(extendedAST)) {
      expression = acquireArrayCreation(extendedAST);
    }
    else if(isArrayAccess(extendedAST)) {
      expression = acquireArrayAccess(extendedAST);
    }
    else if(isBooleanLiteral(extendedAST)) {
      expression = acquireBooleanLiteral(extendedAST);
    } 
    else if(isStringLiteral(extendedAST)) {
    	expression = acquireStringLiteral(extendedAST);
    }
    else if(isCharLiteral(extendedAST)) {
    	expression = acquireCharLiteral(extendedAST);
    }
    else if(isInstanceofOperator(extendedAST)) {
    	expression = acquireInstanceof(extendedAST);
    } 
    else if(isArrayInitializer(extendedAST)) {
      expression = acquireArrayInitializer(extendedAST);
    }
    else {
      System.out.println("Expression: " + extendedAST.toStringTree());
      System.out.println("Type: " + extendedAST.getType());
      throw new IllegalArgumentException("Unknown expression");
    }
    if(expression == null) {
      System.out.println("Expression: " + extendedAST.toStringTree());
      System.out.println("Type: " + extendedAST.getType());
      System.err.println("The expression was null while we were acquiring the expression");
      return null;

      //throw new Error("The expression was null while we were acquiring the expression");
    }

    //Add decorators
    if(Config.DEBUG) {
      if(expression.getDecorator(Decorator.ALL_DECORATOR) != null) {
    	  throw new ChameleonProgrammerException("Acquired expression already has ALL decorator: "+expression.getClass().getName());
      }
    }
    setAllDecorator(extendedAST, expression);
//    int start = extendedAST.extractBeginOffset(_linkage);
//    int stop  = extendedAST.extractEndOffset(_linkage);
//    _linkage.decoratePosition(start, stop-start, Decorator.ALL_DECORATOR, expression);
		
    return expression;
  }
//	
//	protected ExtendedAST getFirstLengthNotNullAST(ExtendedAST extendedAST){
//
//		return extendedAST;
//	}  
  
  /**********
   * PREFIX *
   **********/
  
  public boolean isPrefixOperator(ExtendedAST extendedAST) {
    int type = extendedAST.getType();
    return type == JavaTokenTypes.UNARY_MINUS ||
           type == JavaTokenTypes.UNARY_PLUS ||
           type == JavaTokenTypes.DEC ||
           type == JavaTokenTypes.INC ||
           type == JavaTokenTypes.BNOT ||
           type == JavaTokenTypes.LNOT;
  }
  
  public Expression acquirePrefixOperator(ExtendedAST extendedAST) {
    String methodName = extendedAST.getText();
    ExtendedAST targetAST = extendedAST.firstChild();
    InvocationTarget target = acquireTarget(targetAST);
    PrefixOperatorInvocation poi = new PrefixOperatorInvocation(methodName, target);
    setOperatorDecorator(extendedAST,poi);
    setInvocationDecorator(extendedAST,poi);
    return poi;
  }

//  private void setOperatorInvocationDecorator(ExtendedAST extendedAST, Invocation operatorInvoc) {
//		int offset = 0;
//		try {
//			offset = extendedAST.extractBeginOffset(_linkage);
//			int length = extendedAST.extractEndOffset(_linkage)-offset;
//			Decorator accessdec = new Decorator(offset,length,operatorInvoc,Decorator.OPERATOR_INVOCATION_DECORATOR);
//			operatorInvoc.setDecorator(accessdec,Decorator.OPERATOR_INVOCATION_DECORATOR);
//			_linkage.addPosition(Decorator.CHAMELEON_CATEGORY,accessdec);
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		} catch (BadPositionCategoryException e) {
//			e.printStackTrace();
//		}
//	
//  } TODO remove

protected InvocationTarget acquireTarget(ExtendedAST targetAST) {
    InvocationTarget result = null;
    if(targetAST == null) {
      return null;
    }
    else if(targetAST.getType() == JavaTokenTypes.IDENT) {
      result = new NamedTarget(targetAST.getText());
    }
    else if(targetAST.getType() == JavaTokenTypes.LITERAL_super) {
      result = new SuperTarget();
    }
    else if(targetAST.getType() == JavaTokenTypes.ARRAY_DECLARATOR) {
      result = flattenArrayDeclarator(targetAST);
    }
    else if(targetAST.getType() == JavaTokenTypes.DOT) {
    	//result = acquireTarget(targetAST.firstChild().nextSibling());
      //FIXME: if the targetAST is like:
    	//   .
    	//   |
    	//   super - b
    	// => "super" is not parsed 
    	//    (the Junit test TestVar will not succeed!)
    	result = acquireTarget(targetAST.firstChild().nextSibling());
//    	if(targetAST.firstChild().getType() == JavaTokenTypes.LITERAL_super){
//    		result = acquireTarget(targetAST.firstChild());
//    	}
    	
      //result = acquireTarget(context, targetAST.firstChild().nextSibling());
      if(result instanceof LiteralWithTypeReference) {
      	((LiteralWithTypeReference)result).setTypeReference(new JavaTypeReference(flattenDot(targetAST.firstChild())));
      }
      else {
      	InvocationTarget prec = acquireTarget(targetAST.firstChild());
//      	if((result instanceof NamedTarget) && (prec instanceof NamedTarget)) {
//      	  ((NamedTarget)prec).setName(((NamedTarget)prec).getName() + "." + ((NamedTarget)result).getName());
//      	  result = prec;
//      	}
//      	else {
      	  result.setTarget(prec);
//      	}
      }
    }
    else {
			result = acquire(targetAST);
		}
    setInvocationDecorator(targetAST,result);
    return result;
  }

  private void setInvocationDecorator(ExtendedAST targetAST, InvocationTarget result) {
		int offset;

			offset = targetAST.extractBeginOffset(_linkage);
			int length = targetAST.extractEndOffset(_linkage)-offset;
			_linkage.decoratePosition(offset, length, Decorator.INVOCATION_TARGET_DECORATOR, result);
  }

/**
	 * @param targetAST
	 * @return
	 */
	private InvocationTarget flattenArrayDeclarator(ExtendedAST targetAST) {
    // how many bracketgroups "[]" follow the type?
    int nbArrays = 0;
    while (targetAST.getType() == JavaTokenTypes.ARRAY_DECLARATOR){
      nbArrays++;
      targetAST = targetAST.firstChild();
    }
    String typeName = flattenDot(targetAST);
    while(nbArrays > 0) {
      typeName += "[]";
      nbArrays--;
    }
    return new NamedTarget(typeName);
	}

	/***********
   * POSTFIX *
   ***********/
  
  public boolean isPostfixOperator(ExtendedAST extendedAST) {
    int type = extendedAST.getType();
    return type == JavaTokenTypes.POST_DEC ||
           type == JavaTokenTypes.POST_INC;
  }
  
  public Expression acquirePostfixOperator(ExtendedAST extendedAST) {
    String methodName = extendedAST.getText();
    ExtendedAST targetAST = extendedAST.firstChild();
    InvocationTarget target = acquireTarget(targetAST);
    PostfixOperatorInvocation poi = new PostfixOperatorInvocation(methodName, target);
    setOperatorDecorator(extendedAST,poi);
    setInvocationDecorator(extendedAST,poi);
    return poi;
  }

  /*********
   * INFIX *
   *********/
  
  public boolean isInfixOperator(ExtendedAST extendedAST) {
    int type = extendedAST.getType();
    return type == JavaTokenTypes.STAR ||
           type == JavaTokenTypes.DIV ||
           type == JavaTokenTypes.MOD ||
           type == JavaTokenTypes.PLUS ||
           type == JavaTokenTypes.MINUS ||
           type == JavaTokenTypes.SR ||
           type == JavaTokenTypes.SL ||
           type == JavaTokenTypes.BSR ||
           type == JavaTokenTypes.LE ||
           type == JavaTokenTypes.LT ||
           type == JavaTokenTypes.GT ||
           type == JavaTokenTypes.GE ||
           type == JavaTokenTypes.EQUAL ||
           type == JavaTokenTypes.NOT_EQUAL ||
           type == JavaTokenTypes.BAND ||
           type == JavaTokenTypes.BOR ||
           type == JavaTokenTypes.BXOR;
  }
  
  public Expression acquireInfixOperator(ExtendedAST extendedAST) {
    String methodName = extendedAST.getText();
    ExtendedAST leftAST = extendedAST.firstChild();
    Expression left = acquire(leftAST);
    Expression right= null;
    if(leftAST != null) {
    	right = acquire(leftAST.nextSibling());
    }
		InfixOperatorInvocation result = new InfixOperatorInvocation(methodName, left);
    result.addParameter(new ActualParameter(right));
    setInvocationDecorator(extendedAST,result);
    setOperatorDecorator(extendedAST, result);
    return result;
  }

  /**************
   * INSTANCEOF *
   **************/
  
  public boolean isInstanceofOperator(ExtendedAST extendedAST) {
		return extendedAST.getType() == JavaTokenTypes.LITERAL_instanceof;
	}
  
  public Expression acquireInstanceof(ExtendedAST extendedAST) {
    Expression left = null;
    JavaTypeReference	right = null;
		left = acquire(extendedAST.firstChild());
	    right = new JavaTypeReference(flattenDot(extendedAST.firstChild().nextSibling()));
    	InstanceofExpression ioe= new InstanceofExpression(left, right);
	    setInstanceOfDecorators(extendedAST, ioe,left,right);
	    return ioe;
  }
  
  private void setInstanceOfDecorators(ExtendedAST extendedAST, InstanceofExpression ioe, Expression left, JavaTypeReference right) {

		setSmallDecorator(ioe, extendedAST, Decorator.CODEWORD_DECORATOR);
		// TODO type decorator?
  }

/***************
   * INT LITERAL *
   ***************/
  
  public boolean isIntLiteral(ExtendedAST extendedAST) {
    return(extendedAST.getType() == JavaTokenTypes.NUM_INT);
  }
  
  public Expression acquireIntLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("int");
    RegularLiteral r = new RegularLiteral(tr, extendedAST.getText());

  	return r;
  }
  
  
  
  /****************
   * LONG LITERAL *
   ****************/
  
  public boolean isLongLiteral(ExtendedAST extendedAST) {
    return(extendedAST.getType() == JavaTokenTypes.NUM_LONG);
  }
  
  public Expression acquireLongLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("long");
    RegularLiteral r = new RegularLiteral(tr, extendedAST.getText());
  	return r;
  }
  
  /*****************
   * FLOAT LITERAL *
   *****************/
  
  public boolean isFloatLiteral(ExtendedAST extendedAST) {
    return(extendedAST.getType() == JavaTokenTypes.NUM_FLOAT);
  }
  
  public Expression acquireFloatLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("float");
    RegularLiteral r = new RegularLiteral(tr, extendedAST.getText());
  	return r;
  }
  
  /*******************
   * BOOLEAN LITERAL *
   *******************/
  
  public boolean isBooleanLiteral(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.LITERAL_true ||
           extendedAST.getType() == JavaTokenTypes.LITERAL_false;
  }
  
  public Expression acquireBooleanLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("boolean");
	  RegularLiteral rl = new RegularLiteral(tr, extendedAST.getText());
	  return rl;
  }
  


/******************
   * STRING LITERAL *
   ******************/
  
  public boolean isStringLiteral(ExtendedAST extendedAST) {
  	return extendedAST.getType() == JavaTokenTypes.STRING_LITERAL;
  }
  
  public Expression acquireStringLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("java.lang.String");
	  RegularLiteral r = new RegularLiteral(tr, extendedAST.getText());
	  return r;
  }
  
 

/****************
   * CHAR LITERAL *
   ****************/
  
  public boolean isCharLiteral(ExtendedAST extendedAST) {
  	return extendedAST.getType() == JavaTokenTypes.CHAR_LITERAL;
  }
  
  public Expression acquireCharLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("char");
  	RegularLiteral r =  new RegularLiteral(tr, extendedAST.getText());
  	return r;
  }
  
  /*****************
   * CLASS LITERAL *
   *****************/
  
  public boolean isClassLiteral(ExtendedAST extendedAST) {
    return (extendedAST.getType() == JavaTokenTypes.LITERAL_class) ||
           ((extendedAST.getType() == JavaTokenTypes.DOT) &&
           (extendedAST.firstChild().nextSibling().getType() == JavaTokenTypes.LITERAL_class));
  }
  
  public Expression acquireClassLiteral(ExtendedAST extendedAST) {
    ClassLiteral result = new ClassLiteral();
    JavaTypeReference tr = new JavaTypeReference("java.lang.Class");
    if(extendedAST.getType() == JavaTokenTypes.DOT) {
      result.setTypeReference(tr); 
    }
    return result;
  }
  
  /******************
   * DOUBLE LITERAL *
   ******************/
  
  public boolean isDoubleLiteral(ExtendedAST extendedAST) {
    return(extendedAST.getType() == JavaTokenTypes.NUM_DOUBLE);
  }
  
  public Expression acquireDoubleLiteral(ExtendedAST extendedAST) {
	  JavaTypeReference tr = new JavaTypeReference("double");
    RegularLiteral r = new RegularLiteral(tr, extendedAST.getText());
  	return r;
  }
  
  /****************
   * THIS LITERAL *
   ****************/
  
  public boolean isThisLiteral(ExtendedAST extendedAST) {
    return (extendedAST.getType() == JavaTokenTypes.LITERAL_this) ||
           ( 
             (extendedAST.getType() == JavaTokenTypes.DOT) &&
             (extendedAST.firstChild().nextSibling().getType() == JavaTokenTypes.LITERAL_this)
           );
  }
  
//  public Expression acquireThisLiteral(ExtendedAST extendedAST) {
//    ThisLiteral result = new ThisLiteral(context);
//    JavaTypeReference tr = new JavaTypeReference(result, flattenDot(extendedAST.firstChild()));
//    if(extendedAST.getType() == JavaTokenTypes.DOT) {
//      result.setTypeReference(tr); 
//    }
//    setCodewordDecorator(extendedAST,result);
//    return result;
//  }
  
  public Expression acquireThisLiteral(ExtendedAST extendedAST) {
	    ThisLiteral result = new ThisLiteral();
	    if(extendedAST.getType() == JavaTokenTypes.DOT) {
	      result.setTypeReference(new JavaTypeReference(flattenDot(extendedAST.firstChild()))); 
	    }
	    setCodewordDecorator(extendedAST,result);
	    return result;
	  }
  /****************
   * NULL LITERAL *
   ****************/
  
  public boolean isNullLiteral(ExtendedAST extendedAST) {
    return(extendedAST.getType() == JavaTokenTypes.LITERAL_null);
  }
  
  public Expression acquireNullLiteral(ExtendedAST extendedAST) {
	  NullLiteral n = new NullLiteral();
	  setCodewordDecorator(extendedAST,n);
    return n;
  }
  
  /********************
   * MEHOD INVOCATION *
   ********************/
  
  public boolean isMethodInvocation(ExtendedAST extendedAST) {
    return(extendedAST.getType() == JavaTokenTypes.METHOD_CALL);
  }
  
  public Expression acquireMethodInvocation(ExtendedAST extendedAST) {
    Invocation result = acquireInvocation(extendedAST.firstChild());
    setInvocationDecorator(extendedAST, result);
    return result;
  }

  private Invocation acquireInvocation(ExtendedAST extendedAST) {
    Invocation result;
    String name;
    InvocationTarget target;
    if(extendedAST.getType() == JavaTokenTypes.IDENT) {
      name = extendedAST.getText();
      target = null;
    }
    else {
      name = extendedAST.firstChild().nextSibling().getText();
      target = acquireTarget(extendedAST.firstChild());
    }
    
    if(name.equals("super")) {
      result = new SuperConstructorDelegation();
    } 
    else if (name.equals("this")) {
      result = new ThisConstructorDelegation();
    }
    else {
    	result = new RegularMethodInvocation( name, target);
    }
    // Actual parameters
    acquireParameters(extendedAST.nextSibling(), result);
    return result;
  }
  
  public void acquireParameters(ExtendedAST parameterAST, final Invocation invocation) {
      //ExtendedAST parameterAST = extendedAST;
      if(parameterAST != null) {
        ExtendedAST[] parameters = parameterAST.childrenOfType(JavaTokenTypes.EXPR);
        new Visitor() {
          public void visit(Object o) {
				   Expression expr = acquire(((ExtendedAST)o).firstChild());
				   invocation.addParameter(new ActualParameter(expr));
          }
        }.applyTo(parameters);
      }
  }
  
  /**************************
   * CONSTRUCTOR INVOCATION *
   **************************/
  
  public boolean isConstructorInvocation(ExtendedAST extendedAST) {
    return (
            (extendedAST.getType() == JavaTokenTypes.LITERAL_new) &&
            (! isArrayConstruction(extendedAST))
           ) ||
           (
            (extendedAST.getType() == JavaTokenTypes.DOT) &&
            (extendedAST.firstChild().nextSibling().getType() == JavaTokenTypes.LITERAL_new) &&
            (! isArrayConstruction(extendedAST.firstChild().nextSibling()))
            );
  }
  
  public boolean isArrayConstruction(ExtendedAST extendedAST) {
      return extendedAST.getType() == JavaTokenTypes.LITERAL_new &&
             extendedAST.firstChild().nextSibling() != null &&
             extendedAST.firstChild().nextSibling().getType() == JavaTokenTypes.ARRAY_DECLARATOR;
  }
  
  public Expression acquireConstructorInvocation(ExtendedAST extendedAST) {
		  ConstructorInvocation result;
		  String typeName;
		  if(extendedAST.getType() == JavaTokenTypes.LITERAL_new) {
			  typeName = flattenDot(extendedAST.firstChild());
			  JavaTypeReference tr = new JavaTypeReference(typeName);
			  result = new ConstructorInvocation(tr,null);
		  }
		  else {
			  
			  typeName = flattenDot(extendedAST.firstChild().nextSibling().firstChild());
			  JavaTypeReference tr =new JavaTypeReference(typeName);
			  Expression expr = acquire(extendedAST.firstChild());
			  result = new ConstructorInvocation(tr, expr);
			  
		  }
		  acquireParameters(extendedAST.firstChild().nextSibling(), result);
		  if(extendedAST.firstChildOfType(JavaTokenTypes.OBJBLOCK) != null) {
			  result.setAnonymousType(getFactory().getAnonymousTypeAcquirer(_linkage).acquire(extendedAST));
		  }
		  setCodewordDecorator(extendedAST, result);
		  return result;
  }

  /******************
   * ARRAY CREATION *
   ******************/
  
  public boolean isArrayCreation(ExtendedAST extendedAST) {
    return isArrayConstruction(extendedAST) ||
           (
            (extendedAST.getType() == JavaTokenTypes.DOT) &&
            (isArrayConstruction(extendedAST.firstChild().nextSibling()))
            );
  }
  
  public Expression acquireArrayCreation(ExtendedAST extendedAST) {
	  ExtendedAST dimExprAST = extendedAST.firstChild().nextSibling().firstChild();
      ArrayList<ArrayIndex> dimInits = new ArrayList<ArrayIndex>();
      int dimension = 0;
      while ((dimExprAST != null) && (dimExprAST.getType() == JavaTokenTypes.ARRAY_DECLARATOR)) {
        Expression expr = null;
        if(dimExprAST.nextSibling() != null) {
          expr = acquire(dimExprAST.nextSibling().firstChild());
        } 
        DimensionInitializer di = new DimensionInitializer(expr);
        //dimInits.add(0, di);
        dimInits.add(0, new FilledArrayIndex(expr));
        dimension++;
        dimExprAST = dimExprAST.firstChild();
      }
      if(dimExprAST == null) {
          //dimInits.add(0,new DimensionInitializer(null));
    	  dimInits.add(0,new EmptyArrayIndex(1));
      }
      else {
    	  Expression expr = acquire(dimExprAST.firstChild());
    	  //DimensionInitializer di = new DimensionInitializer(null,expr);
    	  FilledArrayIndex fai = new FilledArrayIndex(expr);
          //dimInits.add(0,di);
          dimInits.add(0, fai);
      }
      dimension++;
      String typeName = flattenDot(extendedAST.firstChild());
      for(int i = 0; i < dimension; i++) {
          typeName +="[]";
      }
      JavaTypeReference tr =new JavaTypeReference(typeName);
      final ArrayCreationExpression result = new ArrayCreationExpression(tr);
      new Visitor() {
          public void visit(Object o) {
              //result.addDimensionInitializer((DimensionInitializer)o);
        	  result.addDimensionInitializer((ArrayIndex)o);
          }
      }.applyTo(dimInits);

      ArrayInitializer init = acquireArrayInitializer(extendedAST.firstChild().nextSibling().nextSibling());
      for (Iterator iter = dimInits.iterator(); iter.hasNext();) {
  		ArrayIndex element = (ArrayIndex) iter.next();
  		
  	  }
      result.setInitializer(init);
      setCodewordDecorator(extendedAST, result);
      setTypeDecorator(extendedAST.firstChild(), result);
      return result;
     }
     

/*********************
 * ARRAY INITIALIZER *
 *********************/
  
  public boolean isArrayInitializer(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.ARRAY_INIT;
  }
  public ArrayInitializer acquireArrayInitializer(ExtendedAST extendedAST) {
		  if(extendedAST == null) {
			  return null;
		  }
		  ArrayInitializer result = new ArrayInitializer();
		  ExtendedAST iter = extendedAST.firstChild();
		  
		  while(iter != null) {
			  if(iter.getType() == JavaTokenTypes.ARRAY_INIT) {
				  result.addInitializer(acquireArrayInitializer(iter));
			  }
			  else {
				  result.addInitializer(acquire(iter.firstChild()));
			  }
			  iter = iter.nextSibling();
		  }
		  return result;
}

/****************
 * ARRAY ACCESS *
 ****************/
  
public boolean isArrayAccess(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.INDEX_OP ||
           (extendedAST.getType() == JavaTokenTypes.DOT &&
            extendedAST.getType() == JavaTokenTypes.INDEX_OP);
  }
  
  public Expression acquireArrayAccess(ExtendedAST extendedAST) {
	  ExtendedAST dimExprAST = extendedAST.firstChild();
      ArrayList dimExprs = new ArrayList();
      while (dimExprAST.getType() == JavaTokenTypes.INDEX_OP) {
        if(dimExprAST.nextSibling() != null) {
            if(dimExprAST.nextSibling().getType() == JavaTokenTypes.IDENT) {
              //dimExprs.add(0, acquire(null,dimExprAST.nextSibling()));
            	FilledArrayIndex index = new FilledArrayIndex(acquire(dimExprAST.nextSibling()));
				dimExprs.add(0, index);
            }
            else {
            	dimExprs.add(0, new FilledArrayIndex(acquire(dimExprAST.nextSibling().firstChild())));
            }
        }
        dimExprAST = dimExprAST.firstChild();
      }
      //dimExprs.add(0,acquire(null,dimExprAST.nextSibling().firstChild()));
      FilledArrayIndex index = new FilledArrayIndex(acquire(dimExprAST.nextSibling().firstChild()));
	  dimExprs.add(0,index);
      Expression expr = acquire(dimExprAST);
      final ArrayAccessExpression result = new ArrayAccessExpression(expr);
      new Visitor() {
          public void visit(Object o) {
              //result.addIndex((Expression)o);
          	try{
        	  result.addIndex((FilledArrayIndex)o);
          	} catch(ClassCastException e) {
          		throw e;
          	}
          }
      }.applyTo(dimExprs);
      for (Iterator iter = dimExprs.iterator(); iter.hasNext();) {
    	  ArrayIndex element = (ArrayIndex) iter.next();
		
	  }
      return result;
  }
  
  
  /************
   * VARIABLE *
   ************/
  
  public boolean isVariableReference(ExtendedAST extendedAST) {
    return (extendedAST.getType() == JavaTokenTypes.IDENT) ||
           (
            (extendedAST.getType() == JavaTokenTypes.DOT) &&
            (extendedAST.firstChild().nextSibling().getType() == JavaTokenTypes.IDENT)
           );
  }
  
  public Expression acquireVariableReference(ExtendedAST extendedAST) {
  	try{
	  NamedTarget nt =(NamedTarget)acquireTarget(extendedAST);
	  VariableReference r = new VariableReference(nt);
	  return r;
  	}
  	catch(ClassCastException exc) {
  	  NamedTarget nt =(NamedTarget)acquireTarget(extendedAST);
  	  VariableReference r = new VariableReference(nt);
  	  return r;
  	}
  }
  
  /**************
   * ASSIGNMENT *
   **************/
  
  public boolean isAssignment(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.ASSIGN;
  }
  
  public Expression acquireAssignment(ExtendedAST extendedAST) {
  	Expression result = null;
    ExtendedAST assignableAST = extendedAST.firstChild();
    if(assignableAST != null) {
		  Expression value = acquire(assignableAST.nextSibling());
			Expression assignable = acquire(assignableAST);
			result = new AssignmentExpression((Assignable)assignable,value);
			setAssignmentDecorator(extendedAST,result);
    }
		return result;
  }
 
  private void setAssignmentDecorator(ExtendedAST extendedAST, Expression exp) {
	 setSmallDecorator(exp, extendedAST, Decorator.OPERATOR_DECORATOR);
  }

  private void setAllDecorator(ExtendedAST extendedAST, ElementImpl el) {
	  setDecorator(el, extendedAST, Decorator.ALL_DECORATOR);
	}   

  private void setTypeDecorator(ExtendedAST extendedAST, ElementImpl el) {
	  setDecorator(el, extendedAST, Decorator.TYPE_DECORATOR);
	}   
  
  private void setCodewordDecorator(ExtendedAST extendedAST, ElementImpl el) {
	  setSmallDecorator(el, extendedAST, Decorator.CODEWORD_DECORATOR);
	}  
  
  
  private void setOperatorDecorator(ExtendedAST extendedAST, ElementImpl el) {
	  setSmallDecorator(el, extendedAST, Decorator.OPERATOR_DECORATOR);
	
	}  
  



/*******************
   * CONDITIONAL AND *
   *******************/
  
  public boolean isCondAndOperator(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.LAND;
  }
  
  public Expression acquireCondAndOperator(ExtendedAST extendedAST) {
    Expression left = acquire(extendedAST.firstChild());
    Expression right = acquire(extendedAST.firstChild().nextSibling());
    ConditionalAndExpression r = new ConditionalAndExpression(left, right);
    setOperatorDecorator(extendedAST, r);
    return r;
	  
  }
 
  /******************
   * CONDITIONAL OR *
   ******************/
  
  public boolean isCondOrOperator(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.LOR;
  }
  
  public Expression acquireCondOrOperator(ExtendedAST extendedAST) {
		  Expression left = acquire(extendedAST.firstChild());
		  Expression right = acquire(extendedAST.firstChild().nextSibling());
		  ConditionalOrExpression r = new ConditionalOrExpression(left, right);
		  setOperatorDecorator(extendedAST, r);
		  return r;
  }
 
  /***************
   * CONDITIONAL *
   ***************/
  
  public boolean isConditionalOperator(ExtendedAST extendedAST) {
    return extendedAST.getType() == JavaTokenTypes.QUESTION;
  }
  
  public Expression acquireConditionalOperator(ExtendedAST extendedAST) {
	  	Expression left = acquire(extendedAST.firstChild());
	    Expression middle = acquire(extendedAST.firstChild().nextSibling());
	    Expression right = acquire(extendedAST.firstChild().nextSibling().nextSibling());
	    ConditionalExpression r = new ConditionalExpression(left, middle, right);
	    setOperatorDecorator(extendedAST, r);
	    return r;
  }
 
  /************************
   * OPERATOR ASSIGNMENTS *
   ************************/
  
  public boolean isCombiAssignment(ExtendedAST extendedAST) {
      int type = extendedAST.getType();
      return type == JavaTokenTypes.STAR_ASSIGN ||
             type == JavaTokenTypes.DIV_ASSIGN ||
             type == JavaTokenTypes.MOD_ASSIGN ||
             type == JavaTokenTypes.PLUS_ASSIGN ||
             type == JavaTokenTypes.MINUS_ASSIGN ||
             type == JavaTokenTypes.SR_ASSIGN ||
             type == JavaTokenTypes.SL_ASSIGN ||
             type == JavaTokenTypes.BSR_ASSIGN ||
             type == JavaTokenTypes.BAND_ASSIGN ||
             type == JavaTokenTypes.BOR_ASSIGN ||
             type == JavaTokenTypes.BXOR_ASSIGN;
  }
  
  public Expression acquireCombiAssignment(ExtendedAST extendedAST) {
	      String methodName = extendedAST.getText();
	      Expression left = acquire(extendedAST.firstChild());
	      Expression right = acquire(extendedAST.firstChild().nextSibling());
	      InfixOperatorInvocation result = new InfixOperatorInvocation(methodName, left);
	      result.addParameter(new ActualParameter(right));
	      setOperatorDecorator(extendedAST, result);
	      return result;
  }
 
 /********
  * CAST *
  ********/
  
 public boolean isClassCast(ExtendedAST extendedAST) {
   return extendedAST.getType() == JavaTokenTypes.TYPECAST;
 }
  
 public Expression acquireClassCast(ExtendedAST extendedAST) {
	   JavaTypeReference type = new JavaTypeReference(flattenDot(extendedAST.firstChild().firstChild()));
	   String s = flattenDot(extendedAST.firstChild().firstChild());
	   Expression expr = acquire(extendedAST.firstChild().nextSibling());
	   ClassCastExpression r = new ClassCastExpression(type, expr);
	   setCastDecorators(extendedAST,r);
	   return r;
 }

private void setCastDecorators(ExtendedAST extendedAST, ClassCastExpression r) {
		setDecorator(r, extendedAST.firstChild(), Decorator.TYPE_DECORATOR);
	
}
 
}
