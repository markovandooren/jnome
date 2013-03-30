package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.Declaration;
import be.kuleuven.cs.distrinet.chameleon.core.element.ElementImpl;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupContext;
import be.kuleuven.cs.distrinet.chameleon.core.reference.UnresolvableCrossReference;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.VerificationResult;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.util.association.Multi;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;

public class GenericTypeReference extends ElementImpl implements JavaTypeReference {

	public GenericTypeReference(BasicJavaTypeReference target,List<ActualTypeArgument> arguments) {
		setTarget(target);
		addAllArguments(arguments);
	}
	
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

  private Single<BasicJavaTypeReference> _target = new Single<BasicJavaTypeReference>(this,true);

  public BasicJavaTypeReference target() {
  	return _target.getOtherEnd();
  }
  
  public void setTarget(BasicJavaTypeReference target) {
  	set(_target, target);
  }
  

	@Override
	public GenericTypeReference clone() {
		List<ActualTypeArgument> args = new ArrayList<ActualTypeArgument>();
		for(ActualTypeArgument arg: typeArguments()) {
			args.add(arg.clone());
		}
		return new GenericTypeReference(target().clone(), args);
	}

	public JavaTypeReference componentTypeReference() {
		return this;
	}

	public JavaTypeReference erasedReference() {
		return target().erasedReference();
	}

	public JavaTypeReference toArray(int dimension) {
  	JavaTypeReference result;
  	if(dimension > 0) {
  	  result = new ArrayTypeReference(clone(), dimension);
  	} else {
  		result = this;
  	}
  	return result;
	}

	public Type getType() throws LookupException {
		return getElement();
	}

	public TypeReference intersection(TypeReference other) {
		return other.intersectionDoubleDispatch(this);
	}

	public TypeReference intersectionDoubleDispatch(TypeReference other) {
		return language(ObjectOrientedLanguage.class).createIntersectionReference(clone(), other.clone());
	}

	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference other) {
		IntersectionTypeReference result = other.clone();
		result.add(clone());
		return result;
	}

	@Override
	public VerificationResult verifySelf() {
		Type referencedElement;
		try {
			referencedElement = getElement();
			if(referencedElement != null) {
				return Valid.create();
			} else {
				return new UnresolvableCrossReference(this);
			}
		} catch (LookupException e) {
			return new UnresolvableCrossReference(this);
		}
	}

	public Type getElement() throws LookupException {
		BasicJavaTypeReference target = target();
		if(target == null) {
			throw new LookupException("Generic type reference does not have a target.");
		} else {
			Type element = target.getElement();
			return convertGenerics(element);
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

	public Declaration getDeclarator() throws LookupException {
		return target().getElement();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(target().toString());
		result.append('<');
		List<ActualTypeArgument> args = typeArguments();
		int size = args.size();
		for(int i=0; i<size;i++) {
			result.append(args.get(i).toString());
			if(i < size - 1) {
				result.append(',');
			}
		}
		result.append('>');
		return result.toString();
	}

	@Override
	public LookupContext targetContext() throws LookupException {
		return getElement().targetContext();
	}
}
