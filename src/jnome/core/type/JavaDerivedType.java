package jnome.core.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jnome.core.expression.invocation.NonLocalJavaTypeReference;
import jnome.core.language.Java;

import chameleon.core.lookup.LookupException;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.DerivedType;
import chameleon.oo.type.Parameter;
import chameleon.oo.type.ParameterSubstitution;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.CapturedTypeParameter;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.TypeConstraint;
import chameleon.oo.type.generics.TypeParameter;

public class JavaDerivedType extends DerivedType {

	public <P extends Parameter> JavaDerivedType(Class<P> kind, List<P> parameters, Type baseType) {
		super(kind,parameters,baseType);
	}

	public JavaDerivedType(List<ParameterSubstitution> parameters, Type baseType) {
		super(parameters, baseType);
	}

	public JavaDerivedType(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

	public JavaDerivedType(Type baseType, List<ActualTypeArgument> typeArguments) throws LookupException {
		super(baseType, typeArguments);
	}

	
	@Override
	public void newAccumulateAllSuperTypes(Set<Type> acc) throws LookupException {
		Type captureConversion = captureConversion();
		if(captureConversion != this) {
			captureConversion.newAccumulateAllSuperTypes(acc);
		} else {
			super.newAccumulateAllSuperTypes(acc);
		}
	}
	
	public void newAccumulateSelfAndAllSuperTypes(Set<Type> acc) throws LookupException {
		Type captureConversion = captureConversion();
		if(captureConversion != this) {
			captureConversion.newAccumulateSelfAndAllSuperTypes(acc);
		} else {
			super.newAccumulateSelfAndAllSuperTypes(acc);
		}
	}
	
	public Type captureConversion() throws LookupException {
		Type result = this;
		List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
		if(! (parameter(TypeParameter.class,1) instanceof CapturedTypeParameter)) {
			Type base = baseType();
			List<TypeParameter> baseParameters = base.parameters(TypeParameter.class);
			Iterator<TypeParameter> formals = baseParameters.iterator();
			List<TypeParameter> actualParameters = parameters(TypeParameter.class);
			Iterator<TypeParameter> actuals = actualParameters.iterator();
			// substitute parameters by their capture bounds.
			// ITERATOR because we iterate over 'formals' and 'actuals' simultaneously.
			List<TypeConstraint> toBeSubstituted = new ArrayList<TypeConstraint>();
			while(actuals.hasNext()) {
				TypeParameter formalParam = formals.next();
				if(!(formalParam instanceof FormalTypeParameter)) {
					throw new LookupException("Type parameter of base type is not a formal parameter.");
				}
				TypeParameter actualParam = actuals.next();
				if(!(actualParam instanceof InstantiatedTypeParameter)) {
					throw new LookupException("Type parameter of type instantiation is not an instantiated parameter: "+actualParam.getClass().getName());
				}
				typeParameters.add(((InstantiatedTypeParameter) actualParam).capture((FormalTypeParameter) formalParam,toBeSubstituted));
			}
			result = language(Java.class).createdCapturedType(new ParameterSubstitution(TypeParameter.class,typeParameters), base);
			result.setUniParent(parent());
			for(TypeParameter newParameter: typeParameters) {
				for(TypeParameter oldParameter: baseParameters) {
					JavaTypeReference tref = new BasicJavaTypeReference(oldParameter.signature().name());
					tref.setUniParent(newParameter);
					if(newParameter instanceof CapturedTypeParameter) {
						List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
						for(TypeConstraint constraint : constraints) {
							if(toBeSubstituted.contains(constraint)) {
								NonLocalJavaTypeReference.replace(tref, oldParameter, (JavaTypeReference) constraint.typeReference());
							}
						}
					} else {
						throw new ChameleonProgrammerException();
					}
				}
			}
		}
		return result;
	}
}
