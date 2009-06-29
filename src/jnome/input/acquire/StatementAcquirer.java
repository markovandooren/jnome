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
import org.jnome.input.acquire.TypeAcquirer.TypeConnector;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import chameleon.support.expression.SuperConstructorDelegation;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.LookupException;
import chameleon.core.element.ElementImpl;
import chameleon.core.expression.Expression;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.type.Type;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.LocalVariable;
import chameleon.linkage.ILinkage;
import chameleon.support.expression.ConstructorDelegation;
import chameleon.support.expression.ThisConstructorDelegation;
import chameleon.support.modifier.Final;
import chameleon.support.statement.BreakStatement;
import chameleon.support.statement.CaseLabel;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.ContinueStatement;
import chameleon.support.statement.DefaultLabel;
import chameleon.support.statement.DoStatement;
import chameleon.support.statement.EmptyStatement;
import chameleon.support.statement.FinallyClause;
import chameleon.support.statement.ForInit;
import chameleon.support.statement.ForStatement;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.LabeledStatement;
import chameleon.support.statement.LocalClassStatement;
import chameleon.support.statement.LocalVariableDeclarationStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.StatementExprList;
import chameleon.support.statement.StatementExpression;
import chameleon.support.statement.SwitchCase;
import chameleon.support.statement.SwitchLabel;
import chameleon.support.statement.SwitchStatement;
import chameleon.support.statement.SynchronizedStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;
import chameleon.support.statement.WhileStatement;

/**
 * @author Marko van Dooren
 */
public class StatementAcquirer extends Acquirer {

	/**
	 * @param factory
	 */
	public StatementAcquirer(Factory factory, ILinkage linkage) {
		super(factory, linkage);
	}

	public Statement acquire(ExtendedAST statementAST) {
		if (statementAST.getType() == JavaTokenTypes.END_CURLY)
			statementAST = statementAST.firstChild();
		// System.out.println("Acquiring in statementAcquirer:
		// "+statementAST.getText());
		Statement statement = null;

		if (isBlock(statementAST)) {
			statement = acquireBlock(statementAST);
		}
		else if (isBreak(statementAST)) {
			statement = acquireBreak(statementAST);
		}
		else if (isContinue(statementAST)) {
			statement = acquireContinue(statementAST);
		}
		else if (isDo(statementAST)) {
			statement = acquireDo(statementAST);
		}
		else if (isReturn(statementAST)) {
			statement = acquireReturn(statementAST);
		}
		else if (isEmptyStatement(statementAST)) {
			statement = acquireEmptyStatement(statementAST);
		}
		else if (isExpressionStatement(statementAST)) {
			statement = acquireExpressionStatement(statementAST);
		}
		else if (isFor(statementAST)) {
			statement = acquireFor(statementAST);
		}
		else if (isIf(statementAST)) {
			statement = acquireIf(statementAST);
		}
		else if (isLabeledStatement(statementAST)) {
			statement = acquireLabeledStatement(statementAST);
		}
		else if (isVariableDeclaration(statementAST)) {
			statement = acquireVariableDeclaration(statementAST);
		}
		else if (isSwitch(statementAST)) {
			statement = acquireSwitch(statementAST);
		}
		else if (isSynchronized(statementAST)) {
			statement = acquireSynchronized(statementAST);
			// decoratorName = Decorator.CODEWORD_DECORATOR;
		}
		else if (isThrow(statementAST)) {
			statement = acquireThrow(statementAST);
		}
		else if (isTry(statementAST)) {
			statement = acquireTry(statementAST);
		}
		else if (isWhile(statementAST)) {
			statement = acquireWhile(statementAST);
		}
		else if (isLocalClassDef(statementAST)) {
			statement = acquireLocalClassDef(statementAST);
		}
		else if (isExplicitConstructorCall(statementAST)) {
			statement = acquireExplicitConstructorCall(statementAST);
		}
		else {
			System.out.println("Statement: " + statementAST.toStringTree());
			System.out.println("Type: " + statementAST.getType());
			throw new Error("Unknown statement");
		}
		if (statement == null) {
			System.out.println("Statement: " + statementAST.toStringTree());
			System.out.println("Type: " + statementAST.getType());
			System.err
					.println("There was a null statement, this is not allowed");
			// throw new Error("There was a null statement, this is not
			// allowed");
			return null;
		}

		return statement;
	}

	/***************************************************************************
	 * LOCAL CLASS DEFINITION *
	 **************************************************************************/

	private boolean isLocalClassDef(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.CLASS_DEF;
	}

	private Statement acquireLocalClassDef(ExtendedAST statementAST) {
		final LocalClassStatement result = new LocalClassStatement();
		TypeAcquirer acquirer = getFactory().getJavaClassAcquirer(_linkage);
		acquirer.acquire(statementAST, new TypeConnector() {
			public void connect(Type t) {
				result.setType(t);
			}
		});
		return result;
	}

