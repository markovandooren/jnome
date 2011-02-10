package jnome.core.type;

import java.util.List;

import jnome.core.language.Java;

import org.rejuse.logic.ternary.Ternary;
import org.rejuse.predicate.SafePredicate;

import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.modifier.Constructor;
import chameleon.support.modifier.Public;

public abstract class AnonymousType extends RegularType {

	public AnonymousType(SimpleNameSignature sig) {
		super(sig);
	}
	
	public AnonymousType(String name) {
		super(name);
	}

	public List<Member> localMembers() throws LookupException {
		List<Member> result = super.localMembers();
		List<NormalMethod> superMembers = implicitConstructors();
	  result.addAll(superMembers);
		return result;
	}
	
	public abstract TypeReference typeReference();

	public List<NormalMethod> implicitConstructors() throws LookupException {
		TypeReference tref = typeReference();
	  Type writtenType = tref.getType();
	  List<NormalMethod> superMembers = writtenType.localMembers(NormalMethod.class);
	  new SafePredicate<NormalMethod>() {
		  @Override
		  public boolean eval(NormalMethod object) {
			  return object.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR) == Ternary.TRUE;
		  }
	  }.filter(superMembers);
	  //if the super type is an interface, there will be no constructor, so we must
	  //create a default constructor.
	  if(superMembers.isEmpty()) {
	  	NormalMethod cons = defaultDefaultConstructor(tref, writtenType);
	  	superMembers.add(cons);
	  }
		return superMembers;
	}

	private NormalMethod defaultDefaultConstructor(TypeReference tref, Type writtenType) {
		NormalMethod cons = language(Java.class).createNormalMethod(new SimpleNameDeclarationWithParametersHeader(writtenType.signature().name()), tref.clone());
		cons.addModifier(new Constructor());
		cons.addModifier(new Public());
		cons.setUniParent(this);
		return cons;
	}

	public <D extends Member> List<D> localMembers(DeclarationSelector<D> selector) throws LookupException {
		List<D> result = super.localMembers(selector);
		List<NormalMethod> superMembers = implicitConstructors();
	  result.addAll(selector.selection(superMembers));
		return result;
	}

}