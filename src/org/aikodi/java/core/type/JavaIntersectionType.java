package org.aikodi.java.core.type;

import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.IntersectionType;
import org.aikodi.chameleon.oo.type.Type;
import java.util.ArrayList;

public class JavaIntersectionType extends IntersectionType implements JavaType {

	public JavaIntersectionType(List<Type> types) {
		super(types);
	}

	protected JavaIntersectionType(String name, List<Type> types) {
		super(name,types);
	}
	

	@Override
	protected JavaIntersectionType cloneSelf() {
		List<Type> types = types();
		return new JavaIntersectionType(createSignature(types),types);
	}

	@Override
	public Type erasure() {
	  throw new ChameleonProgrammerException();
	}
	
	@Override
	public Type captureConversion() throws LookupException {
		List<Type> types = types();
		List<Type> captured = new ArrayList<>(types.size());
		for(Type type: types) {
			captured.add(((JavaType)type).captureConversion());
		}
		return language().plugin(ObjectOrientedFactory.class).createIntersectionType(captured);
	}
}
