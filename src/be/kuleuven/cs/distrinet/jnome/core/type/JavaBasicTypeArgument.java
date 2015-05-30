package be.kuleuven.cs.distrinet.jnome.core.type;

import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.BasicTypeArgument;

public class JavaBasicTypeArgument extends BasicTypeArgument {
	
//	private CreationStackTrace _trace = new CreationStackTrace();
	
	public JavaBasicTypeArgument(TypeReference ref) {
		super(ref);
	}

	@Override
	protected JavaBasicTypeArgument cloneSelf() {
		return new JavaBasicTypeArgument(null);
	}

}
