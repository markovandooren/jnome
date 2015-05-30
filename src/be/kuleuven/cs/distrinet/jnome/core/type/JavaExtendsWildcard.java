package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.ExtendsWildcard;

public class JavaExtendsWildcard extends ExtendsWildcard {

	public JavaExtendsWildcard(TypeReference ref) {
		super(ref);
	}

	@Override
	protected JavaExtendsWildcard cloneSelf() {
		return new JavaExtendsWildcard(null);
	}
}
