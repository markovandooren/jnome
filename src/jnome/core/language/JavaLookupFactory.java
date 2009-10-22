package jnome.core.language;

import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.namespace.Namespace;

public class JavaLookupFactory extends LookupStrategyFactory {

  public  LookupStrategy createLocalLookupStrategy(DeclarationContainer element) {
		if(element instanceof Namespace) {
			return new JavaNoRelativeNamespaceLookupStrategy((Namespace)element);
		} else {
			return super.createLocalLookupStrategy(element);
		}
  }

}