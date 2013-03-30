package be.kuleuven.cs.distrinet.jnome.core.language;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.DeclarationContainer;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContextFactory;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;

public class JavaLookupFactory extends LookupContextFactory {

  public  LookupContext createLocalLookupStrategy(DeclarationContainer element) {
		if(element instanceof Namespace) {
			return new JavaNonNestedPackageLookupStrategy((Namespace)element);
		} else {
			return super.createLocalLookupStrategy(element);
		}
  }

}
