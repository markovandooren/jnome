package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.DeclarationSelector;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.SelectionResult;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.method.SimpleNameMethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Constructor;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;

public abstract class AnonymousType extends RegularType implements JavaType {

	public AnonymousType(SimpleNameSignature sig) {
		super(sig);
	}
	
	public AnonymousType(String name) {
		super(name);
	}
	
	public AnonymousType() {
		
	}

	public List<Member> localMembers() throws LookupException {
		List<Member> result = super.localMembers();
		List<NormalMethod> superMembers = implicitConstructors();
	  result.addAll(superMembers);
		return result;
	}
	
	@Override
	public PropertySet<Element, ChameleonProperty> inherentProperties() {
		PropertySet<Element, ChameleonProperty> result = new PropertySet<Element, ChameleonProperty>();
		ObjectOrientedLanguage language = language(ObjectOrientedLanguage.class);
		result.add(language.ABSTRACT.inverse());
		result.add(language.CLASS.inverse());
		result.add(language.REFINABLE.inverse());
		return result;
	}
	
	protected abstract TypeReference typeReference();

	protected List<NormalMethod> implicitConstructors() throws LookupException {
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
	  	superMembers.add(defaultDefaultConstructor(tref, writtenType));
	  }
		return superMembers;
	}

	private NormalMethod defaultDefaultConstructor(TypeReference tref, Type writtenType) {
		NormalMethod cons = language().plugin(ObjectOrientedFactory.class).createNormalMethod(new SimpleNameMethodHeader(writtenType.signature().name(), clone(tref)));
		cons.addModifier(new Constructor());
		cons.addModifier(new Public());
		cons.setUniParent(this);
		return cons;
	}

	public <D extends Member> List<? extends SelectionResult> localMembers(DeclarationSelector<D> selector) throws LookupException {
		List<SelectionResult> result = (List)super.localMembers(selector);
		List<NormalMethod> superMembers = implicitConstructors();
	  result.addAll(selector.selection(superMembers));
		return result;
	}

}
