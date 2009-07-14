package jnome.core.language;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.Namespace;

public class JavaNoRelativeNamespaceLookupStrategy extends LookupStrategy {


	public JavaNoRelativeNamespaceLookupStrategy(LookupStrategy local, Namespace root) {
		_local = local;
		_root = root;
	}

	private Namespace _root;
	
	private LookupStrategy _local;

	public <D extends Declaration> D lookUp(DeclarationSelector<D> selector) throws LookupException {
		return _local.lookUp(new JavaNoRelativeNamespaceSelector<D>(selector, _root));
	}
}
