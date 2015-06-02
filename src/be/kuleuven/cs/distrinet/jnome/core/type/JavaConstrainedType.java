/**
 * 
 */
package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.ConstrainedType;

/**
 * @author Marko van Dooren
 *
 */
public class JavaConstrainedType extends ConstrainedType implements JavaType {

	public JavaConstrainedType(Type lowerBound, Type upperBound) {
		super(lowerBound, upperBound);
	}

	@Override
	public Type erasure() {
		Type low = ((JavaType)lowerBound()).erasure();
		Type up = ((JavaType)upperBound()).erasure();
		ConstrainedType result = language().plugin(ObjectOrientedFactory.class).reallyCreateConstrainedType(low, up);
		result.setUniParent(parent());
		return result;
	}

	public JavaConstrainedType cloneSelf() {
		return new JavaConstrainedType(lowerBound(), upperBound());
	}

	@Override
	public Type captureConversion() throws LookupException {
		Type low = ((JavaType)lowerBound()).captureConversion();
		Type up = ((JavaType)upperBound()).captureConversion();
		return language().plugin(ObjectOrientedFactory.class).createConstrainedType(low, up, parent());
	}
}
