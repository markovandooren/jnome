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

import org.jnome.mm.language.Java;

import chameleon.core.language.Language;
import chameleon.linkage.ILinkage;

/**
 * @author marko
 */
public class Factory {

	public Language getLanguage() {
		return new Java();
	}
	
	/**
	 * @return
	 */
	public ExpressionAcquirer getExpressionAcquirer(ILinkage linkage) {
		return new ExpressionAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public MemberVariableAcquirer getMemberVariableAcquirer(ILinkage linkage) {
		return new MemberVariableAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public StatementAcquirer getStatementAcquirer(ILinkage linkage) {
		return new StatementAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public ParameterAcquirer getParameterAcquirer(ILinkage linkage) {
		return new ParameterAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public InterfaceAcquirer getInterfaceAcquirer(ILinkage linkage) {
		return new InterfaceAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public JavaClassAcquirer getJavaClassAcquirer(ILinkage linkage) {
		return new JavaClassAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public CompilationUnitAcquirer getCompilationUnitAcquirer(ILinkage linkage) {
		return new CompilationUnitAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public ConstructorAcquirer getConstructorAcquirer(ILinkage linkage) {
		return new ConstructorAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public AnonymousTypeAcquirer getAnonymousTypeAcquirer(ILinkage linkage) {
		return new AnonymousTypeAcquirer(this, linkage);
	}

	/**
	 * @param b
	 * @return
	 */
	public RegularMethodAcquirer getRegularMethodAcquirer(boolean b,
			ILinkage linkage) {
		return new RegularMethodAcquirer(this, b, linkage);
	}

	/**
	 * @return
	 */
//	public InnerClassAcquirer getInnerClassAcquirer(ILinkage linkage) {
//		return new InnerClassAcquirer(this, linkage);
//	}

	/**
	 * @return
	 */
//	public InnerInterfaceAcquirer getInnerInterfaceAcquirer(ILinkage linkage) {
//		return new InnerInterfaceAcquirer(this, linkage);
//	}

	/**
	 * @return
	 */
//	public LocalTypeAcquirer getLocalTypeAcquirer(ILinkage linkage) {
//		return new LocalTypeAcquirer(this, linkage);
//	}

	/**
	 * @return
	 */
	public NewMethodAcquirer getNewMethodAcquirer(ILinkage linkage) {
		return new NewMethodAcquirer(this, linkage);
	}

	/**
	 * @return
	 */
	public ObjectBlockAcquirer getObjectBlockAcquirer(ILinkage linkage) {
		return new ObjectBlockAcquirer(this, linkage);
	}
}
