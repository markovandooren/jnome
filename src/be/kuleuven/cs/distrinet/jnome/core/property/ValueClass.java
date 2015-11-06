/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.property;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.property.DynamicChameleonProperty;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.view.ObjectOrientedView;

import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.property.PropertyMutex;
import be.kuleuven.cs.distrinet.rejuse.property.PropertyUniverse;

/**
 * A property that determines if a Java class represents
 * a value class.
 * 
 * @author Marko van Dooren
 */
public class ValueClass extends DynamicChameleonProperty {

  public ValueClass() {
    super("value", Type.class);
  }

  public ValueClass(PropertyMutex<ChameleonProperty> mutex,
      ChameleonProperty inverse) {
    super("value", mutex, inverse, Type.class);
  }

  public ValueClass(PropertyMutex<ChameleonProperty> mutex) {
    super("value", mutex, Type.class);
  }

  /**
   * @{inheritDoc}
   * 
   * @return True if the class has a method with the signature
   * equals(java.lang.Object). False otherwise.
   */
  @Override
  protected Ternary selfAppliesTo(Element element) {
    try {
      boolean hasEquals = false;
      if(element instanceof Type) {
        List<Method> members = ((Type)element).directlyDeclaredMembers(Method.class);
        for(Method m: members) {
          if(m.signature().name().equals("equals") &&
              m.nbFormalParameters() == 1 &&
              m.formalParameter(0).getType().equals(m.view(ObjectOrientedView.class).topLevelType())) {
            hasEquals = true;
          }
        }
      }
      return Ternary.of(hasEquals);
    } catch(LookupException exc) {
      return Ternary.UNKNOWN;
    }
  }
}
