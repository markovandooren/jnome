package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import jnome.core.expression.invocation.ConstructorInvocation;


import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.method.Method;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.util.Util;

public class AnonymousInnerClass extends AnonymousType {

	public AnonymousInnerClass(ConstructorInvocation invocation) {
		super("TODO");
		_invocation = invocation;
	}
	
	private ConstructorInvocation _invocation;
	
	public ConstructorInvocation invocation() {
		return _invocation;
	}
	
	public TypeReference typeReference() {
		return nearestAncestor(ConstructorInvocation.class).getTypeReference();
	}

	@Override
	public List<InheritanceRelation> inheritanceRelations() {
		List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
		SubtypeRelation subtypeRelation = new SubtypeRelation(typeReference().clone());
		subtypeRelation.setUniParent(this);
		result.add(subtypeRelation);
		return result;
	}

	public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
  	if(element instanceof SubtypeRelation) {
  		Element parent = parent();
  		if(parent != null) {
  			return lexicalParametersLookupStrategy();
//  		  return parent().lexicalContext(this);
  		} else {
  			throw new LookupException("Parent of type is null when looking for the parent context of a type.");
  		}
  	} else {
  	  return super.lexicalLookupStrategy(element);
  	}
  }

	@Override
	public List<Element> children() {
		List<Element> result = (List)modifiers();
    Util.addNonNull(signature(), result);
		Util.addNonNull(parameterBlock(TypeParameter.class), result);
		Util.addNonNull(body(), result);
		return result;
	}


	@Override
	protected void copyContents(Type from, boolean link) {
		copyEverythingExceptInheritanceRelations(from,link);
	}

	@Override
	protected AnonymousInnerClass cloneThis() {
		return new AnonymousInnerClass(invocation());
	}
	
	
}
