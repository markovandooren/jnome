package be.kuleuven.cs.distrinet.jnome.core.namespacedeclaration;

import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.NamespaceReference;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespaceReference;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.DemandImport;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.Import;
import be.kuleuven.cs.distrinet.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public class JavaNamespaceDeclaration extends NamespaceDeclaration {

  static {
    excludeFieldName(JavaNamespaceDeclaration.class,"_defaultImport");
  }
  
  public JavaNamespaceDeclaration() {
  	this(new RootNamespaceReference());
  }

  public JavaNamespaceDeclaration(String fqn) {
  	this(new SimpleReference<Namespace>(fqn, Namespace.class));
  }
  
	public JavaNamespaceDeclaration(CrossReference<Namespace> ref) {
		super(ref);
		verify(ref);
		set(_defaultImport,new DemandImport(new NamespaceReference("java.lang")));
	}

	public void verify(CrossReference<Namespace> ref) {
		if(ref instanceof SimpleReference && ((SimpleReference)ref).name().equals("")) {
			throw new IllegalArgumentException("If you want a namespace declaration for the root namespace, use a RootNamespaceReference, or use the default constructor.");
		}
	}
	
	@Override
	public NamespaceDeclaration cloneThis() {
		return new JavaNamespaceDeclaration(namespaceReference().clone());
	}
	
	@Override
	public List<? extends Import> implicitImports() {
		return Collections.singletonList(_defaultImport.getOtherEnd());
	}

	private Single<Import> _defaultImport = new Single<Import>(this);

}
