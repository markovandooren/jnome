package be.kuleuven.cs.distrinet.jnome.core.modifier;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.support.modifier.Constructor;

import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
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
  protected Constructor cloneSelf() {
  	return new JavaConstructor();
  }

}