	/***************************************************************************
	 * BLOCK *
	 **************************************************************************/

	// private boolean isBlock(ExtendedAST statementAST) {
	// return statementAST.getType() == JavaTokenTypes.SLIST;
	// }
	private boolean isBlock(ExtendedAST statementAST) {
		if (statementAST == null) {
			System.out.println("block AST is null");
		}
		return (statementAST.getType() == JavaTokenTypes.SLIST)
				|| ((statementAST.getType() == JavaTokenTypes.END_CURLY) && (statementAST
						.firstChild().getType() == JavaTokenTypes.SLIST));
	}

	// private Statement acquireBlock(ExtendedAST
	// statementAST) {
	// Block block = new Block(context);
	//
	// // Acquire statements one by one. decorators are set within the
	// statements, thus none are needed here
	// ExtendedAST subStatementAST = statementAST.firstChild();
	// while(subStatementAST != null) {
	// Statement statement = acquire(block, subStatementAST);
	// block.addStatement(statement);
	// subStatementAST = subStatementAST.nextSibling();
	// }
	//    
	//
	// return block;
	// }

	private Statement acquireBlock(ExtendedAST statementAST) {
		// statementAST is an SLIST or LCURLY containing an SLIST
		Block block = new Block();

		// Acquire statements one by one

		ExtendedAST subStatementAST;
		if (statementAST.getType() == JavaTokenTypes.SLIST) {
			subStatementAST = statementAST.firstChild();
		}
		else {
			subStatementAST = statementAST.firstChild().firstChild();
		}
		while (subStatementAST != null) {
			Statement statement = acquire(subStatementAST);
			block.addStatement(statement);
			subStatementAST = subStatementAST.nextSibling();
		}

		return block;
	}

	/***************************************************************************
	 * BREAK *
	 **************************************************************************/

	private boolean isBreak(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_break;
	}

	private Statement acquireBreak(ExtendedAST statementAST) {
		ExtendedAST labelAST = statementAST.firstChild();
		String label = null;
		if (labelAST != null) {
			label = labelAST.getText();
		}
		Statement statement = new BreakStatement(label);
		setCodeWordDecorator(statementAST, statement,
				Decorator.CODEWORD_DECORATOR);
		return statement;
	}

	/***************************************************************************
	 * CONSTRUCTOR CALL *
	 **************************************************************************/

	private boolean isExplicitConstructorCall(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.SUPER_CTOR_CALL
				|| statementAST.getType() == JavaTokenTypes.CTOR_CALL;
	}

	private Statement acquireExplicitConstructorCall(ExtendedAST statementAST) {
		ConstructorDelegation invocation;
		if (statementAST.getType() == JavaTokenTypes.SUPER_CTOR_CALL) {
			invocation = new SuperConstructorDelegation();
		}
		else {
			invocation = new ThisConstructorDelegation();
		}
		getFactory().getExpressionAcquirer(_linkage).acquireParameters(
				statementAST.firstChild(), invocation);
		StatementExpression statement = new StatementExpression(invocation);
		setSuperDecorator(statementAST, statement, Decorator.CODEWORD_DECORATOR);
		return statement;
	}

	/*
	 * I had to make this method apart from the rest because someone "forgot" to
	 * take super in account
	 */
	private void setSuperDecorator(ExtendedAST statementAST, StatementExpression statement, String decName) {
		// ExtendedAST superAST = statementAST.
		int offset = statementAST.extractBeginOffset(_linkage) - 5;
		int length = statementAST.getLength() + 4; // we rekenen voor ( = 1
		// char, super = 5 char

		_linkage.decoratePosition(offset, length, decName, statement);

	}

	/***************************************************************************
	 * CONTINUE *
	 **************************************************************************/

	private boolean isContinue(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_continue;
	}

	private Statement acquireContinue(ExtendedAST statementAST) {
		ExtendedAST labelAST = statementAST.firstChild();
		String label = null;
		if (labelAST != null) {
			label = labelAST.getText();
		}
		Statement statement = new ContinueStatement(label);
		setCodeWordDecorator(statementAST, statement,
				Decorator.CODEWORD_DECORATOR);
		return statement;
	}

	/***************************************************************************
	 * DO *
	 **************************************************************************/

	private boolean isDo(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_do;
	}

	private Statement acquireDo(ExtendedAST statementAST) {
		ExtendedAST subStatementAST = statementAST.firstChild();
		ExtendedAST exprAST = subStatementAST.nextSibling();

		// acquire the condition
		Expression expression = getFactory()
				.getExpressionAcquirer(_linkage).acquire(exprAST.firstChild());

		// acquire the statement
		Statement statement = acquire(subStatementAST);

		// create a new do statement
		Statement returnstatement = new DoStatement(expression,
				statement);
		setCodeWordDecorator(statementAST, returnstatement,
				Decorator.CODEWORD_DECORATOR);


		return returnstatement;
	}

