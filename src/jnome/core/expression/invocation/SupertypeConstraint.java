/**
 * 
 */
package jnome.core.expression.invocation;

import java.util.HashSet;
import java.util.Set;

import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.generics.TypeParameter;

public class SupertypeConstraint extends SecondPhaseConstraint {

	public SupertypeConstraint(TypeParameter param, JavaTypeReference type) {
		super(param,type);
	}
	
	
}