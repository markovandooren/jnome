package jnome.core.language;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.Namespace;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.type.Type;
import chameleon.core.type.inheritance.InheritanceRelation;

public class JavaInheritanceLookupStrategy extends LookupStrategy {

	private InheritanceRelation _inheritanceRelation;

	public JavaInheritanceLookupStrategy(InheritanceRelation relation) {
		_inheritanceRelation = relation;
	}
	
	public InheritanceRelation<?> inheritanceRelation() {
		return _inheritanceRelation;
	}

	@Override
	public <T extends Declaration> T lookUp(DeclarationSelector<T> selector) throws LookupException {
		Type parent = inheritanceRelation().parent();
		Namespace root = parent.getNamespace().defaultNamespace();
		return parent.lexicalContext(inheritanceRelation()).lookUp(new JavaInheritanceDeclarationSelector<T>(selector, root));
	}
	
  public static class JavaInheritanceDeclarationSelector<D extends Declaration> extends DeclarationSelector<D> {

		public JavaInheritanceDeclarationSelector(DeclarationSelector<D> selector, Namespace defaultNamespace) {
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
		public D filter(Declaration declaration) throws LookupException {
			if((declaration instanceof Namespace) && (((Namespace)declaration).parent() != defaultNamespace())) {
				return null;
			} else {
				return selector().filter(declaration);
			}
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

}
