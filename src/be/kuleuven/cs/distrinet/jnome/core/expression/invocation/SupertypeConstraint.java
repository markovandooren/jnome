/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.HashSet;
import java.util.Set;

import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;

public class SupertypeConstraint extends SecondPhaseConstraint {

	public SupertypeConstraint(TypeParameter param, JavaTypeReference type) {
		super(param,type);
	}

	/**
	 * Do nothing for a supertype constraint
	 */
	@Override
	public void process() {
	}
	
	
}
