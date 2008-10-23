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

import org.jnome.decorator.Decorator;
import org.jnome.input.parser.ExtendedAST;
import org.jnome.input.parser.JavaTokenTypes;
import org.jnome.mm.modifier.StrictFP;
import org.jnome.mm.type.JavaTypeReference;
import org.rejuse.java.collections.Visitor;

import chameleon.core.modifier.AccessModifier;
import chameleon.core.modifier.TypeModifier;
import chameleon.core.statement.Block;
import chameleon.core.type.StaticInitializer;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Abstract;
import chameleon.support.modifier.Final;
import chameleon.support.modifier.Static;
import chameleon.util.Util;

/**
 * @author Marko van Dooren
 */
public abstract class TypeAcquirer extends Acquirer {

	/**
	 * @param factory
	 */
	public TypeAcquirer(Factory factory, ILinkage linkage) {
		super(factory, linkage);
	}
	
	public interface TypeConnector {
		public void connect(Type type);
	}

	public void acquire(ExtendedAST objectTypeAST,TypeConnector connector) {
		try {
			//System.out.println("Acquiring " + objectTypeAST.getText());
			// acquire name
			String name = extractName(objectTypeAST);

			// acquire access modifier: public
			AccessModifier access = extractAccess(null, objectTypeAST);

			// create a new resolved objecttype
			// try {
			Type type = createType(name, access);
			
			// We need to connect the type to the parent. Otherwise we cannot determine which
			// type is java.lang.Object and thus must have no supertype.
			connector.connect(type);
			
			createDecorator(type, objectTypeAST);

			acquireOtherModifiers(type, objectTypeAST);
			// acquire documentation block
			// TODO acquireDocumentationBlock(type, objectTypeAST);

			acquireSuperObjectTypes(type, objectTypeAST);
			acquireObjectBlock(type, objectTypeAST);
			//return type;
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquire errr***");
			throw e;
		}

	}

	private void createDecorator(Type type, ExtendedAST typeAST) {
		setDecorator(type, typeAST, Decorator.ALL_DECORATOR);
	}

	protected void acquireObjectBlock(Type type, ExtendedAST objectTypeAST) {
		try {
			// old version
			// ExtendedAST[] members =
			// objectTypeAST.firstChildOfType(JavaTokenTypes.OBJBLOCK).children();
			ExtendedAST[] members = new ExtendedAST[0];
			// new version: extra level in tree ( "}" of class added to tree )
			try {
				ExtendedAST[] temp = objectTypeAST.firstChildOfType(
						JavaTokenTypes.OBJBLOCK).children();
				members = temp[0].children();
			}
			catch (NullPointerException e) {}
			for (int i = 0; i < members.length; i++) {
				ExtendedAST current = members[i];
				if (isMethod(current)) {
					acquireMethod(type, current);
				}
				else if (isInstanceInitializer(current)) {
					acquireInstanceInitializer(type, current);
				}
				else if (isStaticInitializer(current)) {
					acquireStaticInitializer(type, current);
				}
				else if (isVariable(current)) {
					acquireVariable(type, current);
				}
				else if (isConstructor(current)) {
					acquireConstructor(type, current);
				}
				else if (isInnerClass(current)) {
					acquireInnerClass(type, current);
				}
				else if (isInnerInterface(current)) {
					acquireInnerInterface(type, current);
				}
			}
			// acquireStaticInitializers(type, objectTypeAST);

			// acquireInstanceInitializers(type, objectTypeAST);

			// acquireMethods(type, objectTypeAST);

			acquireDefaultConstructor(type);

			// acquireVariables(type, objectTypeAST);

			// acquireInnerTypes(type, objectTypeAST);
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireObjectBlock errr***");
		}
	}

	protected void acquireObjectBlockAgain(Type type, ExtendedAST objectTypeAST) {
		try {
			ExtendedAST[] members = objectTypeAST.firstChild().children();
			// System.out.println("TypeAcquirer.acquireObjectBlockAgain / #
			// members: "+members.length);

			for (int i = 0; i < members.length; i++) {
				ExtendedAST current = members[i];
				if (isMethod(current)) {
					acquireMethod(type, current);
				}
				else if (isInstanceInitializer(current)) {
					acquireInstanceInitializer(type, current);
				}
				else if (isStaticInitializer(current)) {
					acquireStaticInitializer(type, current);
				}
				else if (isVariable(current)) {
					acquireVariable(type, current);
				}
				else if (isConstructor(current)) {
					acquireConstructor(type, current);
				}
				else if (isInnerClass(current)) {
					acquireInnerClass(type, current);
				}
				else if (isInnerInterface(current)) {
					acquireInnerInterface(type, current);
				}
			}
			// acquireStaticInitializers(type, objectTypeAST);

			// acquireInstanceInitializers(type, objectTypeAST);

			// acquireMethods(type, objectTypeAST);
			/*
			 * acquireDefaultConstructor(type);
			 */
			// acquireVariables(type, objectTypeAST);
			// acquireInnerTypes(type, objectTypeAST);
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireObjectBlockAgain errr***");
		}
	}

