package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.reference.UnresolvableCrossReference;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.IntersectionTypeReference;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.util.Util;

public class GenericTypeReference extends NamespaceElementImpl<GenericTypeReference> implements JavaTypeReference<GenericTypeReference> {

	public GenericTypeReference(BasicJavaTypeReference target,List<ActualTypeArgument> arguments) {
		setTarget(target);
		addAllArguments(arguments);
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
  
  private OrderedMultiAssociation<GenericTypeReference,ActualTypeArgument> _genericParameters = new OrderedMultiAssociation<GenericTypeReference, ActualTypeArgument>(this);

  private SingleAssociation<GenericTypeReference,BasicJavaTypeReference> _target = new SingleAssociation<GenericTypeReference,BasicJavaTypeReference>(this);

  public BasicJavaTypeReference target() {
  	return _target.getOtherEnd();
  }
  
  public void setTarget(BasicJavaTypeReference target) {
  	setAsParent(_target, target);
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

	public TypeReference intersectionDoubleDispatch(IntersectionTypeReference<?> other) {
		IntersectionTypeReference<?> result = other.clone();
		result.add(clone());
		return result;
	}

	public List<? extends Element> children() {
		List<Element> result = Util.createNonNullList(target());
		result.addAll(typeArguments());
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

//	@Override
//	public void setName(String name) {
//		target().setName(name);
//	}

}
