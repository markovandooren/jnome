/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.tool.design;

import org.aikodi.chameleon.core.validation.Invalid;

/**
 * @author Marko van Dooren
 *
 */
public interface MessageFormatter {

  String format(Invalid problem);
}
