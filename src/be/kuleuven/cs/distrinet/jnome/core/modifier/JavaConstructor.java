package be.kuleuven.cs.distrinet.jnome.core.modifier;

import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Constructor;
/**
 * A class for Java constructor modifiers.
 * 
 * Difference with super class:
 *   1) A Java constructor is not inheritable. 
 *  
 * @author Marko
 */
public class JavaConstructor extends Constructor {

	/**
	 * A Java constructor is not inheritable.
	 */
 /*@
   @ also public behavior
   @
   @ post \result.contains(language().CONSTRUCTOR);
   @*/
  public PropertySet<Element,ChameleonProperty> impliedProperties() {
  	PropertySet<Element,ChameleonProperty> result = super.impliedProperties();
    result.add(language(ObjectOrientedLanguage.class).INHERITABLE.inverse());
    return result;
  }
  
  @Override
  public Constructor clone() {
  	return new JavaConstructor();
  }

}