	/***************************************************************************
	 * RETURN *
	 **************************************************************************/

	private boolean isReturn(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_return;
	}

	private Statement acquireReturn(ExtendedAST statementAST) {
		ExtendedAST exprAST = statementAST.firstChild();
		Expression expression = null;
		if (exprAST != null) {
			exprAST = exprAST.firstChild();
			ExpressionAcquirer exprAcquirer = getFactory()
					.getExpressionAcquirer(_linkage);
			expression = exprAcquirer.acquire(exprAST);
		}
		Statement statement = new ReturnStatement(expression);
		setCodeWordDecorator(statementAST, statement,
				Decorator.CODEWORD_DECORATOR);

		return statement;
	}

	// /*************************
	/***************************************************************************
	 * EMPTY *
	 **************************************************************************/

	private boolean isEmptyStatement(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.EMPTY_STAT;
	}

	private Statement acquireEmptyStatement(ExtendedAST statementAST) {
		EmptyStatement empty = new EmptyStatement();
		setEmptyStatementDecorator(statementAST, empty);
		return empty;

	}

	private void setEmptyStatementDecorator(ExtendedAST statementAST, EmptyStatement empty) {
	// try {
	// int offset = statementAST.extractBeginOffset(_linkage);
	// int length = statementAST.getLength();
	// Decorator accessdec = new
	// Decorator(offset,length,empty,Decorator.EMPTY_STATEMENT_DECORATOR);
	// empty.setDecorator(accessdec,Decorator.EMPTY_STATEMENT_DECORATOR);
	// _linkage.addPosition(Decorator.CHAMELEON_CATEGORY,accessdec);
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// } catch (BadPositionCategoryException e) {
	// e.printStackTrace();
	// }
	}

	/***************************************************************************
	 * EXPRESSION *
	 **************************************************************************/

	private boolean isExpressionStatement(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.EXPR;
	}

	private Statement acquireExpressionStatement(ExtendedAST statementAST) {
		ExpressionAcquirer exprAcquirer = getFactory()
				.getExpressionAcquirer(_linkage);
		ExtendedAST exprAST = statementAST.firstChild();
		Expression expression = exprAcquirer.acquire(exprAST);
		Statement statement = new StatementExpression(expression);
		return statement;
	}

	/***************************************************************************
	 * FOR *
	 **************************************************************************/

	private boolean isFor(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_for;
	}

	private Statement acquireFor(ExtendedAST statementAST) {
		// acquirer init code
		ExtendedAST initAST = statementAST
				.firstChildOfType(JavaTokenTypes.FOR_INIT);
		ForInit initCode = null;
		if (initAST.firstChild() != null) {
			initCode = acquireForInitCode(initAST);
		}

		// acquire condition
		ExtendedAST conditionAST = statementAST
				.firstChildOfType(JavaTokenTypes.FOR_CONDITION);
		ExpressionAcquirer exprAcquirer = getFactory()
				.getExpressionAcquirer(_linkage);
		Expression condition = null;
		if (conditionAST.firstChild() != null) {
			condition = exprAcquirer.acquire(conditionAST
					.firstChild().firstChild());
		}

		// acquire update code
		ExtendedAST updateAST = statementAST
				.firstChildOfType(JavaTokenTypes.FOR_ITERATOR);
		StatementExprList updateCode = null;
		if (updateAST.firstChild() != null) {
			updateCode = acquireStatementExprList(updateAST);
		}

		// acquire statement
		ExtendedAST subStatementAST = updateAST.nextSibling();
		Statement statement = acquire(subStatementAST);

		// create a new for statement
		Statement r = new ForStatement(initCode, condition,
				updateCode, statement);

		// setDecorator(statementAST,statement,Decorator.CODEWORD_DECORATOR);

		setForDecorators(statementAST, r);

		return r;

	}

