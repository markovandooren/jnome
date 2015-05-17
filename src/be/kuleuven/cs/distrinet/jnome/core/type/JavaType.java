package be.kuleuven.cs.distrinet.jnome.core.type;

import static be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations.forAll;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

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
	
	@Override
	default boolean properSubTypeOf(Type other) throws LookupException {
    boolean result = false;
    Type snd = captureConversion(other);
    Type baseType = superTypeJudge().get(snd);
    if(baseType != null) {
      result = baseType.compatibleParameters(snd, new TypeFixer());
    }
    return result;
	}
	
	 public static Type captureConversion(Type type) throws LookupException {
	    Type result = type;
	    if(result instanceof JavaTypeInstantiation) {
	      result = ((JavaTypeInstantiation)result).captureConversion();
	    }
	    return result;
	  }

//	  public boolean rawType(Type type) {
//	    return CollectionOperations.forAll(type.parameters(TypeParameter.class), p -> p instanceof FormalTypeParameter);
//	  }

//	 public static boolean compatibleParameters(Type first, Type second, TypeFixer trace) throws LookupException {
//	    return forAll(first.parameters(TypeParameter.class), second.parameters(TypeParameter.class), (f,s) -> f.compatibleWith(s, trace));
//	  }
//
	 @Override
	public default boolean upperBoundNotHigherThan(Type other,
			TypeFixer trace) throws LookupException {
		 boolean result = false;
		 if(this.sameAs(other)) {
			 result = true;
		 } else if(other instanceof RawType) {
			  result = superTypeJudge().get(other) != null;
			} else {
				Type snd = captureConversion(other);
				Type sameBase = getSuperType(snd);
				result = sameBase != null && (sameBase instanceof RawType || sameBase.compatibleParameters(snd, trace));
			}
			return result || other.lowerBoundAtLeatAsHighAs(this, trace);
	}
}
