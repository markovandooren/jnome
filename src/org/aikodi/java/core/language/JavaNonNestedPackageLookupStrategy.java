package org.aikodi.java.core.language;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LocalLookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.rejuse.java.collections.TypeFilter;

/**
 * A lookup strategy that searches for declarations withing a package but
 * removed any subpackages that are selected. In Java, packages cannot be looked up
 * relative to their "parent" packages (except when the "parent" package is the root package
 * of course).
 * 
 * @author Marko van Dooren
 */
public class JavaNonNestedPackageLookupStrategy extends LocalLookupContext<Namespace> {


	public JavaNonNestedPackageLookupStrategy(Namespace element) {
		super(element);
	}

  protected <D extends Declaration> List<? extends SelectionResult> declarations(DeclarationSelector<D> selector) throws LookupException {
  	List<? extends SelectionResult> result = declarationContainer().declarations(selector);
  	if(declarationContainer() != declarationContainer().defaultNamespace()) {
  		new TypeFilter(Namespace.class).discard(result);
  	}
  	return result;
  }

}
