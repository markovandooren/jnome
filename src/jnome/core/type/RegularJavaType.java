package jnome.core.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jnome.core.expression.invocation.SuperConstructorDelegation;
import jnome.core.method.JavaNormalMethod;
import jnome.core.modifier.JavaConstructor;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.modifier.Modifier;
import chameleon.core.tag.TagImpl;
import chameleon.oo.expression.NamedTarget;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.member.Member;
import chameleon.oo.method.RegularImplementation;
import chameleon.oo.method.SimpleNameMethodHeader;
import chameleon.oo.statement.Block;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeElement;
import chameleon.oo.type.inheritance.InheritanceRelation;
import chameleon.oo.type.inheritance.SubtypeRelation;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.modifier.Constructor;
import chameleon.support.modifier.Public;
import chameleon.support.statement.StatementExpression;
import chameleon.util.Util;

public class RegularJavaType extends RegularType {

	public RegularJavaType(SimpleNameSignature sig) {
		super(sig);
		setDefaultDefaultConstructor();
	}

	public RegularJavaType(String name) {
		this(new SimpleNameSignature(name));
	}
	
	protected RegularType cloneThis() {
		return new RegularJavaType(signature().clone());
	}

	protected NormalMethod defaultDefaultConstructor() {
		return _defaultDefaultConstructor;
	}
	
	private NormalMethod _defaultDefaultConstructor;
	
	protected void setDefaultDefaultConstructor() {
		// FIXME Because this code is ran when a regular Java type is constructed, we cannot ask the
		//       language for the factory. Management of the constructor should be done lazily. When
		//       the type is actually used, we can assume that a language is attached. Otherwise, we
		//       throw an exception.
		JavaNormalMethod cons = new JavaNormalMethod(new SimpleNameMethodHeader(signature().name(), new BasicJavaTypeReference(signature().name())));
		cons.addModifier(new Constructor());
		cons.addModifier(new Public());
		Block body = new Block();
		cons.setImplementation(new RegularImplementation(body));
		body.addStatement(new StatementExpression(new SuperConstructorDelegation()));
		cons.setUniParent(this);
		_defaultDefaultConstructor = cons;
	}
	
	protected void clearDefaultDefaultConstructor() {
		_defaultDefaultConstructor = null;
	}
	
  @Override
	public List<Member> implicitMembers() {
  	List<Member> result = new ArrayList<Member>();
  	Util.addNonNull(defaultDefaultConstructor(), result);
  	return result;
	}

  public void reactOnDescendantAdded(Element element) {
  	if(element instanceof TypeElement) {
  		if(isConstructor(element)) {
		    clearDefaultDefaultConstructor();
  		}
  	}
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
  	
  }

  public void reactOnDescendantReplaced(Element oldElement, Element newElement) {
  	reactOnDescendantRemoved(oldElement);
  	reactOnDescendantAdded(newElement);
  }
  
//  @Override
//  protected void addImplicitInheritanceRelations(List<InheritanceRelation> list) {
//    if(explicitNonMemberInheritanceRelations().isEmpty() && (! "Object".equals(name())) && (! getFullyQualifiedName().equals("java.lang.Object"))) {
//    	InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(new NamedTarget("java.lang"),"Object"));
//    	relation.setUniParent(this);
//    	relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
//    	list.add(relation);
//    }
//  }
  
  @Override
  public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
    if(explicitNonMemberInheritanceRelations().isEmpty() && (! "Object".equals(name())) && (! getFullyQualifiedName().equals("java.lang.Object"))) {
    	InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(new NamedTarget("java.lang"),"Object"));
    	relation.setUniParent(this);
    	relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
    	List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
    	result.add(relation);
    	return result;
    } else {
    	return Collections.EMPTY_LIST;
    }
  }
  
  @Override
  public boolean hasInheritanceRelation(InheritanceRelation relation) throws LookupException {
  	return super.hasInheritanceRelation(relation) || relation.hasMetadata(IMPLICIT_CHILD);
  }
  
  public final static String IMPLICIT_CHILD = "IMPLICIT CHILD";

}