	protected void acquireStaticInitializers(final Type type, ExtendedAST objectTypeAST) {
		try {
			ExtendedAST[] staticInits = objectTypeAST.firstChildOfType(
					JavaTokenTypes.OBJBLOCK).childrenOfType(
					JavaTokenTypes.STATIC_INIT);
			new Visitor() {
				public void visit(Object element) {
					acquireStaticInitializer(type, (ExtendedAST) element);
				}
			}.applyTo(staticInits);
		}
		catch (NullPointerException e) {
			System.err
					.println("TypeAcquirer acquireStaticInitializers errr***");
		}
	}

	protected boolean isStaticInitializer(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.STATIC_INIT;
	}

	protected void acquireStaticInitializer(final Type type, ExtendedAST initializerAST) {
		Block block = (Block) getFactory().getStatementAcquirer(_linkage)
				.acquire(initializerAST.firstChild());
		StaticInitializer StatInit = new StaticInitializer(block);
		type.addStaticInitializer(StatInit);
	}

	protected void acquireInstanceInitializers(final Type type, ExtendedAST objectTypeAST) {
		try {
			ExtendedAST[] staticInits = objectTypeAST.firstChildOfType(
					JavaTokenTypes.OBJBLOCK).childrenOfType(
					JavaTokenTypes.INSTANCE_INIT);
			new Visitor() {
				public void visit(Object element) {
					acquireInstanceInitializer(type, (ExtendedAST) element);
				}
			}.applyTo(staticInits);
		}
		catch (NullPointerException e) {
			System.err
					.println("TypeAcquirer acquireInstanceInitializers errr***");
		}
	}

	protected boolean isInstanceInitializer(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.INSTANCE_INIT;
	}

	protected void acquireInstanceInitializer(final Type type, ExtendedAST initAST) {
		Block block = (Block) getFactory().getStatementAcquirer(_linkage)
				.acquire(initAST.firstChild());
		type.addInstanceInitializer(block);
	}

	protected void acquireInnerTypes(Type type, ExtendedAST objectTypeAST) {
		acquireInnerClasses(type, objectTypeAST);
		acquireInnerInterfaces(type, objectTypeAST);
	}

	protected void acquireInnerInterfaces(Type type, ExtendedAST objectTypeAST) {
		try {
			ExtendedAST objBlockAST = objectTypeAST
					.firstChildOfType(JavaTokenTypes.OBJBLOCK);

			ExtendedAST[] classDefArray = objBlockAST
					.childrenOfType(JavaTokenTypes.INTERFACE_DEF);

			// Add classes one by one
			for (int i = 0; i < classDefArray.length; i++) {
				ExtendedAST classDefAST = classDefArray[i];
				acquireInnerInterface(type, classDefAST);
				// create a new ResolvedObjectTypeAcquirer
			}
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireInnerInterfaces errr***");
		}
	}

	protected boolean isInnerInterface(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.INTERFACE_DEF;
	}

	protected void acquireInnerInterface(final Type type, ExtendedAST classDefAST) {
		try {
			InterfaceAcquirer acquirer = getFactory().getInterfaceAcquirer(_linkage);
			acquirer.acquire(classDefAST, new TypeConnector() {
				public void connect(Type t) {
					type.addType(t);
				}
			});
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireInnerInterface errr***");
		}
	}

	protected void acquireInnerClasses(Type type, ExtendedAST objectTypeAST) {
		try {
			ExtendedAST objBlockAST = objectTypeAST
					.firstChildOfType(JavaTokenTypes.OBJBLOCK);

			ExtendedAST[] classDefArray = objBlockAST
					.childrenOfType(JavaTokenTypes.CLASS_DEF);

			// Add classes one by one
			for (int i = 0; i < classDefArray.length; i++) {
				ExtendedAST classDefAST = classDefArray[i];
				acquireInnerClass(type, classDefAST);
				// create a new ResolvedObjectTypeAcquirer
			}
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireInnerClasses errr***");
		}
	}

	protected boolean isInnerClass(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.CLASS_DEF;
	}

	protected void acquireInnerClass(final Type type, ExtendedAST classDefAST) {
		try {
			JavaClassAcquirer acquirer = getFactory().getJavaClassAcquirer(_linkage);
			acquirer.acquire(classDefAST, new TypeConnector() {
				public void connect(Type t) {
					type.addType(t);
				}
			});
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireInnerClass errr***");
		}
	}

