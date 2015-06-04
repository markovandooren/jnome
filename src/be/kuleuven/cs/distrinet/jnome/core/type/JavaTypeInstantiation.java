package be.kuleuven.cs.distrinet.jnome.core.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aikodi.chameleon.core.declaration.Declaration;
import org.aikodi.chameleon.core.lookup.DeclarationSelector;
import org.aikodi.chameleon.core.lookup.LocalLookupContext;
import org.aikodi.chameleon.core.lookup.LookupException;
import org.aikodi.chameleon.core.lookup.SelectionResult;
import org.aikodi.chameleon.core.tag.TagImpl;
import org.aikodi.chameleon.exception.ChameleonProgrammerException;
import org.aikodi.chameleon.oo.language.ObjectOrientedLanguage;
import org.aikodi.chameleon.oo.type.TypeInstantiation;
import org.aikodi.chameleon.oo.type.Parameter;
import org.aikodi.chameleon.oo.type.ParameterSubstitution;
import org.aikodi.chameleon.oo.type.Type;
import org.aikodi.chameleon.oo.type.Type.SuperTypeJudge;
import org.aikodi.chameleon.oo.type.generics.TypeArgument;
import org.aikodi.chameleon.oo.type.generics.CapturedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.FormalTypeParameter;
import org.aikodi.chameleon.oo.type.generics.InstantiatedTypeParameter;
import org.aikodi.chameleon.oo.type.generics.TypeConstraint;
import org.aikodi.chameleon.oo.type.generics.TypeParameter;
import org.aikodi.chameleon.oo.type.inheritance.InheritanceRelation;
import org.aikodi.chameleon.oo.type.inheritance.SubtypeRelation;
import org.aikodi.chameleon.util.Lists;
import org.aikodi.chameleon.util.StackOverflowTracer;
import org.aikodi.chameleon.util.Util;

import be.kuleuven.cs.distrinet.jnome.core.expression.invocation.NonLocalJavaTypeReference;
import be.kuleuven.cs.distrinet.jnome.core.language.Java7;

public class JavaTypeInstantiation extends TypeInstantiation implements JavaType {

	public <P extends Parameter> JavaTypeInstantiation(Class<P> kind, List<P> parameters, Type baseType) {
		super(kind,parameters,baseType);
	}

	public JavaTypeInstantiation(List<ParameterSubstitution<?>> parameters, Type baseType) {
		super(parameters, baseType);
	}

	public JavaTypeInstantiation(ParameterSubstitution substitution, Type baseType) {
		super(substitution, baseType);
	}

	public JavaTypeInstantiation(Type baseType, List<TypeArgument> typeArguments) throws LookupException {
		super(baseType, typeArguments);
	}

