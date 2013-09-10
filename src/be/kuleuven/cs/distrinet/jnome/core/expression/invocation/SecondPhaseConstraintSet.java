package be.kuleuven.cs.distrinet.jnome.core.expression.invocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.language.WrongLanguageException;
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
import be.kuleuven.cs.distrinet.chameleon.oo.type.generics.EqualityConstraint;
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
import be.kuleuven.cs.distrinet.rejuse.predicate.AbstractPredicate;
import be.kuleuven.cs.distrinet.rejuse.predicate.TypePredicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;

public class SecondPhaseConstraintSet extends ConstraintSet<SecondPhaseConstraint> {

	public SecondPhaseConstraintSet(MethodInvocation invocation, MethodHeader invokedMethod, FirstPhaseConstraintSet origin) {
		super(invocation,invokedMethod);
		_assignments = new TypeAssignmentSet(typeParameters());
		_origin = origin;
	}
	
	private FirstPhaseConstraintSet _origin;

	private List<JavaTypeReference> Us(TypeParameter Tj, Class<? extends SecondPhaseConstraint> kind) throws LookupException {
		List<JavaTypeReference> Us = new ArrayList<JavaTypeReference>();
		for(SecondPhaseConstraint constraint: constraints()) {
			if((kind.isInstance(constraint)) && constraint.typeParameter().sameAs(Tj)) {
				Us.add(constraint.URef());
			}
		}
		return Us;
	}
	


	
	private Type leastUpperBound(List<? extends JavaTypeReference> Us, Java language) throws LookupException {
		return language.subtypeRelation().leastUpperBound(Us);
	}

	private void processSuperTypeConstraints() throws LookupException {
		Java language = null;
		for(TypeParameter p: typeParameters()) {
			boolean hasSuperConstraints = false;
			for(SecondPhaseConstraint constraint: constraints()) {
				if(constraint instanceof SupertypeConstraint && constraint.typeParameter().sameAs(p)) {
					hasSuperConstraints = true;
					break;
				}
			}
			if(hasSuperConstraints) {
				List<JavaTypeReference> Us = Us(p, SupertypeConstraint.class);
				if(language == null) {
					language = p.language(Java.class);
				}
				add(new ActualTypeAssignment(p, leastUpperBound(Us,language)));
			}
		}
	}
	
	private void processEqualityConstraints() throws LookupException {
		boolean searching = true;
		while(searching) {
			// Keep processing until there are no equality constraints.
			List<? extends SecondPhaseConstraint> constraints = constraints();
			new TypePredicate<EqualTypeConstraint>(EqualTypeConstraint.class).filter(constraints);
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
  	// JLS 15.12.2.8
  	// In context of assignment with type S.
  	if(inContextOfAssignmentConversion()) {
  		processUnresolved(S());
  	} else {
  		// Perform under the assumption that S is java.lang.Object
  		if(! unresolvedParameters().isEmpty()) {
//  			TypeParameter typeParameter = typeParameters().get(0);
  			View view = invocation().view();
				ObjectOrientedLanguage language = (ObjectOrientedLanguage) view.language();
  			processUnresolved((JavaTypeReference) language.createTypeReferenceInNamespace(language.getDefaultSuperClassFQN(),view.namespace()));
  		}
  	}
  }

  private JavaTypeReference box(JavaTypeReference ref) throws WrongLanguageException, LookupException {
  	return ref.language(Java.class).box(ref, ref.namespace());
  }
  
	private void processUnresolved(JavaTypeReference Sref) throws LookupException {
		JavaTypeReference RRef = (JavaTypeReference) invokedGenericMethod().returnTypeReference();
		FirstPhaseConstraintSet constraints = new FirstPhaseConstraintSet(invocation(), invokedGenericMethod());
		View view = RRef.view();
		JavaTypeReference SprimeRef = box(Sref);
		Java java = view.language(Java.class);
		if(! RRef.getElement().sameAs(java.voidType(RRef.view().namespace()))) {
		  // the constraint S >> R', provided R is not void	
			JavaTypeReference RprimeRef = substitutedReference(RRef);
			constraints.add(new GGConstraint(SprimeRef, RprimeRef.getType()));
		}
		// additional constraints Bi[T1=B(T1) ... Tn=B(Tn)] >> Ti where Bi is the declared bound of Ti
		for(TypeParameter param: typeParameters()) {
			JavaTypeReference Bi = (JavaTypeReference) param.upperBoundReference();
			JavaTypeReference BiAfterSubstitution= substitutedReference(Bi);
			
			Type Ti = (Type) param.selectionDeclaration();
			Type BTi = assignments().type(param);
			if(BTi == null) {
				BTi = Ti;
			}
			constraints.add(new GGConstraint(BiAfterSubstitution, Ti));
			constraints.add(new SSConstraint(java.reference(BTi), BiAfterSubstitution.getElement()));
			
		}
		for(GGConstraint constraint: _origin.generatedGG()) {
			constraints.add(new GGConstraint(substitutedReference(constraint.ARef()), constraint.F()));
		}
		for(EQConstraint constraint: _origin.generatedEQ()) {
			constraints.add(new EQConstraint(substitutedReference(constraint.ARef()), constraint.F()));
		}
		
		SecondPhaseConstraintSet seconds = constraints.secondPhase();
		// JLS: Any equality constraints are resolved ...
		seconds.processEqualityConstraints();
		// JLS: ..., and then, for each remaining constraint of the form Ti <: Uk, the argument
		//      Ti is inferred to be glb(U1,...,Uk)
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

	
	private Set<TypeParameter> unresolvedParameters() {
		Set<TypeParameter> typeParameters = ImmutableSet.copyOf(typeParameters());
		return Sets.difference(typeParameters, resolvedParameters());
	}

	private Set<TypeParameter> resolvedParameters() {
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
			JavaTypeReference replacement = RRef.language(Java.class).reference(type);
//			replacement.setUniParent(RRef.language().defaultNamespace()); XXX
			RprimeRef = (JavaTypeReference) NonLocalJavaTypeReference.replace(replacement, assignment.parameter(), RprimeRef);
		}
		return RprimeRef;
	}
  
	private JavaTypeReference S() throws LookupException {
  	if(! inContextOfAssignmentConversion()) {
  		throw new ChameleonProgrammerException();
  	} else {
//  		return new DirectJavaTypeReference(((AssignmentExpression)invocation().parent()).getVariable().getType());
  		return ((TypeParameter)typeParameters().get(0)).language(Java.class).reference(((AssignmentExpression)invocation().parent()).getVariableExpression().getType());
  	}
  }
  
  private boolean inContextOfAssignmentConversion() {
  	return invocation().parent() instanceof AssignmentExpression;
  }
  
}
