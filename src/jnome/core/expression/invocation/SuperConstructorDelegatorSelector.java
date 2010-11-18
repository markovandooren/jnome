package jnome.core.expression.invocation;

import chameleon.core.declaration.DeclarationContainer;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.method.NormalMethod;

class SuperConstructorDelegatorSelector extends NamelessConstructorSelector {

	/**
	 * @param superConstructorDelegation
	 */
	SuperConstructorDelegatorSelector(SuperConstructorDelegation superConstructorDelegation) {
		super(superConstructorDelegation);
	}

	@Override
	public String selectionName(DeclarationContainer<?,?> container) {
		return container.nearestAncestor(Type.class).signature().name();
	}
	
}