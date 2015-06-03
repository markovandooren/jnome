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
		 boolean result = false;
		 if(other instanceof RawType) {
			  result = superTypeJudge().get(other) != null;
			} else if(other instanceof JavaType) {
				Type snd = ((JavaType)other).captureConversion();
				Type sameBase = getSuperType(snd);
				result = sameBase != null && (sameBase instanceof RawType || sameBase.compatibleParameters(snd, trace));
			}
			return result;
	}
}
