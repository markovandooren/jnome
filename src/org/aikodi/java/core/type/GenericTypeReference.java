package org.aikodi.java.core.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.element.ElementImpl;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.reference.UnresolvableCrossReference;
import org.aikodi.chameleon.core.validation.Valid;
import org.aikodi.chameleon.core.validation.Verification;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.util.association.Multi;
import org.aikodi.chameleon.util.association.Single;
import org.aikodi.java.core.language.Java7;

public class GenericTypeReference extends ElementImpl implements JavaTypeReference {

	public GenericTypeReference(BasicJavaTypeReference target,List<TypeArgument> arguments) {
		setTarget(target);
		addAllArguments(arguments);
	}
	
  public List<TypeArgument> typeArguments() {
  	return _genericParameters.getOtherEnds();
  }
  
  public void addArgument(TypeArgument arg) {
  	add(_genericParameters,arg);
  }
  
  public void addAllArguments(List<TypeArgument> args) {
  	for(TypeArgument argument : args) {
  		addArgument(argument);
  	}
  }
  
  public void removeArgument(TypeArgument arg) {
  	remove(_genericParameters,arg);
  }
  
  private Multi<TypeArgument> _genericParameters = new Multi<TypeArgument>(this);

  private Single<BasicJavaTypeReference> _target = new Single<BasicJavaTypeReference>(this, true, "target");

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
				List<TypeArgument> typeArguments = typeArguments();
				Java7 language = language(Java7.class);
				if (typeArguments.size() > 0) {
					result = language.createDerivedType(type, typeArguments);
//					result = DerivedType.create(type, typeArguments);
					
					// This is going to give trouble if there is a special lexical context
					// selection for 'type' in its parent.
					// set to the type itself? seems dangerous as well.
					result.setUniParent(type.lexical().parent());
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

	public String toString() {
		return toString(new HashSet<>());
	}
	
	@Override
	public String toString(Set<Element> visited) {
		StringBuilder result = new StringBuilder();
		result.append(target().toString(visited));
		result.append('<');
		List<TypeArgument> args = typeArguments();
		int size = args.size();
		for(int i=0; i<size;i++) {
			result.append(args.get(i).toString(visited));
			if(i < size - 1) {
				result.append(',');
			}
		}
		result.append('>');
		return result.toString();
	}

}
