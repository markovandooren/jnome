package jnome.core.language;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.Namespace;
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
		return parent.lexicalContext(inheritanceRelation()).lookUp(new JavaNoRelativeNamespaceSelector<T>(selector, root));
	}

}
