package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.exception.ChameleonProgrammerException;
import be.kuleuven.cs.distrinet.chameleon.oo.expression.MethodInvocation;
import be.kuleuven.cs.distrinet.chameleon.oo.language.ObjectOrientedLanguage;
import be.kuleuven.cs.distrinet.chameleon.oo.method.MethodHeader;
import be.kuleuven.cs.distrinet.chameleon.oo.type.IntersectionType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ActualTypeArgumentWithTypeReference;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.BasicTypeArgument;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.ExtendsWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedParameterType;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.InstantiatedTypeParameter;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.SuperWildcard;
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.TypeParameter;
import be.kuleuven.cs.distrinet.chameleon.support.expression.AssignmentExpression;
import be.kuleuven.cs.distrinet.chameleon.util.Util;
import be.kuleuven.cs.distrinet.chameleon.workspace.View;
import be.kuleuven.cs.distrinet.jnome.core.language.Java;
import be.kuleuven.cs.distrinet.jnome.core.type.JavaTypeReference;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.UnsafePredicate;

public class SecondPhaseConstraintSet extends ConstraintSet<SecondPhaseConstraint> {

	public SecondPhaseConstraintSet(MethodInvocation invocation, MethodHeader invokedMethod) {
		super(invocation,invokedMethod);
		_assignments = new TypeAssignmentSet(typeParameters());
	}

	public Set<Type> ST(JavaTypeReference U) throws LookupException {
		Set<Type> result = U.getElement().getAllSuperTypes();
		result.add(U.getElement());
		return result;
	}

	public Set<Type> EST(JavaTypeReference U) throws LookupException {
		Set<Type> STU = ST(U);
		Set<Type> result = new HashSet<Type>();
		for(Type type:STU) {
			Type erasure = U.language(Java.class).erasure(type);
			result.add(erasure);
		}
		return result;
	}

