package jnome.core.type;

import java.util.Iterator;
import java.util.List;

import org.rejuse.association.OrderedReferenceSet;
import org.rejuse.association.Reference;

import chameleon.core.Config;
import chameleon.core.element.ChameleonProgrammerException;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTarget;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceOrType;
import chameleon.core.namespace.NamespaceOrTypeReference;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.GenericArgument;
import chameleon.core.type.generics.GenericParameter;
import chameleon.core.type.generics.InstantiatedGenericParameter;

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
  
  public List<GenericArgument> typeArguments() {
  	return _genericParameters.getOtherEnds();
  }
  
  public void addArgument(GenericArgument arg) {
  	if(arg != null) {
  		_genericParameters.add(arg.parentLink());
  	}
  }
  
  public void addAllArguments(List<GenericArgument> args) {
  	for(GenericArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(GenericArgument arg) {
  	if(arg != null) {
  		_genericParameters.remove(arg.parentLink());
  	}
  }
  
  private OrderedReferenceSet<JavaTypeReference,GenericArgument> _genericParameters = new OrderedReferenceSet<JavaTypeReference, GenericArgument>(this);
  
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
      result = parent().lexicalContext(this).lookUp(selector()); // (getName());
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
        result = parent().lexicalContext(this).lookUp(selector()); // (getName());
      }
      throw new LookupException("Result of type reference lookup is null: "+getFullyQualifiedName(),this);
    }
  }

  
  private Type fillInTypeArguments(Type type) throws LookupException {
  	Type result = type;
  	List<GenericArgument> typeArguments = typeArguments();
  	if(typeArguments.size() > 0) {
  	result = type.clone();
  	// This is going to give trouble if there is a special lexical context selection for 'type' in its parent.
  	// set to the type itself? seems dangerous as well.
  	result.setUniParent(type.parent());
		List<GenericParameter> parameters = result.directlyDeclaredElements(GenericParameter.class);
		Iterator<GenericParameter> parametersIterator = parameters.iterator();
		Iterator<GenericArgument> argumentsIterator = typeArguments.iterator();
		while(parametersIterator.hasNext()) {
			GenericParameter parameter = parametersIterator.next();
			GenericArgument argument = argumentsIterator.next();
			InstantiatedGenericParameter instantiated = new InstantiatedGenericParameter(parameter.signature().clone(),argument.type());
			result.replace(parameter,instantiated);
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
  	for(GenericArgument typeArgument: typeArguments()) {
  		result.addArgument(typeArgument.clone());
  	}
  	return result;
  }

	public void addArrayDimension(int arrayDimension) {
		_arrayDimension += arrayDimension;
	}
  
}
