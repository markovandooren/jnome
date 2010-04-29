package jnome.core.type;

import java.util.List;

import jnome.core.language.Java;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.declaration.TargetDeclaration;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceOrTypeReference;
import chameleon.core.reference.CrossReference;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;

public class BasicJavaTypeReference extends BasicTypeReference<BasicJavaTypeReference> implements JavaTypeReference<BasicJavaTypeReference> {

	public BasicJavaTypeReference(CrossReference<?,?,? extends TargetDeclaration> target, String name) {
  	super(target,name);
  }
  
  public BasicJavaTypeReference(CrossReference<?,?,? extends TargetDeclaration> target, SimpleNameSignature signature) {
  	super(target,signature);
  }
  
  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public BasicJavaTypeReference(NamedTarget target) {
  	super(target.getTarget() == null ? null : new NamespaceOrTypeReference((NamedTarget)target.getTarget()),target.getName());
  }
  
  public BasicJavaTypeReference(String fqn) {
  	super(fqn);
  	if(Config.DEBUG) {
  		if((fqn != null) && (fqn.contains("["))) {
  			throw new ChameleonProgrammerException("Initializing a type reference with a [ in the name.");
  		}
  	}
  }
  
  public List<ActualTypeArgument> typeArguments() {
  	return _genericParameters.getOtherEnds();
  }
  
  public void addArgument(ActualTypeArgument arg) {
  	if(arg != null) {
  		_genericParameters.add(arg.parentLink());
  	}
  }
  
  public void addAllArguments(List<ActualTypeArgument> args) {
  	for(ActualTypeArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(ActualTypeArgument arg) {
  	if(arg != null) {
  		_genericParameters.remove(arg.parentLink());
  	}
  }
  
  private OrderedMultiAssociation<JavaTypeReference,ActualTypeArgument> _genericParameters = new OrderedMultiAssociation<JavaTypeReference, ActualTypeArgument>(this);
  
  public List<Element> children() {
  	List<Element> result = super.children();
  	result.addAll(_genericParameters.getOtherEnds());
  	return result;
  }
  
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
  
  protected <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
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

//  public Type erasure() throws LookupException {
//    Type result = null;
//
//    result = super.getElement(selector());
//    
//    if(result != null) {
//      return result;
//    } else {
//      throw new LookupException("Result of type reference lookup is null: "+signature(),this);
//    }
//  }

  
  private Type convertGenerics(Type type) throws LookupException {
  	Type result = type;
		if (type != null) {
			if(! (type instanceof RawType)) {
				List<ActualTypeArgument> typeArguments = typeArguments();
				if (typeArguments.size() > 0) {
					result = new DerivedType(type, typeArguments);
					// This is going to give trouble if there is a special lexical context
					// selection for 'type' in its parent.
					// set to the type itself? seems dangerous as well.
					result.setUniParent(type.parent());
				} else if(type instanceof RegularType){
					// create raw type if necessary. The erasure method will check that.
					result = language(Java.class).erasure(type);
				}
			}
		}
		return result;
	}

  public BasicJavaTypeReference clone() {
  	BasicJavaTypeReference result =  new BasicJavaTypeReference((getTarget() == null ? null : getTarget().clone()),(SimpleNameSignature)signature().clone());
  	for(ActualTypeArgument typeArgument: typeArguments()) {
  		result.addArgument(typeArgument.clone());
  	}
  	return result;
  }

	public JavaTypeReference erasedReference() {
	  CrossReference<?, ?, ? extends TargetDeclaration> erasure = language(Java.class).erasure(getTarget());
		JavaTypeReference result = new BasicJavaTypeReference(erasure, (SimpleNameSignature)signature().clone());
	  return result;
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}

	protected Type typeConstructor() throws LookupException {
		return super.getElement(selector());
	}
	
  private Type _genericCache;
  
  @Override
  public void flushLocalCache() {
  	super.flushLocalCache();
  	_genericCache = null;
  }
  
  protected Type getGenericCache() {
  	if(Config.cacheElementReferences() == true) {
  	  return _genericCache;
  	} else {
  		return null;
  	}
  }
  
  protected void setGenericCache(Type value) {
    	if(Config.cacheElementReferences() == true) {
    		_genericCache = value;
    	}
  }

}
