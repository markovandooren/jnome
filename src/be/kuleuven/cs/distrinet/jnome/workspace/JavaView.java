package be.kuleuven.cs.distrinet.jnome.workspace;

import java.util.HashMap;
import java.util.Map;

import org.aikodi.chameleon.core.language.Language;
import org.aikodi.chameleon.core.namespace.RootNamespace;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.view.ObjectOrientedView;

import be.kuleuven.cs.distrinet.jnome.core.type.DirectJavaTypeReference;

public class JavaView extends ObjectOrientedView {

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
  
  public Type topLevelType() {
  	return _topLevelType;
  }
  
  public void setTopLevelType(Type type) {
  	_topLevelType = type;
  }
  
  private Type _topLevelType;
  
  public TypeReference primitiveTypeReference(String name) {
    return new DirectJavaTypeReference(primitiveType(name));
  }
}
