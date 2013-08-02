package be.kuleuven.cs.distrinet.jnome.core.language;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LocalLookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.SelectionResult;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.rejuse.java.collections.TypeFilter;

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
  
  //PAPER: customize lookup

}
