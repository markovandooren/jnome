package org.aikodi.java.core.type;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.TypeFixer;
import org.aikodi.chameleon.oo.type.generics.TypeVariable;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.LazyFormalAlias;
import org.aikodi.chameleon.util.StackOverflowTracer;

public class JavaLazyFormalAlias extends LazyFormalAlias implements JavaType {

	public JavaLazyFormalAlias(String name, FormalTypeParameter param) {
		super(name, param);
	}

	@Override
	public Type erasure() {
		return this;
	}

	@Override
	public TypeVariable cloneSelf() {
		return new JavaLazyFormalAlias(name(), parameter());
	}

	@Override
	public boolean uniSubtypeOf(Type other, TypeFixer trace) throws LookupException {
		if(trace.contains(other, parameter())) {
			return true;
		} 
		//    if(sameAs(other)) {
		//      return true;
		//    }
		trace.add(other, parameter());
		return indirectionTarget().subtypeOf(other, trace);
	}

	@Override
	public boolean uniSupertypeOf(Type other, TypeFixer trace) throws LookupException {
		if(trace.contains(other, parameter())) {
			return true;
		}
		trace.add(other, parameter());
		return other.subtypeOf(indirectionTarget(), trace);
	}

}
