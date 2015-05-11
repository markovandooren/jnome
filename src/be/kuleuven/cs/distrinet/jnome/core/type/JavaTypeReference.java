package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.workspace.View;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public interface JavaTypeReference extends TypeReference {

//	public void setArrayDimension(int i);
	
//	public JavaTypeReference addArrayDimension(int i);
	
//	public int arrayDimension();

//	public Type erasure() throws LookupException;
	
//	public List<ActualTypeArgument> typeArguments();
	
	public JavaTypeReference toArray(int dimension);
	
	public JavaTypeReference erasedReference();
	
	public JavaTypeReference componentTypeReference();
	
	public default JavaTypeReference box() throws LookupException {
	  Java7 language = language(Java7.class);
    return language.box(this, view().namespace());
	}
}
