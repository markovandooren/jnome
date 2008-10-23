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

import org.jnome.mm.type.JavaClass;
import org.jnome.mm.type.JavaTypeLocalContext;

import chameleon.core.modifier.AccessModifier;
import chameleon.core.type.Type;
import chameleon.linkage.ILinkage;

/**
 * @author marko
 */
public class JavaClassAcquirer extends ConsClassAcquirer {

	/**
   * @param factory
   */
  public JavaClassAcquirer(Factory factory, ILinkage linkage) {
    super(factory, linkage);
  }

  public Type createType(String name, AccessModifier access) {
		return new JavaClass(name, access,new JavaTypeLocalContext());
	}

}