	private void setForDecorators(ExtendedAST ast, Statement stt) {
		ExtendedAST[] children = ast.children();
		ExtendedAST init = children[0];
		ExtendedAST cond = children[1];
		ExtendedAST iter = children[2];
		ExtendedAST body = children[3];

		ExtendedAST assignment = null;
		if (children.length > 3) assignment = children[3].firstChild();

		// for keyword
		setSmallDecorator(stt, ast, Decorator.CODEWORD_DECORATOR);
		// for INIT
		{
			int offset = init.extractBeginOffset(_linkage);
			int eoffset = init.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ITERATION_INIT_DECORATOR, stt);
		}
		// FOR CONDITION
		{
			int offset = cond.extractBeginOffset(_linkage);
			int eoffset = cond.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ITERATION_CONDITION_DECORATOR, stt);

		}
		// FOR ITERATOR
		{
			int offset = iter.extractBeginOffset(_linkage);
			int eoffset = iter.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ITERATION_ITERATION_DECORATOR, stt);

		}
		// FOR BODY
		{
			int offset = body.extractBeginOffset(_linkage);
			int eoffset = body.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ITERATION_BODY, stt);

		}

		if (!(assignment == null)) {
			int offset = assignment.extractBeginOffset(_linkage);
			int eoffset = assignment.extractEndOffset(_linkage);
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ASSIGNMENT_DECORATOR, stt);

		}

	}

	private ForInit acquireForInitCode(ExtendedAST initCodeAST) {
			if (initCodeAST.firstChild().getType() == JavaTokenTypes.VARIABLE_DEF) {
				ExtendedAST[] inits = initCodeAST
						.childrenOfType(JavaTokenTypes.VARIABLE_DEF);
				LocalVariableDeclarationStatement result = new LocalVariableDeclarationStatement();
				for (int i = 0; i < inits.length; i++) {
					// localvariable handelt in dit geval de decorator af
					LocalVariableDeclarationStatement varDecl = (LocalVariableDeclarationStatement) acquire(
							inits[i]);
					result.addVariable((LocalVariable) varDecl.getVariables()
							.get(0));
				}
				return result;
			}
			else {
				return acquireStatementExprList(initCodeAST);
			}
	}

	private StatementExprList acquireStatementExprList(ExtendedAST initCodeAST) {
		ExtendedAST listAST = initCodeAST.firstChild();
		// acquire the expressions in the list one by one
		ExtendedAST[] expressions = listAST
				.childrenOfType(JavaTokenTypes.EXPR);
		// acquire the first expression
		StatementExprList result = new StatementExprList();
		// acquire the other expressions
		for (int i = 0; i < expressions.length; i++) {
			Expression expr = getFactory().getExpressionAcquirer(_linkage)
					.acquire(expressions[i].firstChild());
			StatementExpression statementExpression = new StatementExpression(expr);
			result.addStatement(statementExpression);
		}

		return result;
	}

	/***************************************************************************
	 * IF *
	 **************************************************************************/

	private boolean isIf(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_if;
	}

	private Statement acquireIf(ExtendedAST statementAST) {
		Expression condition = extractCondition(statementAST);

		Statement thenStatement = extractThenStatement(statementAST);
		// setDecorator(statementAST,thenStatement,Decorator.CODEWORD_DECORATOR);
		Statement elseStatement = extractElseStatement(statementAST);
		// if(elseStatement !=null)
		// setDecorator(statementAST,elseStatement,Decorator.CODEWORD_DECORATOR);
		IfThenElseStatement statement = new IfThenElseStatement(condition, thenStatement, elseStatement);
		// setDecorator(statementAST,statement,Decorator.CODEWORD_DECORATOR);
		setIfDecorators(statementAST, statement);
		return statement;
	}

	private void setIfDecorators(ExtendedAST statementAST, IfThenElseStatement returnstatement) {
		ExtendedAST[] children = statementAST.children();

		ExtendedAST cond = children[0]; // expression
		ExtendedAST elseAST;
		ExtendedAST body;
		if (children.length == 2) {
			body = children[1];
			elseAST = null;
		}
		else {
			body = children[1];
			elseAST = children[2];
		}

		// if keyword
		setSmallDecorator(returnstatement, statementAST,
				Decorator.CODEWORD_DECORATOR);

		// if CONDITION
		{
			int offset = cond.extractBeginOffset(_linkage);
			int eoffset = cond.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.IF_CONDITION_DECORATOR, returnstatement);

		}

		// if BODY
		{
			int offset = body.extractBeginOffset(_linkage);
			int eoffset = body.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.IF_BODY, returnstatement);
		}

		// else keyword: vanaf het einde van de if body, tot aan het begin van
		// de else body
		if (elseAST != null) {
			int offset = body.extractEndOffset(_linkage);
			int eoffset = elseAST.extractBeginOffset(_linkage);
			int length = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.CODEWORD_DECORATOR, returnstatement);
		}

		// else BODY
		if (elseAST != null) {
			int offset = elseAST.extractBeginOffset(_linkage);
			int eoffset = elseAST.extractEndOffset(_linkage);
			int lengte = eoffset - offset;
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ELSE_BODY, returnstatement);

		}

	}

	private Expression extractCondition(ExtendedAST statementAST) {
			ExtendedAST conditionAST = statementAST.firstChild();
			return getFactory().getExpressionAcquirer(_linkage).acquire(
					conditionAST.firstChild());
	}

	private Statement extractThenStatement(ExtendedAST statementAST) {
			ExtendedAST thenAST = statementAST.firstChild().nextSibling();
			return acquire(thenAST);
	}

	private Statement extractElseStatement(ExtendedAST statementAST) {
			ExtendedAST elseAST = getElseStatementAST(statementAST);
			if (elseAST != null) {
				return acquire(elseAST);
			}
			else {
				return null;
			}
	}

	private ExtendedAST getElseStatementAST(ExtendedAST statementAST) {
		return statementAST.firstChild().nextSibling().nextSibling();
	}

	/***************************************************************************
	 * LABEL *
	 **************************************************************************/
	private boolean isLabeledStatement(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LABELED_STAT;
	}

	private Statement acquireLabeledStatement(ExtendedAST statementAST) {
			ExtendedAST labelAST = statementAST
					.firstChildOfType(JavaTokenTypes.IDENT);
			String label = labelAST.getText();

			// acquire statement
			ExtendedAST subStatementAST = labelAST.nextSibling();
			Statement statement = acquire(subStatementAST);

			Statement returnstatement = new LabeledStatement(label,
					statement);
			// setDecorator(statementAST,returnstatement,Decorator.CODEWORD_DECORATOR);
			return returnstatement;
	}

	/***************************************************************************
	 * VARIABLE *
	 **************************************************************************/

	private boolean isVariableDeclaration(ExtendedAST statementAST) {
		// int type = statementAST.firstChild().getType();
		return statementAST.getType() == JavaTokenTypes.VARIABLE_DEF;
	}

	private Statement acquireVariableDeclaration(ExtendedAST statementAST) {
		// ExtendedAST[] variables
		// = statementAST.childrenOfType(JavaTokenTypes.VARIABLE_DEF);
		// // acquire the first variable
		LocalVariableDeclarationStatement result = new LocalVariableDeclarationStatement();
		// // acquire the other variables
		// for (int i = 0; i < variables.length; i++) {
		// LocalVariable variable = acquireLocalVariable(variables[i]);
		// result.addVariable((LocalVariable) variable);
		// }
		// return result;
		//
		LocalVariable variable = acquireLocalVariable(statementAST);
		setCodeWordDecorator(statementAST, result,
				Decorator.VARIABLE_DECLARATION_DECORATOR);
		result.addVariable(variable);
		return result;
	}

	private LocalVariable acquireLocalVariable(ExtendedAST statementAST) {
		// acquire variable name
		String name = extractName(statementAST);

		JavaTypeReference type = extractType(null, statementAST);

		Expression expr = extractInitCode(statementAST);

		LocalVariable result = new LocalVariable(name, type, expr);
		// acquire modifier final
		if (containsModifier(statementAST, JavaTokenTypes.FINAL)) {
			result.addModifier(new Final());
		}

		result.setInitialization(expr);
		setLocalVariableDecorator(statementAST, result);
		return result;
	}

	private void setLocalVariableDecorator(ExtendedAST statementAST, LocalVariable var) {

		ExtendedAST[] children = statementAST.children();
		ExtendedAST[] modifiers = children[0].children();
		ExtendedAST type = children[1].children()[0];
		ExtendedAST varName = children[2];
		ExtendedAST assignment = null;
		if (children.length > 3) assignment = children[3].firstChild();

		for (int i = 0; i < modifiers.length; i++) {
			ExtendedAST currentChild = modifiers[i];
			setDecorator(var, currentChild, Decorator.ACCESSTYPE_DECORATOR);
		}
		// TYPE
		{
			int offset, eoffset;

			if (type.getText().equals("[")) { // array
				offset = type.firstChild().extractBeginOffset(_linkage);
				eoffset = type.extractEndOffset(_linkage);
			}
			else {
				offset = type.extractBeginOffset(_linkage);
				eoffset = offset + type.getLength();
			}

			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.TYPE_DECORATOR, var);
		}

		// var name
		{
			setSmallDecorator(var, varName, Decorator.NAME_DECORATOR);
		}

		if (!(assignment == null)) {
			int offset = assignment.extractBeginOffset(_linkage);
			int eoffset = assignment.extractEndOffset(_linkage);
			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.ASSIGNMENT_DECORATOR, var);
		}

	}

	/*
	 * This is for the local variable assignment
	 */
	protected Expression extractInitCode(ExtendedAST variableAST) {
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
			expr = getFactory().getExpressionAcquirer(_linkage).acquire(
					exprAST);
		}
		return expr;
	}

	/***************************************************************************
	 * SWITCH *
	 **************************************************************************/

	private boolean isSwitch(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_switch;
	}

	private Statement acquireSwitch(ExtendedAST statementAST) {
		ExtendedAST exprAST = statementAST
				.firstChildOfType(JavaTokenTypes.EXPR);
		Expression expr = getFactory().getExpressionAcquirer(_linkage)
				.acquire(exprAST.firstChild());

		// create a new switch statement with the acquired expression
		SwitchStatement statement = new SwitchStatement(expr);
		// setDecorator(statementAST,statement,Decorator.CODEWORD_DECORATOR);

		// acquire the switch cases
		ExtendedAST[] caseGroups = statementAST
				.childrenOfType(JavaTokenTypes.CASE_GROUP);
		for (int i = 0; i < caseGroups.length; i++) {
			SwitchCase switchCase = acquireSwitchCases(caseGroups[i]);
			// setDecorator(statementAST,switchCase,Decorator.CODEWORD_DECORATOR);
			statement.addCase(switchCase);
		}
		setCodeWordDecorator(statementAST, statement,
				Decorator.CODEWORD_DECORATOR);
		return statement;
	}

	/**
	 * @param extendedAST
	 * @return
	 */
	private SwitchCase acquireSwitchCases(ExtendedAST statementAST) {
		SwitchCase result = new SwitchCase();
		ExtendedAST[] labels = statementAST
				.childrenOfType(JavaTokenTypes.LITERAL_case);
		for (int i = 0; i < labels.length; i++) {
			result.addLabel(acquireCaseLabel(labels[i]));
		}
		ExtendedAST defaultLabel = statementAST
				.firstChildOfType(JavaTokenTypes.LITERAL_default);
		if (defaultLabel != null) {
			DefaultLabel defLabel = new DefaultLabel();
			setDefaultLabelDecorator(statementAST, defLabel,
					Decorator.CODEWORD_DECORATOR);
			result.addLabel(defLabel);
		}

		// ExtendedAST temp =
		// statementAST.firstChildOfType(JavaTokenTypes.END_CURLY);
		ExtendedAST list = statementAST
				.firstChildOfType(JavaTokenTypes.SLIST);
		ExtendedAST stat = list.firstChild();
		while (stat != null) {
			result.addStatement(acquire(stat));
			stat = stat.nextSibling();
		}
		setSwitchCaseDecorators(statementAST, result);
		return result;
	}

	private SwitchLabel acquireCaseLabel(ExtendedAST switchLabelAST) {
		ExtendedAST exprAST = switchLabelAST.firstChild().firstChild();
		Expression expr = getFactory().getExpressionAcquirer(_linkage)
				.acquire(exprAST);
		// create a new case label with the acquired expression
		CaseLabel label = new CaseLabel(expr);
		setDecorator(switchLabelAST, label, Decorator.CODEWORD_DECORATOR);
		return label;
	}

	private void setDecorator(ExtendedAST switchLabelAST, CaseLabel label, String decName) {
		setSmallDecorator(label, switchLabelAST, decName);
	}

	private void setDefaultLabelDecorator(ExtendedAST switchLabelAST, DefaultLabel defLabel, String decName) {

		int offset = switchLabelAST.extractBeginOffset(_linkage);
		int length = switchLabelAST.firstChild().getLength();
		_linkage.decoratePosition(offset, length, decName, defLabel);

	}

	private void setSwitchCaseDecorators(ExtendedAST statementAST, SwitchCase switchCase) {

		setDecorator(switchCase, statementAST.firstChild(),
				Decorator.CODEWORD_DECORATOR);
	}

	/***************************************************************************
	 * SYNCHRONIZED *
	 **************************************************************************/

	private boolean isSynchronized(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_synchronized;
	}

	private Statement acquireSynchronized(ExtendedAST statementAST) {
		ExtendedAST exprAST = statementAST.firstChild();
		ExtendedAST subStatementAST = exprAST.nextSibling();

		// acquire the condition
		Expression expression = getFactory()
				.getExpressionAcquirer(_linkage).acquire(exprAST.firstChild());

		// acquire the statement
		Statement statement = acquire(subStatementAST);
		// setDecorator(statementAST,statement,Decorator.CODEWORD_DECORATOR);
		SynchronizedStatement returnstatement = new SynchronizedStatement(
				statement, expression);
		setCodeWordDecorator(statementAST, returnstatement,
				Decorator.CODEWORD_DECORATOR);
		return returnstatement;

	}

	/***************************************************************************
	 * THROW *
	 **************************************************************************/

	private boolean isThrow(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_throw;
	}

	private Statement acquireThrow(ExtendedAST statementAST) {
		ExtendedAST exprAST = statementAST.firstChild().firstChild();
		Expression expression = getFactory()
				.getExpressionAcquirer(_linkage).acquire(exprAST);
		ThrowStatement statement = new ThrowStatement(expression);
		setCodeWordDecorator(statementAST, statement,
				Decorator.CODEWORD_DECORATOR);

		return statement;
	}

	// private void setThrowDecorator(ExtendedAST statementAST, ThrowStatement
	// statement) {
	// try{
	// int offset = statementAST.extractBeginOffset(_linkage);
	// int eoffset = statementAST.extractEndOffset(_linkage);
	// int lengte = eoffset - offset;
	// Decorator accessdec = new
	// Decorator(offset,lengte,statement,Decorator.THROW_DECORATOR);
	// statement.setDecorator(accessdec,Decorator.THROW_DECORATOR);
	// _linkage.addPosition(Decorator.CHAMELEON_CATEGORY,accessdec);
	// }
	// catch(BadLocationException ble){
	// ble.printStackTrace();
	// } catch (BadPositionCategoryException e) {
	// e.printStackTrace();
	// }
	// }

	/***************************************************************************
	 * TRY *
	 **************************************************************************/

	private boolean isTry(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_try;
	}

	// private Statement acquireTry(ElementImpl context,ExtendedAST
	// statementAST) {
	// Block tryBlock = extractTryBlock(null,statementAST);
	// TryStatement result = new TryStatement(context,tryBlock);
	//
	// // acquire catch clauses
	// acquireCatchClauses(statementAST, result);
	// // acquire finally clause
	// ExtendedAST finallyAST =
	// statementAST.firstChildOfType(JavaTokenTypes.LITERAL_finally);
	// if(finallyAST != null) {
	// ExtendedAST temp =
	// statementAST.firstChildOfType(JavaTokenTypes.END_CURLY);
	// ExtendedAST finallyBlockAST =
	// temp.firstChildOfType(JavaTokenTypes.SLIST);
	// Statement statement = acquire(null,finallyBlockAST);
	// FinallyClause finallyClause = new FinallyClause(result,statement);
	// statement.setElementParent(finallyClause);
	// result.setFinallyClause(finallyClause);
	// }
	// setTryDecorators(statementAST,result);
	// return result;
	// }

	private Statement acquireTry(ExtendedAST statementAST) {
		Block tryBlock = extractTryBlock(statementAST);
		TryStatement result = new TryStatement(tryBlock);
		// acquire catch clauses
		acquireCatchClauses(statementAST, result);
		// acquire finally clause
		ExtendedAST finallyAST = statementAST
				.firstChildOfType(JavaTokenTypes.LITERAL_finally);
		if (finallyAST != null) {
			ExtendedAST finallyBlockAST = finallyAST
					.firstChildOfType(JavaTokenTypes.END_CURLY);
			Statement statement = acquire(finallyBlockAST);
			FinallyClause finallyClause = new FinallyClause(statement);
			result.setFinallyClause(finallyClause);
		}
		setTryDecorators(statementAST, result);
		return result;
	}

	private void acquireCatchClauses(ExtendedAST tryCatchAST, TryStatement result) {
		ExtendedAST[] catchClausesAST = tryCatchAST
				.childrenOfType(JavaTokenTypes.LITERAL_catch);
		for (int i = 0; i < catchClausesAST.length; i++) {
			ExtendedAST currentAST = catchClausesAST[i];
			setCodeWordDecorator(currentAST, result,
					Decorator.CODEWORD_DECORATOR);
			CatchClause catchClause = acquireCatchClause(currentAST);
			result.addCatchClause(catchClause);
		}
	}

	private CatchClause acquireCatchClause(ExtendedAST catchAST) {
		ExtendedAST paramAST = catchAST
				.firstChildOfType(JavaTokenTypes.PARAMETER_DEF);
		FormalParameter param = acquireExceptionParameter(paramAST);

		// acquire the catch block
		ExtendedAST temp = catchAST
				.firstChildOfType(JavaTokenTypes.END_CURLY);
		ExtendedAST catchBlockAST = temp
				.firstChildOfType(JavaTokenTypes.SLIST);
		Block catchBlock = (Block) acquire(catchBlockAST);

		CatchClause clause = new CatchClause(param, catchBlock);
		setCatchImplementationDecorator(catchAST, clause);
		return clause;
	}

	private void setCatchImplementationDecorator(ExtendedAST catchAST, CatchClause clause) {

		setDecorator(clause, catchAST, Decorator.IMPLEMENTATION_DECORATOR);

	}

	private FormalParameter acquireExceptionParameter(ExtendedAST statementAST) {
		// acquire variable name
		String name = extractName(statementAST);

		JavaTypeReference type = extractType(null, statementAST);

		FormalParameter result = new FormalParameter(name, type);
		// setDecorator(statementAST,result,Decorator.CODEWORD_DECORATOR);
		setExceptionParameterDecorator(statementAST, result);
		return result;
	}

	private void setExceptionParameterDecorator(ExtendedAST statementAST, FormalParameter var) {

		ExtendedAST[] children = statementAST.children();
		ExtendedAST[] modifiers = children[0].children();
		ExtendedAST type = children[1].children()[0];
		ExtendedAST varName = children[2];

		for (int i = 0; i < modifiers.length; i++) {
			ExtendedAST currentChild = modifiers[i];

			setSmallDecorator(var, currentChild, Decorator.ACCESSTYPE_DECORATOR);
		}
		// TYPE
		{

			int offset, eoffset;

			if (type.getText().equals("[")) { // array
				offset = type.firstChild().extractBeginOffset(_linkage);
				eoffset = type.extractBeginOffset(_linkage);
			}
			else {
				offset = type.extractBeginOffset(_linkage);
				eoffset = offset + type.getLength();
			}

			_linkage.decoratePosition(offset, eoffset - offset,
					Decorator.TYPE_DECORATOR, var);
		}

		// var name
		{
			setSmallDecorator(var, varName, Decorator.NAME_DECORATOR);
		}

	}

	// private Block extractTryBlock(ElementImpl context,ExtendedAST
	// tryCatchAST) {
	// ExtendedAST temp =
	// tryCatchAST.firstChildOfType(JavaTokenTypes.END_CURLY);
	// ExtendedAST tryBlockAST = temp.firstChildOfType(JavaTokenTypes.SLIST);
	// return (Block)acquire(context,tryBlockAST);
	// }

	public Block extractTryBlock(ExtendedAST tryCatchAST) {

		ExtendedAST tryBlockAST = tryCatchAST
				.firstChildOfType(JavaTokenTypes.END_CURLY);
		return (Block) acquire(tryBlockAST);
	}

	private void setTryDecorators(ExtendedAST statementAST, TryStatement statement) {
		ExtendedAST[] children = statementAST.children();

		// try keyword
		{
			int line = statementAST.getLineNumber();
			int column = statementAST.getColumnNumber();
			int offset = _linkage.getLineOffset(line - 1) + column - 1;
			int length = statementAST.getLength();
			_linkage.decoratePosition(offset, length,
					Decorator.CODEWORD_DECORATOR, statement);
		}

		// try implementation
		{
			ExtendedAST current = children[0];
			int offset = current.extractBeginOffset(_linkage);
			int eoffset = current.extractEndOffset(_linkage);
			int length = eoffset - offset;
			_linkage.decoratePosition(offset, length,
					Decorator.IMPLEMENTATION_DECORATOR, statement);
		}

		// catch clauses
		for (int i = 1; i < children.length; i++) {
			ExtendedAST current = children[i];
			int offset = current.extractBeginOffset(_linkage);
			int eoffset = current.extractEndOffset(_linkage);
			int length = eoffset - offset;
			_linkage.decoratePosition(offset, length,
					Decorator.IMPLEMENTATION_DECORATOR, statement);
		}

	}

	/***************************************************************************
	 * WHILE *
	 **************************************************************************/

	private boolean isWhile(ExtendedAST statementAST) {
		return statementAST.getType() == JavaTokenTypes.LITERAL_while;
	}

	private Statement acquireWhile(ExtendedAST statementAST) {
		ExtendedAST exprAST = statementAST.firstChild();
		ExtendedAST subStatementAST = exprAST.nextSibling();

		Expression condition = getFactory().getExpressionAcquirer(_linkage)
				.acquire(exprAST.firstChild());

		// acquire the statement
		Statement statement = acquire(subStatementAST);

		Statement returnstatement = new WhileStatement(condition,
				statement);

		setWhileDecorators(statementAST, returnstatement);
		return returnstatement;
	}

	private void setWhileDecorators(ExtendedAST statementAST, Statement returnstatement) {
		ExtendedAST[] children = statementAST.children();

		ExtendedAST cond = null;
		ExtendedAST body = null;

		try {
			cond = children[0];
			body = children[1];
		}
		catch (ArrayIndexOutOfBoundsException e) {}

		// while keyword
		setSmallDecorator(returnstatement, statementAST,
				Decorator.CODEWORD_DECORATOR);

		// WHILE CONDITION
		if (cond != null) {
			int offset = cond.extractBeginOffset(_linkage);
			int eoffset = cond.extractEndOffset(_linkage);
			int length = eoffset - offset;
			_linkage.decoratePosition(offset, length,
					Decorator.ITERATION_CONDITION_DECORATOR, returnstatement);
		}

		// WHILE BODY
		if (body != null) {
			int offset = statementAST.extractBeginOffset(_linkage);
			int eoffset = statementAST.extractEndOffset(_linkage);
			int length = eoffset - offset;
			_linkage.decoratePosition(offset, length, Decorator.ITERATION_BODY,
					returnstatement);

		}

	}

	/*
	 * set the codeWordDecorator for the given AST, statement with the correct
	 * decoratorname use only for AST where lineNumber && ColNumber !=0
	 */
	private void setCodeWordDecorator(ExtendedAST statementAST, Statement statement, String decName) {
		setSmallDecorator(statement, statementAST, Decorator.CODEWORD_DECORATOR);
	}

}
