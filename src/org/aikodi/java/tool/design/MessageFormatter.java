/**
 * 
 */
package org.aikodi.java.tool.design;

import org.aikodi.chameleon.core.validation.Invalid;

/**
 * @author Marko van Dooren
 *
 */
public interface MessageFormatter {

  String format(Invalid problem);
}
