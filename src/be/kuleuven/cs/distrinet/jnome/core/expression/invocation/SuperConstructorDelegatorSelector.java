package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;

class SuperConstructorDelegatorSelector extends NamelessConstructorSelector {

	/**
	 * @param superConstructorDelegation
	 */
	SuperConstructorDelegatorSelector(SuperConstructorDelegation superConstructorDelegation) {
		super(superConstructorDelegation);
	}

	@Override
	public String selectionName(DeclarationContainer container) {
		return container.nearestAncestor(Type.class).signature().name();
	}
	
}
