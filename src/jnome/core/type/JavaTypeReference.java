package jnome.core.type;

import java.util.List;

import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public interface JavaTypeReference<E extends JavaTypeReference> extends TypeReference<E> {

	public E clone();
	
//	public void setArrayDimension(int i);
	
//	public JavaTypeReference addArrayDimension(int i);
	
//	public int arrayDimension();

//	public Type erasure() throws LookupException;
	
//	public List<ActualTypeArgument> typeArguments();
	
	public JavaTypeReference toArray(int dimension);
	
	public JavaTypeReference erasedReference();
	
	public JavaTypeReference componentTypeReference();
}
