package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.UnresolvableCrossReference;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.IntersectionTypeReference;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.oo.type.generics.ActualTypeArgument;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;

import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

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
	protected GenericTypeReference cloneSelf() {
		return new GenericTypeReference(null, Collections.EMPTY_LIST);
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
  	  result = new ArrayTypeReference(clone(this), dimension);
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
		return language(ObjectOrientedLanguage.class).createIntersectionReference(clone(this), clone(other));
	}

	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference other) {
		IntersectionTypeReference result = clone(other);
		result.add(clone(this));
		return result;
	}

	@Override
	public Verification verifySelf() {
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
				Java7 language = language(Java7.class);
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
