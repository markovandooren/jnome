package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.RootNamespace;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;

public class JavaView extends View {

	public JavaView(RootNamespace namespace, Language language) {
		super(namespace, language);
	}

	//FIXME try to somehow use this cache again.
	//      Perhaps the view is the best object for creating the type references,
	//      or storing the factory that does so. After all, the method to create a
	//      type reference in the root namespace should create one in the root
	//      namespace of this view
	private Map<String, Type> _primitiveCache = new HashMap<String,Type>();
	
	public void storePrimitiveType(String name, Type type) {
		_primitiveCache.put(name, type);
	}
	
  public Type primitiveType(String name) {
  	return _primitiveCache.get(name);
  }
}
