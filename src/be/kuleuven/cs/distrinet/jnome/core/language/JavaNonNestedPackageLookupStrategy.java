package be.kuleuven.cs.distrinet.jnome.core.language;

import java.util.List;

import be.kuleuven.cs.distrinet.rejuse.java.collections.TypeFilter;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LocalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;

public class JavaNonNestedPackageLookupStrategy extends LocalLookupContext<Namespace> {


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
