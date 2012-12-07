package jnome.core.type;

import java.lang.ref.SoftReference;
import java.util.List;

import jnome.core.language.Java;
import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.CrossReferenceTarget;
import chameleon.core.reference.CrossReferenceWithName;
import chameleon.oo.expression.NamedTarget;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.util.CreationStackTrace;
import chameleon.util.association.Multi;

public class BasicJavaTypeReference extends BasicTypeReference implements JavaTypeReference, CrossReferenceWithName<Type> {

//	public static boolean TRACE = false;
//	
//	private CreationStackTrace _trace = (TRACE ? new CreationStackTrace() : null);
	
	public BasicJavaTypeReference(CrossReferenceTarget target, String name) {
  	super(target,name);
  	_trace = new CreationStackTrace();
  }
  
  public BasicJavaTypeReference(CrossReferenceTarget target, SimpleNameSignature signature) {
  	super(target,signature);
  	_trace = new CreationStackTrace();
  }
  
  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public BasicJavaTypeReference(NamedTarget target) {
  	super(target.getTarget() == null ? null : target.getTarget(),target.name());
  	_trace = new CreationStackTrace();
  }
  
  public BasicJavaTypeReference(String fqn) {
  	super(fqn);
  	_trace = new CreationStackTrace();
  }
  
//  @Override
//  protected void notifyParentRemoved(Element element) {
//  	_trace = new CreationStackTrace();
//  }
//  
//  @Override
//  protected void notifyParentSet(Element element) {
//  	_trace = new CreationStackTrace();
//  }
  
  private CreationStackTrace _trace;
  
  public List<ActualTypeArgument> typeArguments() {
  	return _genericParameters.getOtherEnds();
  }
  
  public void addArgument(ActualTypeArgument arg) {
  	add(_genericParameters,arg);
  }
  
  public void addAllArguments(List<ActualTypeArgument> args) {
  	for(ActualTypeArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(ActualTypeArgument arg) {
  	remove(_genericParameters,arg);
  }
  
  private Multi<ActualTypeArgument> _genericParameters = new Multi<ActualTypeArgument>(this);
  
  public JavaTypeReference toArray(int arrayDimension) {
  	JavaTypeReference result;
  	if(arrayDimension > 0) {
  	  result = new ArrayTypeReference(clone(), arrayDimension);
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
				List<ActualTypeArgument> typeArguments = typeArguments();
				Java language = language(Java.class);
				if (typeArguments.size() > 0) {
					result = language.createDerivedType(type, typeArguments);
//					result = DerivedType.create(type, typeArguments);
					
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

  public BasicJavaTypeReference clone() {
  	BasicJavaTypeReference result =  new BasicJavaTypeReference((getTarget() == null ? null : getTarget().clone()),(SimpleNameSignature)signature().clone());
  	for(ActualTypeArgument typeArgument: typeArguments()) {
  		ActualTypeArgument clone = typeArgument.clone();
			result.addArgument(clone);
  	}
  	return result;
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
