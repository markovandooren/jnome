package jnome.core.namespacedeclaration;

import java.util.Collections;
import java.util.List;

import chameleon.core.namespace.Namespace;
import chameleon.core.namespace.NamespaceReference;
import chameleon.core.namespacedeclaration.DemandImport;
import chameleon.core.namespacedeclaration.Import;
import chameleon.core.namespacedeclaration.NamespaceDeclaration;
import chameleon.util.association.Single;

public class JavaNamespaceDeclaration extends NamespaceDeclaration {

  static {
    excludeFieldName(JavaNamespaceDeclaration.class,"_defaultImport");
  }

	public JavaNamespaceDeclaration(Namespace namespace) {
		super(namespace);
		set(_defaultImport,new DemandImport(new NamespaceReference("java.lang")));
	}
	
	@Override
	public NamespaceDeclaration cloneThis() {
		return new JavaNamespaceDeclaration(null);
	}
	
	@Override
	public List<? extends Import> implicitImports() {
		return Collections.singletonList(_defaultImport.getOtherEnd());
	}

	private Single<Import> _defaultImport = new Single<Import>(this);

}