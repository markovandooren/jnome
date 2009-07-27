package jnome.core.type;

import java.util.Iterator;
import java.util.List;

import org.rejuse.association.OrderedReferenceSet;

import chameleon.core.Config;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTarget;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceOrType;
import chameleon.core.namespace.NamespaceOrTypeReference;
import chameleon.core.type.DerivedType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.TypeParameter;
import chameleon.core.type.generics.InstantiatedTypeParameter;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public class JavaTypeReference extends TypeReference {

  public JavaTypeReference(String name) {
    this(name,0);
  }
  
  public JavaTypeReference(NamespaceOrTypeReference target, String name) {
  	super(target,name);
  }
  
  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public JavaTypeReference(NamedTarget target) {
  	super(target.getTarget() == null ? null : new JavaTypeReference((NamedTarget)target.getTarget()),target.getName());
  }
  
  public JavaTypeReference(String name, int arrayDimension) {
  	super(name);
  	if(Config.DEBUG) {
  		if((name != null) && (name.contains("["))) {
  			throw new ChameleonProgrammerException("Initializing a type reference with a [ in the name.");
  		}
  	}
  	setArrayDimension(arrayDimension);
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
  
  private OrderedReferenceSet<JavaTypeReference,ActualTypeArgument> _genericParameters = new OrderedReferenceSet<JavaTypeReference, ActualTypeArgument>(this);
  
  public List<Element> children() {
  	List<Element> result = super.children();
  	result.addAll(_genericParameters.getOtherEnds());
  	return result;
  }
  
  public JavaTypeReference toArray(int arrayDimension) {
  	JavaTypeReference result = clone();
  	result.setArrayDimension(arrayDimension);
  	return result;
  }

  private int _arrayDimension;
  
  public int arrayDimension() {
  	return _arrayDimension;
  }
  
  public void setArrayDimension(int arrayDimension) {
  	_arrayDimension = arrayDimension;
  }
  
  public Type getType() throws LookupException {
    Type result = null;

    result = getCache();
    if (result != null) {
      return result;
    }

    if (getTarget() != null) {
      NamespaceOrType target = getTarget().getNamespaceOrType();
      if (target != null) {
        result = target.targetContext().lookUp(selector());// findType(getName());
      }
    } else {
      result = parent().lexicalLookupStrategy(this).lookUp(selector()); // (getName());
    }
    
    // FILL IN GENERIC PARAMETERS
    result = fillInTypeArguments(result);
    
    
    // ARRAY TYPE
    if ((arrayDimension() != 0) && (result != null)) {
      result = new ArrayType(result,arrayDimension());
    }

    if (result != null) {
      setCache(result);
      return result;
    } else {
    	// REPEAT FOR DEBUGGING
      if (getTarget() != null) {
        NamespaceOrType target = getTarget().getNamespaceOrType();
        if (target != null) {
          result = target.targetContext().lookUp(selector());// findType(getName());
        }
      } else {
        result = parent().lexicalLookupStrategy(this).lookUp(selector()); // (getName());
      }
      throw new LookupException("Result of type reference lookup is null: "+getFullyQualifiedName(),this);
    }
  }

  
  private Type fillInTypeArguments(Type type) throws LookupException {
  	Type result = type;
  	List<ActualTypeArgument> typeArguments = typeArguments();
  	if(typeArguments.size() > 0) {
  	result = new DerivedType(type);
  	// This is going to give trouble if there is a special lexical context selection for 'type' in its parent.
  	// set to the type itself? seems dangerous as well.
  	result.setUniParent(type.parent());
		List<TypeParameter> parameters = result.parameters();
		Iterator<TypeParameter> parametersIterator = parameters.iterator();
		Iterator<ActualTypeArgument> argumentsIterator = typeArguments.iterator();
		while(parametersIterator.hasNext()) {
			TypeParameter parameter = parametersIterator.next();
			ActualTypeArgument argument = argumentsIterator.next();
			InstantiatedTypeParameter instantiated = new InstantiatedTypeParameter(parameter.signature().clone(),argument);
			result.replaceParameter(parameter,instantiated);
		}
  	}
		return result;
	}

//  public Type getType() throws LookupException {
//  	Type result = null;
//  	
//    result = getCache();
//    if(result != null) {
//    	return result;
//    }
//
//    if (getArrayDimension() == 0) {
//      if(getTarget() == null) {
//        result = getParent().getContext(this).findType(getName());
//      }
//      else {
//    	  NamespaceOrType target = getTarget().getNamespaceOrType();
//        if(target != null) {
//          result = target.getTargetContext().findType(getName());
//        }
//      }
//    }
//    else {
//      if(getTarget() == null) {
//        result = new ArrayType((Type)getParent().getContext(this).findType(getComponentName()), getArrayDimension());
//      }
//      else {
//    	  NamespaceOrType target = getTarget().getNamespaceOrType();
//        if(target != null) {
//          result = new ArrayType((Type)target.getTargetContext().findType(getComponentName()), getArrayDimension());
//        } 
//      }
//    }
//    setCache(result);
//    return result;
//  }


  public JavaTypeReference clone() {
  	NamespaceOrTypeReference target = getTarget();
  	NamespaceOrTypeReference clone = (target == null ? null : target.clone());
  	JavaTypeReference result =  new JavaTypeReference(clone,getName());
  	result.setArrayDimension(arrayDimension());
  	for(ActualTypeArgument typeArgument: typeArguments()) {
  		result.addArgument(typeArgument.clone());
  	}
  	return result;
  }

	public void addArrayDimension(int arrayDimension) {
		_arrayDimension += arrayDimension;
	}
  
}
