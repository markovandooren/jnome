package jnome.core.language;

import chameleon.core.element.Element;
import chameleon.core.lookup.LexicalLookupStrategy;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.type.inheritance.InheritanceRelation;

public class JavaLookupFactory extends LookupStrategyFactory {
	
	public LookupStrategy createLexicalContext(Element element, LookupStrategy local) {
		if(element instanceof InheritanceRelation) {
			return new JavaInheritanceLookupStrategy((InheritanceRelation)element);
		} else {
		  return super.createLexicalContext(element, local);
		}
	}
 
}