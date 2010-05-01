package jnome.core.type;

import chameleon.core.declaration.SimpleNameSignature;
import chameleon.oo.type.generics.AbstractInstantiatedTypeParameter;
import chameleon.oo.type.generics.ActualTypeArgument;

/**
 * The type argument of an erased type argument must always be in the same context as the parameter itself.
 * Type parameters, however, are cloned and put in a stub to deal with recursive definitions. This means that
 * when the type parameter is cloned, the actual type argument must be cloned as well, and its parent must be
 * the cloned type parameter. We could solve this problem using a cloneForStub() method, but I am not 100% sure that
 * this will always work correctly, though I think that clonedForStub() would only ever be invoked on an erased parameter.
 * Since a potential bug would be very hard to find, a separate class was created for erased type parameters.
 * 
 * 
 * @author Marko van Dooren
 */
public class ErasedTypeParameter extends AbstractInstantiatedTypeParameter<ErasedTypeParameter> {

	public ErasedTypeParameter(SimpleNameSignature signature, ActualTypeArgument argument) {
		super(signature, argument);
	}

	@Override
	public ErasedTypeParameter clone() {
		ActualTypeArgument argument = argument().clone();
		ErasedTypeParameter result = new ErasedTypeParameter(signature().clone(),argument);
		argument.setUniParent(result);
		return result;
	}
	
}
