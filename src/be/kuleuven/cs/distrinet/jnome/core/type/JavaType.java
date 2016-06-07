package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;

public interface JavaType extends Type {

	/**
	 * Return the erasure of this type.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @
   @*/
	public Type erasure();
	
	/**
	 * @return The capture conversion of this type. The default implementation
	 * returns <code>this</code>.
	 * @throws LookupException
	 */
	public default Type captureConversion() throws LookupException {
		return this;
	}
	@Override
	public default boolean uniSubtypeOf(Type other, TypeFixer trace) throws LookupException {
		Type captureConversion = captureConversion();
		boolean result;
		if(captureConversion != this) {
			result = captureConversion.subtypeOf(other, trace);
		} else {
			result = Type.super.uniSubtypeOf(other, trace);
		}
		return result;
	}
	
}