	public Set<Type> EC(TypeParameter Tj) throws LookupException {
		List<JavaTypeReference> Us = Us(Tj, SupertypeConstraint.class);
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

	private List<JavaTypeReference> Us(TypeParameter Tj, Class<? extends SecondPhaseConstraint> kind) throws LookupException {
		List<JavaTypeReference> Us = new ArrayList<JavaTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if((kind.isInstance(constraint)) && constraint.typeParameter().sameAs(Tj)) {
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
		List<JavaTypeReference> Us = Us(Tj, SupertypeConstraint.class);
		Set<Type> result = new HashSet<Type>();
		for(JavaTypeReference U: Us) {
			result.addAll(Inv(G, U));
		}
		return result;
	}
	
	public Set<Type> Inv(Type G, JavaTypeReference U) throws LookupException {
		Type base = G.baseType();
		Set<Type> superTypes = ST(U);
		//FIXME: Because we use a set, bugs may seem to disappear when debugging.
		//       Do we have to use a set anyway? The operations applied to it
		//       further on should work exactly the same whether there are duplicates or not
		Set<Type> result = new HashSet<Type>();
		for(Type superType: superTypes) {
			if(superType.baseType().sameAs(base)) {
				if(superType instanceof InstantiatedParameterType) {
					while(superType instanceof InstantiatedParameterType) {
						superType = ((InstantiatedParameterType) superType).aliasedType();
					}
				}
				result.add(superType);
			}
		}
		return result; 
	}
	
	public Type lci(Set<Type> types) throws LookupException {
		List<Type> list = new ArrayList<Type>(types);
		return lci(list);
	}
	
	private Type lci(List<Type> types) throws LookupException {
		int size = types.size();
		if(size > 0) {
			Type lci = types.get(0);
			for(int i = 1; i < size; i++) {
				lci = lci(lci, types.get(i));
			}
			return lci;
		} else {
			throw new ChameleonProgrammerException("The list of types to compute lci is empty.");
		}
	}
	
	public Type lci(Type first, Type second) throws LookupException {
		Type result = first;
		if(first.nbTypeParameters(TypeParameter.class) > 0) {
			result = Util.clone(first);
			result.setUniParent(first.parent());
			List<ActualTypeArgument> firstArguments = arguments(first);
			List<ActualTypeArgument> secondArguments = arguments(second);
			int size = firstArguments.size();
			if(secondArguments.size() != size) {
				throw new ChameleonProgrammerException("The number of type parameters from the first list: "+size+" is different from the number of type parameters in the second list: "+secondArguments.size());
			}
			List<TypeParameter> newParameters = lcta(firstArguments, secondArguments);
			result.replaceAllParameters(TypeParameter.class,newParameters);
		}
		return result;
	}
	
	private List<ActualTypeArgument> arguments(Type type) {
		List<TypeParameter> parameters = type.parameters(TypeParameter.class);
		List<ActualTypeArgument> result = new ArrayList<ActualTypeArgument>();
		for(TypeParameter parameter: parameters) {
			result.add(Java.argument(parameter));
		}
		return result;
	}
	
	public List<TypeParameter> lcta(List<ActualTypeArgument> firsts, List<ActualTypeArgument> seconds) throws LookupException {
		List<TypeParameter> result = new ArrayList<TypeParameter>();
		int size = firsts.size();
		for(int i=0; i<size;i++) {
			result.add(new InstantiatedTypeParameter(Util.clone(((InstantiatedTypeParameter)firsts.get(i).parent()).signature()),lcta(firsts.get(i), seconds.get(i))));
		}
		return result;
	}
	
	public List<JavaTypeReference> typeReferenceList(ActualTypeArgumentWithTypeReference first, ActualTypeArgumentWithTypeReference second) throws LookupException {
		List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
		list.add((JavaTypeReference) first.typeReference());
		list.add((JavaTypeReference) second.typeReference());
		return list;
	}
	
	public ActualTypeArgument lcta(ActualTypeArgument first, ActualTypeArgument second) throws LookupException {
		ActualTypeArgument result;
		if(first instanceof BasicTypeArgument || second instanceof BasicTypeArgument) {
			if(first instanceof BasicTypeArgument && second instanceof BasicTypeArgument) {
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = Util.clone(first);
				} else {
					List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
					list.add((JavaTypeReference) ((BasicTypeArgument)first).typeReference());
					list.add((JavaTypeReference) ((BasicTypeArgument)second).typeReference());
					result = first.language(Java.class).createExtendsWildcard(lub(list));
				}
			} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				ExtendsWildcard ext = (ExtendsWildcard)(basic == first ? second : first);
				result = first.language(Java.class).createExtendsWildcard(lub(typeReferenceList(basic,ext)));
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				BasicTypeArgument basic = (BasicTypeArgument) (first instanceof BasicTypeArgument? first : second);
				SuperWildcard ext = (SuperWildcard)(basic == first ? second : first);
				result = first.language(Java.class).createSuperWildcard(first.language(Java.class).glb(typeReferenceList(basic,ext)));
			} else {
				result = null;
			}
		} else if(first instanceof ExtendsWildcard || second instanceof ExtendsWildcard) {
			if(first instanceof ExtendsWildcard && second instanceof ExtendsWildcard) {
				List<JavaTypeReference> list = new ArrayList<JavaTypeReference>();
				list.add((JavaTypeReference) ((ExtendsWildcard)first).typeReference());
				list.add((JavaTypeReference) ((ExtendsWildcard)second).typeReference());
				result = first.language(Java.class).createExtendsWildcard(lub(list));
			} else if(first instanceof SuperWildcard || second instanceof SuperWildcard) {
				ExtendsWildcard ext = (ExtendsWildcard) (first instanceof ExtendsWildcard? first : second);
				SuperWildcard sup = (SuperWildcard)(ext == first ? second : first);
				Type U = ((BasicTypeArgument)first).type();
				Type V = ((BasicTypeArgument)second).type();
				if(U.sameAs(V)) {
					result = first.language(Java.class).createBasicTypeArgument(Util.clone(ext.typeReference()));
				} else {
					result = first.language(Java.class).createPureWildcard();
				}
			} else {
				result = null;
			}
		} else if (first instanceof SuperWildcard && second instanceof SuperWildcard) {
			result = first.language(Java.class).createSuperWildcard(first.language(Java.class).glb(typeReferenceList((SuperWildcard)first,(SuperWildcard)second)));
		} else {
			result = null;
		}
		if(result == null) {
		  throw new ChameleonProgrammerException("lcta is not defined for the given actual type arguments of types " + first.getClass().getName() + " and " + second.getClass().getName());
		}
		result.setUniParent(first.parent()); 
		return result;
	}
	
	public JavaTypeReference lub(List<? extends JavaTypeReference> types) {
  	throw new Error();
	}
	
	public Type CandidateInvocation(Type G, TypeParameter Tj) throws LookupException {
		return lci(Inv(G,Tj));
	}
	