	public abstract Type createType(String name, AccessModifier access);

	protected void acquireVariables(Type type, ExtendedAST objectTypeAST) {
		try {
			// get the subtree with root OBJBLOCK
			ExtendedAST objBlockAST = objectTypeAST
					.firstChildOfType(JavaTokenTypes.OBJBLOCK);

			// Construct an array of AST's with root VARIABLE_DEF
			ExtendedAST[] variableDefArray = objBlockAST
					.childrenOfType(JavaTokenTypes.VARIABLE_DEF);

			// Add variables one by one
			for (int i = 0; i < variableDefArray.length; i++) {
				ExtendedAST variableAST = variableDefArray[i];
				acquireVariable(type, variableAST);
				// create a new ObjectTypeVariableAcquirer
			}
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireVariables errr***");
		}
	}

	protected boolean isVariable(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.VARIABLE_DEF;
	}

	protected void acquireVariable(Type type, ExtendedAST variableAST) {
		try {
			MemberVariableAcquirer acquirer = getFactory()
					.getMemberVariableAcquirer(_linkage);
			acquirer.acquire(type, variableAST);
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireVariable errr***");
		}
	}

	protected void acquireMethods(Type type, ExtendedAST objectTypeAST) {
		try {
			// get the subtree with root OBJBLOCK
			ExtendedAST objBlockAST = objectTypeAST
					.firstChildOfType(JavaTokenTypes.OBJBLOCK);
			// System.out.println("OBJBLOCK" + objBlockAST.toStringTree());

			// Construct an array of AST's with root METHOD_DEF
			ExtendedAST[] methodDefArray = objBlockAST
					.childrenOfType(JavaTokenTypes.METHOD_DEF);

			// Add methods one by one
			for (int i = 0; i < methodDefArray.length; i++) {
				ExtendedAST methodAST = methodDefArray[i];
				// System.out.println("METHOD :"+methodAST.toStringTree());
				acquireMethod(type, methodAST);
				// create a new MethodAcquirer
			}
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireMethods errr***");
		}
	}

	protected boolean isMethod(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.METHOD_DEF;
	}

	protected void acquireMethod(Type type, ExtendedAST methodAST) {
		try {
			RegularMethodAcquirer acquirer = getFactory()
					.getRegularMethodAcquirer(isInterface(), _linkage);
			acquirer.acquire(type, methodAST);
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireMethod errr***");
		}
	}

	protected abstract boolean isInterface();

	protected abstract void acquireDefaultConstructor(Type type);

	protected void acquireConstructor(Type type, ExtendedAST constructorAST) {
		try {
			getFactory().getConstructorAcquirer(_linkage).acquire(type,
					constructorAST);
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireConstructor errr***");
		}
	}

	protected boolean isConstructor(ExtendedAST ast) {
		return ast.getType() == JavaTokenTypes.CTOR_DEF;
	}

	protected abstract void acquireSuperObjectTypes(Type type, ExtendedAST objectTypeAST);

	protected void acquireOtherModifier(Type type, ExtendedAST objectTypeAST, int mod, TypeModifier modifier) {
		try {
			if (containsModifier(objectTypeAST, mod)) {
				type.addModifier(modifier);
			}
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireOtherModifier errr***");
		}
	}

	protected void acquireOtherModifiers(Type type, ExtendedAST objectTypeAST) {
		acquireOtherModifier(type, objectTypeAST, JavaTokenTypes.FINAL,
				new Final());
		acquireOtherModifier(type, objectTypeAST, JavaTokenTypes.ABSTRACT,
				new Abstract());
		acquireOtherModifier(type, objectTypeAST,
				JavaTokenTypes.LITERAL_static, new Static());
		acquireOtherModifier(type, objectTypeAST, JavaTokenTypes.STRICTFP,
				new StrictFP());
	}

	protected void acquireSuperInterfaces(Type type, ExtendedAST superInterfacesAST) {
		try {
			if (superInterfacesAST != null) {
				ExtendedAST superInterfaceAST = superInterfacesAST.firstChild();
				// Add implemented interfaces one by one
				while (superInterfaceAST != null) {
					List names = superInterfaceAST.getListOfStrings();
					String name = Util.concat(names);
					// System.out.println("### added super interface "+name);
					type.addSuperType(new JavaTypeReference(name));

					// get the AST containing the following superinterface
					superInterfaceAST = superInterfaceAST.nextSibling();
				}
			}
		}
		catch (NullPointerException e) {
			System.err.println("TypeAcquirer acquireSuperInterfaces errr***");
		}
	}

}
