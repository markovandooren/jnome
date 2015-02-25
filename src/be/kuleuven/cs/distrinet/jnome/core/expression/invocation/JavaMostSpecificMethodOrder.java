package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.List;

import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.relation.WeakPartialOrder;
import org.aikodi.chameleon.oo.expression.MethodInvocation;
import org.aikodi.chameleon.oo.method.Method;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;

import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.ArrayType;
import be.kuleuven.cs.distrinet.jnome.core.variable.MultiFormalParameter;

public class JavaMostSpecificMethodOrder<M extends MethodSelectionResult> extends WeakPartialOrder<M> {
	
	MethodInvocation _invocation;
	
	public JavaMostSpecificMethodOrder(MethodInvocation invocation) {
		_invocation = invocation;
	}

	@Override
	public boolean contains(M firstResult, M secondResult) throws LookupException {
		boolean result = false;
		if(firstResult != secondResult) {
			int firstPhase = firstResult.phase();
			int secondPhase = secondResult.phase();
			if(firstPhase < secondPhase) {
				result = true;
			} else if(firstPhase > secondPhase) {
				result = false;
			} else {
				Method first = firstResult.template();
				Method second = secondResult.template();
				if(! first.sameAs(second)) {
					if(!(first.lastFormalParameter() instanceof MultiFormalParameter) && ! (second.lastFormalParameter() instanceof MultiFormalParameter)) {
						result = containsFixedArity(first, second);
					} else if((first.lastFormalParameter() instanceof MultiFormalParameter) && (second.lastFormalParameter() instanceof MultiFormalParameter)){
						result = containsVariableArity(firstResult, secondResult);
					}
					
				}
			}
		}
		return result;
	}

	public boolean containsVariableArity(M firstResult, M secondResult) throws LookupException {
		Method first = firstResult.template();
		Method second = secondResult.template();

		boolean result = true;
		Java language = (Java) first.language(Java.class);
		List<Type> firstTypes = first.header().formalParameterTypes();
		List<Type> secondTypes = second.header().formalParameterTypes();
		int firstSize = firstTypes.size();
		firstTypes.set(firstSize-1, ((ArrayType)firstTypes.get(firstSize-1)).elementType());
		int secondSize = secondTypes.size();
		secondTypes.set(secondSize-1, ((ArrayType)secondTypes.get(secondSize-1)).elementType());
		int n;
		int k;
		if(firstSize >= secondSize) {
			n = firstSize;
			k = secondSize;
		} else {
			n = secondSize;
			k = firstSize;
		}
		List typeParameters = second.typeParameters();
		List<Type> Ss;
		if(typeParameters.size() > 0) {
			FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(_invocation,second.header());
			for(int i=0; i < k-1; i++) {
				constraints.add(new SSConstraint(language.reference(firstTypes.get(i)), secondTypes.get(i)));
			}
			TypeAssignmentSet As = constraints.resolve();
			result = As.valid();
			if(result) {
			  Ss = JavaMethodInvocation.formalParameterTypesInContext(second, As);
			} else {
				// If the actual type parameters are invalid, don't bother
				// substituting them
				Ss = null;
			}
		} else {
			Ss = new ArrayList<Type>(secondTypes);
		}
		for(int i=0; result && i<k-1;i++) {
			result = firstTypes.get(i).subTypeOf(Ss.get(i));
		}
		if(result && firstSize >= secondSize) {
			for(int i=k-1; result && i<n;i++) {
				result = firstTypes.get(i).subTypeOf(Ss.get(k-1));
			}
		} else {
			for(int i=k-1; result && i<n;i++) {
				result = firstTypes.get(k-1).subTypeOf(Ss.get(i));
			}
		}
		return result;
	}
	
	public boolean containsFixedArity(Method first, Method second) throws LookupException {
		boolean result = true;
		Java language = (Java) first.language(Java.class);
		List<Type> Ts = first.header().formalParameterTypes();
		List<Type> Us = second.header().formalParameterTypes();
		int size =Ts.size();
		boolean hasFormalTypeParameter = false;
		for(TypeParameter p: second.typeParameters()) {
			if(p instanceof FormalTypeParameter) {
				hasFormalTypeParameter = true;
				break;
			}
		}
		List<Type> Ss;
		if(hasFormalTypeParameter) {
			FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(_invocation, second.header());
			for(int i=0; i < size; i++) {
				constraints.add(new SSConstraint(language.reference(Ts.get(i)), Us.get(i)));
			}
			TypeAssignmentSet As = constraints.resolve();
			result = As.valid();
			if(result) {
			  Ss = JavaMethodInvocation.formalParameterTypesInContext(second, As);
			} else {
				// If the actual type parameters are invalid, don't bother
				// substituting them
				Ss = null;
			}
		} else {
			Ss = new ArrayList<Type>(Us);
		}
		for(int i=0; result && i<size;i++) {
			result = Ts.get(i).subTypeOf(Ss.get(i));
		}
		
		return result;
	}
}
