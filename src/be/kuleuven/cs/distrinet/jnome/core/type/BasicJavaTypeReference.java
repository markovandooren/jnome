package be.kuleuven.cs.distrinet.jnome.core.type;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.Config;
import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.namespace.Namespace;
import org.aikodi.chameleon.core.reference.CrossReference;
import org.aikodi.chameleon.core.reference.CrossReferenceTarget;
import org.aikodi.chameleon.core.reference.CrossReferenceWithName;
import org.aikodi.chameleon.core.reference.MultiTypeReference;
import org.aikodi.chameleon.oo.expression.NamedTarget;
import org.aikodi.chameleon.oo.type.BasicTypeReference;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.util.Util;
import org.aikodi.chameleon.util.association.Multi;

import com.google.common.collect.ImmutableSet;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class BasicJavaTypeReference extends BasicTypeReference implements JavaTypeReference, CrossReferenceWithName<Type> {

	public BasicJavaTypeReference(CrossReferenceTarget target, String name) {
  	super(target,name);
  }
  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public BasicJavaTypeReference(NamedTarget target) {
  	this(target.getTarget() == null ? null : typeReferenceTarget((NamedTarget) target.getTarget()), target.name());
  }
  
  public BasicJavaTypeReference(String fqn) {
  	this(typeReferenceTarget(Util.getAllButLastPart(fqn)),Util.getLastPart(fqn));
  }
  
  protected static CrossReferenceTarget typeReferenceTarget(NamedTarget target) {
  	if(target == null) {
  		return null;
  	} else {
  		CrossReferenceTarget t = target.getTarget();
  		if(t == null) {
  			return new MultiTypeReference<Declaration>(target.name(), _typeReferenceTargetTypes,Declaration.class,Declaration.class);
  		} else {
  			return new MultiTypeReference<Declaration>(typeReferenceTarget((NamedTarget) t),target.name(), _typeReferenceTargetTypes,Declaration.class);
  		}
  	}
  }
  
  public static CrossReferenceTarget typeReferenceTarget(String fqn) {
		return fqn == null ? null : new MultiTypeReference<Declaration>(fqn, _typeReferenceTargetTypes,Declaration.class,Declaration.class);
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
  
  public List<TypeArgument> typeArguments() {
  	return _typeArguments.getOtherEnds();
  }
  
  public void addArgument(TypeArgument arg) {
  	add(_typeArguments,arg);
  }
  
  public void addAllArguments(List<TypeArgument> args) {
  	for(TypeArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(TypeArgument arg) {
  	remove(_typeArguments,arg);
  }
  
  private Multi<TypeArgument> _typeArguments = new Multi<TypeArgument>(this);
  {
  	_typeArguments.enableCache();
  }
  
  public int arrayDimension() {
  	return 0;
  }
  
  @Override
  public Type getElement() throws LookupException {
    Type result = getGenericCache();
    if(result != null) {
      return result;
    }
    synchronized(this) {
      if(result != null) {
        return result;
      }

      result = super.getElement();

      //First cast result to Type, then back to X.
      //Because the selector is the connected selector of this Java type reference,
      //we know that result is a Type.
      // FILL IN GENERIC PARAMETERS
      result =convertGenerics((Type)result);

//      if(result != null) {
        setGenericCache((Type)result);
        return result;
//      } else {
//        throw new LookupException("Result of type reference lookup is null: "+name(),this);
//      }
    }
  }

  private Type convertGenerics(Type type) throws LookupException {
  	Type result = type;
//		if (type != null) {
			if(! (type instanceof RawType)) {
				Java7 language = language(Java7.class);
				
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
//		}
		return result;
	}

  public boolean hasTypeArguments() {
  	return _typeArguments.size() > 0;
  }
  
  public BasicJavaTypeReference cloneSelf() {
  	return new BasicJavaTypeReference( null ,name());
  }

	@SuppressWarnings("unchecked")
	public JavaTypeReference erasedReference() {
		JavaTypeReference result = null;
	  CrossReferenceTarget target = getTarget();
	  if(target instanceof CrossReference) {
	  	CrossReference<? extends Declaration> erasure = language(Java7.class).erasure((CrossReference)target);
	  	result = new BasicJavaTypeReference(erasure, name());
	  } else if (target == null) {
	  	result = new BasicJavaTypeReference(null, name());
	  }
	  return result;
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}

	protected Type typeConstructor() throws LookupException {
	  //FIXME Document why this method skips the overwritten version of getElement(DeclarationSelector)!
		return super.getElement();
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
  
  public String toString(Set<Element> visited) {
  	StringBuffer result = new StringBuffer(super.toString(visited));
  	List<TypeArgument> arguments = typeArguments();
  	if(! arguments.isEmpty()) {
  		result.append('<');
  		int size = arguments.size();
  		for(int i =0; i<size;i++) {
  			result.append(arguments.get(i).toString(visited));
  			if(i<size-1) {
  				result.append(',');
  			}
  		}
  		result.append('>');
  	}
  	return result.toString();
  }

}
