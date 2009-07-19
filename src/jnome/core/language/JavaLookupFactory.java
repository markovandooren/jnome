package jnome.core.language;

import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.namespace.Namespace;
import chameleon.core.namespacepart.NamespacePart;

public class JavaLookupFactory extends LookupStrategyFactory {

  public  LookupStrategy createLocalLookupStrategy(DeclarationContainer element) {
		if(element instanceof Namespace) {
//			return new JavaNoRelativeNamespaceLookupStrategy(super.createLocalLookupStrategy(element), element.language().defaultNamespace());
			return new JavaNoRelativeNamespaceLookupStrategy((Namespace)element);
		} else {
			return super.createLocalLookupStrategy(element);
		}
  }

}