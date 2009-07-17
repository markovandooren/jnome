/**
 * 
 */
package jnome.core.language;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;
import chameleon.core.relation.WeakPartialOrder;

/**
 * Use this as a lexical selector to prevent selection of namespaces that are not at the top level.
 *  
 * @author Marko van Dooren
 *
 * @param <D>
 */
public class JavaNoRelativeNamespaceSelector<D extends Declaration> extends DeclarationSelector<D> {

	public JavaNoRelativeNamespaceSelector(DeclarationSelector<D> selector, Namespace defaultNamespace) {
		_selector = selector;
		_defaultNamespace = defaultNamespace;
	}
	
	private Namespace _defaultNamespace;
	
	public Namespace defaultNamespace() {
		return _defaultNamespace;
	}
	
	private DeclarationSelector<D> _selector;
	
	public DeclarationSelector<D> selector() {
		return _selector;
	}

	/**
	 * If the given declaration is a namespace and its parent is not the default namespace, then
	 * null is returned.
	 */
	@Override
	public Declaration<?,?,?,D> filter(Declaration declaration) throws LookupException {
		Declaration<?,?,?,D> result = selector().filter(declaration);
		if((result != null) && (declaration instanceof Namespace) && (((Namespace)declaration).parent() != defaultNamespace())) {
			result = null;
		}
//		if((declaration instanceof Namespace) && (((Namespace)declaration).parent() != defaultNamespace())) {
//			return null;
//		} else {
//			return selector().filter(declaration);
//		}
		return result;
	}

	@Override
	public WeakPartialOrder<D> order() {
		return selector().order();
	}

	@Override
	public Class<D> selectedClass() {
		return selector().selectedClass();
	}
	
}