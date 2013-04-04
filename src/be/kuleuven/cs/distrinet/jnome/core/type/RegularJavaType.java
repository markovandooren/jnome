package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.SuperConstructorDelegation;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaNormalMethod;
import be.kuleuven.cs.distrinet.jnome.core.modifier.JavaConstructor;
import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.Modifier;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;
import be.kuleuven.cs.distrinet.chameleon.core.tag.TagImpl;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.NamedTarget;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.method.RegularImplementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.SimpleNameMethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeElement;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Constructor;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.support.statement.StatementExpression;
import be.kuleuven.cs.distrinet.chameleon.util.Util;

public class RegularJavaType extends RegularType implements JavaType {

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
	
	/**
	 * Set the default default constructor.
	 */
	protected void setDefaultDefaultConstructor() {
		JavaNormalMethod cons = createDefaultConstructorWithoutAccessModifier();
		cons.addModifier(new Public());
	}

	/**
	 * Create a default default constructor without an access modifier.
	 */
	protected JavaNormalMethod createDefaultConstructorWithoutAccessModifier() {
		// FIXME Because this code is ran when a regular Java type is constructed, we cannot ask the
		//       language for the factory. Management of the constructor should be done lazily. When
		//       the type is actually used, we can assume that a language is attached. Otherwise, we
		//       throw an exception.
		JavaNormalMethod cons = new JavaNormalMethod(new SimpleNameMethodHeader(signature().name(), new BasicJavaTypeReference(signature().name())));
		cons.addModifier(new Constructor());
		Block body = new Block();
		cons.setImplementation(new RegularImplementation(body));
		body.addStatement(new StatementExpression(new SuperConstructorDelegation()));
		cons.setUniParent(this);
		_defaultDefaultConstructor = cons;
		return cons;
	}
	
	protected void clearDefaultDefaultConstructor() {
		_defaultDefaultConstructor = null;
	}
	
	/**
	 * A Java reference type has a default constructor when
	 * no other constructor is present.
	 */
  @Override
	public List<Member> implicitMembers() {
  	List<Member> result = new ArrayList<Member>();
  	Util.addNonNull(defaultDefaultConstructor(), result);
  	return result;
	}

  /**
   * If the added element is a constructor, the default default
   * constructor is removed.
   */
  public void reactOnDescendantAdded(Element element) {
  	if(element instanceof TypeElement) {
  		if(isConstructor(element)) {
		    clearDefaultDefaultConstructor();
  		}
  	}
  }
  
  /**
   * A Java reference type is not overridable. A such, if B extends A,
   * and both A and B have a nested class with name C, then B.C does
   * not override A.C.
   */
  @Override
  public PropertySet<Element, ChameleonProperty> inherentProperties() {
  	PropertySet<Element, ChameleonProperty> result = new PropertySet<Element, ChameleonProperty>();
  	result.add(language(ObjectOrientedLanguage.class).OVERRIDABLE.inverse());
  	return result;
  }

	private boolean isConstructor(Element element) {
		//FIXME element.isTrue(language(Java.class).CONSTRUCTOR) doesn't work since the type
		//      and the constructor aren't connected to the model during parsing.
		// The suck is strong in this one
		List<Modifier> mods = ((TypeElement) element).modifiers();
		for(Modifier mod:mods) {
			if(mod instanceof JavaConstructor) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If an element is removed, we check whether it is the
	 * last remaining constructor. If it is, we add the
	 * default default constructor.
	 */
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
  
  @Override
  public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
  	//FIXME speed avoid creating collection
    String defaultSuperClassFQN = language(ObjectOrientedLanguage.class).getDefaultSuperClassFQN();
		if(explicitNonMemberInheritanceRelations().isEmpty() && 
    	 (! getFullyQualifiedName().equals(defaultSuperClassFQN)) //"java.lang.Object"
    	) {
//    	InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(new NamedTarget("java.lang"),"Object"));
    	InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(defaultSuperClassFQN));
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

	@Override
	public Type erasure() {
		Java language = language(Java.class);
		RawType result = _rawTypeCache;
		if(result == null) {
				if(is(language.INSTANCE) == Ternary.TRUE) {
					Type outmostType = farthestAncestor(Type.class);
					if(outmostType == null) {
						outmostType = this;
					}
					RawType outer;
					if(outmostType instanceof RawType) {
						outer = (RawType) outmostType;
					} else {
						outer = new RawType(outmostType);
					}
					RawType current = outer;
					List<Type> outerTypes = ancestors(Type.class);
					outerTypes.add(0, this);

					int size = outerTypes.size();
					for(int i = size - 2; i>=0;i--) {
						SimpleReference<RawType> simpleRef = new SimpleReference<RawType>(outerTypes.get(i).signature().name(), RawType.class);
						simpleRef.setUniParent(current);
						try {
							current = simpleRef.getElement();
						} catch (LookupException e) {
							e.printStackTrace();
							throw new ChameleonProgrammerException("An inner type of a newly created outer raw type cannot be found",e);
						}
					}
					result = current;
				} else {
					// static
					result = new RawType(this);
				}
				_rawTypeCache = result;
			}
		  return result;	
		}

	private RawType _rawTypeCache;
	
	@Override
	public synchronized void flushLocalCache() {
		super.flushLocalCache();
		_rawTypeCache = null;
	}

}
