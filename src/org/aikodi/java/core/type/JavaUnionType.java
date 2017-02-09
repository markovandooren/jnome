package org.aikodi.java.core.type;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.plugin.ObjectOrientedFactory;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.UnionType;

public class JavaUnionType extends UnionType implements JavaType {

	public JavaUnionType(List<Type> types) {
		super(types);
	}

	protected JavaUnionType(String name, List<Type> types) {
		super(name, types);
	}

	@Override
	protected UnionType cloneSelf() {
		List<Type> types = types();
		return new JavaUnionType(createSignature(types),types);
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
		return language().plugin(ObjectOrientedFactory.class).createUnionType(captured);
	}

}