	public Type Candidate(Type W, TypeParameter Tj) throws LookupException {
		if(W.parameters(TypeParameter.class).size() > 0) {
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
		if(candidates.isEmpty()) {
			throw new LookupException("No candidates for the inferred type of parameter "+Tj.signature().name()+" of class "+Tj.nearestAncestor(Type.class).getFullyQualifiedName());
		} else if(candidates.size() == 1) {
			return candidates.get(0);
		} else {
		  return IntersectionType.create(candidates);
		}
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
		while(searching) {
			// Keep processing until there are no equality constraints.
			List<? extends SecondPhaseConstraint> constraints = constraints();
			new TypePredicate<SecondPhaseConstraint, EqualTypeConstraint>(EqualTypeConstraint.class).filter(constraints);
		  EqualTypeConstraint eq = first(EqualTypeConstraint.class);
			if(eq != null) {
			  eq.process();
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
  		if(! typeParameters().isEmpty()) {
  			TypeParameter typeParameter = typeParameters().get(0);
  			View view = typeParameter.view();
				ObjectOrientedLanguage language = (ObjectOrientedLanguage) view.language();
  			processUnresolved((JavaTypeReference) language.createTypeReferenceInNamespace(language.getDefaultSuperClassFQN(),view.namespace()));
  		}
  	}
  }

	private void processUnresolved(JavaTypeReference S) throws LookupException {
		JavaTypeReference RRef = (JavaTypeReference) invokedGenericMethod().returnTypeReference();
		FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(), invokedGenericMethod());
		View view = RRef.view();
		Java java = view.language(Java.class);
		if(! RRef.getElement().sameAs(java.voidType(RRef.view().namespace()))) {
		  // the constraint S >> R', provided R is not void	
			JavaTypeReference RprimeRef = substitutedReference(RRef);
			constraints.add(new GGConstraint(S, RprimeRef.getType()));
		}
		// additional constraints Bi[T1=B(T1) ... Tn=B(Tn)] >> Ti where Bi is the declared bound of Ti
		for(TypeParameter param: typeParameters()) {
			JavaTypeReference bound = (JavaTypeReference) param.upperBoundReference();
			JavaTypeReference Bi= substitutedReference(bound);
			
			Type Ti = assignments().type(param);
			if(Ti == null) {
			 Ti = (Type) param.selectionDeclaration();
			 constraints.add(new GGConstraint(Bi, Ti));
			} else {
		   constraints.add(new SSConstraint(java.reference(Ti), Bi.getElement()));
		  }

//			 Type Ti = param.selectionDeclaration();
//			 constraints.add(new GGConstraint(Bi, Ti));
			
		}
		SecondPhaseConstraintSet seconds = constraints.secondPhase();
		seconds.processEqualityConstraints();
		seconds.processSubtypeConstraints();
		// SPEED why not use the 'java' var and view from above? 
		// not going to change this during the refactoring, too risky
		for(TypeParameter param: seconds.unresolvedParameters()) {
			View v = param.view();
			ObjectOrientedLanguage l = v.language(ObjectOrientedLanguage.class);
			seconds.add(new ActualTypeAssignment(param, l.getDefaultSuperClass(v.namespace())));
		}
		for(TypeParameter param: unresolvedParameters()) {
			add(seconds.assignments().assignment(param));
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
		List<JavaTypeReference> URefs = Us(Tj, SubtypeConstraint.class);
		List<Type> Us = new ArrayList<Type>();
		for(JavaTypeReference URef: URefs) {
			Us.add(URef.getElement());
		}
		Type intersectionType = IntersectionType.create(Us);
		return intersectionType;
	}

	
	public Set<TypeParameter> unresolvedParameters() {
		Set<TypeParameter> typeParameters = ImmutableSet.copyOf(typeParameters());
		return Sets.difference(typeParameters, resolvedParameters());
	}

	public Set<TypeParameter> resolvedParameters() {
		Builder<TypeParameter> builder = ImmutableSet.<TypeParameter>builder();
		for(TypeAssignment assignment: assignments().assignments()) {
			builder.add(assignment.parameter());
		}
		return builder.build();
	}
	


	private JavaTypeReference substitutedReference(JavaTypeReference RRef) throws LookupException {
		JavaTypeReference RprimeRef = Util.clone(RRef);
		RprimeRef.setUniParent(RRef.parent());
		// Let R' = R[T1=B(T1) ... Tn=B(Tn)] where B(Ti) is the type inferred for Ti in the previous section, or Ti if no type was inferred.
		for(TypeAssignment assignment: assignments().assignments()) {
			Type type = assignment.type();
//			JavaTypeReference replacement = new DirectJavaTypeReference(type);
			JavaTypeReference replacement = RRef.language(Java.class).reference(type);
//			replacement.setUniParent(RRef.language().defaultNamespace()); XXX
			RprimeRef = (JavaTypeReference) NonLocalJavaTypeReference.replace(replacement, assignment.parameter(), RprimeRef);
		}
		return RprimeRef;
	}
  
  public JavaTypeReference S() throws LookupException {
  	if(! inContextOfAssignmentConversion()) {
  		throw new ChameleonProgrammerException();
  	} else {
//  		return new DirectJavaTypeReference(((AssignmentExpression)invocation().parent()).getVariable().getType());
  		return ((TypeParameter)typeParameters().get(0)).language(Java.class).reference(((AssignmentExpression)invocation().parent()).getVariableExpression().getType());
  	}
  }
  
  public boolean inContextOfAssignmentConversion() {
  	return invocation().parent() instanceof AssignmentExpression;
  }
  
}
