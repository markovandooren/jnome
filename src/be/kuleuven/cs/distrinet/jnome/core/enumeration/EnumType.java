package be.kuleuven.cs.distrinet.jnome.core.enumeration;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.declaration.SimpleNameSignature;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.support.modifier.Private;
import be.kuleuven.cs.distrinet.jnome.core.method.JavaNormalMethod;
import be.kuleuven.cs.distrinet.jnome.core.type.RegularJavaType;

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

	public EnumType(SimpleNameSignature sig) {
		super(sig);
	}

	public EnumType(String name) {
		super(name);
	}

	/**
	 * JLS 8.3
	 * 
	 * An enum type with the name "E" has the following explicit members:
	 * <ul>
	 *  <li></li>
	 * </ul>
	 */
	@Override
	public List<Member> implicitMembers() {
		List<Member> result = super.implicitMembers();
		return result;
	}
	
	/**
	 * The default default constructor of an enum type is private.
	 */
	protected void setDefaultDefaultConstructor() {
		JavaNormalMethod cons = createDefaultConstructorWithoutAccessModifier();
		cons.addModifier(new Private());
	}

}
