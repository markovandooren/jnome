package org.aikodi.java.core.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.declaration.Declarator;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.core.factory.Factory;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.modifier.Modifier;
import org.aikodi.chameleon.core.property.ChameleonProperty;
import org.aikodi.chameleon.core.reference.NameReference;
import org.aikodi.chameleon.core.tag.TagImpl;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.method.RegularImplementation;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.statement.Block;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeInstantiation;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.modifier.Constructor;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.chameleon.support.statement.StatementExpression;
import org.aikodi.java.core.expression.invocation.SuperConstructorDelegation;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.core.method.JavaMethod;
import org.aikodi.java.core.modifier.JavaConstructor;
import org.aikodi.java.workspace.JavaView;
import org.aikodi.rejuse.logic.ternary.Ternary;
import org.aikodi.rejuse.property.PropertySet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class RegularJavaType extends AbstractJavaType {

	public RegularJavaType(String name) {
		super(name);
		setDefaultDefaultConstructor(false);
	}

	@Override
	public boolean assignableTo(Type other) throws LookupException {
		//FIXME If getDirectSuperTypes is correct, this code should no longer be necessary.
		boolean result = super.assignableTo(other);
		if(! result) {
			result = language(Java7.class).subtypeRelation().convertibleThroughUncheckedConversionAndSubtyping(this, other);
		}
		return result;
	}

	public TypeInstantiation createDerivedType(List<TypeArgument> typeArguments) throws LookupException {
		return new JavaTypeInstantiation(this, typeArguments);
	}

	protected RegularType cloneSelf() {
		RegularJavaType regularJavaType = new RegularJavaType(name());
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
		JavaMethod cons = createDefaultConstructorWithoutAccessModifier(rebuildCache);
		cons.addModifier(new Public());
	}

	/**
	 * Create a default default constructor without an access modifier.
	 */
	protected JavaMethod createDefaultConstructorWithoutAccessModifier(boolean rebuildCache) {
		// FIXME Because this code is ran when a regular Java type is constructed,
		// we cannot ask the
		// language for the factory. Management of the constructor should be done
		// lazily. When
		// the type is actually used, we can assume that a language is attached.
		// Otherwise, we
		// throw an exception.
		JavaMethod cons = new JavaMethod(new SimpleNameMethodHeader(name(), new BasicJavaTypeReference(name())));
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

	private void setDefaultDefaultConstructor(JavaMethod method) {
		_defaultDefaultConstructor = method;
		_implicitMemberCache = null;
	}

	protected List<Declaration> buildImplicitMembersCache() {
		Builder<Declaration> builder = ImmutableList.<Declaration> builder();
		NormalMethod defaultDefaultConstructor = defaultDefaultConstructor();
		if (defaultDefaultConstructor != null) {
			builder.add(defaultDefaultConstructor);
		}
		NormalMethod classMethod = getClassMethod(this);
		if (classMethod != null) {
			builder.add(classMethod);
		}
		return builder.build();
	}



//	private NormalMethod _getClassMethod;

	/**
	 * If the added element is a constructor, the default default constructor is
	 * removed.
	 */
	public void reactOnDescendantAdded(Element element) {
		if (element instanceof Declarator) {
			if (isConstructor(element)) {
				clearDefaultDefaultConstructor();
			}
		}
	}

	/**
	 * A Java reference type is not overridable. A such, if B extends A, and both
	 * A and B have a nested class with name C, then B.C does not override A.C.
	 */
	@Override
	public PropertySet<Element, ChameleonProperty> inherentProperties() {
		PropertySet<Element, ChameleonProperty> result = new PropertySet<Element, ChameleonProperty>();
		result.add(language(ObjectOrientedLanguage.class).OVERRIDABLE.inverse());
		return result;
	}

	private boolean isConstructor(Element element) {
		// FIXME element.isTrue(language(Java.class).CONSTRUCTOR) doesn't work since
		// the type
		// and the constructor aren't connected to the model during parsing.
		// The suck is strong in this one
		List<Modifier> mods = ((Declarator) element).modifiers();
		for (Modifier mod : mods) {
			if (mod instanceof JavaConstructor) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If an element is removed, we check whether it is the last remaining
	 * constructor. If it is, we add the default default constructor.
	 */
	public void reactOnDescendantRemoved(Element element) {
		if (isConstructor(element)) {
			List<Declarator> elements = body().elements();
			for (Declarator el : elements) {
				if (isConstructor(el)) {
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
		// FIXME speed avoid creating collection
		if (explicitNonMemberInheritanceRelations().isEmpty()) {
			JavaView view = view(JavaView.class);
			Type topLevelType = view.topLevelType();
			if (topLevelType != this) {
				InheritanceRelation relation = new SubtypeRelation(new DirectJavaTypeReference(topLevelType));
				relation.setUniParent(this);
				relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
				List<InheritanceRelation> result = new ArrayList<InheritanceRelation>(1);
				result.add(relation);
				return result;
			}
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean hasInheritanceRelation(InheritanceRelation relation) throws LookupException {
		return super.hasInheritanceRelation(relation) || relation.hasMetadata(IMPLICIT_CHILD);
	}

	public final static String IMPLICIT_CHILD = "IMPLICIT CHILD";

	@Override
	public Type erasure() {
		// FIXME this code seems to have been duplicated a number of times.
		Java7 language = language(Java7.class);
		Type result = _rawTypeCache;
		if (result == null) {
			if (is(language.INSTANCE) == Ternary.TRUE) {
				Type outmostType = lexical().farthestAncestor(Type.class);
				Type outer;
				if (outmostType == null) {
//					outmostType = this;
//					if(nbTypeParameters(TypeParameter.class) > 0) {
					  outer = new RawType(this);
//					} else {
//						outer = this;
//					}
				} else {
					if (outmostType instanceof RawType) {
						outer = (RawType) outmostType;
					} else {
						outer = ((JavaType)outmostType).erasure();
					}
				}
				Type current = outer;
				List<Type> outerTypes = lexical().ancestors(Type.class);
				outerTypes.add(0, this);

				int size = outerTypes.size();
				Factory expressionFactory = language.plugin(Factory.class);
				for (int i = size - 2; i >= 0; i--) {
					NameReference<Type> simpleRef = expressionFactory.createNameReference(outerTypes.get(i).name(),
							(Class) Type.class);
					simpleRef.setUniParent(current);
					try {
						current = simpleRef.getElement();
					} catch (LookupException e) {
						e.printStackTrace();
						throw new ChameleonProgrammerException("An inner type of a newly created outer raw type cannot be found", e);
					}
				}
				result = current;
			} else {
				// static
				if(nbTypeParameters(TypeParameter.class) > 0) {
				  result = new RawType(this);
				} else {
					result = this;
				}
			}
			_rawTypeCache = result;
		}
		return result;
	}

	private Type _rawTypeCache;

	public ArrayType toArray() {
		if (_arrayType == null) {
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
