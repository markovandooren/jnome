package be.kuleuven.cs.distrinet.jnome.core.type;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.chameleon.core.Config;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.TargetDeclaration;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceTarget;
import be.kuleuven.cs.distrinet.chameleon.core.reference.CrossReferenceWithName;
import be.kuleuven.cs.distrinet.chameleon.core.reference.MultiTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NamedTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.type.BasicTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.util.CreationStackTrace;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;

public class BasicJavaTypeReference extends BasicTypeReference implements JavaTypeReference, CrossReferenceWithName<Type> {

	public BasicJavaTypeReference(CrossReferenceTarget target, String name) {
  	super(target,name);
  }
  public BasicJavaTypeReference(CrossReferenceTarget target, SimpleNameSignature signature) {
  	super(target,signature);
  }
  
  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public BasicJavaTypeReference(NamedTarget target) {
  	super(target.getTarget() == null ? null : typeReferenceTarget((NamedTarget) target.getTarget()), target.name());
  }
  
  public BasicJavaTypeReference(String fqn) {
  	super(typeReferenceTarget(Util.getAllButLastPart(fqn)),Util.getLastPart(fqn));
  }
  
  protected static CrossReferenceTarget typeReferenceTarget(NamedTarget target) {
  	if(target == null) {
  		return null;
  	} else {
  		CrossReferenceTarget t = target.getTarget();
  		if(t == null) {
  			return new MultiTypeReference<Declaration>(target.name(), _typeReferenceTargetTypes);
  		} else {
  			return new MultiTypeReference<Declaration>(typeReferenceTarget((NamedTarget) t),target.name(), _typeReferenceTargetTypes);
  		}
  	}
  }
  
  public static CrossReferenceTarget typeReferenceTarget(String fqn) {
		return fqn == null ? null : new MultiTypeReference<Declaration>(fqn, _typeReferenceTargetTypes);
  }
  
	private static Set _typeReferenceTargetTypes = ImmutableSet.<Class>builder().add(Type.class).add(Namespace.class).build();

//  @Override
//  protected void notifyParentRemoved(Element element) {
//  	_trace = new CreationStackTrace();
//  }
//  
//  @Override
//  protected void notifyParentSet(Element element) {
//  	_trace = new CreationStackTrace();
//  }
  
  public List<ActualTypeArgument> typeArguments() {
  	return _typeArguments.getOtherEnds();
  }
  
  public void addArgument(ActualTypeArgument arg) {
  	add(_typeArguments,arg);
  }
  
  public void addAllArguments(List<ActualTypeArgument> args) {
  	for(ActualTypeArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(ActualTypeArgument arg) {
  	remove(_typeArguments,arg);
  }
  
  private Multi<ActualTypeArgument> _typeArguments = new Multi<ActualTypeArgument>(this);
  
  public JavaTypeReference toArray(int arrayDimension) {
  	JavaTypeReference result;
  	if(arrayDimension > 0) {
  	  result = new ArrayTypeReference(clone(this), arrayDimension);
  	} else {
  		result = this;
  	}
  	return result;
  }

  public int arrayDimension() {
  	return 0;
  }
  
  public <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
    X result = null;

	  boolean realSelector = selector.equals(selector());
	  if(realSelector) {
	    result = (X) getGenericCache();
	  }
	  if(result != null) {
	   	return result;
	  }

    result = super.getElement(selector);
     
    if(realSelector) {
    	//First cast result to Type, then back to X.
    	//Because the selector is the connected selector of this Java type reference,
    	//we know that result is a Type.
      // FILL IN GENERIC PARAMETERS
      result = (X) convertGenerics((Type)result);
    }
    
    if(result != null) {
    	if(realSelector) {
        setGenericCache((Type)result);
    	}
      return result;
    } else {
      throw new LookupException("Result of type reference lookup is null: "+signature(),this);
    }
  }

  private Type convertGenerics(Type type) throws LookupException {
  	Type result = type;
		if (type != null) {
			if(! (type instanceof RawType)) {
				Java language = language(Java.class);
				
				//Does not work because there is no distinction yet between a diamond empty list and a non-diamond empty list.
//				if(type.nbTypeParameters(TypeParameter.class) > 0) {
				
				if (hasTypeArguments()) {
					result = language.createDerivedType(type, typeArguments());

					// This is going to give trouble if there is a special lexical context
					// selection for 'type' in its parent.
					// set to the type itself? seems dangerous as well.
					result.setUniParent(type.parent());
				} else if(type instanceof RegularType){
					// create raw type if necessary. The erasure method will check that.
					result = language.erasure(type);
				}
			}
		}
		return result;
	}

  public boolean hasTypeArguments() {
  	return _typeArguments.size() > 0;
  }
  
  public BasicJavaTypeReference cloneSelf() {
  	return new BasicJavaTypeReference( null ,(SimpleNameSignature)null);
  }

	@SuppressWarnings("unchecked")
	public JavaTypeReference erasedReference() {
		JavaTypeReference result = null;
	  CrossReferenceTarget target = getTarget();
	  if(target instanceof CrossReference) {
	  	CrossReference<? extends TargetDeclaration> erasure = language(Java.class).erasure((CrossReference)target);
	  	result = new BasicJavaTypeReference(erasure, (SimpleNameSignature)signature().clone());
	  } else if (target == null) {
	  	result = new BasicJavaTypeReference(null, (SimpleNameSignature)signature().clone());
	  }
	  return result;
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}

	protected Type typeConstructor() throws LookupException {
		return super.getElement(selector());
	}
	
  private SoftReference<Type> _genericCache;
  
  @Override
  public synchronized void flushLocalCache() {
  	super.flushLocalCache();
  	_genericCache = null;
  }
  
  protected synchronized Type getGenericCache() {
  	Type result = null;
  	if(Config.cacheElementReferences() == true) {
  	  result = (_genericCache == null ? null : _genericCache.get());
  	}
  	return result;
  }
  
  protected synchronized void setGenericCache(Type value) {
    	if(Config.cacheElementReferences() == true) {
    		_genericCache = new SoftReference<Type>(value);
    	}
  }
  
  public String toString() {
  	StringBuffer result = new StringBuffer(super.toString());
  	List<ActualTypeArgument> arguments = typeArguments();
  	if(! arguments.isEmpty()) {
  		result.append('<');
  		int size = arguments.size();
  		for(int i =0; i<size;i++) {
  			result.append(arguments.get(i).toString());
  			if(i<size-1) {
  				result.append(',');
  			}
  		}
  		result.append('>');
  	}
  	return result.toString();
  }

}
