package jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jnome.core.language.Java;
import jnome.core.type.DirectJavaTypeReference;
import jnome.core.type.JavaTypeReference;

import org.rejuse.predicate.TypePredicate;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.expression.Invocation;
import chameleon.core.language.Language;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.type.IntersectionType;
import chameleon.core.type.Type;
import chameleon.core.type.generics.ActualTypeArgument;
import chameleon.core.type.generics.ActualTypeArgumentWithTypeReference;
import chameleon.core.type.generics.BasicTypeArgument;
import chameleon.core.type.generics.ExtendsWildCard;
import chameleon.core.type.generics.FormalTypeParameter;
import chameleon.core.type.generics.InstantiatedTypeParameter;
import chameleon.core.type.generics.PureWildCard;
import chameleon.core.type.generics.SuperWildCard;
import chameleon.core.type.generics.TypeParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.support.expression.AssignmentExpression;

public class SecondPhaseConstraintSet extends ConstraintSet<SecondPhaseConstraint> {

	public Set<Type> ST(JavaTypeReference U) throws LookupException {
		return U.getElement().getAllSuperTypes();
	}

	public Set<Type> EST(JavaTypeReference<?> U) throws LookupException {
		Set<Type> STU = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type type:STU) {
			result.add(U.language(Java.class).erasure(type));
		}
		return result;
	}

	public Set<Type> EC(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = Us(Tj);
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

	private List<JavaTypeReference> Us(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = new ArrayList<JavaTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if(constraint.typeParameter().sameAs(Tj)) {
				Us.add(constraint.URef());
			}
		}
		return Us;
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
	
	public Set<Type> Inv(Type G, TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = Us(Tj);
		Set<Type> result = new HashSet<Type>();
		for(JavaTypeReference U: Us) {
			result.addAll(Inv(G, U));
		}
		return result;
	}
	
	public Set<Type> Inv(Type G, JavaTypeReference U) throws LookupException {
		Type base = G.baseType();
		Set<Type> superTypes = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type superType: superTypes) {
			if(superType.baseType().sameAs(base)) {
				result.add(superType);
			}
		}
		return result; 
	}
	
	public Type lci(Set<Type> types) throws LookupException {
		List<Type> list = new ArrayList<Type>(types);
		return lci(list);
	}
	
	public Type lci(List<Type> types) throws LookupException {
		int size = types.size();
		if(size == 1) {
			return types.get(0);
		} else if(size >= 2) {
			Type lci = lci(types.get(0), types.get(1));
			if(size == 2) {
				return lci;
			} else {
				List<Type> others = new ArrayList<Type>(types);
				// remove the first
				others.remove(0);
				// replace the second with lci
				others.set(0, lci);
				return lci(others);
				
			}
		} else {
			throw new ChameleonProgrammerException("The list of types to compute lci contains less than one element.");
		}
	}
	
	public Type lci(Type first, Type second) throws LookupException {
		Type result = first.clone();
		List<ActualTypeArgument> firstArguments = arguments(first);
		List<ActualTypeArgument> secondArguments = arguments(second);
		int size = firstArguments.size();
		if(secondArguments.size() != size) {
			throw new ChameleonProgrammerException("The number of type parameters from the first list: "+size+" is different from the number of type parameters in the second list: "+secondArguments.size());
		}
		List<TypeParameter> newParameters = lcta(firstArguments, secondArguments);
		result.replaceAllParameter(newParameters);
		return result;
	}
	
	public List<ActualTypeArgument> arguments(Type type) {
		List<TypeParameter> parameters = type.parameters();
		List<ActualTypeArgument> result = new ArrayList<ActualTypeArgument>();
		for(TypeParameter parameter: parameters) {
			if(parameter instanceof InstantiatedTypeParameter) {
				result.add(((InstantiatedTypeParameter) parameter).argument());
			} else {
				throw new ChameleonProgrammerException("Trying to get the actual type arguments of a type that still has formal type parameters");
			}
		}
		return result;
	}
	
	public List<TypeParameter> lcta(List<ActualTypeArgument> firsts, List<ActualTypeArgument> seconds) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			result.add(new InstantiatedTypeParameter(((InstantiatedTypeParameter)firsts.get(i).parent()).signature().clone(),lcta(firsts.get(i), seconds.get(i))));
		}
		return result;
	}
	
	public List<JavaTypeReference> typeReferenceList(ActualTypeArgumentWithTypeReference first, ActualTypeArgumentWithTypeReference second) throws LookupException {
		List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
		list.add((JavaTypeReference) first.typeReference());
		list.add((JavaTypeReference) second.typeReference());
		return list;
	}
	
	public ActualTypeArgument lcta(ActualTypeArgument<?> first, ActualTypeArgument second) throws LookupException {
		if(first instanceof BasicTypeArgument || second instanceof BasicTypeArgument) {
			if(first instanceof BasicTypeArgument && second instanceof BasicTypeArgument) {
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					return first.clone();
				} else {
					List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
					list.add((JavaTypeReference) ((BasicTypeArgument)first).typeReference());
					list.add((JavaTypeReference) ((BasicTypeArgument)second).typeReference());
					return new ExtendsWildCard(lub(list));
				}
			}
			if(first instanceof ExtendsWildCard || second instanceof ExtendsWildCard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				ExtendsWildCard ext = (ExtendsWildCard)(basic == first ? second : first);
				return new ExtendsWildCard(lub(typeReferenceList(basic,ext)));
			}
			if(first instanceof SuperWildCard || second instanceof SuperWildCard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				SuperWildCard ext = (SuperWildCard)(basic == first ? second : first);
				return new SuperWildCard(first.language(Java.class).glb(typeReferenceList(basic,ext)));
			}
		} else if(first instanceof ExtendsWildCard || second instanceof ExtendsWildCard) {
			if(first instanceof ExtendsWildCard && second instanceof ExtendsWildCard) {
				List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
				list.add((JavaTypeReference) ((ExtendsWildCard)first).typeReference());
				list.add((JavaTypeReference) ((ExtendsWildCard)second).typeReference());
				return new ExtendsWildCard(lub(list));
			}
			if(first instanceof SuperWildCard || second instanceof SuperWildCard) {
				ExtendsWildCard ext = (ExtendsWildCard) (first instanceof ExtendsWildCard? first : second);
				SuperWildCard sup = (SuperWildCard)(ext == first ? second : first);
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					return new BasicTypeArgument(ext.typeReference().clone());
				} else {
					return new PureWildCard();
				}
			}
		} else if (first instanceof SuperWildCard && second instanceof SuperWildCard) {
			return new SuperWildCard(first.language(Java.class).glb(typeReferenceList((SuperWildCard)first,(SuperWildCard)second)));
		}
		throw new ChameleonProgrammerException("lcta is not defined for the given actual type arguments of types " + first.getClass().getName() + " and " + second.getClass().getName());
	}
	
	public JavaTypeReference lub(List<? extends JavaTypeReference> types) {
  	throw new Error();
	}
	
	public Type CandidateInvocation(Type G, TypeParameter Tj) throws LookupException {
		return lci(Inv(G,Tj));
	}
	
	public Type Candidate(Type W, TypeParameter Tj) throws LookupException {
		if(W.parameters().size() > 0) {
			return CandidateInvocation(W, Tj);
		} else {
			return W;
		}
	}
	
	public Type inferredType(TypeParameter Tj) throws LookupException {
		List<Type> MEC = new ArrayList<Type>(MEC(Tj));
		List<Type> candidates = new ArrayList<Type>();
		for(Type W:MEC) {
			candidates.add(Candidate(W,Tj));
		}
		return new IntersectionType(candidates);
	}
	
	private void processSuperTypeConstraints() throws LookupException {
		for(TypeParameter p: typeParameters()) {
			boolean hasSuperConstraints = false;
			for(SecondPhaseConstraint constraint: constraints()) {
				if(constraint instanceof SupertypeConstraint && constraint.typeParameter().sameAs(p)) {
					hasSuperConstraints = true;
					break;
				}
			}
			if(hasSuperConstraints) {
				add(new ActualTypeAssignment(p, inferredType(p)));
			}
		}
	}
	
	private void processEqualityConstraints() throws LookupException {
		boolean searching = true;
		int index = 0;
		while(searching) {
			List<? extends SecondPhaseConstraint> constraints = constraints();
			new TypePredicate<SecondPhaseConstraint, EqualTypeConstraint>(EqualTypeConstraint.class).filter(constraints);
			if(constraints.size() > 0) {
				for(SecondPhaseConstraint constraint: constraints()) {
					EqualTypeConstraint eq = (EqualTypeConstraint) constraint;
					eq.process();
				}
			} else {
				searching = false;
			}
		}
	}
	
	
	public TypeAssignmentSet assignments() {
		return _assignments;
	}
	
	public void add(TypeAssignment assignment) {
		_assignments.add(assignment);
	}
	
	private TypeAssignmentSet _assignments;

  public void process() throws LookupException {
  	processEqualityConstraints();
  	processSuperTypeConstraints();
  	processUnresolvedParameters();
  }
  
  private void processUnresolvedParameters() throws LookupException {
  	if(inContextOfAssignmentConversion()) {
  		processUnresolved(S());
  	} else {
  		ObjectOrientedLanguage language = (ObjectOrientedLanguage) typeParameters().get(0).language(ObjectOrientedLanguage.class);
			processUnresolved((JavaTypeReference) language.createTypeReference(language.getDefaultSuperClassFQN()));
  	}
  }

	private void processUnresolved(JavaTypeReference S) throws LookupException {
		JavaTypeReference<?> RRef = (JavaTypeReference) invokedGenericMethod().returnTypeReference();
		FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet();
		if(! RRef.getElement().sameAs(RRef.language(Java.class).voidType())) {
		  // the constraint S >> R', provided R is not void	
			JavaTypeReference RprimeRef = substitutedReference(RRef);
			constraints.add(new GGConstraint(S, RprimeRef.getType()));
		}
		// additional constraints Bi[T1=B(T1) ... Tn=B(Tn)] >> Ti where Bi is the declared bound of Ti
		for(TypeParameter param: unresolvedParameters()) {
			JavaTypeReference bound = (JavaTypeReference) ((FormalTypeParameter)param).upperBoundReference();
			JavaTypeReference Bi= substitutedReference(bound);
			constraints.add(new GGConstraint(Bi, param.upperBound()));
		}
		SecondPhaseConstraintSet seconds = constraints.secondPhase();
		seconds.processEqualityConstraints();
		seconds.processSubtypeConstraints();
		for(TypeParameter<?> param: unresolvedParameters()) {
			add(new ActualTypeAssignment(param, param.language(ObjectOrientedLanguage.class).getDefaultSuperClass()));
		}
	}
	
	private void processSubtypeConstraints() throws LookupException {
		for(TypeParameter p: typeParameters()) {
			boolean hasSubConstraints = false;
			for(SecondPhaseConstraint constraint: constraints()) {
				if(constraint instanceof SubtypeConstraint && constraint.typeParameter().sameAs(p)) {
					hasSubConstraints = true;
					break;
				}
			}
			if(hasSubConstraints) {
				add(new ActualTypeAssignment(p, glb(p)));
			}
		}

	}
	
	private Type glb(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> URefs = Us(Tj);
		List<Type> Us = new ArrayList<Type>();
		for(JavaTypeReference URef: URefs) {
			Us.add(URef.getElement());
		}
		return new IntersectionType(Us);
	}

	
	public List<TypeParameter> unresolvedParameters() {
		List<TypeParameter> result = typeParameters();
		result.removeAll(resolvedParameters());
		return result;
	}

	public List<TypeParameter> resolvedParameters() {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		for(TypeAssignment assignment: assignments().assignments()) {
			result.add(assignment.parameter());
		}
		return result;
	}
	


	private JavaTypeReference substitutedReference(JavaTypeReference<?> RRef) throws LookupException {
		JavaTypeReference RprimeRef = RRef.clone();
		RprimeRef.setUniParent(RRef.parent());
		// Let R' = R[T1=B(T1) ... Tn=B(Tn)] where B(Ti) is the type inferred for Ti in the previous section, or Ti if no type was inferred.
		for(TypeAssignment assignment: assignments().assignments()) {
			Type type = assignment.type();
//			JavaTypeReference replacement = new DirectJavaTypeReference(type);
			JavaTypeReference replacement = RRef.language(Java.class).reference(type);
			replacement.setUniParent(RRef.language().defaultNamespace());
			NonLocalJavaTypeReference.replace(replacement, assignment.parameter(), RprimeRef);
		}
		return RprimeRef;
	}
  
  public JavaTypeReference<?> S() throws LookupException {
  	if(! inContextOfAssignmentConversion()) {
  		throw new ChameleonProgrammerException();
  	} else {
//  		return new DirectJavaTypeReference(((AssignmentExpression)invocation().parent()).getVariable().getType());
  		return ((TypeParameter<?>)typeParameters().get(0)).language(Java.class).reference(((AssignmentExpression)invocation().parent()).getVariable().getType());
  	}
  }
  
  public boolean inContextOfAssignmentConversion() {
  	return invocation().parent() instanceof AssignmentExpression;
  }
  
  public Invocation invocation() {
  	return _invocation;
  }
  
  private Invocation _invocation;
  
  public Method invokedGenericMethod() {
  	return _invokedGenericMethod;
  }
  
  private Method _invokedGenericMethod;
}