	@Override
	public List<InheritanceRelation> implicitNonMemberInheritanceRelations() {
		//FIXME speed avoid creating collection
		String defaultSuperClassFQN = language(ObjectOrientedLanguage.class).getDefaultSuperClassFQN();
		if(explicitNonMemberInheritanceRelations().isEmpty() && 
				(! getFullyQualifiedName().equals(defaultSuperClassFQN)) //"java.lang.Object"
				) {
			InheritanceRelation relation = new SubtypeRelation(language(ObjectOrientedLanguage.class).createTypeReference(defaultSuperClassFQN));
			relation.setUniParent(this);
			relation.setMetadata(new TagImpl(), IMPLICIT_CHILD);
			List<InheritanceRelation> result = new ArrayList<InheritanceRelation>();
			result.add(relation);
			return result;
		} else {
			return Collections.EMPTY_LIST;
		}
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

	@Override
	public SuperTypeJudge superTypeJudge() throws LookupException {
		SuperTypeJudge result = _judge;
		if(result == null) {
			if(_judgeLock.compareAndSet(false, true)) {
				try {
					Type captureConversion = captureConversion();
					if(captureConversion != this) {
						result = captureConversion.superTypeJudge();
					}
					else {
						result = new SuperTypeJudge();
						accumulateSuperTypeJudge(result);
					}
					_judge = result;
				} catch(LookupException e) {
					throw e;
				} finally {
					_judgeLock.compareAndSet(true, false);
				}

			} else {
				//spin lock
				while((result = _judge) == null) {}
			}
		}
		return result;
	}

	@Override
	public void accumulateSuperTypeJudge(SuperTypeJudge judge) throws LookupException {
		Type captureConversion = captureConversion();
		if(captureConversion != this) {
			captureConversion.accumulateSuperTypeJudge(judge);
		} else {
			super.accumulateSuperTypeJudge(judge);
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

	@Override
	public Type erasure() {
		return ((JavaType)origin()).erasure();
	}

	public Type captureConversion() throws LookupException {
		if(_captureConversion == null) {
			Type result = this;

			if(! (parameter(TypeParameter.class,0) instanceof CapturedTypeParameter)) {
				List<TypeParameter> actualParameters = parameters(TypeParameter.class);
				List<TypeParameter> typeParameters = Lists.create();
				Type base = baseType();
				List<TypeParameter> formalParameters = base.parameters(TypeParameter.class);
				Iterator<TypeParameter> formals = formalParameters.iterator();
				Iterator<TypeParameter> actuals = actualParameters.iterator();
				// substitute parameters by their capture bounds.
				// ITERATOR because we iterate over 'formals' and 'actuals' simultaneously.
				List<TypeConstraint> toBeSubstituted = Lists.create();
				boolean doCapture = false;
				while(actuals.hasNext()) {
					TypeParameter formalParam = formals.next();
					if(!(formalParam instanceof FormalTypeParameter)) {
						throw new LookupException("Type parameter of base type is not a formal parameter.");
					}
					TypeParameter actualParam = actuals.next();
					if(!(actualParam instanceof InstantiatedTypeParameter)) {
						throw new LookupException("Type parameter of type instantiation is not an instantiated parameter: "+actualParam.getClass().getName());
					}
					InstantiatedTypeParameter instantiatedTypeParameter = (InstantiatedTypeParameter) actualParam;
					if(instantiatedTypeParameter.hasWildCardBound()) {
						doCapture = true;
					}
					TypeParameter capturedParameter = instantiatedTypeParameter.capture((FormalTypeParameter) formalParam,toBeSubstituted);
					typeParameters.add(capturedParameter);
				}
				if(doCapture) {
					// Everything works as well when we pass 'this' instead of 'base'.
					result = language(Java7.class).createdCapturedType(new ParameterSubstitution(TypeParameter.class,typeParameters), base);
					result.setUniParent(parent());
					for(TypeParameter newParameter: typeParameters) {
						for(TypeParameter oldParameter: formalParameters) {
							//If we replace references to the old parameters with references to the captured type parameters, then
							// why is the capturing done with non-locals pointing to the formal?
							JavaTypeReference tref = new BasicJavaTypeReference(oldParameter.name());
							tref.setUniParent(newParameter);
							if(newParameter instanceof CapturedTypeParameter) {
								List<TypeConstraint> constraints = ((CapturedTypeParameter)newParameter).constraints();
								for(TypeConstraint constraint : constraints) {
									if(toBeSubstituted.contains(constraint)) {
										NonLocalJavaTypeReference.replace(tref, oldParameter, (JavaTypeReference) constraint.typeReference());
									}
								}
							}
						}
					}
				} 
			}
			_captureConversion = result;
		}
		return _captureConversion;
	}

	public JavaTypeInstantiation clone() {
		List<ParameterSubstitution<?>> args = clonedParameters();
		return new JavaTypeInstantiation(args,baseType());
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(name());
		List<TypeParameter> parameters = parameters(TypeParameter.class);
		if(parameters.size() > 0) {
			result.append('<');
			Iterator<TypeParameter> iter = parameters.iterator();
			while(iter.hasNext()) {
				TypeParameter parameter = iter.next();
				result.append(parameter.toString());
				if(iter.hasNext()) {
					result.append(",");
				}
			}
			result.append('>');
		}
		return result.toString();
	}

	@Override
	public LocalLookupContext<?> targetContext() throws LookupException {
		Type captureConversion = captureConversion();
		if(captureConversion != this) {
			return captureConversion.targetContext();
		} else {
			return super.targetContext();
		}
	}

	private Type _captureConversion;


	@Override
	public <D extends Declaration> List<? extends SelectionResult> declarations(DeclarationSelector<D> selector)
			throws LookupException {
		Type captureConversion = captureConversion();
		List<? extends SelectionResult> result;
		if(captureConversion != this) {
			result = captureConversion.declarations(selector);
		} else {
			result = super.declarations(selector);
		}
		return result;
	}

	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		Type captureConversion = captureConversion();
		List<? extends Declaration> result;
		if(captureConversion != this) {
			result = captureConversion.declarations();
		} else {
			result = super.declarations();
		}
		return result;
	}

}
