package org.aikodi.java.core.type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.lang.ref.SoftReference;

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
import org.aikodi.java.core.language.Java7;

import com.google.common.collect.ImmutableSet;

public class BasicJavaTypeReference extends org.aikodi.chameleon.oo.type.generics.GenericTypeReference implements JavaTypeReference {

	public BasicJavaTypeReference(CrossReferenceTarget target, String name) {
  	super(target,name);
  }

  /**
   * THIS ONLY WORKS WHEN THE NAMED TARGET CONSISTS ENTIRELY OF NAMEDTARGETS.
   * @param target
   */
  public BasicJavaTypeReference(NamedTarget target) {
  	this(target.getTarget() == null ? null : typeReferenceTarget((NamedTarget) target.getTarget(), typeReferenceTargetTypes()), target.name());
  }
  
  public BasicJavaTypeReference(String fqn) {
  	this(typeReferenceTarget(Util.getAllButLastPart(fqn), typeReferenceTargetTypes()),Util.getLastPart(fqn));
  }
  
  public BasicJavaTypeReference(String fqn, Set<? extends Class<? extends Declaration>> classes) {
  	this(typeReferenceTarget(Util.getAllButLastPart(fqn),set(classes)),Util.getLastPart(fqn));
  }
  
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
  
	@Override
	protected boolean canBeGenericType(Type type) {
		return ! (type instanceof RawType);
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
