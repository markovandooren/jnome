package jnome.core.language;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.namespacepart.NamespacePart;
import chameleon.core.type.inheritance.InheritanceRelation;

public class JavaLookupFactory extends LookupStrategyFactory {

//	//TODO: I think this method can be removed now that we have the wrapping.
//	public LookupStrategy createLexicalContext(Element element, LookupStrategy local) {
//		if(element instanceof InheritanceRelation) {
//			return new JavaInheritanceLookupStrategy((InheritanceRelation)element);
//		} else {
//		  return super.createLexicalContext(element, local);
//		}
//	}
 
	public LookupStrategy wrapLocalStrategy(LookupStrategy targetContext, Element element) {
		if(element instanceof NamespacePart) {
			return new JavaNoRelativeNamespaceLookupStrategy(targetContext, element.language().defaultNamespace());
		} else {
			return targetContext;
		}
	}
}