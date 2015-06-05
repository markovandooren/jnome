package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.generics.AbstractInstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.util.Pair;

/**
 * The type argument of an erased type argument must always be in the same context as the parameter itself.
 * Type parameters, however, are cloned and put in a stub to deal with recursive definitions. This means that
 * when the type parameter is cloned, the actual type argument must be cloned as well, and its parent must be
 * the cloned type parameter. We could solve this problem using a cloneForStub() method, but I am not 100% sure that
 * this will always work correctly, though I think that clonedForStub() would only ever be invoked on an erased parameter.
 * Since a potential bug would be very hard to find, a separate class was created for erased type parameters.
 * 
 * @author Marko van Dooren
 */
public class ErasedTypeParameter extends AbstractInstantiatedTypeParameter {

	public ErasedTypeParameter(String name, TypeArgument argument) {
		super(name, argument);
	}

	@Override
	protected ErasedTypeParameter cloneSelf() {
		// We must clone the argument manually because it is not referenced through
		// a bidirectional association.
		TypeArgument argument = clone(argument());
		ErasedTypeParameter result = new ErasedTypeParameter(name(),argument);
		argument.setUniParent(result);
		return result;
	}
	
//	public boolean compatibleWith(TypeParameter other,TypeFixer trace) throws LookupException {
//		return other instanceof ErasedTypeParameter && super.compatibleWith(other, trace);
//	}
	
//	@Override
//	public boolean contains(TypeParameter other, TypeFixer trace) throws LookupException {
//		return other instanceof ErasedTypeParameter && super.contains(other, trace);
//	}
}
