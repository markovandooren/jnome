package jnome.core.type;

import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;
import jnome.core.modifier.JavaConstructor;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.member.Member;
import chameleon.core.modifier.Modifier;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeElement;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.support.member.simplename.SimpleNameMethodHeader;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.modifier.Constructor;
import chameleon.support.modifier.Public;
import chameleon.util.Util;

public class RegularJavaType extends RegularType {

	public RegularJavaType(SimpleNameSignature sig) {
		super(sig);
		setDefaultDefaultConstructor();
	}

	public RegularJavaType(String name) {
		super(name);
		setDefaultDefaultConstructor();
	}
	
	protected RegularType cloneThis() {
		return new RegularJavaType(signature().clone());
	}

	protected NormalMethod defaultDefaultConstructor() {
		return _defaultDefaultConstructor;
	}
	
	private NormalMethod _defaultDefaultConstructor;
	
	protected void setDefaultDefaultConstructor() {
		NormalMethod cons = new NormalMethod(new SimpleNameMethodHeader(signature().name()), new BasicJavaTypeReference(signature().name()));
		cons.addModifier(new Constructor());
		cons.addModifier(new Public());
		cons.setUniParent(this);
		_defaultDefaultConstructor = cons;
	}
	
	protected void clearDefaultDefaultConstructor() {
		_defaultDefaultConstructor = null;
	}
	
	protected void initDefaultConstructor() {
		if(!_initialized) {
	    setDefaultDefaultConstructor();
	    _initialized = true;
		}
	}
	
	private boolean _initialized = false;

	
	
  @Override
	public List<Member> implicitMembers() {
  	List<Member> result = new ArrayList<Member>();
  	Util.addNonNull(defaultDefaultConstructor(), result);
  	return result;
	}

//	@Override
//	public <M extends Member> List<M> members(Class<M> kind) throws LookupException {
//  	List<M> result = super.members(kind);
//  	NormalMethod cons = defaultDefaultConstructor();
//		if(kind.isInstance(cons)) {
//  		result.add((M)cons);
//  	}
//  	return result;
//	}

//	public <D extends Member> List<D> members(DeclarationSelector<D> selector) throws LookupException {
//		// 1) All defined members of the requested kind are added.
//		List<D> result = localMembers(selector);
//		
//		Declaration cons = defaultDefaultConstructor();
//		if(cons != null) {
//			ArrayList l = new ArrayList<Declaration>();
//			l.add(cons);
//			result.addAll(selector.selection(l));
//		}
//
//		// 2) Fetch all potentially inherited members from all inheritance relations
//		for (InheritanceRelation rel : inheritanceRelations()) {
//				rel.accumulateInheritedMembers(selector, result);
//		}
//		// The selector must still apply its order to the candidates.
//		//selector.applyOrder(result);
//		
//		return selector.selection(result);
//  }
  
  public void reactOnDescendantAdded(Element element) {
  	if(element instanceof TypeElement) {
  		if(isConstructor(element)) {
		    clearDefaultDefaultConstructor();
  		}
  	}
//  	if(element.isTrue(language(Java.class).CONSTRUCTOR)) {
//  		clearDefaultDefaultConstructor();
//  	}
  }

	private boolean isConstructor(Element element) {
		List<Modifier> mods = ((TypeElement) element).modifiers();
		for(Modifier mod:mods) {
			if(mod instanceof JavaConstructor) {
				return true;
			}
		}
		return false;
	}

  public void reactOnDescendantRemoved(Element element) {
  	if(isConstructor(element)) {
  		List<TypeElement> elements = body().elements();
  		for(TypeElement el: elements) {
  			if(isConstructor(el)) {
  				return;
  			}
  		}
  		setDefaultDefaultConstructor();
  	}
  	
  	//  	Java language = language(Java.class);
//		if(element.isTrue(language.CONSTRUCTOR)) {
//  		List<TypeElement> elements = body().elements();
//  		for(TypeElement el: elements) {
//  			if(el.isTrue(language.CONSTRUCTOR)) {
//  				return;
//  			}
//  		}
//  		setDefaultDefaultConstructor();
//  	}
  }

  public void reactOnDescendantReplaced(Element oldElement, Element newElement) {
  	reactOnDescendantRemoved(oldElement);
  	reactOnDescendantAdded(newElement);
  }

}
