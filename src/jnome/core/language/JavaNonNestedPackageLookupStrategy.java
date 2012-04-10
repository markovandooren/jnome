package jnome.core.language;

import java.util.List;

import org.rejuse.java.collections.TypeFilter;

import chameleon.core.declaration.Declaration;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LocalLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.Namespace;

public class JavaNonNestedPackageLookupStrategy extends LocalLookupStrategy<Namespace> {


	public JavaNonNestedPackageLookupStrategy(Namespace element) {
		super(element);
	}

  protected <D extends Declaration> List<D> declarations(DeclarationSelector<D> selector) throws LookupException {
  	List<D> result = declarationContainer().declarations(selector);
  	if(declarationContainer() != declarationContainer().defaultNamespace()) {
  		new TypeFilter(Namespace.class).discard(result);
  	}
  	return result;
  }
  
  //PAPER: customize lookup

}
