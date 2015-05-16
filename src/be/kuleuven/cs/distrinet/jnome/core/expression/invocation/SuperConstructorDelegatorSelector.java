package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;

class SuperConstructorDelegatorSelector extends NamelessConstructorSelector {

	/**
	 * @param superConstructorDelegation
	 */
	SuperConstructorDelegatorSelector(SuperConstructorDelegation superConstructorDelegation) {
		super(superConstructorDelegation);
	}

	@Override
	public String selectionName(DeclarationContainer container) {
		return container.nearestAncestor(Type.class).name();
	}
	
	@Override
	public boolean isGreedy() {
		return true;
	}
	
}
