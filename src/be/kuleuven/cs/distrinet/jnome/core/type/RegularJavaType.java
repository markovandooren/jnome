package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.modifier.Modifier;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.reference.SimpleReference;
import be.kuleuven.cs.distrinet.chameleon.core.tag.TagImpl;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.ExpressionFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.method.RegularImplementation;
import be.kuleuven.cs.distrinet.chameleon.oo.method.SimpleNameMethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.type.RegularType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeElement;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.support.member.simplename.method.NormalMethod;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Constructor;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Native;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.support.statement.StatementExpression;
import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.SuperConstructorDelegation;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaNormalMethod;
import be.kuleuven.cs.distrinet.jnome.core.modifier.JavaConstructor;
import be.kuleuven.cs.distrinet.jnome.workspace.JavaView;
import be.kuleuven.cs.distrinet.rejuse.logic.ternary.Ternary;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class RegularJavaType extends RegularType implements JavaType {

	public RegularJavaType(SimpleNameSignature sig) {
		super(sig);
		setDefaultDefaultConstructor(false);
	}

	public RegularJavaType(String name) {
		this(new SimpleNameSignature(name));
	}
	
	protected RegularType cloneSelf() {
		RegularJavaType regularJavaType = new RegularJavaType(clone(signature()));
		regularJavaType.setSignature(null);
		regularJavaType.parameterBlock(TypeParameter.class).disconnect();
		return regularJavaType;
	}

	protected NormalMethod defaultDefaultConstructor() {
		return _defaultDefaultConstructor;
	}
	
	private NormalMethod _defaultDefaultConstructor;
	
	/**
	 * Set the default default constructor.
	 */
	protected void setDefaultDefaultConstructor(boolean rebuildCache) {
		JavaNormalMethod cons = createDefaultConstructorWithoutAccessModifier(rebuildCache);
		cons.addModifier(new Public());
	}

	/**
	 * Create a default default constructor without an access modifier.
	 */
	protected JavaNormalMethod createDefaultConstructorWithoutAccessModifier(boolean rebuildCache) {
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
		setDefaultDefaultConstructor(cons);
		return cons;
	}
	
	protected void clearDefaultDefaultConstructor() {
		setDefaultDefaultConstructor(null);
	}
	
	private void setDefaultDefaultConstructor(JavaNormalMethod method) {
		_defaultDefaultConstructor = method;
		_implicitMemberCache = null;
	}
	
	/**
	 * A Java reference type has a default constructor when
	 * no other constructor is present.
	 */
  @Override
	public List<Member> implicitMembers() {
  	if(_implicitMemberCache == null) {
  		_implicitMemberCache = buildImplicitMembersCache();
  	}
  	return _implicitMemberCache;
	}

	protected List<Member> buildImplicitMembersCache() {
  	Builder<Member> builder = ImmutableList.<Member>builder();
  	NormalMethod defaultDefaultConstructor = defaultDefaultConstructor();
  	if(defaultDefaultConstructor != null) {
  		builder.add(defaultDefaultConstructor);
  	}
  	NormalMethod classMethod = getClassMethod();
  	if(classMethod != null) {
  		builder.add(classMethod);
  	}
  	return builder.build();
	}
	
	private List<Member> _implicitMemberCache;

  /**
   * This is actually cheating because the getClass method in Java
   * is a member of Object. Maybe we should set the parent to Object
   * instead of the current class. Anyhow, the Java language specification
   * cheats as well.
   * @return
   */
  public NormalMethod getClassMethod() {
  	if(_getClassMethod == null) {
  		if(view(JavaView.class).topLevelType() != this) {
  			Java language = language(Java.class);
  			BasicJavaTypeReference returnType = language.createTypeReference("java.lang.Class");
  			JavaTypeReference erasedThisType = language.createTypeReference(name());
  			ActualTypeArgument arg = language.createExtendsWildcard(erasedThisType);
  			returnType.addArgument(arg);
  			_getClassMethod = new NormalMethod(new SimpleNameMethodHeader("getClass", returnType));
  			_getClassMethod.addModifier(new Public());
  			_getClassMethod.addModifier(new Native());
  			_getClassMethod.setUniParent(body());
  		}
  	}
  	return _getClassMethod;
  }
  
  private NormalMethod _getClassMethod;

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
  		setDefaultDefaultConstructor(true);
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
    	List<InheritanceRelation> result = new ArrayList<InheritanceRelation>(1);
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
		//FIXME this code seems to have been duplicated a number of times.
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
					ExpressionFactory expressionFactory = language.plugin(ExpressionFactory.class);
					for(int i = size - 2; i>=0;i--) {
						SimpleReference<RawType> simpleRef = expressionFactory.createSimpleReference(outerTypes.get(i).signature().name(), RawType.class);
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
	
	public ArrayType toArray() {
		if(_arrayType == null) {
			_arrayType = new ArrayType(this);
		}
		return _arrayType;
	}
	
	private ArrayType _arrayType;
	
	@Override
	public synchronized void flushLocalCache() {
		super.flushLocalCache();
		_rawTypeCache = null;
		_arrayType = null;
		// The implicit member cache is kept up to date via the
		// reactTo... methods.
	}

}
