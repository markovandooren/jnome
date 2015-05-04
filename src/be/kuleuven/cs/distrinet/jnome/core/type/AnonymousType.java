package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.List;

import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.member.Member;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeReference;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.modifier.Constructor;
import org.aikodi.chameleon.support.modifier.Public;

import be.kuleuven.cs.distrinet.rejuse.collection.CollectionOperations;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

public abstract class AnonymousType extends RegularType implements JavaType {

	public AnonymousType(String name) {
		super(name);
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
	  Type writtenType = tref.getElement();
	  List<NormalMethod> superMembers = writtenType.localMembers(NormalMethod.class);
	  CollectionOperations.filter(superMembers, m -> m.is(language(ObjectOrientedLanguage.class).CONSTRUCTOR) == Ternary.TRUE);
	  //if the super type is an interface, there will be no constructor, so we must
	  //create a default constructor.
	  if(superMembers.isEmpty()) {
	  	superMembers.add(defaultDefaultConstructor(tref, writtenType));
	  }
		return superMembers;
	}

	private NormalMethod defaultDefaultConstructor(TypeReference tref, Type writtenType) {
		NormalMethod cons = language().plugin(ObjectOrientedFactory.class).createNormalMethod(new SimpleNameMethodHeader(writtenType.name(), clone(tref)));
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
