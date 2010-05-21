package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import jnome.core.expression.invocation.ConstructorInvocation;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.SafePredicate;

import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.member.Member;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.util.Util;

public class AnonymousInnerClass extends RegularType {

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

	public List<Member> localMembers() throws LookupException {
		List<Member> result = super.localMembers();
  	List<NormalMethod> superMembers = implicitConstructors();
    result.addAll(superMembers);
		return result;
	}

	private List<NormalMethod> implicitConstructors() throws LookupException {
		TypeReference tref = typeReference();
 	  Type writtenType = tref.getType();
	  List<NormalMethod> superMembers = writtenType.localMembers(NormalMethod.class);
	  new SafePredicate<NormalMethod>() {
		  @Override
		  public boolean eval(NormalMethod object) {
			  return object.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR) == Ternary.TRUE;
		  }
	  }.filter(superMembers);
		return superMembers;
	}
	
	public <D extends Member> List<D> localMembers(DeclarationSelector<D> selector) throws LookupException {
		List<D> result = super.localMembers(selector);
  	List<NormalMethod> superMembers = implicitConstructors();
    result.addAll(selector.selection(superMembers));
		return result;
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
		Util.addNonNull(parameterBlock(), result);
		Util.addNonNull(body(), result);
		return result;
	}


	@Override
	protected void copyContents(Type from, boolean link) {
		copyEverythingExceptionInheritanceRelations(from,link);
	}

	@Override
	protected AnonymousInnerClass cloneThis() {
		return new AnonymousInnerClass(invocation());
	}
	
	
}
