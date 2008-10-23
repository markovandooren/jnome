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
import org.jnome.mm.type.AnonymousInnerType;
import org.jnome.mm.type.JavaTypeReference;

import chameleon.core.modifier.AccessModifier;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;

/**
 * @author marko
 */
public class AnonymousTypeAcquirer extends TypeAcquirer {

  /**
   * @param factory
   */
  public AnonymousTypeAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }

  public AnonymousInnerType createType(String name, AccessModifier access) {
  	AnonymousInnerType result = new AnonymousInnerType();
    return result;
  }

  protected void acquireSuperObjectTypes(Type type, ExtendedAST objectTypeAST) {
    String typeName = flattenDot(objectTypeAST.firstChild());
    if (typeName == null) {
      typeName = "Object";
    }
    type.addSuperType(new JavaTypeReference(typeName));
  }

  public AnonymousInnerType acquire(ExtendedAST objectTypeAST) {
    try {
    	AnonymousInnerType type = createType("TODO", null);
		setDecorator(type, objectTypeAST.firstChild(),Decorator.TYPE_DECORATOR);
		setSmallDecorator(type, objectTypeAST,Decorator.CODEWORD_DECORATOR);
		acquireSuperObjectTypes(type, objectTypeAST);
		acquireObjectBlock(type, objectTypeAST);
		return type;
	} catch (NullPointerException npe) {
		System.err.println("***AnonymousTypeAcquirer acquire went wrong***");
		throw npe;
	}
  }

  protected boolean isInterface() {
    return false;
  }
  
  protected void acquireDefaultConstructor(Type type) {
  }
}
