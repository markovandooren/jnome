package be.kuleuven.cs.distrinet.jnome.core.language;

import org.aikodi.chameleon.core.declaration.DeclarationContainer;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupContextFactory;
import org.aikodi.chameleon.core.namespace.Namespace;

public class JavaLookupFactory extends LookupContextFactory {

  public  LookupContext createLocalLookupStrategy(DeclarationContainer element) {
		if(element instanceof Namespace) {
			return new JavaNonNestedPackageLookupStrategy((Namespace)element);
		} else {
			return super.createLocalLookupStrategy(element);
		}
  }

}
