package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.oo.method.Method;

/**
 * The result of selecting a method during lookup. Object that implement
 * this interface store the type assignment that is done during selection
 * to avoid repeating it later when a best candidate must be selected. The
 * phase recording the phase in which the result was selected. The lower
 * the phase, the higher the priority of the result.
 * 
 * @author Marko van Dooren
 */
public interface MethodSelectionResult extends SelectionResult {
	
	/**
	 * Return the method that was used during selection.
	 */
	public Method template();
	
	/**
	 * Return the phase in which the template of this result was selected.
	 * @return
	 */
	public int phase();
	
	/**
	 * Return the type assignments that were made during the selection
	 * of the template. The result is null if the method has no type parameters.
	 */
	public TypeAssignmentSet typeAssignment();
	
	public boolean requiredUncheckedConversion();
}