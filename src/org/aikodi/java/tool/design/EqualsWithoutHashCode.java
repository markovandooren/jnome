/**
 * 
 */
package org.aikodi.java.tool.design;

import java.util.List;

import org.aikodi.chameleon.analysis.Analysis;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.validation.BasicProblem;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.java.core.language.Java7;

/**
 * @author Marko van Dooren
 *
 */
public class EqualsWithoutHashCode extends Analysis<Type,Verification, LookupException> {

  /**
   * @param type
   * @param initial
   */
  public EqualsWithoutHashCode() {
    super(Type.class, Valid.create());
  }

  /**
   * @{inheritDoc}
   */
  @Override
  protected void analyze(Type element) throws LookupException {
    Java7 java = element.language(Java7.class);
    boolean equals = element.isTrue(java.TYPE_WITH_VALUE_SEMANTICS);
    boolean isInterface = element.isTrue(java.INTERFACE());
    if(equals && ! isInterface) {
      boolean hasHashCode = false;
      List<Method> methods = element.directlyDeclaredMembers(Method.class);
      for(Method method: methods) {
        if(method.name().equals("hashCode") && method.nbFormalParameters() == 0) {
          hasHashCode = true;
        }
      }
      if(! hasHashCode) {
        setResult(result().and(new BasicProblem(element, "Class "+element.getFullyQualifiedName()+" overrides equals(Object) but does not override hashCode().")));
      }
    }
  }

}
