package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import org.aikodi.chameleon.core.lookup.LocalLookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.TypeInstantiation;
import org.aikodi.chameleon.oo.type.ParameterSubstitution;
import org.aikodi.chameleon.oo.type.Type;

/**
 * A class that represents a captured type.
 * 
 * 
 * FIXME This should not be a subtype of {@link TypeInstantiation}. Must
 *       introduce an intermediate class for code sharing. The current
 *       implementation of{@link #targetContext()} "violates" behavioral subtyping. 
 *       The contract is not written down in DerivedType, but it should be.
 * 
 * @author Marko van Dooren
 */
public class CapturedType extends JavaDerivedType {

	public CapturedType(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

	public CapturedType(List<ParameterSubstitution<?>> parameters, Type baseType) {
		super(parameters, baseType);
	}

	@Override
	public CapturedType clone() {
		return new CapturedType(clonedParameters(),baseType());
	}
	
	@Override
	public LocalLookupContext<?> targetContext() throws LookupException {
		return localContext();
	}

}
