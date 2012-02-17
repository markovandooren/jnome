package jnome.core.expression.invocation;

import chameleon.core.declaration.DeclarationContainer;
import chameleon.oo.type.Type;

public class NamelessConstructorSelector extends AbstractConstructorSelector {
  /**
	 * 
	 */
	private final ConstructorDelegation _constructorDelegation;

	protected ConstructorDelegation invocation() {
		return _constructorDelegation;
	}
	
	/**
	 * @param constructorDelegation
	 */
	public NamelessConstructorSelector(ConstructorDelegation constructorDelegation) {
		_constructorDelegation = constructorDelegation;
	}

	@Override
	public String selectionName(DeclarationContainer container) {
		return _constructorDelegation.nearestAncestor(Type.class).signature().name();
	}
}