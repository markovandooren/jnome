/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.core.validation.Invalid;

/**
 * @author Marko van Dooren
 *
 */
public class BBCodeFormatter implements MessageFormatter {

  /**
   * @{inheritDoc}
   */
  @Override
  public String format(Invalid problem) {
    return "TEXT:1:[error] "+problem.message().replace(":","");
  }

}
