package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.SelectionResult;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;

public class NamelessConstructorSelector extends AbstractConstructorSelector {
	
	public List<? extends SelectionResult> selection(List<? extends Declaration> selectionCandidates) throws LookupException {
		return super.selection(withoutNonConstructors(selectionCandidates));
	}
	
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
		return _constructorDelegation.nearestAncestor(Type.class).name();
	}
}
