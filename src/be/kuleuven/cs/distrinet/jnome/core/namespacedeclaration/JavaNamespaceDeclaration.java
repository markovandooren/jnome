package be.kuleuven.cs.distrinet.jnome.core.namespacedeclaration;

import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.namespace.NamespaceReference;
import org.aikodi.chameleon.core.namespace.RootNamespaceReference;
import org.aikodi.chameleon.core.namespacedeclaration.Import;
import org.aikodi.chameleon.core.namespacedeclaration.NamespaceDeclaration;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.reference.NameReference;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.imports.JavaDemandImport;

public class JavaNamespaceDeclaration extends NamespaceDeclaration {

  static {
    excludeFieldName(JavaNamespaceDeclaration.class,"_defaultImport");
  }
  
  public JavaNamespaceDeclaration() {
  	this(new RootNamespaceReference());
  }

  public JavaNamespaceDeclaration(String fqn) {
  	this(new NameReference<Namespace>(fqn, Namespace.class));
  }
  
	public JavaNamespaceDeclaration(CrossReference<Namespace> ref) {
		super(ref);
		verify(ref);
		set(_defaultImport,new JavaDemandImport(new NamespaceReference("java.lang")));
		_defaultImport.lock();
	}

	public void verify(CrossReference<Namespace> ref) {
		if(ref instanceof NameReference && ((NameReference)ref).name().equals("")) {
			throw new IllegalArgumentException("If you want a namespace declaration for the root namespace, use a RootNamespaceReference, or use the default constructor.");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected NamespaceDeclaration cloneSelf() {
		return new JavaNamespaceDeclaration((CrossReference)null);
	}
	
	@Override
	public List<? extends Import> implicitImports() {
		return Collections.singletonList(_defaultImport.getOtherEnd());
	}

	private Single<Import> _defaultImport = new Single<Import>(this, "default import");

}
