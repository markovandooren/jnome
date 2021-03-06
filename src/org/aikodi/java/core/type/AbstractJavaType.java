package org.aikodi.java.core.type;

import java.util.List;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.oo.method.SimpleNameMethodHeader;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.ClassImpl;
import org.aikodi.chameleon.oo.type.RegularType;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.support.member.simplename.method.NormalMethod;
import org.aikodi.chameleon.support.modifier.Native;
import org.aikodi.chameleon.support.modifier.Public;
import org.aikodi.java.core.language.Java7;
import org.aikodi.java.workspace.JavaView;

public abstract class AbstractJavaType extends RegularType implements JavaType {

	protected List<Declaration> _implicitMemberCache;

	/**
	 * A Java reference type has a default constructor when no other constructor
	 * is present.
	 */
	@Override
	public List<Declaration> implicitMembers() {
		if (_implicitMemberCache == null) {
			_implicitMemberCache = buildImplicitMembersCache();
		}
		return _implicitMemberCache;
	}

	public AbstractJavaType(String name) {
		super(name);
	}

	protected abstract List<Declaration> buildImplicitMembersCache();

	/**
	 * This is actually cheating because the getClass method in Java is a member
	 * of Object. Maybe we should set the parent to Object instead of the current
	 * class.
	 * 
	 * @return
	 */
	protected static NormalMethod getClassMethod(ClassImpl type) {
		NormalMethod result = null;
		if (type.view(JavaView.class).topLevelType() != type) {
			Java7 language = type.language(Java7.class);
			BasicJavaTypeReference returnType = language.createTypeReference("java.lang.Class");
			JavaTypeReference erasedThisType = language.createTypeReference(type.name());
			TypeArgument arg = language.createExtendsWildcard(erasedThisType);
			returnType.addArgument(arg);
			result = language.plugin(ObjectOrientedFactory.class).createNormalMethod(new SimpleNameMethodHeader("getClass", returnType));
//			result = new NormalMethod(new SimpleNameMethodHeader("getClass", returnType));
			result.addModifier(new Public());
			result.addModifier(new Native());
			result.setUniParent(type);
		}
		return result;
	}

}