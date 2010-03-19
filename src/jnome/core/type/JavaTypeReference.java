package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

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
import chameleon.core.namespace.RootNamespace;
import chameleon.core.reference.CrossReference;
import chameleon.core.type.ConstructedType;
import chameleon.core.type.DerivedType;
import chameleon.core.type.RegularType;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.TypeConstraint;
import chameleon.core.type.generics.TypeConstraintWithReferences;
import chameleon.core.type.generics.TypeParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;

/**
 * A class for Java type references. They add support for array types and generic parameters.
 * 
 * @author Marko van Dooren
 */
public class JavaTypeReference extends TypeReference {

  public JavaTypeReference(String fqn) {
    this(fqn,0);
  }
  
  public JavaTypeReference(CrossReference<?,?,? extends TargetDeclaration> target, String name) {
  	super(target,name);
  }
  
  public JavaTypeReference(CrossReference<?,?,? extends TargetDeclaration> target, SimpleNameSignature signature) {
  	super(target,signature);
  }
  
  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public JavaTypeReference(NamedTarget target) {
  	super(target.getTarget() == null ? null : new NamespaceOrTypeReference((NamedTarget)target.getTarget()),target.getName());
  }
  
  public JavaTypeReference(String fqn, int arrayDimension) {
  	super(fqn);
  	if(Config.DEBUG) {
  		if((fqn != null) && (fqn.contains("["))) {
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
  
  private OrderedMultiAssociation<JavaTypeReference,ActualTypeArgument> _genericParameters = new OrderedMultiAssociation<JavaTypeReference, ActualTypeArgument>(this);
  
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
  
  protected <X extends Declaration> X getElement(DeclarationSelector<X> selector) throws LookupException {
    X result = null;

	  boolean realSelector = selector.equals(selector());
	  if(realSelector) {
	    result = (X) getCache();
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
      // ARRAY TYPE
      if ((arrayDimension() != 0) && (result != null)) {
        result = (X) new ArrayType(((Type)result),arrayDimension());
      }
    }
    
    if(result != null) {
    	if(realSelector) {
    		// This will flush the cache if the result is derived, for example, if it is an array type or generic instance
        setCache((Type)result);
    	}
      return result;
    } else {
      throw new LookupException("Result of type reference lookup is null: "+signature(),this);
    }
  }

  public Type erasure() throws LookupException {
    Type result = null;

    result = super.getElement(selector());
    
    if(result != null) {
      return result;
    } else {
      throw new LookupException("Result of type reference lookup is null: "+signature(),this);
    }
  }

  
  private Type convertGenerics(Type type) throws LookupException {
  	Type result = type;
		if (type != null) {
			List<ActualTypeArgument> typeArguments = typeArguments();
			if (typeArguments.size() > 0) {
				result = new DerivedType(type, typeArguments);
				// This is going to give trouble if there is a special lexical context
				// selection for 'type' in its parent.
				// set to the type itself? seems dangerous as well.
				result.setUniParent(type.parent());
			} else if(type instanceof RegularType){
				result = erasure(type);
			}
		}
		return result;
	}

  private Type erasure(Type original) throws LookupException {
  	String fullyQualifiedName = original.getFullyQualifiedName();
  	Type result;
  	if(original instanceof ArrayType) {
  		ArrayType arrayType = (ArrayType) original;
  		result = new ArrayType(erasure(arrayType.componentType()), arrayType.dimension());
  	} else if(original instanceof ConstructedType){
  		FormalTypeParameter formal = ((ConstructedType)original).parameter();
  		List<TypeConstraint> constraints = formal.constraints();
  		if(constraints.size() > 0) {
  			TypeConstraint first = constraints.get(0);
  			if(first instanceof TypeConstraintWithReferences<?>) {
  			  result = ((JavaTypeReference)((TypeConstraintWithReferences<?>)first).typeReferences().get(0)).erasure();	
  			} else {
  				throw new ChameleonProgrammerException("The type constraint of type "+first.getClass().getName()+" is not a valid Java element");
  			}
  		} else {
  			result = language(ObjectOrientedLanguage.class).getDefaultSuperClass();
  		}
  	} 
  	else {
  		// Regular TYPE
			List<TypeParameter> parameters = original.parameters();
			int size = parameters.size();
			if(size > 0 && (parameters.get(0) instanceof FormalTypeParameter)) {
				List<ActualTypeArgument> args = new ArrayList<ActualTypeArgument>(size);
				String defaultSuperClassFQN = language(ObjectOrientedLanguage.class).getDefaultSuperClassFQN();
				RootNamespace defaultNamespace = original.language().defaultNamespace();
				for(int i=0; i<size;i++) {
					// FIXME is this where they mean left-most bound ? and is |G| applying erasure to the body of G where
					// references to type parameters are replaced by the left-most bound?
					BasicTypeArgument argument = new BasicTypeArgument(new JavaTypeReference(defaultSuperClassFQN));
					argument.setUniParent(defaultNamespace);
					args.add(argument);
				}
				result = new DerivedType(original, args);
				result.setUniParent(original.parent());
			} else {
  			result = original;
  		}
  	}
  	return result;
  }
  
  public JavaTypeReference clone() {
  	JavaTypeReference result =  new JavaTypeReference((getTarget() == null ? null : getTarget().clone()),(SimpleNameSignature)signature().clone());
  	result.setArrayDimension(arrayDimension());
  	for(ActualTypeArgument typeArgument: typeArguments()) {
  		result.addArgument(typeArgument.clone());
  	}
  	return result;
  }

	public void addArrayDimension(int arrayDimension) {
		if(arrayDimension > Integer.MAX_VALUE - _arrayDimension) {
			throw new ChameleonProgrammerException("Overflow of array dimension. Current value: "+_arrayDimension+" trying to add: "+arrayDimension);
		}
		if(_arrayDimension  < -arrayDimension) {
			throw new ChameleonProgrammerException("Trying to give a negative array dimension to a type reference.  Current value: "+_arrayDimension+" trying to add: "+arrayDimension);
		}
		_arrayDimension += arrayDimension;
	}

	public void decreaseArrayDimension(int arrayDimension) {
		if(- arrayDimension > Integer.MAX_VALUE - _arrayDimension) {
			throw new ChameleonProgrammerException("Overflow of array dimension. Current value: "+_arrayDimension+" trying to subtract: "+arrayDimension);
		}
		if(_arrayDimension < arrayDimension) {
			throw new ChameleonProgrammerException("Trying to give a negative array dimension to a type reference.  Current value: "+_arrayDimension+" trying to subtract: "+arrayDimension);
		}
		_arrayDimension -= arrayDimension;
	}

}
