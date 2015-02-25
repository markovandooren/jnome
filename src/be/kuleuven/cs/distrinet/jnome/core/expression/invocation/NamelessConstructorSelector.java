package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.oo.type.Type;

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
