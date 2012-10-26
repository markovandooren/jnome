package jnome.workspace;

import java.util.HashMap;
import java.util.Map;

import chameleon.core.language.Language;
import chameleon.core.namespace.RootNamespace;
import chameleon.oo.type.Type;
import chameleon.workspace.View;

public class JavaView extends View {

	public JavaView(RootNamespace namespace, Language language) {
		super(namespace, language);
	}

	private Map<String, Type> _primitiveCache = new HashMap<String,Type>();
	
	public void storePrimitiveType(String name, Type type) {
		_primitiveCache.put(name, type);
	}
	

}
