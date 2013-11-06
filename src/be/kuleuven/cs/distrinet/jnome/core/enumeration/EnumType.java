package be.kuleuven.cs.distrinet.jnome.core.enumeration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.property.ChameleonProperty;
import be.kuleuven.cs.distrinet.chameleon.core.tag.TagImpl;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.method.Method;
import be.kuleuven.cs.distrinet.chameleon.oo.plugin.ObjectOrientedFactory;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.InheritanceRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.type.inheritance.SubtypeRelation;
import be.kuleuven.cs.distrinet.chameleon.oo.variable.FormalParameter;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Final;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Private;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Public;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Static;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaMethod;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.BasicJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;
import be.kuleuven.cs.distrinet.rejuse.property.PropertySet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * A class for enum types in Java. Enum types in Java differ from
 * regular types in several ways:
 * <ul>
 * <li>Enum types must be top level types.</li>
 * <li>An enum type cannot extend a class, but it can implement interfaces.</li>
 * <li>Enum types cannot be instantiated explicitly.</li>
 * <li>Enum types have a fixed number of instances: constants.</li>
 * <li>An enum type is final if it has no constants.</li>
 * <li>An enum type is abstract when it has constants.</li>
 * <li>The default constructor (if any) of an enum type is private.</li>
 * </ul>
 * 
 * @author Marko van Dooren
 */
public class EnumType extends RegularJavaType {

	public EnumType(String name) {
		super(name);
	}

	/**
	 * JLS 8.3
	 * 
	 * An enum type with the name "E" has the following explicit members:
	 * <ul>
	 *  <li>public static E[] values();</li>
	 *  <li>public static E valueOf(String name);</li>
	 * </ul>
	 */
	@Override
	protected List<Member> buildImplicitMembersCache() {
		Builder<Member> builder = ImmutableList.<Member>builder();
		builder.addAll(super.buildImplicitMembersCache());
		builder.add(values());
		builder.add(valueOf());
		return builder.build();
	}
	
	protected Method values() {
		JavaTypeReference enumTypeReference = language(Java.class).createTypeReference(name());
		JavaTypeReference tref = new ArrayTypeReference(enumTypeReference, 1);
		return createMethod(tref, "values");
	}

	protected Method createMethod(JavaTypeReference tref, String name) {
		ObjectOrientedFactory plugin = language().plugin(ObjectOrientedFactory.class);
		Method result = plugin.createNormalMethod(name, tref);
		result.addModifier(new Public());
		result.addModifier(new Static());
		result.setUniParent(body());
		return result;
	}
	
	protected Method valueOf() {
		Java java = language(Java.class);
		JavaTypeReference tref = java.createTypeReference(name());
		Method result = createMethod(tref, "valueOf");
		TypeReference type = java.createTypeReference("java.lang.String");
		result.header().addFormalParameter(new FormalParameter("argument", type));
		return result;
	}
	
	/**
	 * The default default constructor of an enum type is private.
	 */
	@Override
	protected void setDefaultDefaultConstructor(boolean rebuildCache) {
		JavaMethod cons = createDefaultConstructorWithoutAccessModifier(rebuildCache);
		cons.addModifier(new Private());
	}

	/**
	 * JLS 3: 8.9 page 250 
	 * 
	 * An enum type is final unless it has a constant with a class body.
	 */
	@Override
	public PropertySet<Element, ChameleonProperty> inherentProperties() {
		PropertySet<Element, ChameleonProperty> result = new PropertySet<Element, ChameleonProperty>();
		for(EnumConstant constant: body().children(EnumConstant.class)) {
			if(constant.body() != null) {
				Final finalModifier = new Final();
				finalModifier.setUniParent(this);
				result.addAll(finalModifier.impliedProperties());
				break;
			}
		}
		return result;
	}
	
	@Override
	public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
		List<InheritanceRelation> result = new ArrayList<>();
		Java language = language(Java.class);
		BasicJavaTypeReference enumReference = (BasicJavaTypeReference) language.createTypeReference("java.lang.Enum");
		enumReference.addArgument(language.createBasicTypeArgument(language.createTypeReference(getFullyQualifiedName())));
		InheritanceRelation relation = new SubtypeRelation(enumReference);
  	relation.setUniParent(this);
  	relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
  	result.add(relation);
		return result;
	}
	
}
