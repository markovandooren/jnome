/**
 * 
 */
package org.aikodi.java.tool.design;

import org.aikodi.chameleon.core.validation.Invalid;

/**
 * @author Marko van Dooren
 *
 */
public class DefaultFormatter implements MessageFormatter {

  /**
   * @{inheritDoc}
   */
  @Override
  public String format(Invalid problem) {
    return problem.message();
  }

  
}
