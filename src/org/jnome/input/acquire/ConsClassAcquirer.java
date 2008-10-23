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

import chameleon.core.language.Language;
import chameleon.core.method.Implementation;
import chameleon.core.method.MethodContext;
import chameleon.core.method.MethodLocalContext;
import chameleon.core.method.RegularImplementation;
import chameleon.core.method.RegularMethod;
import chameleon.core.statement.Block;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.linkage.ILinkage;
import chameleon.support.modifier.Public;

public abstract class ConsClassAcquirer extends ClassAcquirer {

    /**
   * @param factory
   */
  public ConsClassAcquirer(Factory factory,ILinkage linkage) {
    super(factory,linkage);
  }

    protected void acquireDefaultConstructor(Type type) {
      try {
		if(type.getConstructors().isEmpty()) {
			  Public pub = new Public();
			  TypeReference tr = new TypeReference(type.getName());
			  MethodLocalContext mlc = getFactory().getLanguage().contextFactory().getMethodLocalContext();
		    RegularMethod cons = new RegularMethod(pub,type.getName(), tr, new MethodContext(), mlc);
		    Block block = new Block();
		    Implementation impl = new RegularImplementation(block);
		    cons.setImplementation(impl);
		    type.addMethod(cons);
		  }
	} catch (NullPointerException e) {
		System.err.println("ConsClassAcquirer acquireDefaultConstructor something wrong");
		e.printStackTrace();
	}
    }
    

}
