package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.predicate.UnsafePredicate;

import jnome.core.language.Java;
import jnome.core.type.JavaTypeReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.type.Type;
import chameleon.core.type.TypeReference;
import chameleon.core.type.generics.TypeParameter;

public class SecondPhaseConstraintSet extends ConstraintSet<SecondPhaseConstraint> {

	public Set<Type> ST(JavaTypeReference U) throws LookupException {
		return U.getElement().getAllSuperTypes();
	}

	public Set<Type> EST(JavaTypeReference U) throws LookupException {
		Set<Type> STU = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type type:STU) {
			result.add(U.language(Java.class).erasure(type));
		}
		return result;
	}

	public Set<Type> EC(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = new ArrayList<JavaTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if(constraint.typeParameter().sameAs(Tj)) {
				Us.add(constraint.URef());
			}
		}
		List<Set<Type>> ESTs = new ArrayList<Set<Type>>();
		for(JavaTypeReference URef: Us) {
			ESTs.add(EST(URef));
		}
		Set<Type> result;
		int size = ESTs.size();
		if(size > 0) {
			result = ESTs.get(0);
			for(int i = 1; i< size; i++) {
				result.retainAll(ESTs.get(i));
			}
		} else {
		  result = new HashSet<Type>();
		}
		return result;
		// Take intersection
	}
	
	public Set<Type> MEC(TypeParameter Tj) throws LookupException {
		final Set<Type> EC = EC(Tj);
		new UnsafePredicate<Type, LookupException>() {
			@Override
			public boolean eval(final Type first) throws LookupException {
				return ! new UnsafePredicate<Type, LookupException>() {
					@Override
					public boolean eval(Type second) throws LookupException {
						return (! first.sameAs(second)) && (second.subTypeOf(first));
					}
				}.exists(EC);
			}
		}.filter(EC);
		return EC;
	}
	
}
