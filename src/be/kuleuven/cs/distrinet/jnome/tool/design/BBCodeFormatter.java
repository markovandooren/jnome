/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.tool.design;

import java.util.HashMap;
import java.util.Map;

import org.aikodi.chameleon.core.validation.Invalid;

import be.kuleuven.cs.distrinet.jnome.tool.design.SuspiciousCastAnalysis.SuspiciousCast;

/**
 * @author Marko van Dooren
 *
 */
public class BBCodeFormatter implements MessageFormatter {

	private Map<Class<? extends Invalid>, String> _errorCodes;
	private final static String ERROR="error";
	private final static String WARNING="warning";
	
	public BBCodeFormatter() {
		_errorCodes = new HashMap<>();
		_errorCodes.put(SuspiciousCast.class, WARNING);
	}
	
	private String errorCode(Invalid problem) {
		String result = _errorCodes.get(problem.getClass());
		if(result == null) {
			result = ERROR;
		}
		return result;
	}
	
  /**
   * @{inheritDoc}
   */
  @Override
  public String format(Invalid problem) {
    return "TEXT:1:["+errorCode(problem)+"] "+problem.message().replace(":","");
  }

}
