package be.kuleuven.cs.distrinet.jnome.core.type;

import static be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations.forAll;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Pair;

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
    //SPEED iterate over the supertype graph 
    Set<Type> supers = getSelfAndAllSuperTypesView();
    Type snd = captureConversion(other);

    Iterator<Type> typeIterator = supers.iterator();
    while((!result) && typeIterator.hasNext()) {
      Type current = typeIterator.next();
      result = sameBaseTypeWithCompatibleParameters(current, snd, new ArrayList<Pair<Type, TypeParameter>>()); //(snd instanceof RawType && other.baseType().sameAs(current.baseType())) || 
    }
    return result;
	}
	
	 public static Type captureConversion(Type type) throws LookupException {
	    Type result = type;
	    if(result instanceof JavaDerivedType) {
	      result = ((JavaDerivedType)result).captureConversion();
	    }
	    return result;
	  }

	 public static boolean sameBaseTypeWithCompatibleParameters(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
	    return first.baseType().sameAs(second.baseType()) && compatibleParameters(first, second, trace);
	  }

//	  public boolean rawType(Type type) {
//	    return CollectionOperations.forAll(type.parameters(TypeParameter.class), p -> p instanceof FormalTypeParameter);
//	  }

	 public static boolean compatibleParameters(Type first, Type second, List<Pair<Type, TypeParameter>> trace) throws LookupException {
	    return forAll(first.parameters(TypeParameter.class), second.parameters(TypeParameter.class), (f,s) -> f.compatibleWith(s, trace));
	  }

}
